package com.dhl.pizer.service;

import com.dhl.pizer.entity.WayBillTask;

import java.util.List;

import com.dhl.pizer.dao.WayBillTaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WayBillTaskService {
    @Autowired
    private WayBillTaskRepository wayBillTaskRepository;

    public WayBillTask addOrUpdate(WayBillTask wayBillTask) {
        return wayBillTaskRepository.save(wayBillTask);
    }

    public Page<WayBillTask> list(Pageable pageable) {
        return wayBillTaskRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        wayBillTaskRepository.deleteById(id);
    }

    public List<WayBillTask> find(String taskId) {
        return wayBillTaskRepository.findAllByTaskId(taskId);
    }
}
