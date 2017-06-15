//设置舞台
function appendln(text) {
    var ta = document.getElementById('responseText');
    ta.value = '';
    ta.value += text + "\r\n";
}

function show_action(data) {
	console.log("展示成功");
	console.log(data);
    appendln("接收:" + data.getAction().getMessage());
    console.log("展示成功");
}


