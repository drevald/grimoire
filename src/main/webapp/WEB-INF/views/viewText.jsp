<%@ include file = "/WEB-INF/dictHeader.jsp"%>

    <script>

        var i = 0;
        var words = [];

        document.onkeydown = show;

        function show(event) {
            var holder;
            if(window.event) {
                holder = window.event.keyCode;
            } else {
                holder = event.which;
            }
            processKey (holder);
        }

        function processKey(holder) {
            // restoring state of current selection
            element = document.getElementById(i);
            element.style.backgroundColor="white";

            if (holder == 37) {
                if (i > 0) {
                    i--;
                    highlight(i);
                } else {
                    prevPage();
                }
            }
            if (holder == 39) {
                if (i < words.length-1) {
                    i++;
                    highlight(i);
                } else {
                    nextPage();
                }
            }
            if (holder == 38) {
                if (i >20) {
                    i -= 20;
                    highlight(i);
                } else {
                    prevPage();
                }
            }
            if (holder == 40) {
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
            alert("nextPage");
            self.location = "${dict.id}?offset=${offset+size}";
        }

        function prevPage() {
            alert("prevPage");
            if(offset>size) {
                self.location = "${dict.id}?offset=${offset-size}";
            } else {
                alert("${offset} < ${size}");
            }
        }

    </script>

<body>


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

<script>highlight(1);</script>

<%@ include file = "/WEB-INF/footer.jsp"%>