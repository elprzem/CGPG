package com.oa.cgpg.models;

import android.content.Context;

import com.oa.cgpg.dataOperations.AsyncResponse;
import com.oa.cgpg.dataOperations.XMLUserClass;

import java.security.MessageDigest;

/**
 * Created by Tomasz on 2014-11-20.
 */
public class userNetEntity {
    private String username;
    private String password;
    private String email;
    private Context context;
    private AsyncResponse del;

    public userNetEntity(String username, String password, String email, Context context, AsyncResponse d) throws Exception {
        this.username = username;
        this.password = sha512(password);
        this.email = email;
        this.context = context;
        this.del = d;
    }

    public userNetEntity(String username, String password, Context context, AsyncResponse d) throws Exception {
        this.username = username;
        this.password = sha512(password);
        this.context = context;
        this.del = d;
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

    /*
        Funkcja zwraca "OK" do AsyncResponse.processFinish() w momencie poprawnego dodania użytkownika
        lub jakiś syf w momencie błędu (login/mail wykorzystany)
     */
    public void register() {
        XMLUserClass UCR = new XMLUserClass(context, del, username, password, email);
        UCR.execute();
    }

    /*
        Funkcja zwraca "OK" do AsyncResponse.processFinish() userID w przypadku poprawnego zalogowania
        lub '-1' w przypadku niezgodności danych (username i password nie są poprawne.

     */
    public void login() {
        XMLUserClass UCR = new XMLUserClass(context, del, username, password);
        UCR.execute();
    }
}
