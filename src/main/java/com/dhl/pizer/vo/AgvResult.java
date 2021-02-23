package com.dhl.pizer.vo;

public class AgvResult {

    private String name;
    private String lastAction;
    private String lastActionStatus;
    private String status;

    public AgvResult() {
    }

    public AgvResult(String name, String lastAction, String lastActionStatus, String status) {
        this.name = name;
        this.lastAction = lastAction;
        this.lastActionStatus = lastActionStatus;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastAction() {
        return lastAction;
    }

    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }

    public String getLastActionStatus() {
        return lastActionStatus;
    }

    public void setLastActionStatus(String lastActionStatus) {
        this.lastActionStatus = lastActionStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{name:" + name +
                ",lastAction:" + lastAction +
                ",lastActionStatus:" + lastActionStatus +
                ",status:" + status + "}";
    }

}
