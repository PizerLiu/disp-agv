package com.dhl.pizer.controller;

import java.util.Date;
import java.util.List;

import com.dhl.pizer.entity.Set;
import com.dhl.pizer.service.SetService;
import com.dhl.pizer.vo.ResponceBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "设置")
@RestController
@RequestMapping("/config")
public class SetDataController {
    @Autowired
    private SetService setService;

    @ApiOperation("更改设置")
    @PostMapping("/save")
    public ResponceBody updateSet(@RequestBody Set set) {
        // 充值hTag
        Set set2 = new Set();
        set2.setId("601764207ab6bd57abbe0af1");
        set2.setHTag(true);
        set2.setUpdateTime(new Date());
        setService.addOrUpdate(set2);

        Set res = setService.addOrUpdate(set);
        return new ResponceBody().success(res);
    }

    @ApiOperation("更改设置")
    @GetMapping("/find")
    public ResponceBody listSet(@RequestParam("id") String id) {
        Set sets = setService.findAllById(id);
        return new ResponceBody().success(sets);
    }
}
