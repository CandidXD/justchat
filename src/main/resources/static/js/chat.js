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

$(window).on("unload", function () {
    out();
});

function userInfo() {
    $.ajax({
        type: "GET",
        url: localhost + "chat/userInfo",
        data: {},
        success: function (data) {

        },
        error: function () {

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

        },
        error: function () {

        }
    });
}

function cancelMatch() {
    $.ajax({
        type: "POST",
        url: localhost + "chat/cancelmatch",
        data: {},
        success: function (data) {
            var state = data.state;
            if (state == "ERROR") {

            } else {
                layer.msg(data.content, {
                    offset: "150px"
                });
            }
        },
        error: function () {

        }
    });

}

function match(size) {

    if (socket != '') {
        socket.close();
    }
    if (size == 0) {
        $('#cancel-button').css('display', 'inline');
        $('#match-button').css('display', 'none');

        $('#match-button').text("匹配中...");
        $('#match-button').attr('disabled', true);

        $('#match_user_info').text('匹配中...');

        gender = $("#genderId").val();
        age = $("#age").val();
    } else {
        $('#cancel-button-sm').css('display', 'inline');
        $('#match-button-sm').css('display', 'none');

        $('#match-button-sm').text("匹配中...");
        $('#match-button-sm').attr('disabled', true);

        $('#match_user_info').text('匹配中...');

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
                    if (size == 0) {
                        $('#match-button').text("开始匹配");
                        $('#match-button').attr('disabled', false);
                        $('#cancel-button').css('display', 'none');
                        $('#match-button').css('display', 'inline');

                        $('#match_user_info').text('匹配取消');
                    } else {
                        $('#match-button-sm').text("开始匹配");
                        $('#match-button-sm').attr('disabled', false);
                        $('#cancel-button-sm').css('display', 'none');
                        $('#match-button-sm').css('display', 'inline');

                        $('#match_user_info').text('匹配取消');
                    }
                } else {
                    if (size == 0) {
                        $('#match-button').text("重新匹配");
                        $('#match-button').attr('disabled', false);
                        $('#cancel-button').css('display', 'none');
                        $('#match-button').css('display', 'inline');
                    } else {
                        $('#match-button-sm').text("重新匹配");
                        $('#match-button-sm').attr('disabled', false);
                        $('#cancel-button-sm').css('display', 'none');
                        $('#match-button-sm').css('display', 'inline');
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

                }
            },
            error: function () {

            }
        })
    }, 1000);
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
            // AddMsg('未匹配', SendMsgDispose("未匹配，请先进行匹配！"));
            layer.msg("未匹配，请先进行匹配！", {
                offset: "150px"
            });
        }

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
    } else if (content == "聊天已结束") {
        str = "<div class=\"msg min time\" id=\"" + time + "\">" + content + "</div>";
    } else if (content == "匹配中") {
        str = "<div class=\"msg min time\" id=\"" + time + "\">" + content + "</div>";
    } else {
        str = "<div class=\"msg robot\" id=\"" + time + "\"><div class=\"msg-left\" worker=\"" + user + "\"><div class=\"msg-host photo\" style=\"background-image: url(../image/man.jpg)\"></div><div class=\"msg-ball\" title=\"\">" + content + "</div></div></div>";
    }
    return str;
}

