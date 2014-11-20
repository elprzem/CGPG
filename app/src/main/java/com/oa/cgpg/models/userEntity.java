package com.oa.cgpg.models;

import android.content.Context;

import java.security.MessageDigest;

/**
 * Created by Tomasz on 2014-11-20.
 */
public class userEntity {
    private String username;
    private String password;
    private Context context;

    public userEntity(String username, String password, Context context) throws Exception {
        this.username = username;
        this.password = sha512(password);
        this.context = context;
    }

    private String sha512(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(s.getBytes());
        byte[] bytes = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            buffer.append(tmp);
        }
        return buffer.toString();
    }

    public void register(){
        // TODO requester
    }

    public void login(){
        // TODO requester
    }
}
