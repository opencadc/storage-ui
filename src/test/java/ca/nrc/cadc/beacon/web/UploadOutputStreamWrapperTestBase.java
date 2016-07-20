/*
 ************************************************************************
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 *
 * (c) 2011.                         (c) 2011.
 * National Research Council            Conseil national de recherches
 * Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
 * All rights reserved                  Tous droits reserves
 *
 * NRC disclaims any warranties         Le CNRC denie toute garantie
 * expressed, implied, or statu-        enoncee, implicite ou legale,
 * tory, of any kind with respect       de quelque nature que se soit,
 * to the software, including           concernant le logiciel, y com-
 * without limitation any war-          pris sans restriction toute
 * ranty of merchantability or          garantie de valeur marchande
 * fitness for a particular pur-        ou de pertinence pour un usage
 * pose.  NRC shall not be liable       particulier.  Le CNRC ne
 * in any event for any damages,        pourra en aucun cas etre tenu
 * whether direct or indirect,          responsable de tout dommage,
 * special or general, consequen-       direct ou indirect, particul-
 * tial or incidental, arising          ier ou general, accessoire ou
 * from the use of the software.        fortuit, resultant de l'utili-
 *                                      sation du logiciel.
 *
 *
 * @author jenkinsd
 * 2/4/11 - 4:03 PM
 *
 * 
 * 
 ****  C A N A D I A N   A S T R O N O M Y   D A T A   C E N T R E  *****
 ************************************************************************
 */
package ca.nrc.cadc.beacon.web;


import ca.nrc.cadc.beacon.AbstractUnitTest;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;


public abstract class UploadOutputStreamWrapperTestBase<U extends UploadOutputStreamWrapper>
        extends AbstractUnitTest<U>
{
    byte[] testData;
    InputStream inputStream;
    ByteArrayOutputStream outputStream;


    protected UploadOutputStreamWrapperTestBase()
    {
        final Random random = new Random();

        setTestData(new byte[8192 * 4]);
        random.nextBytes(getTestData());

        setInputStream(new ByteArrayInputStream(getTestData()));
        setOutputStream(new ByteArrayOutputStream(getTestData().length));
    }


    @Test
    public void writeNullInput() throws Exception
    {
        try
        {
            testSubject.write(null);
            fail("Cannot process a null OutputStream.");
        }
        catch (IllegalArgumentException e)
        {
            // Good!
        }
    }

    @Test
    public void writeGood() throws Exception
    {
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.update(getTestData());
        testSubject.write(getOutputStream());

        assertEquals("Length should match.", getTestData().length,
                     testSubject.getByteCount());
        assertTrue("Calculated MD5 should match.",
                   Arrays.equals(messageDigest.digest(),
                                 testSubject.getCalculatedMD5()));
        assertTrue("End data should match given data.",
                   Arrays.equals(getTestData(),
                                 getOutputStream().toByteArray()));
    }


    protected byte[] getTestData()
    {
        return testData;
    }

    protected void setTestData(byte[] testData)
    {
        this.testData = testData;
    }

    protected void setInputStream(final InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public ByteArrayOutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setOutputStream(ByteArrayOutputStream outputStream)
    {
        this.outputStream = outputStream;
    }
}
