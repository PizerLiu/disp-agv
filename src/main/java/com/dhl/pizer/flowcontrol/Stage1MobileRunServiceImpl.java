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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Service("mobile-pp-to-takeleadingpoint")
public class Stage1MobileRunServiceImpl extends AbstractLinkedProcessorFlow<Object> {

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

        // 取货点：
        String takeLocation = task.getTakeLocation() ;
        // 查询对应起始点的辅助点，起始点的前置点：
        //String takeLocationF = "LOC-AP5";
        Location location1 = locationRepository.findByLocation(takeLocation);
        String takeLocationF = location1.getAuxiliarylocation();

        // 放货点：
        String deliveryLocation = task.getDeliveryLocation();
        // 查询对应放货点的辅助点，放货点的前置点：
        //String deliveryLocationF = "LOC-AP2";
        Location location2 = locationRepository.findByLocation(deliveryLocation);
        String deliveryLocationF = location2.getAuxiliarylocation();

        // 更新task的stage
        task.setStage(TaskStageEnum.MOBILE_PP_TO_TAKELEADINGPOINT.toString());
        task.setTakeLocation(task.getTakeLocation());
        taskRepository.save(task);

        WayBillTask wayBillTask = wayBillTaskRepository.findByTaskIdAndStatusAndStage(
                taskId,
                Status.RUNNING.getCode(),
                TaskStageEnum.MOBILE_PP_TO_TAKELEADINGPOINT.toString());

        if (wayBillTask != null) {
            // 已经添加数据， 检查执行状态是否结束
            // 开始查询运单状态
            JSONObject queryTaskRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryTaskUrl.getDesc() + wayBillTask.getWayBillTaskId());

            // 更新task的车辆信息
            if (queryTaskRes.get("processingVehicle") == null || queryTaskRes.get("processingVehicle").equals("")) {
                return false;
            }

            String vehicleName = queryTaskRes.get("processingVehicle").toString();
            task.setIntendedVehicle(vehicleName);
            taskRepository.save(task);

            JSONObject queryVehicleRes = HttpClientUtils.getForJsonResult(
                    AppApiEnum.queryVehicleUrl.getDesc() + vehicleName);

            if (queryVehicleRes.get("currentDestination").equals(takeLocationF)) {

                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }

        } else {
            // 提交参数
            JSONObject params = new JSONObject();

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();

            Location takeLocationFLocation = locationRepository.findByLocation(takeLocationF);
            String teethH = Float.toString(takeLocationFLocation.getTeethH());

            // destinations
            // 放下插齿，收回插齿
            JSONArray destinations = new JSONArray();
            JSONObject wait = SeerParamUtil.buildDestinations(
                    takeLocationF, "Wait", "device:requestAtSend",  wayBillTaskId+ ":wait");
            destinations.add(wait);
            JSONObject wait1 = SeerParamUtil.buildDestinations(
                    takeLocationF, "Wait", "device:queryAtExecuted", wayBillTaskId+ ":wait");
            destinations.add(wait1);
            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    takeLocationF, "ForkUnload", "end_height", teethH);
            destinations.add(forkUnload);

            // 补充参数
            params.put("destinations", destinations);
            params.put("dependencies", new ArrayList<>());
            params.put("properties", new ArrayList<>());
            // 先不指定车辆
            params.put("intendedVehicle", "");
            params.put("deadline", task.getDeadlineTime());

            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId).lock(true)
                    .stage(TaskStageEnum.MOBILE_PP_TO_TAKELEADINGPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc() + wayBillTaskId, params);

            return false;
        }

        return false;
    }

    @Override
    public void exit() {

    }
}
