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
import com.dhl.pizer.service.RegService;
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
@Service("takepoint-to-dischargeleadingpoint")
public class Stage4RunServiceImpl extends AbstractLinkedProcessorFlow {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RegService regService;

    @Override
    public boolean entry(ControlArgs controlArgs) throws BugException {

        String taskId = controlArgs.getTaskId();

        Task task = taskRepository.findByTaskId(taskId);
        // 更新task的stage
        task.setStage(TaskStageEnum.TAKEPOINT_TO_DISCHARGELEADINGPOINT.toString());
        taskRepository.save(task);

        WayBillTask wayBillTask = wayBillTaskRepository.findByTaskIdAndStatusAndStage(
                taskId,
                Status.RUNNING.getCode(),
                TaskStageEnum.TAKEPOINT_TO_DISCHARGELEADINGPOINT.toString());

        if (wayBillTask != null) {
            // 已经添加数据， 检查执行状态是否结束
            // 开始查询运单状态
            JSONObject queryTaskRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryTaskUrl.getDesc() + wayBillTask.getWayBillTaskId());

            JSONObject queryVehicleRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryVehicleUrl.getDesc() + task.getIntendedVehicle());

            if (queryVehicleRes.get("currentDestination").equals("LOC-AP1005")) {

                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }
        } else {

            Example<Location> locationExample = null;

            // 若已经有指定的放货库位
            String deliveryLocation = task.getDeliveryLocation();
            if (deliveryLocation != null && !deliveryLocation.equals("")) {
                // 查看该库位是否被锁
                locationExample = Example.of(Location.builder().type("deliveryLocation")
                        .lock(false).location(deliveryLocation).build());
            } else {
                // todo 选择空闲放货库位，同时设置占用库位，库位占用何时释放掉
                Location location = Location.builder().type("deliveryLocation").lock(false).build();
                locationExample = Example.of(location);
            }

            List<Location> locations = locationRepository.findAll(locationExample);
            if (locations.size() == 0) {
                log.warn("放货库位已被占用，请等待！");
                return false;
            }

            Location location = locations.get(0);
            String targetLocation = location.getLocation();

            // 设置库位锁定
            location.setLock(true);
            location.setUpdateTime(new Date());
            locationRepository.save(location);

            // 设置task的目标放货库位
            task.setDeliveryLocation(targetLocation);
            task.setUpdateTime(new Date());
            taskRepository.save(task);

            // 提交参数
            JSONObject params = new JSONObject();

            // destinations
            // 放下插齿，收回插齿
            String assistLocation = "";
            if (targetLocation.equals("LOC-AP1006")) {
                assistLocation = "LOC-AP1003";
            }
            if (targetLocation.equals("LOC-AP1007")) {
                assistLocation = "LOC-AP1004";
            }
            if (targetLocation.equals("LOC-AP1008")) {
                assistLocation = "LOC-AP1005";
            }

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();

            // destinations
            // 放下插齿，收回插齿
            JSONArray destinations = new JSONArray();
            JSONObject wait = SeerParamUtil.buildDestinations(
                    assistLocation, "Wait", "device:requestAtSend",  wayBillTaskId+ ":wait");
            destinations.add(wait);
            JSONObject wait1 = SeerParamUtil.buildDestinations(
                    assistLocation, "Wait", "device:queryAtExecuted", wayBillTaskId+ ":wait");
            destinations.add(wait1);

            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    assistLocation, "ForkUnload", "end_height", "1");
            destinations.add(forkUnload);

            // 补充参数
            params.put("deadline", task.getDeadlineTime());
            params.put("destinations", destinations);
            params.put("dependencies", new ArrayList<>());
            params.put("properties", new ArrayList<>());
            params.put("intendedVehicle", "");

            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId)
                    .stage(TaskStageEnum.TAKEPOINT_TO_DISCHARGELEADINGPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc() + wayBillTaskId, params);

            // agv任务可放行，将上个阶段放行
            List<String> stages = ProjectToStagesRelation.projectToStagesMap.get(task.getProject());
            String lastStage = stages.get(stages.indexOf(TaskStageEnum.TAKEPOINT_TO_DISCHARGELEADINGPOINT.toString()) - 1);

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
