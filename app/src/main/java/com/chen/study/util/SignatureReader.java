package com.chen.study.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SignatureReader
{

    public static void main(String[] args)
    {
        int[] l = {0xAB, 0xA5, 0xDF, 0x4B, 0xB6, 0x80, 0xBF, 0xFD, 0x6B, 0x0F, 0x16, 0xCB, 0x9A, 0xC2, 0xAF, 0xC1, 0xE3, 0x9F, 0x31, 0x88};

        byte[] bytes = new byte[l.length];
        for (int i = 0; i < l.length; i++)
        {
            bytes[i] = (byte) l[i];
        }

//        System.out.println(Base64.encode(bytes));

        System.out.println(new SignatureReader().getSignture("C:\\Users\\ASUS\\Desktop\\foreign\\打包\\子包\\sign_astro.apk"));
    }

    public String getSignture(String path)
    {
        String mArchiveSourcePath = path;

        byte[] readBuffer = new byte[1024 * 8];

        try
        {
            JarFile jarFile = new JarFile(mArchiveSourcePath);
            Certificate[] certs = null;

            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements())
            {
                JarEntry je = (JarEntry) entries.nextElement();

                if (je.isDirectory())
                {
                    continue;
                }

                if (je.getName().startsWith("META-INF/"))
                {
                    continue;
                }

                Certificate[] localCerts = loadCertificates(jarFile, je, readBuffer);

                if (localCerts == null)
                {
                    System.err.println("Package has no certificates at entry " + je.getName() + " ignoring!");
                    jarFile.close();
                    return null;
                }
                else if (certs == null)
                {
                    certs = localCerts;
                }
                else
                {
                    for (int i = 0; i < certs.length; i++)
                    {
                        boolean found = false;
                        for (int j = 0; j < localCerts.length; j++)
                        {
                            if (certs[i] != null && certs[i].equals(localCerts[j]))
                            {
                                found = true;
                                break;
                            }
                        }
                        if (!found || certs.length != localCerts.length)
                        {
                            System.err.println("Package has mismatched certificates at entry " + je.getName() + "; ignoring!");
                            jarFile.close();
                            return null; // false
                        }
                    }
                }
            }

            jarFile.close();

            if (certs.length > 0)
                return new String(toChars(certs[0].getEncoded()));
            else return null;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


        return null;
    }

    private static char[] toChars(byte[] mSignature)
    {
        byte[] sig = mSignature;
        final int N = sig.length;
        final int N2 = N * 2;
        char[] text = new char[N2];

        for (int j = 0; j < N; j++)
        {
            byte v = sig[j];
            int d = (v >> 4) & 0xf;
            text[j * 2] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
            d = v & 0xf;
            text[j * 2 + 1] = (char) (d >= 10 ? ('a' + d - 10) : ('0' + d));
        }

        return text;
    }

    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer)
    {
        try
        {

            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1)
            {

            }
            is.close();

            return (Certificate[]) (je != null ? je.getCertificates() : null);
        }
        catch (IOException e)
        {
            System.err.println("Exception reading " + je.getName() + " in " + jarFile.getName() + ": " + e);
        }
        return null;
    }
}
