package com.dhl.pizer.dao;

import java.util.List;
import com.dhl.pizer.entity.WayBillTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WayBillTaskRepository extends MongoRepository<WayBillTask, String> {

    List<WayBillTask> findAllByTaskId(String taskId);

    WayBillTask findByTaskIdAndStatusAndStage(String taskId, Integer status, String stage);

    WayBillTask findAllByTaskIdAndStage(String taskId, String stage);

    WayBillTask findByWayBillTaskId(String wayBillTaskId);

}
