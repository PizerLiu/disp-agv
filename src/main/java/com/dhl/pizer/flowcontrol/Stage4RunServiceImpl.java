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
import com.dhl.pizer.service.RegService;
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
        task.setStage(TaskStageEnum.PICKUPPOINT_TO_PLUGBOARDTEST.toString());
        taskRepository.save(task);

        WayBillTask wayBillTask = WayBillTask.builder().taskId(taskId)
                .stage(TaskStageEnum.PICKUPPOINT_TO_PLUGBOARDTEST.toString()).build();
        Example<WayBillTask> example = Example.of(wayBillTask);
        Optional<WayBillTask> wayBillTaskOp = wayBillTaskRepository.findOne(example);
        if (wayBillTaskOp.isPresent()) {
            // 已经添加数据， 检查执行状态是否结束
            // 开始查询运单状态
            wayBillTask = wayBillTaskOp.get();
            JSONObject queryRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryTaskUrl.getDesc() + wayBillTask.getWayBillTaskId());
            if (queryRes.get("state").equals("FINISHED")) {
                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }
        } else {
            // todo 选择空闲放货库位，同时设置占用库位，库位占用何时释放掉
            Location location = Location.builder().type("deliveryLocation").lock(false).build();
            Example<Location> locationExample = Example.of(location);
            List<Location> locations = locationRepository.findAll(locationExample);
            if (locations.size() == 0) {
                log.warn("放货库位全部被占用，请等待！");
                return false;
            }

            // 到达取货位置，开绿灯
            regService.setRegLed("NO.1", true);

            location = locations.get(0);
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
            if (targetLocation.equals("AP1006")) {
                assistLocation = "AP1003";
            }
            if (targetLocation.equals("AP1007")) {
                assistLocation = "AP1004";
            }
            if (targetLocation.equals("AP1008")) {
                assistLocation = "AP1005";
            }

            JSONArray destinations = new JSONArray();
            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    assistLocation, "ForkUnload", "end_height", "0");
            destinations.add(forkUnload);

            // 补充参数
            params.put("deadline", task.getDeadlineTime());
            params.put("destinations", destinations.toString());
            params.put("dependencies", "[]");
            params.put("properties", "[]");
            params.put("intendedVehicle", "");

            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc(), params);

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();
            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId)
                    .stage(TaskStageEnum.PICKUPPOINT_TO_PLUGBOARDTEST.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);
        }

        return true;
    }

    @Override
    public void exit() {

    }
}
