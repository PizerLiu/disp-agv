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
@Document(collection = "location")
public class Location implements Serializable {

    @Id
    private String id;

    @ApiModelProperty("库位")
    private String location;

    // 部分库位和ip绑定，ip没有绑定的才会参与需求2的手机发送任务
    @ApiModelProperty("绑定ip")
    private String ip;

    @ApiModelProperty("占用情况")
    private boolean lock;

    @ApiModelProperty("占用taskId")
    private String taskId = "";

    @ApiModelProperty("父级Id")
    private String fatherLocationId = "";

    @ApiModelProperty("放货库位(deliveryLocation)、取货库位(takeLocation)、辅助点(auxiliary)")
    private String type;

    @ApiModelProperty("绑定辅助点")
    private String auxiliarylocation;

    @ApiModelProperty("辅助点绑定情况")
    private String dtauxiliary = "00";

    @ApiModelProperty("插齿高度")
    private float teethH;

    private Date createTime;

    private Date updateTime;

}
