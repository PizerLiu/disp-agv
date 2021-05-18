package com.dhl.pizer.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "set")
public class Set implements Serializable{
    @Id
    private String id;

    @ApiModelProperty("是否开启相机检测")
    private boolean setcamera;

    @ApiModelProperty("是否连接")
    private boolean setpower;

    @ApiModelProperty("货物可取tag")
    private boolean hTag;

    @ApiModelProperty("是否连接")
    private boolean setPhotoelectricity;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("IP")
    private String setIp;
}
