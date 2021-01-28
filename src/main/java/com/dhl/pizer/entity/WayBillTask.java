package com.dhl.pizer.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@Document(collection = "way_bill_task")
public class WayBillTask implements Serializable {

    @Id
    private String id;

    @ApiModelProperty("task-id")
    private String taskId;

    @ApiModelProperty("task阶段")
    private String stage;

    @ApiModelProperty("运单id")
    private String wayBillTaskId;

    @ApiModelProperty("运单任务状态")
    private int status;

    @ApiModelProperty("运单参数")
    private String param;

    private Date createTime;

    private Date updateTime;

}
