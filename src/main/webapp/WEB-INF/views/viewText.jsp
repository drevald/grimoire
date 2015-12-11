<%@ include file = "/WEB-INF/dictHeader.jsp"%>

    <script>

        var currSelectionId = 0;
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

            var newSelectionId = 0;

            if (holder == 37) {
                if (currSelectionId > 0) {
                    newSelectionId = currSelectionId - 1;
                    highlight(newSelectionId);
                } else {
                    prevPage();
                }
            }
            if (holder == 39) {
                if (currSelectionId < words.length-1) {
                    newSelectionId = currSelectionId + 1;
                    highlight(newSelectionId);
                } else {
                    nextPage();
                }
            }
            if (holder == 38) {
                if (currSelectionId >20) {
                    newSelectionId = currSelectionId - 20;
                    highlight(newSelectionId);
                } else {
                    prevPage();
                }
            }
            if (holder == 40) {
                if (currSelectionId + 19 < words.length) {
                    newSelectionId = currSelectionId + 20;
                    highlight(newSelectionId);
                } else {
                    nextPage();
                }
            }
        }

        function highlight(i) {

            // restoring state of current selection
            element = document.getElementById(currSelectionId);
            element.style.backgroundColor="white";

            currSelectionId = i;

            element = document.getElementById(currSelectionId);
            element.style.backgroundColor="yellow";
            element1 = document.getElementById("result");
            element1.innerText = words[currSelectionId];
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

<script>highlight(0);</script>

<%@ include file = "/WEB-INF/footer.jsp"%>