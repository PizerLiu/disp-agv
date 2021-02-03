package com.dhl.pizer.flowcontrol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.conf.AppApiEnum;
import com.dhl.pizer.conf.Prefix;
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

        WayBillTask wayBillTask = WayBillTask.builder().taskId(taskId).status(Status.RUNNING.getCode())
                .stage(TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString()).build();
        Example<WayBillTask> example = Example.of(wayBillTask);
        Optional<WayBillTask> wayBillTaskOp = wayBillTaskRepository.findOne(example);
        if (wayBillTaskOp.isPresent()) {
            // 已经添加数据， 检查执行状态是否结束
            // 开始查询运单状态
            wayBillTask = wayBillTaskOp.get();
//            JSONObject queryRes = HttpClientUtils.getForJsonResult(
//                    AppApiEnum.queryTaskUrl.getDesc() + wayBillTask.getWayBillTaskId());
//            if (queryRes.get("state").equals("FINISHED")) {
            if (true) {
                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }
        } else {
            // 提交参数
            JSONObject params = new JSONObject();

            // destinations
            // 放下插齿，收回插齿
            JSONArray destinations = new JSONArray();
            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    task.getDeliveryLocation(), "ForkUnload", "end_height", "0");
            destinations.add(forkUnload);

            // 补充参数
            params.put("deadline", task.getDeadlineTime());
            params.put("destinations", destinations.toString());
            params.put("dependencies", "[]");
            params.put("properties", "[]");
            params.put("intendedVehicle", "");

//            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc(), params);

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();
            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId)
                    .stage(TaskStageEnum.DISCHARGELEADINGPOINT_TO_DISCHARGEPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            return false;
        }

        return true;
    }

    @Override
    public void exit() {

    }
}
