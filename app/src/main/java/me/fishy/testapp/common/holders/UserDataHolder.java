package me.fishy.testapp.common.holders;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class UserDataHolder {
    private final String username;
    private String password;
    private double balance;
    //title: String, text: String
    private ArrayList<JSONObject> payments = new ArrayList<>();
    private String session;
    //title: String, text: String, date: Date, code: int
    private ArrayList<JSONObject> scheduled = new ArrayList<>();
    private double monthlyPayments;
    private double targetMonthlyPayments;
    private transient int numScheduled = 0;
    public static transient Gson gson = new Gson();
    private static transient UserDataHolder INSTANCE = null;

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

    public ArrayList<JSONObject> getPayments() {
        return payments;
    }

    public void setPayments (ArrayList<JSONObject> p){
        this.payments = p;
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

    public void addToBalance(double value){
        balance += value;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUUID() {
        return this.session;
    }

    public ArrayList<JSONObject> getScheduled() {
        return scheduled;
    }

    public void setScheduled(ArrayList<JSONObject> scheduled) {
        this.scheduled = scheduled;
    }

    public void addToScheduled(JSONObject next){
        if (scheduled == null){
            scheduled = new ArrayList<>();
        }

        scheduled.add(next);
    }

    public int addToNumScheduled() {
        numScheduled += 1;
        return numScheduled;
    }

    public void addToPayments(JSONObject object){
        scheduled.add(object);
    }

    public double getMonthlyPayments() {
        return monthlyPayments;
    }

    public void setMonthlyPayments(double monthlyPayments) {
        this.monthlyPayments = monthlyPayments;
    }

    public void addToMonthlyPayments(double amount){
        this.monthlyPayments += amount;
    }

    public double getTargetMonthlyPayments() {
        return targetMonthlyPayments;
    }

    public void setTargetMonthlyPayments(double targetMonthlyPayments) {
        this.targetMonthlyPayments = targetMonthlyPayments;
    }

    public void removeFromScheduled(JSONObject object){
        this.scheduled.remove(object);
    }
}
