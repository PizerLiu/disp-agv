package com.dhl.pizer.flowcontrol.flowchain;

import com.dhl.pizer.vo.BugException;

public interface FlowChainBuilder {

    ProcessorFlowChain build(String taskId) throws BugException;

}
