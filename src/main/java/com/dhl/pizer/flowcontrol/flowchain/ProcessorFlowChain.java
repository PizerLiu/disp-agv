package com.dhl.pizer.flowcontrol.flowchain;

import com.dhl.pizer.vo.BugException;

public abstract class ProcessorFlowChain extends AbstractLinkedProcessorFlow<Object> {

    /**
     * Add a processor to the tail of this flow chain.
     *
     * @param protocolProcessor processor to be added.
     */
    public abstract void addLast(AbstractLinkedProcessorFlow<?> protocolProcessor);

    /**
     * Add a run chain function.
     */
    public abstract boolean run(ControlArgs controlArgs) throws BugException;

}
