package com.oa.cgpg;

/**
 * Created by Izabela on 2014-11-17.
 */
public class LoggedUserInfo {
    private static LoggedUserInfo singleInstance = null;
    private String userName;
    private String email;
    private int userId;
    private boolean isLoggedIn;

    private LoggedUserInfo() {
        isLoggedIn = false;
    }

    public static LoggedUserInfo getInstance() {
        if (singleInstance == null) {
            singleInstance = new LoggedUserInfo();
        }
        return singleInstance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.isLoggedIn = loggedIn;
    }
    public void setEmail(String email){ this.email = email;}
    public String getEmail () {return this.email;}
}
