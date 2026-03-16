<%@ page pageEncoding="UTF-8" %>
<%@ include file = "/WEB-INF/dictHeader.jsp"%>

<div class="col-sm-8 p-5">

    <script>
        var currSelectionId = 0;
        var words = [];
        var wordValues = [];

        document.onkeydown = show;
        function show(event) {
            var holder = window.event ? window.event.keyCode : event.which;
            processKey(holder);
        }

        function processKey(holder) {
            if (holder == 37) prevWord();
            if (holder == 39) nextWord();
            if (holder == 38) prevWordFast();
            if (holder == 40) nextWordFast();
        }

        var csrfParam = '${_csrf.parameterName}';
        var csrfToken = '${_csrf.token}';
        var translationCache = {}; // cache[wordIndex][translatorId] = translation string

        function highlight(i) {
            document.getElementById(currSelectionId).style.backgroundColor = 'white';
            currSelectionId = i;
            document.getElementById(currSelectionId).style.backgroundColor = 'yellow';
            document.getElementById('result').innerHTML = '<span style="color:#999">' + (words[i] || 'Not translated yet') + '</span>';
            if (wordValues[i]) {
                fetch('/text/view/${dict.id}/lookup?word=' + encodeURIComponent(wordValues[i]))
                    .then(r => r.json())
                    .then(data => renderWord(data, wordValues[i]))
                    .catch(() => {});
            }
        }

        function showCached(wordIndex, translatorId) {
            var t = (translationCache[wordIndex] || {})[translatorId];
            if (t !== undefined) {
                document.getElementById('result').innerHTML = t
                    ? '<b>' + wordValues[wordIndex] + '</b><br>' + t
                    : '<span style="color:#999">No translation found</span>';
                return true;
            }
            return false;
        }

        function translateSelected() {
            var wordIndex = currSelectionId;
            var word = wordValues[wordIndex];
            if (!word) return;
            var selected = document.querySelector('input[name="translatorId"]:checked');
            if (!selected) return;
            var translatorId = selected.value;

            if (showCached(wordIndex, translatorId)) return;

            document.getElementById('result').innerHTML = '<span style="color:#999">Translating…</span>';
            fetch('/text/view/${dict.id}/translate-ajax', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: csrfParam + '=' + encodeURIComponent(csrfToken)
                    + '&wordValue=' + encodeURIComponent(word)
                    + '&translatorId=' + encodeURIComponent(translatorId)
            })
            .then(r => r.json())
            .then(data => {
                if (!translationCache[wordIndex]) translationCache[wordIndex] = {};
                translationCache[wordIndex][translatorId] = data.translation || null;
                if (wordIndex === currSelectionId) showCached(wordIndex, translatorId);
            })
            .catch(() => {
                document.getElementById('result').innerHTML = '<span style="color:#999">Error</span>';
            });
        }

        function renderWord(data, word) {
            var html = '<h5>' + word + '</h5>';
            if (data.translations && data.translations.length > 0) {
                html += '<b>Translations</b><br>';
                data.translations.forEach(function(t) {
                    html += '<span>' + t.word + '</span>';
                    if (t.pos) html += ' <small style="color:#888">(' + t.pos + ')</small>';
                    html += '<br>';
                });
            }
            if (data.definitions && data.definitions.length > 0) {
                var examples = data.definitions.filter(function(d) { return d.example; });
                if (examples.length > 0) {
                    html += '<br><b>Examples</b><br>';
                    examples.forEach(function(d) {
                        html += '<i style="color:#555">' + d.example + '</i><br>';
                    });
                }
            }
            if (html) document.getElementById('result').innerHTML = html;
        }

        function nextWordFast() {
            if (currSelectionId + 19 < words.length) highlight(currSelectionId + 20);
            else nextPage();
        }
        function prevWordFast() {
            if (currSelectionId > 20) highlight(currSelectionId - 20);
            else prevPage();
        }
        function nextWord() {
            if (currSelectionId < words.length - 1) highlight(currSelectionId + 1);
            else nextPage();
        }
        function prevWord() {
            if (currSelectionId > 0) highlight(currSelectionId - 1);
            else prevPage();
        }
        function nextPage() { self.location = "${dict.id}?offset=${offset+size}"; }
        function prevPage() {
            if (${offset} >= ${size}) self.location = "${dict.id}?offset=${offset-size}";
        }
    </script>

<body>

<div class="container" style="height:100%">
    <div class="row">
        <%-- Left: book text --%>
        <div class="col-6 mb-5">
            ${text}
        </div>

        <%-- Right: translation panel --%>
        <div class="col-6 text-left d-flex flex-column mb-5">
            <div class="flex-grow-1" id="result"
                 style="border:1px solid #ddd;border-radius:6px;padding:16px;overflow-y:auto;min-height:200px;font-size:1.1em">
                <span style="color:#999">Нажмите на слово…</span>
            </div>
            <div class="mt-3">
                <c:forEach var="translator" items="${translators}" varStatus="s">
                    <label style="margin-right:12px;cursor:pointer">
                        <input type="radio" name="translatorId" value="${translator.id}"
                               onchange="translateSelected()" ${s.first ? 'checked' : ''}/>
                        &nbsp;${translator.provider.title}
                    </label>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<script>highlight(0);</script>

</div>

<%@ include file = "/WEB-INF/footer.jsp"%>
