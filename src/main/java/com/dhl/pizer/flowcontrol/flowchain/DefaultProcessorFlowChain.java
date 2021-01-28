package com.dhl.pizer.flowcontrol.flowchain;

import com.dhl.pizer.vo.BugException;

/**
 *
 * DefaultProcessorFlowChain <- ProcessorFlowChain <- AbstractLinkedProcessorFlow
 *
 */
public class DefaultProcessorFlowChain extends ProcessorFlowChain {

    AbstractLinkedProcessorFlow<?> first = new AbstractLinkedProcessorFlow<Object>() {

        @Override
        public boolean entry(ControlArgs controlArgs) {

            return super.fireEntry(controlArgs);
        }

        @Override
        public void exit() {

            super.fireExit();
        }
    };
    AbstractLinkedProcessorFlow<?> end = first;

    @Override
    public void addLast(AbstractLinkedProcessorFlow<?> protocolProcessor) {

        end.setNext(protocolProcessor);
        end = protocolProcessor;
    }

    @Override
    public boolean entry(ControlArgs controlArgs) throws BugException {

        return first.transformEntry(controlArgs);
    }

    @Override
    public void exit() {

        first.exit();
    }

    @Override
    public void setNext(AbstractLinkedProcessorFlow<?> next) {
        addLast(next);
    }

    @Override
    public AbstractLinkedProcessorFlow<?> getNext() {
        return first.getNext();
    }

    @Override
    public boolean run(ControlArgs controlArgs) throws BugException {

        boolean process = true;
        while (first != null) {

            boolean nextProcess = first.entry(controlArgs);
            first.exit();

            if (!nextProcess) {
                // 灰度流量不通过
                process = false;
                break;
            }

            first = first.getNext();
        }

        return process;
    }

}
