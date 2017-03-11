<%@ page import="java.util.ArrayList" %>
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

    ArrayList<String> tmpFiles = (ArrayList<String>) session.getAttribute("tmpFile");
%>
<html>
<head>
    <title><%=uname%>的文件</title>
    <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="js/spark-md5.min.js"></script>
    <script type="text/javascript">
        var curDirLoc;
        var fileMD5;

        $(document).ready(function () {
            curDirLoc = getQueryString("loc");
            if (curDirLoc == null || curDirLoc == "") {
                curDirLoc = encodeURIComponent("/");
            } else {
                if (curDirLoc.lastIndexOf("/") != curDirLoc.length - 1) {
                    curDirLoc += encodeURIComponent("/");
                }
            }
            $(".curLoc").text("当前位置:" + decodeURIComponent(curDirLoc));
            addFileChangeLis();
            $.getJSON("showfile.json?loc=" + curDirLoc, function (result) {
                result = jsonSort(result, "file");
                for (var i = 0; i < result.length; i++) {
                    var uFile = result[i];
                    var fType = "文件夹";
                    if (uFile.file) {
                        fType = "文件";
                    }
                    var fName = uFile.name;
                    var btnText = "<button onclick='delFile(\"" + fName + "\")'>删除</button><button onclick='rename(\"" + fName + "\")'>重命名</button>";
                    if (!uFile.select) {
                        btnText += "<button onclick='selectFile(\"" + fName + "\")'>选择</button>";
                    } else {
                        btnText += "<button onclick='selectFile(\"" + fName + "\")'>取消选择</button>";
                    }
                    if (uFile.file) {
                        $(".fileTable").append("<tr><td><a href='" + uFile.loc + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td><td>" + btnText + "</td></tr>");
                    } else {
                        var url = encodeURIComponent(curDirLoc + uFile.name);
                        $(".fileTable").append("<tr><td><a href='showfile.jsp?loc=" + url + "'>" + fName + "</a></td><td>" + fType + "</td><td>" + uFile.time + "</td><td>" + btnText + "</td></tr>");
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

        function copyFile() {
            $.post("fileAction.do", {
                code: "copy",
                desFile: encodeURIComponent(decodeURIComponent(curDirLoc)),
            }, function (data, status) {
                if (data == "200") {
//                        alert("删除成功");
                } else {
                    alert("出现错误");
                }
            });
            location.reload();
        }

        function clearSelectFiles() {
            $.post("fileAction.do", {
                code: "clearSelect",
            }, function (data, status) {
                if (data == "200") {
//                        alert("删除成功");
                } else {
                    alert("出现错误");
                }
            });
            location.reload();
        }

        function moveFile() {
            $.post("fileAction.do", {
                code: "move",
                desFile: encodeURIComponent(decodeURIComponent(curDirLoc)),
            }, function (data, status) {
                if (data == "200") {
//                        alert("删除成功");
                } else {
                    alert("出现错误");
                }
            });
            location.reload();
        }

        function submitForm() {
            var fileName = $("#fileName").val();
            $("#fileName").remove();
            $("#formData").attr("action", "upload.do?fileloc=" + btoa(decodeURIComponent(curDirLoc)) + "&fileName=" + btoa(fileName) + "&md5=" + fileMD5);
            $("#formData").submit();
        }

        function addFileChangeLis() {
            document.getElementById("upFile").addEventListener("change", function () {
                var path = document.getElementById("upFile").value;
                $("#fileName").attr("value", path.substring(path.lastIndexOf("\\") + 1, path.length));

                var fileReader = new FileReader();
                var blobSlice = File.prototype.mozSlice || File.prototype.webkitSlice || File.prototype.slice;
                var file = document.getElementById("upFile").files[0];
                if (file != null) {
                    var chunkSize = 2097152;
                    var chunks = Math.ceil(file.size / chunkSize);
                    var currentChunk = 0;
                    var spark = new SparkMD5();
                    fileReader.onload = function (e) {
//                    console.log("读取文件", currentChunk + 1, "/", chunks);
                        $("#info").text("读取文件中:" + ((currentChunk + 1) / chunks * 100).toFixed(3) + "%");
                        //每块交由sparkMD5进行计算
                        spark.appendBinary(e.target.result);
                        currentChunk++;

                        //如果文件处理完成计算MD5，如果还有分片继续处理
                        if (currentChunk < chunks) {
                            loadNext();
                        } else {
                            fileMD5 = spark.end().toUpperCase();
                            $("#info").text("文件MD5:" + fileMD5);
                            $("#btnSubmit").removeAttr("disabled");
                        }
                    };
                }

                function loadNext() {
                    var start = currentChunk * chunkSize, end = start + chunkSize >= file.size ? file.size : start + chunkSize;

                    fileReader.readAsBinaryString(blobSlice.call(file, start, end));
                }

                loadNext();
            });
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

        function getQueryString(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
            var r = window.location.search.substr(1).match(reg);
            if (r != null) return unescape(r[2]);
            return null;
        }
    </script>
</head>
<body>
<p class="curLoc"></p>
<%
    if (tmpFiles != null && tmpFiles.size() != 0) {
%>
<button onclick="copyFile()">复制到此</button>
<button onclick="moveFile()">移动到此</button>
<button onclick="clearSelectFiles()">清空选择的文件</button>
<a>已选择了<%=tmpFiles.size()%>个文件</a>
<%
    }
%>
<form id="formData" method="post" enctype="multipart/form-data">
    <input type="file" name="file" id="upFile">
    <input type="text" name="fileName" id="fileName" disabled="disabled">
    <button id="btnSubmit" onclick="submitForm()" disabled="disabled">上载</button>
</form>
<p id="info"></p>
<button onclick="backToDir()">返回上一级</button>
<button onclick="newDir()">新建文件夹</button>
<table class="fileTable">
    <tr>
        <td>文件名</td>
        <td>类型</td>
        <td>修改时间</td>
        <td>操作</td>
    </tr>
</table>
</body>
</html>
