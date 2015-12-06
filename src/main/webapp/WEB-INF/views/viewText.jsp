<%@ include file = "/WEB-INF/dictHeader.jsp"%>

    <script>

        var i = 0;
        var words = [];

        function show(x) {

            // restoring state of current selection
            element = document.getElementById(i);
            element.style.backgroundColor="white";

            if (event.keyCode == 37) {
                if (i > 0) {
                    i--;
                    highlight(i);
                } else {
                    prevPage();
                }
            }
            if (event.keyCode == 39) {
                if (i < words.length-1) {
                    i++;
                    highlight(i);
                } else {
                    nextPage();
                }
            }
            if (event.keyCode == 38) {
                if (i >20) {
                    i -= 20;
                    highlight(i);
                } else {
                    prevPage();
                }
            }
            if (event.keyCode == 40) {
                if (i + 19 < words.length) {
                    i += 20;
                    highlight(i);
                } else {
                    nextPage();
                }
            }
        }

        function highlight(i) {
            element = document.getElementById(i);
            element.style.backgroundColor="yellow";
            element1 = document.getElementById("result");
            element1.innerText = words[i];
        }

        function nextPage() {
            window.navigate("newsru");
            alert("Next Page");
        }

        function prevPage() {
            self.location("text/view/3?offset=10");
            alert("Prev Page");
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