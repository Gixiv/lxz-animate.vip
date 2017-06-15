
//新增加
var ActionData = CommonProtocol.ActionData;
var ActionData = new ActionData();
//组队系统数据
var GroupData = CommonProtocol.GroupData;
var GroupData = new GroupData();


function move_action() {
    ActionData.setType(1).setArea(1).setCommand(1).setMessage("正在移动");
    commonProtocol.Action = ActionData;
    send();
}
function collect_action() {
    ActionData.setType(1).setArea(1).setCommand(1).setMessage("正在采集");
    commonProtocol.Action = ActionData;
    send();
}

//组队系统
function group_action(id,command) {
	if(!isNaN(id)||!isNaN(command)){
		id = 0;command = 0;
	}
	GroupData.setId(id).setCommand(command);
    commonProtocol.Group = GroupData;
    send();
}




function send() {
    websocket.send(commonProtocol.toArrayBuffer());
    console.log("发送成功");
}


