package com.dhl.pizer.dao;

import java.util.List;
import com.dhl.pizer.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {

    Task findByTaskId(String taskId);

    //注解拿开就是根据id查询并分页，如果多个条件根据json格式来写多个，如 {‘id’:’?0′,’name’:’?1′}
    @Query("{'deviceId':'?0'}")
    Page<Task> findAllByDeviceId(String deviceId, Pageable pageable);

    List<Task> findAllByTaskId(String taskId);
}
