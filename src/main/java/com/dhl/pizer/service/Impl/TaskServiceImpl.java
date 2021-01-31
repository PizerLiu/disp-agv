package com.dhl.pizer.service.Impl;

import com.dhl.pizer.conf.Prefix;
import com.dhl.pizer.conf.ProjectToStagesRelation;
import com.dhl.pizer.conf.Status;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.flowcontrol.flowchain.ControlArgs;
import com.dhl.pizer.flowcontrol.flowchain.DefaultFlowChainBuilder;
import com.dhl.pizer.flowcontrol.flowchain.ProcessorFlowChain;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.util.DateUtil;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.BugException;
import com.dhl.pizer.vo.ResponceBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private DefaultFlowChainBuilder defaultFlowChainBuilder;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public ResponceBody createTask(String projectName, String startLocation) {

        String taskId = Prefix.TaskPrefix + UuidUtils.getUUID();

        // 计算deadline时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -7);
        Date d = c.getTime();

        String deadlineTime = DateUtil.formatDateByFormat(d, DateUtil.DATE_FORMAT) + "T"
                + DateUtil.formatDateByFormat(d, DateUtil.TIME_FORMAT_SSS) + "Z";

        // 创建task任务
        Task task = Task.builder().taskId(taskId).stage(
                ProjectToStagesRelation.projectToStagesMap.get(projectName).get(0)).deadlineTime(deadlineTime)
                .status(Status.RUNNING.getCode()).project(projectName).createTime(new Date())
                .updateTime(new Date()).build();
        taskRepository.insert(task);

        // 执行后续流任务
        try {
            ProcessorFlowChain chain = defaultFlowChainBuilder.build(taskId);
            boolean chainRes = chain.run(ControlArgs.builder().taskId(taskId).startLocation(startLocation).build());

            // 若chain执行完成，则task任务状态改为执行结束
            if (chainRes) {
                task.setStatus(Status.FINISHED.getCode());
                taskRepository.save(task);
            }

        } catch (BugException e) {
            return new ResponceBody().error(e.getCloudErrorCode(), taskId);
        }

        return new ResponceBody().success(taskId);
    }

}
