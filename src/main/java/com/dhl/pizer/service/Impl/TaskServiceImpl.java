package com.dhl.pizer.service.Impl;

import com.dhl.pizer.conf.ErrorCode;
import com.dhl.pizer.conf.Prefix;
import com.dhl.pizer.conf.ProjectToStagesRelation;
import com.dhl.pizer.conf.Status;
import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.SetRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Set;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.flowcontrol.flowchain.ControlArgs;
import com.dhl.pizer.flowcontrol.flowchain.DefaultFlowChainBuilder;
import com.dhl.pizer.flowcontrol.flowchain.ProcessorFlowChain;
import com.dhl.pizer.service.TaskService;
import com.dhl.pizer.util.DateUtil;
import com.dhl.pizer.util.UuidUtils;
import com.dhl.pizer.vo.BugException;
import com.dhl.pizer.vo.ResponceBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private DefaultFlowChainBuilder defaultFlowChainBuilder;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Override
    public ResponceBody createTask(String projectName, String startLocation, String endLocation, String processingVehicle) {

        String taskId = Prefix.TaskPrefix + UuidUtils.getUUID();
        Set set = setRepository.findAllById("601764207ab6bd57abbe0af0");
        if (set == null || !set.isSetpower()) {
            return new ResponceBody().error(ErrorCode.Setting_power_false, taskId);
        }

        // 获取任务总条数
        long size = taskRepository.count();
        int count = Integer.valueOf(String.valueOf(size));

        // 计算deadline时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1 * count);
        Date d = c.getTime();

        String deadlineTime = DateUtil.formatDateByFormat(d, DateUtil.DATE_FORMAT) + "T"
                + DateUtil.formatDateByFormat(d, DateUtil.TIME_FORMAT_SSS) + "Z";

        // 创建task任务
        Task task = Task.builder().taskId(taskId).stage(
                ProjectToStagesRelation.projectToStagesMap.get(projectName).get(0)).deadlineTime(deadlineTime)
                .status(Status.RUNNING.getCode()).project(projectName).takeLocation(startLocation).deliveryLocation(endLocation)
                .intendedVehicle(processingVehicle).createTime(new Date()).updateTime(new Date()).build();
        taskRepository.insert(task);

        // 执行后续流任务
        try {
            ProcessorFlowChain chain = defaultFlowChainBuilder.build(taskId);
            boolean chainRes = chain.run(ControlArgs.builder().taskId(taskId).startLocation(startLocation).build());

            // 若chain执行完成，则task任务状态改为执行结束
            if (chainRes) {
                task.setStatus(Status.FINISHED.getCode());
                taskRepository.save(task);
            }

        } catch (BugException e) {
            return new ResponceBody().error(e.getCloudErrorCode(), taskId);
        }

        return new ResponceBody().success(taskId);
    }

    @Override
    public Set setting(String id) {
        return setRepository.findAllById(id);
    }

    @Override
    public boolean setLocationLock(String deliveryLocation, boolean lock) {
        Example<Location> locationExample = null;
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("lock")
                .withIgnorePaths("teethH");
        locationExample = Example.of(Location.builder().type("deliveryLocation")
                .location(deliveryLocation).build(), matcher);

        List<Location> locations = locationRepository.findAll(locationExample);
        if (locations.size() == 0) {
            return false;
        }

        Location location = locations.get(0);
        location.setLock(lock);
        locationRepository.save(location);
        return true;

    }

}
