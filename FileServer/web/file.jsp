<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");

    String uname = (session.getAttribute("uname") != null) ? (String) session.getAttribute("uname") : "";
    if (uname.equals("")) {
        out.println("请先登陆");
        return;
    }
%>
<html>
<head>
    <title><%=uname%>文件管理</title>
    <script type="text/javascript" src="js/jquery-3.1.1.min.js"></script>
    <script type="text/javascript" src="js/spark-md5.min.js"></script>
    <script type="text/javascript">
        var fileMD5;

        $(document).ready(function () {
            document.getElementById("file").addEventListener("change", function () {
                var path = document.getElementById("file").value;
                $("#fileName").attr("value", path.substring(path.lastIndexOf("\\") + 1, path.length));

                var fileReader = new FileReader();
                var blobSlice = File.prototype.mozSlice || File.prototype.webkitSlice || File.prototype.slice;
                var file = document.getElementById("file").files[0];
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
//                    box.innerText = 'MD5 hash:' + spark.end();
//                        console.info("MD5:", spark.end());
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
        });

        function submitForm() {
            var fileloc = $("#fileloc").val();
            if (fileloc != "") {
                $("#fileloc").remove();
                var fileName = $("#fileName").val();
                $("#fileName").remove();
                $("#formData").attr("action", "upload.do?fileloc=" + btoa(fileloc) + "&fileName=" + fileName + "&md5=" + fileMD5);
                $("#formData").submit();
            } else {
                alert("请填写地址");
            }
            return true;
        }
    </script>
</head>
<body>
<form id="formData" method="post" action="upload.do" enctype="multipart/form-data">
    <input type="text" name="fileloc" id="fileloc" value="/">
    <input type="file" name="file" id="file">
    <input type="text" name="fileName" id="fileName" disabled="disabled">
</form>
<button id="btnSubmit" onclick="submitForm()" disabled="disabled">提交</button>
<br>
<p id="info"></p>
</body>
</html>
