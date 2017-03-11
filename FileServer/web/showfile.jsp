<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String loc = request.getParameter("loc") == null ? "/" : request.getParameter("loc") + "/";

    String uname = (session.getAttribute("uname") != null) ? (String) session.getAttribute("uname") : "";
    if (uname.equals("")) {
        out.println("请先登陆");
        return;
    }

    String tmpFile = (session.getAttribute("tmpFile") != null) ? (String) session.getAttribute("tmpFile") : "";
%>
<html>
<head>
    <title><%=uname%>的文件</title>
    <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
        var curDirLoc =<%=loc%>;

        $(document).ready(function () {
            $.getJSON("showfile.json?loc=" + curDirLoc, function (result) {
                result = jsonSort(result, "file");
                for (var i = 0; i < result.length; i++) {
                    var uFile = result[i];
                    var fType = "文件夹";
                    if (uFile.file) {
                        fType = "文件";
                    }
                    var fName = uFile.name;
                    var btnText = "<td><button onclick='delFile(\"" + fName + "\")'>删除</button><button onclick='rename(\"" + fName + "\")'>重命名</button><button onclick='selectFile(\"" + fName + "\")'>选择</button></td>";
                    if (uFile.file) {
                        $(".fileTable").append("<tr><td><a href='" + uFile.loc + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td>" + btnText + "</tr>");
                    } else {
                        var url = encodeURIComponent(curDirLoc + uFile.name);
                        $(".fileTable").append("<tr><td><a href='showfile.jsp?loc=" + url + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td>" + btnText + "</tr>");
                    }
                }
            });
        });

        function backToDir() {
            var url = decodeURIComponent(curDirLoc);
            if (url != "/") {
                url = url.substring(0, url.lastIndexOf("/"));
                url = encodeURIComponent(url.substring(0, url.lastIndexOf("/")));
//                alert(decodeURIComponent(url));
                window.location.href = "showfile.jsp?loc=" + url;
            }
        }

        function newDir() {
            var dirName = prompt("输入文件夹的名字");
            if (dirName != null) {
                $.post("fileAction.do", {
                    code: "mkdir",
                    dirLoc: encodeURIComponent(decodeURIComponent(curDirLoc) + "/" + dirName)
                }, function (data, status) {
                    if (data == "200") {
//                        alert("创建成功");
                    } else {
                        alert("出现错误");
                    }
                });
            }
            location.reload();
        }

        function delFile(fileName) {
            if (confirm("确定删除???")) {
                $.post("fileAction.do", {
                    code: "del",
                    fileLoc: encodeURIComponent(decodeURIComponent(curDirLoc) + "/" + fileName)
                }, function (data, status) {
                    if (data == "200") {
//                        alert("删除成功");
                    } else {
                        alert("出现错误");
                    }
                });
                location.reload();
            }
        }

        function rename(fileName) {
            var tarName = prompt("输入新的名字");
            if (tarName != null) {
                $.post("fileAction.do", {
                    code: "rename",
                    fileLoc: encodeURIComponent(decodeURIComponent(curDirLoc) + "/" + tarName),
                    desFileName: encodeURIComponent(decodeURIComponent(curDirLoc) + "/" + fileName)
                }, function (data, status) {
                    if (data == "200") {
//                        alert("删除成功");
                    } else {
                        alert("出现错误");
                    }
                });
                location.reload();
            }
        }

        function selectFile(fileName) {
            $.post("fileAction.do", {
                code: "select",
                fileLoc: encodeURIComponent(decodeURIComponent(curDirLoc) + "/" + fileName),
            }, function (data, status) {
                if (data == "200") {
//                        alert("删除成功");
                } else {
                    alert("出现错误");
                }
            });
            location.reload();
        }

        /*
         * @description		根据某个字段实现对json数组的排序
         * @param	 array	要排序的json数组对象
         * @param	 field	排序字段（此参数必须为字符串）
         * @param	 reverse  是否倒序（默认为false）
         * @return	array	返回排序后的json数组
         */
        function jsonSort(array, field, reverse) {
            //数组长度小于2 或 没有指定排序字段 或 不是json格式数据
            if (array.length < 2 || !field || typeof array[0] !== "object") return array;
            //数字类型排序
            if (typeof array[0][field] === "number") {
                array.sort(function (x, y) {
                    return x[field] - y[field]
                });
            }
            //字符串类型排序
            if (typeof array[0][field] === "string") {
                array.sort(function (x, y) {
                    return x[field].localeCompare(y[field])
                });
            }
            if (typeof array[0][field] === "boolean") {
                array.sort(function (x, y) {
                    return x[field] - y[field]
                });
            }
            //倒序
            if (reverse) {
                array.reverse();
            }
            return array;
        }
    </script>
</head>
<body>
<%
    if (tmpFile != null) {
%>
<button></button>
<%
    }
%>
<table class="fileTable">
    <tr>
        <td>文件名</td>
        <td>类型</td>
        <td>修改时间</td>
        <td>操作</td>
    </tr>
    <tr>
        <td>
            <button onclick="backToDir()">返回上一级</button>
            <button onclick="newDir()">新建文件夹</button>
        </td>
    </tr>
</table>
</body>
</html>
