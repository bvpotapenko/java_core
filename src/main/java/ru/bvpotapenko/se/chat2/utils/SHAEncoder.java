package ru.bvpotapenko.se.chat2.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAEncoder {

    private static final Logger LOGGER = LogManager.getLogger(SHAEncoder.class);

    public static String getSHA(String inputString) {
        try {
            return DatatypeConverter.printHexBinary(hashBytes(inputString.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private static byte[] hashBytes(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(bytes);
        return md.digest();
    }
}
