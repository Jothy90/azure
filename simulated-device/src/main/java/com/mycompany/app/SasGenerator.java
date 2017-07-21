package com.mycompany.app;

import com.sun.deploy.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class SasGenerator {
    private static String resourceUrl = "john-hub-hub.azure-devices.net/devices/john-1-test-1";
    private static String key = "P407LDIfVS12MjboYJ1n0mt2HXoramrFLwMllRI5IWs=";

    public static void main( String[] args ) throws IOException, URISyntaxException {
        System.out.println(SasGenerator.GetSASToken(resourceUrl, key));
    }

    private static String GetSASToken(String resourceUri, String key)
    {
        long epoch = System.currentTimeMillis()/1000L;
        int week = 60*60*24;

        String expiry = Long.toString(epoch+week);

        String sasToken = null;
        try {
            String stringToSign = URLEncoder.encode(resourceUri, "UTF-8") + "\n" + expiry;
            String signature = getHMAC256(key, stringToSign);
            sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, "UTF-8") +"&sig=" +
                    URLEncoder.encode(signature, "UTF-8") + "&se=" + expiry;
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        return sasToken;
    }


    public static String getHMAC256(String key, String input) {
        Mac sha256_HMAC = null;
        String hash = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            Base64.Encoder encoder = Base64.getEncoder();

            hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))));

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return hash;
    }
}
