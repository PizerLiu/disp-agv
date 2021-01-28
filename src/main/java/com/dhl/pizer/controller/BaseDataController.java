package com.dhl.pizer.controller;

import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.ResponceBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Api(tags = "运单管理")
@RestController
@RequestMapping("/api/task/")
public class BaseDataController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TaskService taskService;

    @GetMapping("/test")
    public ResponceBody test() {

        Location location = Location.builder().location("BP1000").lock(false)
                .createTime(new Date()).updateTime(new Date()).build();
        locationRepository.insert(location);

        location = Location.builder().location("BP1001").lock(false)
                .createTime(new Date()).updateTime(new Date()).build();
        locationRepository.insert(location);

        location = Location.builder().location("BP1002").lock(false)
                .createTime(new Date()).updateTime(new Date()).build();
        locationRepository.insert(location);

        return new ResponceBody().success(true);
    }

    @ApiOperation("查询运单任务")
    @GetMapping("/get")
    public ResponceBody findTask(@RequestParam("id") String id) {

        String objectId = "60070fccc3aa5f06a9d7ca31";

        Optional<Task> taskOp = taskRepository.findById(objectId);

        if (taskOp.isPresent()) {
            Task task = taskOp.get();
            return new ResponceBody().success(JSONObject.toJSON(task));
        }

        return new ResponceBody().error(null, null);

    }

    @ApiOperation("手持端添加task")
    @GetMapping("/add")
    public ResponceBody insertTask(@RequestParam("start_location") String startLocation) {

        return taskService.createTask("阿斯利康-手持端", startLocation);
    }

}

