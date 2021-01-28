package com.dhl.pizer.controller;

import com.dhl.pizer.dao.LocationRepository;
import com.dhl.pizer.dao.TaskRepository;
import com.dhl.pizer.entity.Location;
import com.dhl.pizer.entity.Task;
import com.dhl.pizer.vo.ResponceBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "运单管理")
@RestController
@RequestMapping("/web/")
public class WebDataController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LocationRepository locationRepository;

    @ApiOperation("分页查看运单信息")
    @GetMapping("/task/list")
    public ResponceBody getTaskList(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching();

        //创建实例
        Example<Task> example = Example.of(Task.builder().build(), matcher);
        Page<Task> tasks = taskRepository.findAll(pageable);

        return new ResponceBody().success(tasks);
    }

    @ApiOperation("添加库位信息")
    @PostMapping("/location/add")
    public ResponceBody insertLocation(@RequestBody Location location) {

        Location res = locationRepository.insert(location);
        return new ResponceBody().success(res);
    }

    @ApiOperation("修改库位信息")
    @PostMapping("/location/edit")
    public ResponceBody editLocation(@RequestBody Location location) {

        Location res = locationRepository.save(location);
        return new ResponceBody().success(res);
    }

    @ApiOperation("修改库位信息")
    @DeleteMapping("/location/delete")
    public ResponceBody editLocation(@RequestParam("id") String id) {

        Location location = Location.builder().id(id).build();
        locationRepository.delete(location);
        return new ResponceBody().success(true);
    }

    @ApiOperation("分页查看运单信息")
    @GetMapping("/location/list")
    public ResponceBody getLocationList(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(page, size, sort);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching();

        //创建实例
        Example<Location> example = Example.of(Location.builder().build(), matcher);
        Page<Location> locations = locationRepository.findAll(pageable);

        return new ResponceBody().success(locations);
    }

}
