<%@ include file = "/WEB-INF/dictHeader.jsp"%>

<div class="col-sm-8 p-5">

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
            // Restore the state of the current selection
            let element = document.getElementById(currSelectionId);
            element.style.backgroundColor = "white"; // Deselect the previous word

            currSelectionId = i; // Update the current selection

            element = document.getElementById(currSelectionId);
            element.style.backgroundColor = "yellow"; // Highlight the new word

            // Display the selected word in the "result" element
            let element1 = document.getElementById("result");
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
            } else {
                prevPage();
            }
        }
        function nextPage() {
            self.location = "${dict.id}?offset=${offset+size}";
        }

        function prevPage() {
            if(${offset} >= ${size}) {
                self.location = "${dict.id}?offset=${offset-size}";
            }
        }

    </script>

<body>

<div class="container" style="height: 100%;">
    <div class="row">
        <div class="col-6">
            ${text}
        </div>
        <div class="col-6 text-center column d-flex flex-column">
                    <!-- Set parent column to use flexbox -->
                    <div class="row flex-grow-1">
                        <!-- This row will fill all available height -->
                        <div id="result" class="col">
                            <!-- Content for the row that will take all the available height -->
                            This row fills all available height of the parent column.
                        </div>
                    </div>
                    <div class="row">
                        <div class="col text-right mb-5">
                            <form action="translate" method="post">
                                Translate using &nbsp;
                                <c:forEach var="translator" items="${translators}">
                                        <input type="radio" name="encoding" value="${translator.id}" checked/>
                                        &nbsp;${translator.provider.title} &nbsp;
                                        <input type="radio" name="encoding" value="${translator.id}" checked/>
                                        &nbsp;${translator.provider.title} &nbsp;
                                </c:forEach>
                                <input type="submit" value="Translate" class="btn btn-primary"/>
                            </form>
                        </div>
                    </div>
                </div>
    </div>
</div>

<script>highlight(0);</script>

</div>

<%@ include file = "/WEB-INF/footer.jsp"%>