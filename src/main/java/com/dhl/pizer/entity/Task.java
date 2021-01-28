package com.dhl.pizer.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
@Document(collection = "task")
public class Task implements Serializable {

    @Id
    private String id;

    @ApiModelProperty("task_id")
    private String taskId;

    @ApiModelProperty("task阶段")
    private String stage;

    @ApiModelProperty("任务状态")
    private int status;

    @ApiModelProperty("task分配车辆")
    private String intendedVehicle;

    @ApiModelProperty("确定放货库位")
    private String deliveryLocation;

    @ApiModelProperty("task工程； 工程对照阶段类型不同")
    private String project;

    private Date createTime;

    private Date updateTime;

}
