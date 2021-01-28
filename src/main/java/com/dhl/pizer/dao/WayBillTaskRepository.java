package com.dhl.pizer.dao;

import com.dhl.pizer.entity.WayBillTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WayBillTaskRepository extends MongoRepository<WayBillTask, String> {


}
