localhost = "http://118.25.22.19:8080/";
uid = '';
province = '';
city = '';
gender = '';
age = '';
socket = '';

// localhost = "http://localhost:8080/";

$(document).ready(
    userInfo()
);

// $(window).onbeforeunload(
//     out()
// );
$(window).unload(function () {
    out();
});


function userInfo() {
    $.ajax({
        type: "GET",
        url: localhost + "chat/userInfo",
        data: {},
        success: function (data) {
            // var state = data.state;
            // if (state == "ERROR") {
            //     layer.msg(data.content, {
            //         offset: "150px"
            //     });
            // } else {
            //     var json = data.content;
            //     $("#profile-pic").attr('src', json.iconSrc);
            //     $("#top-username").text(json.name);
            //     $("#left-username").text(json.name);
            // }
        },
        error: function () {
            // layer.msg("error", {
            //     offset: "150px"
            // });
        }
    })
}

function out() {
    $.ajax({
        type: "GET",
        url: localhost + "chat/out",
        async: false,
        data: {},
        success: function (data) {
            // var state = data.state;
            // if (state == "ERROR") {
            //     layer.msg(data.content, {
            //         offset: "150px"
            //     });
            // } else {
            //     var json = data.content;
            //     $("#profile-pic").attr('src', json.iconSrc);
            //     $("#top-username").text(json.name);
            //     $("#left-username").text(json.name);
            // }
        },
        error: function () {
            // layer.msg("error", {
            //     offset: "150px"
            // });
        }
    });
}

function match(size) {
    if (socket != '') {
        socket.close();
    }
    if (size == 0) {
        $('#match-button').text("匹配中...");
        $('#match-button').attr('disabled', true);
        gender = $("#genderId").val();
        age = $("#age").val();
    } else {
        $('#match-button-sm').text("匹配中...");
        $('#match-button-sm').attr('disabled', true);
        gender = $("#genderId-sm").val();
        age = $("#age-sm").val();
    }
    setTimeout(function () {
        $.ajax({
            type: "POST",
            url: localhost + "chat/match",
            data: {
                'gender': gender,
                'age': age
            },
            success: function (data) {
                var state = data.state;
                if (state == "ERROR") {
                    layer.msg(data.content, {
                        offset: "150px"
                    });
                } else {
                    if (size == 0) {
                        $('#match-button').text("重新匹配");
                        $('#match-button').attr('disabled', false);
                    } else {
                        $('#match-button-sm').text("重新匹配");
                        $('#match-button-sm').attr('disabled', false);
                        $('#collapse-nav').collapse('close')
                    }
                    var json = data.content;
                    age = json.age;
                    uid = json.uid;
                    province = json.province;
                    city = json.city;
                    open();
                    if (json.gender == 0) {
                        gender = '男';
                    } else {
                        gender = '女';
                    }
                    if (json.age == 0) {
                        age = '18岁以下';
                    } else if (json.age == 1) {
                        age = '18-25岁';
                    } else {
                        age = '25岁以上';
                    }
                    $('#match_user_info').text(province + "  " + city + "  " + gender + "  " + age);
                    // layer.msg(uid + "  " + province + "  " + city + "  " + gender, {
                    //     offset: "150px"
                    // });
                }
            },
            error: function () {
                // layer.msg("error", {
                //     offset: "150px"
                // });
            }
        })
    }, 1000);
    // $.ajax({
    //     type: "POST",
    //     url: localhost + "chat/match",
    //     data: {
    //         'gender':  gender,
    //         'age': age
    //     },
    //     success: function (data) {
    //         var state = data.state;
    //         if (state == "ERROR") {
    //             layer.msg(data.content, {
    //                 offset: "150px"
    //             });
    //         } else {
    //             $('#match-button').text("重新匹配");
    //             $('#match-button').attr('disabled', false);
    //             var json = data.content;
    //             age = json.age;
    //             uid = json.uid;
    //             province = json.province;
    //             city = json.city;
    //             open();
    //             if (json.gender == 0) {
    //                 gender = '男';
    //             } else {
    //                 gender = '女';
    //             }
    //             if (json.age == 0) {
    //                 age = '18岁以下';
    //             } else if (json.age == 1) {
    //                 age = '18-25岁';
    //             } else {
    //                 age = '25岁以上';
    //             }
    //             $('#match_user_info').text(province + "  " + city + "  " + gender + "  " + age);
    //             // layer.msg(uid + "  " + province + "  " + city + "  " + gender, {
    //             //     offset: "150px"
    //             // });
    //         }
    //     },
    //     error: function () {
    //         // layer.msg("error", {
    //         //     offset: "150px"
    //         // });
    //     }
    // })
}

