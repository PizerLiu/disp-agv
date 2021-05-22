package com.dhl.pizer.flowcontrol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.conf.AppApiEnum;
import com.dhl.pizer.conf.IpLedConfig;
import com.dhl.pizer.conf.Prefix;
import com.dhl.pizer.conf.ProjectToStagesRelation;
import com.dhl.pizer.conf.Status;
import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.dao.WayBillTaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Set;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.entity.WayBillTask;
import com.dhl.pizer.flowcontrol.flowchain.AbstractLinkedProcessorFlow;
import com.dhl.pizer.flowcontrol.flowchain.ControlArgs;
import com.dhl.pizer.service.RegService;
import com.dhl.pizer.service.SetService;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.socket.NettyClient;
import com.dhl.pizer.socket.NettyClientHandler;
import com.dhl.pizer.socket.NettyServerHandler;
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
@Service("plugboardtest-to-takepoint")
public class Stage3RunServiceImpl extends AbstractLinkedProcessorFlow {

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Autowired
    private NettyClientHandler nettyClientHandler;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SetService setService;

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

        // 更新task的stage
        task.setStage(TaskStageEnum.PLUGBOARDTEST_TO_TAKEPOINT.toString());
        task.setTakeLocation(task.getTakeLocation());
        taskRepository.save(task);

        WayBillTask wayBillTask = wayBillTaskRepository.findByTaskIdAndStatusAndStage(
                taskId,
                Status.RUNNING.getCode(),
                TaskStageEnum.PLUGBOARDTEST_TO_TAKEPOINT.toString());

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

            if ("BEING_PROCESSED".equals(queryTaskRes.get("state"))||
                    "FINISHED".equals(queryTaskRes.get("state")) &&
                            queryVehicleRes.get("currentPosition").equals(takeLocation.replace("LOC-", ""))) {

                // 取走货物，标志位改为true，可再次接收任务
                Set set = new Set();
                set.setId("601764207ab6bd57abbe0af1");
                set.setHTag(true);
                set.setUpdateTime(new Date());
                setService.addOrUpdate(set);

                // 接口查下当前任务状态，若完成则更新FINISHED
                wayBillTask.setStatus(Status.FINISHED.getCode());
                wayBillTask.setUpdateTime(new Date());
                wayBillTaskRepository.save(wayBillTask);
                return true;
            }

        } else {

            // todo 判断相机检测信号，添加判断，没有打开判断开关，则直接通过
            Set set = taskService.setting("601764207ab6bd57abbe0af0");
            if (set.isSetcamera()) {
                if (!nettyClientHandler.isCamTag()) {
                    log.info("相机检测未通过，请等待！");
                    return false;
                }
            }

            // 提交参数
            JSONObject params = new JSONObject();

            // 添加数据
            String wayBillTaskId = Prefix.WayBillPrefix + UuidUtils.getUUID();

            Location takeLocationLocation = locationRepository.findByLocation(takeLocation);
            String teethH = Float.toString(takeLocationLocation.getTeethH());

            // destinations
            // 放下插齿，收回插齿
            JSONArray destinations = new JSONArray();
            JSONObject wait = SeerParamUtil.buildDestinations(
                    takeLocation, "Wait", "device:requestAtSend",  wayBillTaskId+ ":wait");
            destinations.add(wait);
            JSONObject wait1 = SeerParamUtil.buildDestinations(
                    takeLocation, "Wait", "device:queryAtExecuted", wayBillTaskId+ ":wait");
            destinations.add(wait1);
            JSONObject forkForward = SeerParamUtil.buildDestinations(
                    takeLocation, "ForkForward", "fork_dist", "1");
            destinations.add(forkForward);
            JSONObject forkUnload = SeerParamUtil.buildDestinations(
                    takeLocation, "ForkUnload", "end_height", teethH);
            destinations.add(forkUnload);
            JSONObject forkForward1 = SeerParamUtil.buildDestinations(
                    takeLocation, "ForkForward", "fork_dist", "0");
            destinations.add(forkForward1);
            // 从取货点到取货辅助点
            JSONObject forkUnload1 = SeerParamUtil.buildDestinations(
                    takeLocationF, "ForkUnload", "end_height", "0.4");
            destinations.add(forkUnload1);

            // 补充参数
            params.put("wrappingSequence", taskId);
            params.put("destinations", destinations);
            params.put("dependencies", new ArrayList<>());
            params.put("properties", new ArrayList<>());
            // 先不指定车辆
            params.put("intendedVehicle", task.getIntendedVehicle());
            params.put("deadline", task.getDeadlineTime());

            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId).lock(true)
                    .stage(TaskStageEnum.PLUGBOARDTEST_TO_TAKEPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            HttpClientUtils.doPost(AppApiEnum.sendTaskUrl.getDesc() + wayBillTaskId, params);

            // agv任务可放行，将上个阶段放行
            List<String> stages = ProjectToStagesRelation.projectToStagesMap.get(task.getProject());
            String lastStage = stages.get(stages.indexOf(TaskStageEnum.PLUGBOARDTEST_TO_TAKEPOINT.toString()) - 1);

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