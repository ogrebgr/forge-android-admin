package com.bolyartech.forge.admin.app;

public interface LoginPrefs {
    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    void save();

    boolean hasLoginCredentials();
}
