package com.dhl.pizer.conf;

public enum TaskStageEnum {

    PP_TO_TAKELEADINGPOINT("pp-to-takeleadingpoint", "控制车，PP点 -> 取货前置点，抬升插齿高度为1m"),
    PICKUPPOINT_TO_PLUGBOARDTEST("pickuppoint-to-plugboardtest",
            "判断条件： 绿灯\n" +
                    " 1. 满足条件： 取货前置点 -> 检测插齿点\n" +
                    " 2. 不满足条件： 等待"),
    PLUGBOARDTEST_TO_TAKEPOINT("plugboardtest-to-takepoint",
            "判断条件： 相机信号\n" +
                    " 1. 满足条件： 检测插齿点 -> 取货库位\n" +
                    " 2. 不满足条件： 等待"),
    TAKEPOINT_TO_DISCHARGELEADINGPOINT("takepoint-to-dischargeleadingpoint", "控制车，取货库位 -> 放货前置点"),
    DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT("dischargeleadingpoint-to-dischargepoint",
            "判断条件：放货库位空闲（三个库位有一个空闲即可）\n" +
                    " 1. 满足条件： 控制车，放货前置点 -> 放货库位，降低插齿高度为0m\n" +
                    " 2. 不满足条件：继续轮询放货库位是否有空闲"),
    DISCHARGEPOINT_TO_TAKELEADINGPOINT("dischargepoint-to-takeleadingpoint", ""),

    // 5取货辅助点，4取货点， 2放货辅助点，1放货点
    MOBILE_PP_TO_TAKELEADINGPOINT("mobile-pp-to-takeleadingpoint", "控制车，PP点 -> 取货前置点，抬升插齿高度为1m"),
    MOBILE_TAKELEADINGPOINT_TO_TAKEPOINT("mobile-takeleadingpoint-to-takepoint", "控制车，PP点 -> 取货前置点，抬升插齿高度为1m"),
    MOBILE_TAKEPOINT_TO_DISCHARGELEADINGPOINT("mobile-takepoint-to-dischargeleadingpoint", "控制车，PP点 -> 取货前置点，抬升插齿高度为1m"),
    MOBILE_DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT("mobile-dischargeleadingpoint-to-dischargepoint", "控制车，PP点 -> 取货前置点，抬升插齿高度为1m"),

    BASE_TO_PP("base-to-pp", "回停车点"),
    ;

    private String stage;

    private String message;

    TaskStageEnum(String stage, String message) {
        this.stage = stage;
        this.message = message;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
