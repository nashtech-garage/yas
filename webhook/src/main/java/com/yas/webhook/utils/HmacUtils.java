package com.yas.webhook.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HmacUtils {

  public static final String HMAC_SHA_256 = "HmacSHA256";

  public static String hash(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
    SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), HMAC_SHA_256);
    Mac mac = Mac.getInstance(HMAC_SHA_256);
    mac.init(secretKeySpec);

    return new String(mac.doFinal(data.getBytes()));
  }
}
