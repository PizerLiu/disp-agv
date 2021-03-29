package com.dhl.pizer.service;

import com.dhl.pizer.vo.ResponceBody;

public interface TaskService {

    ResponceBody createTask(String projectName, String startLocation, String endLocation);

}
