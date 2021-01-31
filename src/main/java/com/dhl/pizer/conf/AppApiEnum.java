package com.dhl.pizer.conf;

public enum AppApiEnum {

    //  注意deadline每个任务要对这个进行控制，拉开点，否则可能出现交叉
    //  operation是叉车动作，
    //	properties是动作参数，调节叉车高度，
    //	locationname是库位号，
    //	intendedvehicle是叉车号
	/*	参数样例：
    {
        "deadline": "2019-05-16T06:03:14.653772Z",
        "destinations": [{
            "locationName": "003",
            "operation": "ForkLoad",
            "properties": [ {
                "key": "end_height",
                "value": "1"
            }]
        }, {
            "locationName": "001",
            "operation": "ForkUnload",
            "properties": [ {
                "key": "end_height",
                "value": "0"
            }]
        }],
        "dependencies": [],
        "properties": [],
        "intendedVehicle": "Fork-01"
    }
    */

    // operation: ForkForward

    sendTaskUrl("http://192.168.1.7:7100/api/route/transportOrders/"),//发送任务的url
    queryTaskUrl("http://192.168.1.7:7100/api/route/transportOrders/"),//检查任务的url
    startOperation("Wait"),
    startPropertiesKey("device:queryAtExecuted"),
    startPropertiesValue("1"),
    endOperation("ForkUnload"),
    endPropertiesKey("device:requestAtSend"),
    endPropertiesValue("0"),
    dependencies("[]"),
    properties("[]"),
    intendedVehicle("Fork-01"),//叉车号
    upUrl("http://192.168.1.252:55100/v1/plantModel/"),//仙知启动的url
    audioUrl("http://192.168.1.252:19210/robot_other_speaker_req/"),//仙知音频api
    stopVehicleUrl("http://192.168.1.252:55200/v1/vehicles/");//停止车辆正在执行的任务的url get + 车辆名字

    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    AppApiEnum(String desc) {
        this.desc = desc;
    }

}
