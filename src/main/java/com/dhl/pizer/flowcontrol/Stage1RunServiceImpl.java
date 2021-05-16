package com.dhl.pizer.flowcontrol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.pizer.conf.AppApiEnum;
import com.dhl.pizer.conf.ErrorCode;
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
import com.dhl.pizer.service.TaskStageRunService;
import com.dhl.pizer.socket.NoticeWebSocket;
import com.dhl.pizer.util.DateUtil;
import com.dhl.pizer.util.HttpClientUtils;
import com.dhl.pizer.util.SeerParamUtil;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.BugException;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service("pp-to-takeleadingpoint")
public class Stage1RunServiceImpl extends AbstractLinkedProcessorFlow<Object> {

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

        // 更新task的stage
        task.setStage(TaskStageEnum.PP_TO_TAKELEADINGPOINT.toString());
        task.setTakeLocation(task.getTakeLocation());
        taskRepository.save(task);

        WayBillTask wayBillTask = wayBillTaskRepository.findByTaskIdAndStatusAndStage(
                taskId,
                Status.RUNNING.getCode(),
                TaskStageEnum.PP_TO_TAKELEADINGPOINT.toString());

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

            if (("BEING_PROCESSED".equals(queryTaskRes.get("state")) ||
                    "FINISHED".equals(queryTaskRes.get("state"))) &&
                    queryVehicleRes.get("currentPosition").equals(takeLocationF.replace("LOC-", ""))) {

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
            params.put("wrappingSequence", taskId);
            params.put("destinations", destinations);
            params.put("dependencies", new ArrayList<>());
            params.put("properties", new ArrayList<>());
            // 先不指定车辆
            params.put("intendedVehicle", "");
            params.put("deadline", task.getDeadlineTime());

            wayBillTask = WayBillTask.builder().taskId(taskId).wayBillTaskId(wayBillTaskId).lock(true)
                    .stage(TaskStageEnum.PP_TO_TAKELEADINGPOINT.toString()).status(Status.RUNNING.getCode())
                    .param(params.toJSONString()).createTime(new Date()).updateTime(new Date()).build();
            wayBillTaskRepository.insert(wayBillTask);

            // 运单序列
            JSONObject sequenceParams = new JSONObject();
            List wayBillTaskList = new ArrayList();
            JSONObject wayBillTaskParams = new JSONObject();
            wayBillTaskParams.put("name", wayBillTaskId);
            wayBillTaskParams.put("order", params);
            wayBillTaskList.add(wayBillTaskParams);

            sequenceParams.put("transports", wayBillTaskList);
            sequenceParams.put("properties", new ArrayList<>());

            HttpClientUtils.doPost(AppApiEnum.sendSequenceTaskUrl.getDesc() + taskId, sequenceParams);
            return false;
        }

