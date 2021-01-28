package com.dhl.pizer.conf;

public enum ErrorCode {

    Build_Chain_Error("0a1100", "构建该project的chain失败，请检查配置"),
    Task_Not_Exist("0a1101", "taskId不存在，请检查任务");

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
