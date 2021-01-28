package com.dhl.pizer.vo;

import com.dhl.pizer.conf.ErrorCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("restful接口返回格式")
public class ResponceBody<T> implements Serializable {

    private static final long serialVersionUID = 9211889136173018364L;

    @ApiModelProperty("接口成功与否")
    private boolean success;

    @ApiModelProperty("状态码")
    private String code;

    @ApiModelProperty("信息")
    private String message;

    @ApiModelProperty("数据")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponceBody() {

    }

    public ResponceBody(T data) {
        this.setSuccess(true);
        this.code = "0";
        this.message = "";
        this.data = data;
    }

    private ResponceBody(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponceBody<T> success(T data) {

        return new ResponceBody(data);
    }

    public ResponceBody<T> error(ErrorCode code, Object data) {

        return new ResponceBody(false, code.getCode(), code.getMessage(), data);
    }

}
