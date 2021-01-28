package com.dhl.pizer.flowcontrol.flowchain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ControlArgs {

    private String taskId;

    private String startLocation;

}
