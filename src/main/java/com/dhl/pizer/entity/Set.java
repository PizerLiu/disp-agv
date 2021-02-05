package com.dhl.pizer.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

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
}
