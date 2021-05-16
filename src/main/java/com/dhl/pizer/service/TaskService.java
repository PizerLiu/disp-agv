package com.dhl.pizer.service;

import com.dhl.pizer.entity.Set;
import com.dhl.pizer.vo.ResponceBody;

public interface TaskService {

    ResponceBody createTask(String projectName, String startLocation, String endLocation);

    Set setting(String id);

    boolean setLocationLock(String deliveryLocation, boolean lock);

}
