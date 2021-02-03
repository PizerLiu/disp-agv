package com.dhl.pizer.flowcontrol.flowchain;

import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.conf.ErrorCode;
import com.dhl.pizer.conf.ProjectToStagesRelation;
import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.flowcontrol.TaskStageRunFactory;
import com.dhl.pizer.vo.BugException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DefaultFlowChainBuilder implements FlowChainBuilder {

    @Autowired
    private TaskStageRunFactory taskStageRunFactory;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ProcessorFlowChain build(String taskId) throws BugException {

        ProcessorFlowChain chain = new DefaultProcessorFlowChain();

        // 查询task任务信息
        Task task = taskRepository.findByTaskId(taskId);
        if (task == null) {
            throw new BugException(ErrorCode.Task_Not_Exist);
        }

        // 获取该任务阶段信息，根据当前阶段，丢弃已执行完成的阶段
        String currentStage = task.getStage();

        List<String> stages = ProjectToStagesRelation.projectToStagesMap.get(task.getProject());
        if (stages == null || stages.size() == 0) {
            throw new BugException(ErrorCode.Build_Chain_Error);
        }

        for (String stage : stages) {
            // 丢弃已执行完成的阶段
            if (stages.indexOf(stage) < stages.indexOf(currentStage)) {
                continue;
            }

            AbstractLinkedProcessorFlow obj = taskStageRunFactory.createTaskStageRunService(
                    TaskStageEnum.valueOf(stage));
            if (obj == null) {
                log.error("type[" + stage + "] is not exist!");
                throw new BugException(ErrorCode.Build_Chain_Error);
            }
            chain.addLast(obj);
        }

        return chain;
    }
}
