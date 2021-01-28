package com.dhl.pizer.flowcontrol.flowchain;

import com.dhl.pizer.vo.BugException;

public interface ProcessorFlow<T> {

    boolean entry(ControlArgs controlArgs) throws BugException;

    boolean fireEntry(ControlArgs controlArgs);

    void exit();

    void fireExit();

}
