package com.dhl.pizer.service;

import com.dhl.pizer.entity.Set;

import java.util.Date;
import java.util.List;

import com.dhl.pizer.dao.SetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private RegService regService;

    public Set addOrUpdate(Set set) {

        // 页面上设置开关时，注意关灯
        if (set.getId().equals("601764207ab6bd57abbe0af0")) {
            if (set.isSetpower()) {
                regService.setRegLed("LOC-AP5", true);
            } else {
                regService.setRegLed("LOC-AP5", false);
            }
        }

        return setRepository.save(set);
    }

    public Set findAllById(String id) {
        return setRepository.findAllById(id);
    }

}
