package com.dhl.pizer.service;

import com.dhl.pizer.entity.Set;

import java.util.List;

import com.dhl.pizer.dao.SetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetService {

    @Autowired
    private SetRepository setRepository;

    public Set addOrUpdate(Set set) {
        return setRepository.save(set);
    }

    public Set findAllById(String id) {
        return setRepository.findAllById(id);
    }

}
