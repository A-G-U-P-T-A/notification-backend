package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Services.InitService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service public class EncryptionServiceImpl implements EncryptionService, InitService {

    private static MessageDigest md;

    @Override public void initService() {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    @Override public String getMD5(String data) {
        byte[] messageDigest = md.digest(data.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }
}
