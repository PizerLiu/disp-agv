package com.dhl.pizer.conf;

public enum IpLedConfig {

    LED1("192.168.1.1", "交通灯");

    private String ip;

    private String led;

    IpLedConfig(String ip, String led) {
        this.ip = ip;
        this.led = led;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String stage) {
        this.ip = ip;
    }

    public String getLed() {
        return led;
    }

    public void setLed(String led) {
        this.led = led;
    }

}
