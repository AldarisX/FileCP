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
%>
<html>
<head>
    <title><%=uname%>的文件</title>
    <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $.getJSON("showfile.json?loc=<%=loc%>", function (result) {
                result = jsonSort(result, "file");
                for (var i = 0; i < result.length; i++) {
                    var uFile = result[i];
                    var fType = "文件夹";
                    if (uFile.file) {
                        fType = "文件";
                    }
                    var fName = uFile.name;
                    if (uFile.file) {
                        $(".fileTable").append("<tr><td><a href='" + uFile.loc + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td></tr>");
                    } else {
                        var url = encodeURIComponent("<%=loc%>" + uFile.name);
                        $(".fileTable").append("<tr><td><a href='showfile.jsp?loc=" + url + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td></tr>");
                    }
                }
            });
        });

        function backToDir() {
            var url = decodeURIComponent("<%=loc%>");
            if (url != "/") {
                url = url.substring(0, url.lastIndexOf("/"));
                url = encodeURIComponent(url.substring(0, url.lastIndexOf("/")));
//                alert(decodeURIComponent(url));
                window.location.href = "showfile.jsp?loc=" + url;
            }
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
        </td>
    </tr>
</table>
</body>
</html>
