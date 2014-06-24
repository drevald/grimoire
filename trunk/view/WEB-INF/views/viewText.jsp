<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <link rel="icon" type="image/png" href="/grimoire/favicon.png" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf8">
    <title>Grimoire Pro</title>
    <script>
        var i = 0;
        var words = [];
        function show(x) {
            element = document.getElementById(i);
            element.style.backgroundColor="white";
            if (event.keyCode == 37 && i > 0) {
                i--;
            }
            if (event.keyCode == 39 && i < words.length-1) {
                i++;
            }
            if (event.keyCode == 38 && i >20) {
                i -= 20;
            }
            if (event.keyCode == 40 && i + 19 < words.length) {
                i += 20;
            }
            element = document.getElementById(i);
            element.style.backgroundColor="yellow";
            element1 = document.getElementById("result");
            element1.innerText = words[i];
        }
    </script>
</head>

<body onload="javascript:show()" onkeydown="javascript:show()">

<div style="float:left;margin-right:20px">
    <a href="/grimoire/dict">
        Home
    </a>
</div>

<div style="float:left;margin-right:20px">
    <a href="/grimoire/dict/view/">
        Text
    </a>
</div>

<div style="float:left">
    <a href="/grimoire/dict/words/?offset=0">
        Dictionary
    </a>
</div>

<div style="float:right">
    <a href="/grimoire/logout">
        Logout
    </a>
</div>

<br/>

<hr/>


<table width="100%">
    <tr>
        <td  width="50%">
            ${text}
        </td>
        <td  width="50%" valign="top" align="center">
            <h3><div id="result">&nbsp;</div></h3>
        </td>
    </tr>
</table>


<%@ include file = "/WEB-INF/footer.jsp"%>