function open() {
    // 创建一个Socket实例
    socket = new WebSocket('ws://118.25.22.19:8080/websocket/' + uid);
    // 打开Socket
    socket.onopen = function (event) {
        // 监听消息
        socket.onmessage = function (event) {
            AddMsg(province + "  " + city + "  " + gender + "  " + age, event.data);
        }
    };
    // 监听Socket的关闭
    socket.onclose = function (event) {
        // console.log('Client notified socket has closed', event)
        // AddMsg("close", "聊天已结束");
        $('#msgs').text('');
        $('#match_user_info').text('聊天已结束');
    };
}

/////////////////////////////////////////////////////////////////////// 后台信息处理 /////////////////////////////////////////////////////////////////////////////////

// 发送

function send(text) {
    if (socket != '' && socket.readyState == 1) {
        socket.send(text);
    }
}


////////////////////////////////////////////键盘事件////////////////////////////////

// 按Enter键发送信息
$(document).keydown(function (e) {
    var text = document.getElementById("text");
    if (e.keyCode == 13 && e.ctrlKey) {
        text.focus();
        text.value = text.value + ("\n");
    } else if (e.keyCode == 13) {
        // 避免回车键换行
        e.preventDefault();
        SendMsg();
        // 下面写你的发送消息的代码
    }
});


/////////////////////////////////////////////前台信息处理/////////////////////////////////////////////////////////
// 发送信息
function SendMsg() {
    var text = document.getElementById("text");
    if (text.value == "" || text.value == null) {
        // alert("发送信息为空，请输入！");
    }
    else {
        if (socket != '' && socket.readyState == 1) {
            AddMsg('default', SendMsgDispose(text.value));
            send(text.value);
        } else {
            AddMsg('未匹配', SendMsgDispose("未匹配，请先进行匹配！"));
        }
        // var retMsg = AjaxSendMsg(text.value);
        // AddMsg('小龙', retMsg);
        text.value = "";
    }
}

// 发送的信息处理
function SendMsgDispose(detail) {
    while (detail.indexOf("\n") > 0) {
        detail = detail.replace("\n", "<br>").replace(" ", "&nbsp;");
    }
    return detail;
}

// 增加信息
function AddMsg(user, content) {
    var time = (new Date()).valueOf();
    var str = CreadMsg(user, content, time);
    var msgs = document.getElementById("msgs");
    msgs.innerHTML = msgs.innerHTML + str;
    //滚动条置底
    $("#show").scrollTop(
        $("#" + time + "").offset().top - $("#show").offset().top + $("#show").scrollTop()
    );
}

// 生成内容
function CreadMsg(user, content, time) {
    var str = "";
    if (user == 'default') {
        str = "<div class=\"msg guest\" id=\"" + time + "\"><div class=\"msg-right\"><div class=\"msg-host headDefault\"></div><div class=\"msg-ball\" title=\"\">" + content + "</div></div></div>"
    } else if (user == "未匹配") {
        str = "<div class=\"msg min time\" id=\"" + time + "\">" + content + "</div>";
    } else if (content == "聊天已结束") {
        str = "<div class=\"msg min time\" id=\"" + time + "\">" + content + "</div>";
    } else {
        str = "<div class=\"msg robot\" id=\"" + time + "\"><div class=\"msg-left\" worker=\"" + user + "\"><div class=\"msg-host photo\" style=\"background-image: url(../image/man.jpg)\"></div><div class=\"msg-ball\" title=\"\">" + content + "</div></div></div>";
    }
    return str;
}

