package com.dhl.pizer.conf;

public enum ErrorCode {

    Build_Chain_Error("0a1100", "构建该project的chain失败，请检查配置"),
    Task_Not_Exist("0a1101", "taskId不存在，请检查任务"),
    Param_StartLocation_Not_Exist("0a1102", "起始位置参数请正确填写，请检查"),
    Param_EndLocation_Not_Exist("0a1103", "结束位置参数请正确填写，请检查"),
    Setting_power_false("0a1104", "系统开发已关闭，请前往web开启");

    // 成员变量
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
