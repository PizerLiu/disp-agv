package com.dhl.pizer.conf;

public enum Status {

    RUNNING(10, "Running"),
    FINISHED(20, "Finished");

    private int code;
    private String value = null;

    private Status(int code, String value) {
        this.code = code;
        this.value = value;

    }

    public String getValue() {
        return this.value;
    }

    public int getCode() {
        return code;
    }

    public static String getName(int code) {
        for (Status s : Status.values()) {
            if (s.getCode() == code) {
                return s.value;
            }
        }
        return null;
    }

    public static Status getStatus(int code) {
        for (Status s : Status.values()) {
            if (s.getCode() == code) {
                return s;
            }
        }
        return null;
    }

}
