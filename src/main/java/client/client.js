//检测支持环境
if (typeof dcodeIO === 'undefined' || !dcodeIO.ProtoBuf) {
    throw(new Error("ProtoBuf.js is not present. Please see www/index.html for manual setup instructions."));
}
var ProtoBuf = dcodeIO.ProtoBuf;
//读取预先设定的proto
var proto = ProtoBuf.loadProtoFile("/client/proto/ProtocolModule.proto");
//规定的队伍信息类
var Group = proto.build("Group");
//实例化队伍信息类，准备填入实例请求实例
var  instance_Group = new Group();
//*********************************************实际业务逻辑部分，需要按需填写 start**************************************************
//填写队伍信息
instance_Group.Id = 1;           		//队伍ID  
instance_Group.Command  = 1;          	//队伍操作命令 1创建，2加入，3退出，4
//*********************************************实际业务逻辑部分，需要按需填写 end**************************************************
//把序列化后的请求实例作为请求体
var body = new Uint8Array(instance_Group.toArrayBuffer());


// 请求websocket
var websocket = null;
window.onload=function(){ 
	
	websocket = new WebSocket('ws://localhost:19999/ws');
	websocket.binaryType = "arraybuffer";
	
	// 连接成功建立的回调方法
	websocket.onopen = function () {
	    console.log("连接成功");
	    websocket.send(body);
	}
	// 接收到消息的回调方法
	websocket.onmessage = function (res) {
		  console.log("接收到消息");
	    console.log(res.data);
	    console.log(Group.decode(res.data));
	    
	    show_action(Group.decode(res.data));
	}
	// 连接关闭的回调方法
	websocket.onclose = function () {
		  console.log("连接关闭");
	}

}


