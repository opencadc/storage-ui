package org.opencadc.storage;

import ca.nrc.cadc.auth.AuthMethod;
import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.auth.AuthorizationToken;
import ca.nrc.cadc.auth.AuthorizationTokenPrincipal;
import ca.nrc.cadc.auth.SSOCookieCredential;
import ca.nrc.cadc.reg.client.RegistryClient;
import ca.nrc.cadc.rest.RestAction;
import ca.nrc.cadc.util.StringUtil;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.security.auth.Subject;
import net.canfar.storage.web.config.StorageConfiguration;
import net.canfar.storage.web.config.VOSpaceServiceConfig;
import net.canfar.storage.web.config.VOSpaceServiceConfigManager;
import org.apache.log4j.Logger;
import org.opencadc.token.Client;

public abstract class StorageAction extends RestAction {
    private static final Logger LOGGER = Logger.getLogger(StorageAction.class);
    final StorageConfiguration storageConfiguration;
    final VOSpaceServiceConfigManager voSpaceServiceConfigManager;

    public StorageAction() {
        this.storageConfiguration = new StorageConfiguration();
        this.voSpaceServiceConfigManager = new VOSpaceServiceConfigManager(this.storageConfiguration);
    }

    protected StorageAction(
            final StorageConfiguration storageConfiguration,
            final VOSpaceServiceConfigManager voSpaceServiceConfigManager) {
        this.storageConfiguration = storageConfiguration;
        this.voSpaceServiceConfigManager = voSpaceServiceConfigManager;
    }

    protected Subject getCurrentSubject(final URL targetURL) throws Exception {
        final String rawCookieHeader = this.syncInput.getHeader("cookie");
        final Subject subject = AuthenticationUtil.getCurrentSubject();

        if (StringUtil.hasText(rawCookieHeader)) {
            final String[] firstPartyCookies = Arrays.stream(rawCookieHeader.split(";"))
                    .map(String::trim)
                    .filter(cookieString -> cookieString.startsWith(StorageConfiguration.FIRST_PARTY_COOKIE_NAME))
                    .toArray(String[]::new);

            if (firstPartyCookies.length > 0 && storageConfiguration.isOIDCConfigured()) {
                for (final String cookie : firstPartyCookies) {
                    // Only split on the first "=" symbol, and trim any wrapping double quotes
                    final String encryptedCookieValue = cookie.split("=", 2)[1].replaceAll("\"", "");

                    try {
                        final String accessToken = getOIDCClient().getAccessToken(encryptedCookieValue);

                        subject.getPrincipals()
                                .add(new AuthorizationTokenPrincipal(
                                        AuthenticationUtil.AUTHORIZATION_HEADER,
                                        AuthenticationUtil.CHALLENGE_TYPE_BEARER + " " + accessToken));
                        subject.getPublicCredentials()
                                .add(new AuthorizationToken(
                                        AuthenticationUtil.CHALLENGE_TYPE_BEARER,
                                        accessToken,
                                        Collections.singletonList(targetURL.getHost())));
                    } catch (NoSuchElementException noTokenForKeyInCacheException) {
                        LOGGER.warn("Cookie found and decrypted but no value in cache.  Ignoring cookie...");
                    }
                }

                if (!subject.getPrincipals(AuthorizationTokenPrincipal.class).isEmpty()) {
                    // Ensure it's clean first.
                    subject.getPublicCredentials(AuthMethod.class).forEach(authMethod -> subject.getPublicCredentials()
                            .remove(authMethod));
                    subject.getPublicCredentials().add(AuthMethod.TOKEN);
                }
            } else if (AuthenticationUtil.getAuthMethod(subject) == AuthMethod.COOKIE) {
                final Set<SSOCookieCredential> publicCookieCredentials =
                        subject.getPublicCredentials(SSOCookieCredential.class);
                if (!publicCookieCredentials.isEmpty()) {
                    final SSOCookieCredential publicCookieCredential =
                            publicCookieCredentials.toArray(new SSOCookieCredential[0])[0];
                    subject.getPublicCredentials()
                            .add(new SSOCookieCredential(
                                    publicCookieCredential.getSsoCookieValue(),
                                    targetURL.getHost(),
                                    publicCookieCredential.getExpiryDate()));
                }
            }
        }

        return subject;
    }

    Client getOIDCClient() throws IOException {
        return this.storageConfiguration.getOIDCClient();
    }

    protected URL lookupEndpoint(final URI serviceURI, final URI capabilityStandardURI, final AuthMethod authMethod) {
        return new RegistryClient().getServiceURL(serviceURI, capabilityStandardURI, authMethod);
    }

    protected final VOSpaceServiceConfig getCurrentService() {
        final String providedServiceName = this.syncInput.getParameter("service");
        final String serviceName = StringUtil.hasText(providedServiceName)
                ? providedServiceName
                : this.voSpaceServiceConfigManager.getDefaultServiceName();
        LOGGER.debug("Service name: " + serviceName);
        return this.voSpaceServiceConfigManager.getServiceConfig(serviceName);
    }
}
