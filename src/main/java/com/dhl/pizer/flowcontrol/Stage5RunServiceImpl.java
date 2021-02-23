package com.dhl.pizer.flowcontrol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.conf.AppApiEnum;
import com.dhl.pizer.conf.Prefix;
import com.dhl.pizer.conf.ProjectToStagesRelation;
import com.dhl.pizer.conf.Status;
import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.dao.WayBillTaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.entity.WayBillTask;
import com.dhl.pizer.flowcontrol.flowchain.AbstractLinkedProcessorFlow;
import com.dhl.pizer.flowcontrol.flowchain.ControlArgs;
import com.dhl.pizer.util.HttpClientUtils;
import com.dhl.pizer.util.SeerParamUtil;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.BugException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("dischargeleadingpoint-to-dischargepoint")
public class Stage5RunServiceImpl extends AbstractLinkedProcessorFlow {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public boolean entry(ControlArgs controlArgs) throws BugException {

        String taskId = controlArgs.getTaskId();

        Task task = taskRepository.findByTaskId(taskId);
        // 更新task的stage
        task.setStage(TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString());
        taskRepository.save(task);

        WayBillTask wayBillTask = wayBillTaskRepository.findByTaskIdAndStatusAndStage(
                taskId,
                Status.RUNNING.getCode(),
                TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString());

        if (wayBillTask != null) {
            // 已经添加数据， 检查执行状态是否结束
            // 开始查询运单状态
            JSONObject queryRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryTaskUrl.getDesc() + wayBillTask.getWayBillTaskId());

            // todo 判断放货口的信号

            if (queryRes.get("state").equals("FINISHED")) {

                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }
        } else {

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();

            // 提交参数
            JSONObject params = new JSONObject();

            // destinations
            // 放下插齿，收回插齿
            JSONArray destinations = new JSONArray();
            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    task.getDeliveryLocation(), "ForkUnload", "end_height", "0.1");
            destinations.add(forkUnload);
            JSONObject forkUnload1 = SeerParamUtil.buildDestinations(
                    task.getDeliveryLocation(), "ForkForward", "dist", "0");
            destinations.add(forkUnload1);

            // 补充参数
            params.put("deadline", task.getDeadlineTime());
            params.put("destinations", destinations);
            params.put("dependencies", new ArrayList<>());
            params.put("properties", new ArrayList<>());
            params.put("intendedVehicle", "");

            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId)
                    .stage(TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc() + wayBillTaskId, params);

            // agv任务可放行，将上个阶段放行
            List<String> stages = ProjectToStagesRelation.projectToStagesMap.get(task.getProject());
            String lastStage = stages.get(stages.indexOf(TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString()) - 1);

            WayBillTask lastWayBillTask = wayBillTaskRepository.findAllByTaskIdAndStage(taskId, lastStage);
            lastWayBillTask.setLock(false);
            lastWayBillTask.setUpdateTime(new Date());
            wayBillTaskRepository.save(lastWayBillTask);

            return false;
        }

        return false;
    }

    @Override
    public void exit() {

    }
}
