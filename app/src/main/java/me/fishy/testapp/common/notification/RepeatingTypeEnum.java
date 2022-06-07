package me.fishy.testapp.common.notification;

public enum RepeatingTypeEnum {
    NEVER(0),
    DAILY(1),
    MONTHLY(2);

    private int mode;
    private RepeatingTypeEnum(int mode){
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
