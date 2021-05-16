package com.dhl.pizer.controller;

import com.dhl.pizer.dao.WayBillTaskRepository;
import com.dhl.pizer.entity.WayBillTask;
import com.dhl.pizer.vo.AgvResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * agv对外的两个接口
 *
 * @author admin
 */
@Slf4j
@RequestMapping("/devices/api/v1/locationDevices")
@RestController
public class AgvTaskController {

    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    @PostMapping("/{taskname}")
    public AgvResult postTaskInterface(@PathVariable String taskname) {
        try {
            return new AgvResult(taskname, "wait", "EXECUTING", "IDLE");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/{taskname}")
    public AgvResult getTaskInterface(@PathVariable String taskname) {
        try {
            WayBillTask wayBillTask = wayBillTaskRepository.findByWayBillTaskId(taskname);
            if (!wayBillTask.isLock()) {
                log.info("taskname: "  + taskname + " 允许通过！");
                return new AgvResult(taskname, "wait", "DONE", "IDLE");
            }

            return new AgvResult(taskname, "wait", "EXECUTING", "IDLE");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