        return false;
    }

    @Override
    public void exit() {


    }

    public static JSONObject getYdParam(String currentKw, String carId, String endKw, String taskName, int type, String taskTime) {
        JSONObject params = new JSONObject();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -7);
        Date d = c.getTime();

        JSONArray paramArray = new JSONArray();

        if (type == 1) {
            //取货点到放货辅助点，增加一个重启柯蒂斯控制器的点，正常Wait命令即可
            JSONObject restartJsonParam = new JSONObject();
            restartJsonParam.put("locationName", "AP1000");
            restartJsonParam.put("operation", AppApiEnum.startOperation.getDesc());
            JSONArray restartUpArr = new JSONArray();
            restartJsonParam.put("properties", restartUpArr);
            paramArray.add(restartJsonParam);
        }

        JSONObject startJson = new JSONObject();
        startJson.put("key", AppApiEnum.startPropertiesKey.getDesc());
        startJson.put("value", "order_" + taskName + ":wait");
        //JSONArray startArr = new JSONArray();
        //startArr.add(startJson);
        //JSONObject startJsonParam = new JSONObject();
        //startJsonParam.put("locationName", currentKw);
        //startJsonParam.put("operation", AppApiEnum.startOperation.getDesc());
        //startJsonParam.put("properties", startArr);
        JSONObject ehdJson = new JSONObject();
        ehdJson.put("key", AppApiEnum.endPropertiesKey.getDesc());
        ehdJson.put("value", "order_" + taskName + ":arrived");
        JSONArray endArr = new JSONArray();
        endArr.add(startJson);
        endArr.add(ehdJson);
        JSONObject endJsonParam = new JSONObject();
        endJsonParam.put("locationName", endKw);
        endJsonParam.put("operation", AppApiEnum.startOperation.getDesc());
        endJsonParam.put("properties", endArr);
        paramArray.add(endJsonParam);

        if (type == 1) {

            JSONObject upParam = new JSONObject();
            upParam.put("locationName", endKw);
            upParam.put("operation", "ForkLoad");
            JSONArray upArr = new JSONArray();
//			JSONObject downJson = new JSONObject();
//			downJson.put("key", "start_height");
//			downJson.put("value","0");
            JSONObject upJson = new JSONObject();
            upJson.put("key", "end_height");
            upJson.put("value", "1");
//			upArr.add(downJson);
            upArr.add(upJson);
            upParam.put("properties", upArr);
            paramArray.add(upParam);

        }
        if (type == 2) {
            JSONObject downParam = new JSONObject();
            downParam.put("locationName", endKw);
            downParam.put("operation", "ForkUnload");
            JSONArray upArr = new JSONArray();
            JSONObject downJson = new JSONObject();
            downJson.put("key", "end_height");
            downJson.put("value", "0");
            upArr.add(downJson);
            downParam.put("properties", upArr);
            paramArray.add(downParam);
        }
        //paramArray.add(startJsonParam);
        if (StringUtils.isNoneBlank(taskTime)) {
            params.put("deadline", DateUtil.formatDateByFormat(d, DateUtil.DATE_FORMAT) + "T"
                    + DateUtil.formatDateByFormat(d, DateUtil.TIME_FORMAT_SSS) + "Z");
        } else {
            params.put("deadline", taskTime);
        }
        params.put("destinations", paramArray.toString());
        params.put("dependencies", AppApiEnum.dependencies.getDesc());
        params.put("properties", AppApiEnum.properties.getDesc());
        params.put("intendedVehicle", carId);

        return params;
    }

    public static void main(String[] args) {

        JSONObject a = getYdParam("C1-01-01", "Fork-01", "C1-01-03",
                "f9dd337ef8ba4a5ea343d1c88305401e", 1, "2019-06-22T16:57:34.711Z");
        System.out.println(JSONObject.toJSONString(a));


//		Map<String, String> map=JsonUtil.jsonToMap(getYdParam("C1-01-01","Fork-01","C1-01-03","f9dd337ef8ba4a5ea343d1c88305401e",2,"2019-06-22T16:57:34.711Z").toString());
//		Set<Entry<String, String>> set=map.entrySet();
//		for(Entry<String, String> entry:set){
//			System.out.println(entry.getKey()+"====>"+entry.getValue());
//		}

        // 提交参数
//        JSONObject params = new JSONObject();
//
//        // destinations
//        // 放下插齿，收回插齿
//        JSONArray destinations = new JSONArray();
//        JSONObject forkUnload = SeerParamUtil.buildDestinations(
//                "AP1000_00", "ForkUnload", "end_height", "0");
//        destinations.add(forkUnload);
//
//        JSONObject forkForward = SeerParamUtil.buildDestinations(
//                "AP1000_00", "ForkForward", "end_height", "0");
//        destinations.add(forkForward);
//
//        // 补充参数
//        params.put("destinations", destinations.toString());
//        params.put("dependencies", "[]");
//        params.put("properties", "[]");
//        params.put("intendedVehicle", "");

//        System.out.println(JSONObject.toJSON(params));

    }

}
