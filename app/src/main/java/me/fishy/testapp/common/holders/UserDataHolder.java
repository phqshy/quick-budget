package me.fishy.testapp.common.holders;

import java.util.UUID;

public class UserDataHolder {
    private UUID uuid;
    private String username;
    private String password;
    private double balance;

    public UserDataHolder(String username, String password){
        this.username = username;
        this.password = password;
        this.uuid = UUID.randomUUID();
    }

    public double getBalance() {
        return balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
