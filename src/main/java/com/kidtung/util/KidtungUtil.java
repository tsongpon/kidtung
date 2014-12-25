package com.kidtung.util;

import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 */
public final class KidtungUtil {

    private KidtungUtil() {
        //intentional
    }

    public static String generateRandomCode(int codeLength) {
        return RandomStringUtils.randomAlphabetic(codeLength);
    }
}
