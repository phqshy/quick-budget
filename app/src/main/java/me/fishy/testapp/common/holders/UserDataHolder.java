package me.fishy.testapp.common.holders;

import com.google.gson.Gson;

public class UserDataHolder {
    private final String username;
    private String password;
    private double balance;
    public static transient Gson gson = new Gson();
    private static transient UserDataHolder INSTANCE;

    public UserDataHolder(String username, String password){
        this.username = username;
        this.password = password;
    }

    public static Gson getGson(){
        return gson;
    }

    public double getBalance() {
        return balance;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public static UserDataHolder getInstance(){
        return INSTANCE;
    }

    public static void setInstance(UserDataHolder instance){
        INSTANCE = instance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
