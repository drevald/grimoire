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

            if (holder == 37) {
                prevWord();
            }
            if (holder == 39) {
                nextWord();
            }
            if (holder == 38) {
                prevWordFast();
            }
            if (holder == 40) {
                nextWordFast();
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



        function nextWordFast() {
            var newSelectionId = 0;
            if (currSelectionId + 19 < words.length) {
                newSelectionId = currSelectionId + 20;
                highlight(newSelectionId);
            } else {
                nextPage();
            }
        }

        function prevWordFast() {
            var newSelectionId = 0;
            if (currSelectionId >20) {
                newSelectionId = currSelectionId - 20;
                highlight(newSelectionId);
            } else {
                prevPage();
            }
        }

        function nextWord() {
            var newSelectionId = 0;
            if (currSelectionId < words.length-1) {
                newSelectionId = currSelectionId + 1;
                highlight(newSelectionId);
            } else {
                nextPage();
            }
        }

        function prevWord() {
            var newSelectionId = 0;
            if (currSelectionId > 0) {
                newSelectionId = currSelectionId - 1;
                highlight(newSelectionId);
                alert("3");
            } else {
                alert("4");
                prevPage();
            }
        }

        function nextPage() {
            //alert("nextPage");
            self.location = "${dict.id}?offset=${offset+size}";
        }

        function prevPage() {
            //alert("prevPage");
            if(${offset} >= ${size}) {
                //alert("${offset} >= ${size}");
                self.location = "${dict.id}?offset=${offset-size}";
            } else {
                //alert("${offset} < ${size}");
            }
        }

    </script>

<body>


<table width="100%">
    <tr>
        <td width="5%" onclick="javascript:prevWord();" ondblclick="javascript:prevWordFast();">&nbsp;</td>
        <td  width="45%">
            ${text}
        </td>
        <td  width="45%" valign="top" align="center">
            <h3><div id="result">&nbsp;</div></h3>
        </td>
        <td width="5%" onclick="javascript:nextWord();" ondblclick="javascript:nextWordFast();">&nbsp;</td>
    </tr>
</table>

<script>highlight(0);</script>

<%@ include file = "/WEB-INF/footer.jsp"%>