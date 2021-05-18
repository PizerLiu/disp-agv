package com.dhl.pizer.controller;

import com.dhl.pizer.conf.ErrorCode;
import com.dhl.pizer.conf.Status;
import com.dhl.pizer.conf.TaskStageEnum;
import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.SetRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.dao.WayBillTaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Set;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.entity.WayBillTask;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.socket.NettyServerHandler;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.MobileAddTaskDto;
import com.dhl.pizer.vo.ResponceBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Api(tags = "运单管理")
@RestController
@RequestMapping("/api/task/")
public class BaseDataController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    @Autowired
    private NettyServerHandler nettyServerHandler;

    @Autowired
    private LocationRepository locationRepository;

    @GetMapping("/test")
    public ResponceBody test() {

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("teethH");

        Example<Location> locationExample = Example.of(Location.builder().type("takeLocation").lock(false).build(),
                matcher);
        List<Location> locations = locationRepository.findAll(locationExample);

        return new ResponceBody().success(locations);
    }

    // @ApiOperation("查询运单任务")
    // @GetMapping("/get")
    // public ResponceBody findTask(@RequestParam("id") String id) {

    // String objectId = "60070fccc3aa5f06a9d7ca31";

    // Optional<Task> taskOp = taskRepository.findById(objectId);

    // if (taskOp.isPresent()) {
    // Task task = taskOp.get();
    // return new ResponceBody().success(JSONObject.toJSON(task));
    // }

    // return new ResponceBody().error(null, null);

    // }

    @ApiOperation("手持端添加task")
    @GetMapping("/add")
    public ResponceBody insertTask(@RequestParam("start_location") String startLocation) {

        return taskService.createTask("阿斯利康-手持端", "LOC-AP1002", "");
    }

    // @ApiOperation("添加运单任务")
    // @PostMapping("/add")
    // public ResponceBody insertTask(@RequestBody Task task) {
    //
    // Task res = taskRepository.insert(task);
    // return new ResponceBody().success(res);
    // }

    @ApiOperation("获取status=20的运单任务")
    @GetMapping("/get")
    public ResponceBody getTask() {
        List<Task> res = taskRepository.findAllByStatusNot(20);
        return new ResponceBody().success(res);
    }

    @ApiOperation("修改运单任务")
    @PostMapping("/edit")
    public ResponceBody editTask(@RequestBody Task task) {

        Task res = taskRepository.save(task);
        return new ResponceBody().success(res);
    }

    @ApiOperation("删除运单任务")
    @DeleteMapping("/delete")
    public ResponceBody deleteTask(@RequestParam("id") String id) {

        Task task = Task.builder().id(id).build();
        taskRepository.delete(task);
        return new ResponceBody().success(true);
    }

    @ApiOperation("删除全部运单任务")
    @DeleteMapping("/deleteALL")
    public ResponceBody deleteAllTask() {
        taskRepository.deleteAll();
        return new ResponceBody().success(true);
    }

    @ApiOperation("查找库位信息")
    @GetMapping("/find")
    public ResponceBody findTask(@RequestParam("taskId") String taskId) {
        List<Task> tasks = taskRepository.findAllByTaskId(taskId);
        return new ResponceBody().success(tasks);
    }

    @ApiOperation("app添加运单任务")
    @PostMapping("/addby")
    public ResponceBody addTask(@RequestBody MobileAddTaskDto mobileAddTaskDto) {
        return taskService.createTask("阿斯利康-手持端", mobileAddTaskDto.getTakeLocation(),
                mobileAddTaskDto.getDeliveryLocation());
    }
}
