<%@ include file = "/WEB-INF/dictHeader.jsp"%>

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

<body onload="javascript:show()" onkeydown="javascript:show()">


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