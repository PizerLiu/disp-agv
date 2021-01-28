package com.dhl.pizer.flowcontrol;

import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.flowcontrol.flowchain.AbstractLinkedProcessorFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("taskStageRunFactory")
public class TaskStageRunFactory {

    @Autowired
    private Map<String, AbstractLinkedProcessorFlow> taskStageRunServiceMap;

    public AbstractLinkedProcessorFlow createTaskStageRunService(TaskStageEnum taskStageEnum) {
        return taskStageRunServiceMap.get(taskStageEnum.getStage());
    }

}
