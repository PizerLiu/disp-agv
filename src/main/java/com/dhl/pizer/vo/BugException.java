package com.dhl.pizer.vo;

import com.dhl.pizer.conf.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BugException extends Exception {

    private static final long serialVersionUID = -9129843316476393398L;

    // 错误码
    private ErrorCode cloudErrorCode;

    public BugException(ErrorCode cloudErrorCode) {
        this.cloudErrorCode = cloudErrorCode;
    }
}
