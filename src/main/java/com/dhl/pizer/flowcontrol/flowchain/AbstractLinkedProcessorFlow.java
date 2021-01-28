package com.dhl.pizer.flowcontrol.flowchain;

// AbstractLinkedProcessorFlow为一个Slot节点，通过setNext指定下一个Slot节点,
// 通过 fireEntry()方法，调用下一个节点的transformEntry()最终调用到下一个Slot节点的entry方法,本身的结构类似于链表结构

import com.dhl.pizer.vo.BugException;

public abstract class AbstractLinkedProcessorFlow<T> implements ProcessorFlow<T> {

    private AbstractLinkedProcessorFlow<?> next = null;

    @Override
    public boolean fireEntry(ControlArgs controlArgs) {

        return true;
    }

    @SuppressWarnings("unchecked")
    boolean transformEntry(ControlArgs controlArgs) throws BugException {
        return entry(controlArgs);
    }

    @Override
    public void fireExit() {
        if (next != null) {
            next.exit();
        }
    }

    public AbstractLinkedProcessorFlow<?> getNext() {
        return next;
    }

    public void setNext(AbstractLinkedProcessorFlow<?> next) {
        this.next = next;
    }

}
