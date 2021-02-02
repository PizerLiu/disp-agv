package com.dhl.pizer.dao;

import java.util.List;

import com.dhl.pizer.entity.Set;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface SetRepository extends MongoRepository<Set, String>{
    List<Set> findAllById(String id);
}
