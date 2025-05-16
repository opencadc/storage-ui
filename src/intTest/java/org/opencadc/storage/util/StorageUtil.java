package org.opencadc.storage.util;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.auth.AuthorizationToken;
import ca.nrc.cadc.auth.NotAuthenticatedException;
import ca.nrc.cadc.auth.SSLUtil;
import ca.nrc.cadc.auth.SSOCookieCredential;
import ca.nrc.cadc.auth.X509CertificateChain;
import ca.nrc.cadc.net.HttpPost;
import ca.nrc.cadc.net.NetUtil;
import ca.nrc.cadc.net.NetrcFile;
import ca.nrc.cadc.reg.Standards;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.util.FileUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import javax.security.auth.Subject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

public class StorageUtil {
    private static final Logger LOGGER = Logger.getLogger(StorageUtil.class);
    private static final char[] SEED_CHARS;

    static {
        final StringBuilder chars = new StringBuilder(128);

        for (char c = 'a'; c <= 'z'; c++) {
            chars.append(c);
        }

        for (char c = 'A'; c <= 'Z'; c++) {
            chars.append(c);
        }

        for (char c = '0'; c <= '9'; c++) {
            chars.append(c);
        }

        chars.append("_-()=+!,:@*$.");

        SEED_CHARS = chars.toString().toCharArray();
    }

    /**
     * Read in the current user's credentials from the local path.
     *
     * @param vospaceServiceURI The current URL to use to deduce a domain.
     * @return Subject instance, never null.
     */
    public static Subject getCurrentUser(final URI vospaceServiceURI, final URI gmsURI) throws Exception {
        final Subject subject = new Subject();
        final RegistryClient registryClient = new RegistryClient();
        final URL vospaceServiceURL =
                registryClient.getServiceURL(vospaceServiceURI, Standards.VOSPACE_NODES_20, AuthMethod.ANON);

        try {
            final AuthorizationToken bearerToken = StorageUtil.getBearerToken(vospaceServiceURL);
            subject.getPublicCredentials().add(bearerToken);
            subject.getPublicCredentials().add(AuthMethod.TOKEN);
            return subject;
        } catch (MissingResourceException noTokenFile) {
            LOGGER.warn("No bearer token (test.token) found in path.");
        }

        try {
            final X509CertificateChain proxyCertificate = StorageUtil.getProxyCertificate();
            subject.getPublicCredentials().add(proxyCertificate);
            subject.getPublicCredentials().add(AuthMethod.CERT);
            return subject;
        } catch (MissingResourceException noProxyCertificate) {
            LOGGER.warn("No proxy certificate (test.pem) found in path.");
        }

        URL newLoginURL = registryClient.getServiceURL(gmsURI, Standards.UMS_LOGIN_10, AuthMethod.ANON);
        final URL loginURL = newLoginURL == null
                ? registryClient.getServiceURL(gmsURI, Standards.UMS_LOGIN_01, AuthMethod.ANON)
                : newLoginURL;
        final NetrcFile netrcFile = new NetrcFile();
        final PasswordAuthentication passwordAuthentication = netrcFile.getCredentials(loginURL.getHost(), true);
        final Map<String, Object> loginPayload = new HashMap<>();
        loginPayload.put("username", passwordAuthentication.getUserName());
        loginPayload.put("password", String.valueOf(passwordAuthentication.getPassword()));

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpPost httpPost = new HttpPost(loginURL, loginPayload, outputStream);
        httpPost.run();

        final String cookieValue = outputStream.toString();
        LOGGER.info("Using cookie value: " + cookieValue);
        subject.getPublicCredentials()
                .add(new SSOCookieCredential(cookieValue, NetUtil.getDomainName(vospaceServiceURL)));
        subject.getPublicCredentials().add(new SSOCookieCredential(cookieValue, "cadc-ccda.hia-iha.nrc-cnrc.gc.ca"));
        subject.getPublicCredentials().add(new SSOCookieCredential(cookieValue, "canfar.net"));
        subject.getPublicCredentials().add(new SSOCookieCredential(cookieValue, "cadc.dao.nrc.ca"));
        subject.getPublicCredentials().add(AuthMethod.COOKIE);

        if (AuthenticationUtil.getAuthMethod(subject) == AuthMethod.ANON) {
            throw new NotAuthenticatedException("No credentials supplied and anonymous not allowed.");
        }

        return subject;
    }

    public static String getTestDirectoryName() {
        final Properties systemProperties = System.getProperties();
        if (systemProperties.containsKey("test.directory.name")) {
            return systemProperties.getProperty("test.directory.name");
        } else {
            return StorageUtil.generateAlphaNumeric();
        }
    }

    public static URI createTestDirectoryURI(final URI nodeResourceID, final String containerNodePath) {
        return URI.create(nodeResourceID + containerNodePath);
    }

    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with underscores to keep them URL friendly.
     *
     * @return An ASCII string of 16.
     */
    public static String generateAlphaNumeric() {
        return StorageUtil.generateAlphaNumeric(16);
    }

    /**
     * Generate an ASCII string, replacing the '\' and '+' characters with underscores to keep them URL friendly.
     *
     * @param length The desired length of the generated string.
     * @return An ASCII string of the given length.
     */
    public static String generateAlphaNumeric(final int length) {
        return RandomStringUtils.random(length, 0, SEED_CHARS.length, false, false, SEED_CHARS);
    }

    public static String[] parseTestDirPath(String testDir) {
        final String[] parsedPath = testDir.split("/");
        LOGGER.debug("path:" + Arrays.toString(parsedPath));

        return parsedPath;
    }

    private static AuthorizationToken getBearerToken(final URL storageURL) throws Exception {
        final File bearerTokenFile = FileUtil.getFileFromResource("test.token", StorageUtil.class);
        final String bearerToken = new String(Files.readAllBytes(bearerTokenFile.toPath()));
        return new AuthorizationToken(
                "Bearer", bearerToken.replaceAll("\n", ""), List.of(NetUtil.getDomainName(storageURL)));
    }

    private static X509CertificateChain getProxyCertificate() throws Exception {
        final File proxyCertificateFile = FileUtil.getFileFromResource("test.pem", StorageUtil.class);
        return SSLUtil.readPemCertificateAndKey(proxyCertificateFile);
    }
}
