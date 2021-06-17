package com.dhl.pizer.controller;

import java.util.List;

import com.dhl.pizer.entity.Task;
import com.dhl.pizer.entity.WayBillTask;
import com.dhl.pizer.service.WayBillTaskService;
import com.dhl.pizer.vo.ResponceBody;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "任务条例")
@RestController
@RequestMapping("/web")
public class WayBillTaskDataController {
    @Autowired
    private WayBillTaskService wayBillTaskService;

    @ApiOperation("查看任务条例")
    @GetMapping("/waybilltask/list")
    public ResponceBody getLocationList(@RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WayBillTask> wayBillTasks = wayBillTaskService.list(pageable);
        
        return new ResponceBody().success(wayBillTasks);
    }

    @ApiOperation("添加更改任务条例")
    @PostMapping("/waybilltask/addorupdata")
    public ResponceBody addWayBillTask(@RequestBody WayBillTask wayBillTask) {
        WayBillTask res = wayBillTaskService.addOrUpdate(wayBillTask);
        return new ResponceBody().success(res);
    }

    @ApiOperation("删除任务条例")
    @DeleteMapping("/waybilltask/delete")
    public ResponceBody deleteWayBillTask(@RequestParam("id") String id) {
        wayBillTaskService.deleteById(id);
        return new ResponceBody().success(true);
    }

    @ApiOperation("删除全部任务条例")
    @DeleteMapping("/waybilltask/deleteAll")
    public ResponceBody deleteAllWayBillTask() {
//        wayBillTaskService.deleteAll();
        WayBillTask wayBillTask = new WayBillTask();
        wayBillTask.setStatus(10);
        wayBillTaskService.delete(wayBillTask);
        return new ResponceBody().success(true);
    }

    @ApiOperation("查找任务条例")
    @GetMapping("/waybilltask/find")
    public ResponceBody findWayBillTask(@RequestParam("taskId") String taskId) {
        List<WayBillTask> wayBillTasks = wayBillTaskService.find(taskId);
        return new ResponceBody().success(wayBillTasks);
    }
}
