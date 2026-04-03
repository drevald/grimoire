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
        var dictLang = '${dict.langId}';
        var langLocale = {
            'sv':'sv-SE','pl':'pl-PL','de':'de-DE','fr':'fr-FR','es':'es-ES',
            'it':'it-IT','ru':'ru-RU','ar':'ar-SA','zh':'zh-CN','ja':'ja-JP',
            'el':'el-GR','da':'da-DK','fi':'fi-FI','is':'is-IS','la':'la',
            'nl':'nl-NL','pt':'pt-PT','cs':'cs-CZ','hu':'hu-HU','ro':'ro-RO'
        };

        function speakWord(word) {
            if (!word) return;
            fetch('/text/view/${dict.id}/audio?word=' + encodeURIComponent(word))
                .then(function(r) {
                    if (r.ok) {
                        r.blob().then(function(blob) {
                            var url = URL.createObjectURL(blob);
                            var audio = new Audio(url);
                            audio.play();
                            audio.onended = function() { URL.revokeObjectURL(url); };
                        });
                    } else {
                        speakWordFallback(word);
                    }
                })
                .catch(function() { speakWordFallback(word); });
        }

        function speakWordFallback(word) {
            if (!window.speechSynthesis) return;
            window.speechSynthesis.cancel();
            var u = new SpeechSynthesisUtterance(word);
            u.lang = langLocale[dictLang] || dictLang;
            window.speechSynthesis.speak(u);
        }

        function hasTTSVoice() {
            if (!window.speechSynthesis) return false;
            var locale = langLocale[dictLang];
            if (!locale) return false;
            var lang2 = locale.substring(0, 2).toLowerCase();
            return speechSynthesis.getVoices().some(function(v) {
                return v.lang.toLowerCase().startsWith(lang2);
            });
        }

        function attachSpeakButton(word) {
            var escaped = word.replace(/'/g,"\\'");
            var btnHtml = '<button class="btn btn-link btn-sm p-0" onclick="speakWord(\'' + escaped + '\')" title="Pronounce">🔊</button>';
            // Show button if server has audio
            fetch('/text/view/${dict.id}/audio?word=' + encodeURIComponent(word), {method: 'HEAD'})
                .then(function(r) {
                    var el = document.getElementById('pronounce-btn');
                    if (el && r.ok) el.innerHTML = btnHtml;
                })
                .catch(function() {});
            // Also show button if browser TTS has a voice for this language
            if (hasTTSVoice()) {
                var el = document.getElementById('pronounce-btn');
                if (el) el.innerHTML = btnHtml;
            }
        }
        var translationCache = {}; // cache[wordIndex][translatorId] = translation string
        var sentenceHighlightEls = [];
        var resultOwner = 'word'; // 'word' | 'sentence' — prevents async word callbacks overwriting sentence panel

        function clearSentenceHighlight() {
            sentenceHighlightEls.forEach(function(el) {
                var isCurrentWord = el.id !== '' && parseInt(el.id) === currSelectionId;
                el.style.backgroundColor = isCurrentWord ? 'yellow' : '';
            });
            sentenceHighlightEls = [];
        }

        function highlightSentenceSpans(map, sentence) {
            clearSentenceHighlight();
            var normText = map.normText, normToRaw = map.normToRaw, charMap = map.charMap;
            var s = sentence.trim().replace(/\s+/g, ' ');
            var idx = normText.indexOf(s);
            if (idx < 0) idx = normText.toLowerCase().indexOf(s.toLowerCase());
            if (idx < 0) return;
            var normEnd = idx + s.length - 1;
            if (normEnd >= normToRaw.length) normEnd = normToRaw.length - 1;
            var bookText = document.getElementById('bookText');
            var seen = [];
            for (var ni = idx; ni <= normEnd; ni++) {
                var rp = normToRaw[ni];
                var entry = charMap[rp];
                if (!entry) continue;
                var par = entry.node.parentElement;
                if (par && par !== bookText && seen.indexOf(par) < 0) {
                    seen.push(par);
                    par.style.backgroundColor = '#cce5ff';
                    sentenceHighlightEls.push(par);
                }
            }
        }

        function highlight(i) {
            clearSentenceHighlight();
            resultOwner = 'word';
            document.getElementById(currSelectionId).style.backgroundColor = 'white';
            currSelectionId = i;
            document.getElementById(currSelectionId).style.backgroundColor = 'yellow';
            document.getElementById('result').innerHTML = wordHeader(wordValues[i]) + '<span style="color:#999">' + (words[i] || 'Not translated yet') + '</span>';
            if (wordValues[i]) attachSpeakButton(wordValues[i]);
            if (wordValues[i]) {
                fetch('/text/view/${dict.id}/lookup?word=' + encodeURIComponent(wordValues[i]))
                    .then(r => r.json())
                    .then(data => {
                        if (i !== currSelectionId || resultOwner !== 'word') return; // stale or sentence panel active
                        var hasData = (data.translations && data.translations.length > 0) ||
                                      (data.definitions && data.definitions.length > 0);
                        if (hasData) {
                            renderWord(data, wordValues[i]); // Scriptorium has rich data — show it
                        } else {
                            // No Scriptorium data: show cached provider result or fetch from provider
                            var sel = document.querySelector('input[name="translatorId"]:checked');
                            var cached = sel ? (translationCache[i] || {})[sel.value] : undefined;
                            if (cached !== undefined) {
                                showCached(i, sel.value); // already translated this session
                            } else {
                                translateSelected(); // fetch from provider and save to DB
                            }
                        }
                    })
                    .catch(() => { if (i === currSelectionId && resultOwner === 'word') translateSelected(); });
            }
        }

        function wordHeader(word) {
            if (!word) return '';
            return '<b style="color:black">' + word + '</b> <span id="pronounce-btn"></span><br>';
        }

        function showCached(wordIndex, translatorId) {
            if (resultOwner !== 'word') return false;
            var t = (translationCache[wordIndex] || {})[translatorId];
            if (t !== undefined) {
                var wordLabel = wordHeader(wordValues[wordIndex]);
                if (t) {
                    document.getElementById('result').innerHTML = wordLabel + t;
                } else {
                    document.getElementById('result').innerHTML = wordLabel + '<span style="color:#999">' + (words[wordIndex] || 'No translation found') + '</span>';
                }
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

            document.getElementById('result').innerHTML = wordHeader(word) + '<span style="color:#999">Translating…</span>';
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
                if (wordIndex === currSelectionId) showCached(wordIndex, translatorId); // no-op if sentence panel active
            })
            .catch(() => {
                if (wordIndex === currSelectionId && resultOwner === 'word') {
                    document.getElementById('result').innerHTML = wordHeader(word) + '<span style="color:#999">Error</span>';
                }
            });
        }

        // Build a character map from the DOM: rawText (text nodes + BR→\n, skipping scripts),
        // normText (whitespace collapsed), and normToRaw (normPos → rawPos) + charMap (rawPos → {node, offset}).
        function buildDOMTextMap(container) {
            var charMap = [];
            var rawText = '';
            (function walk(el) {
                for (var ci = 0; ci < el.childNodes.length; ci++) {
                    var child = el.childNodes[ci];
                    if (child.nodeName === 'SCRIPT' || child.nodeName === 'STYLE') continue;
                    if (child.nodeName === 'BR') {
                        charMap.push(null);
                        rawText += '\n';
                    } else if (child.nodeType === 3) {
                        var content = child.textContent;
                        for (var j = 0; j < content.length; j++) {
                            charMap.push({node: child, offset: j});
                            rawText += content[j];
                        }
                    } else if (child.nodeType === 1) {
                        walk(child);
                    }
                }
            })(container);
            var normText = '', normToRaw = [], k = 0;
            while (k < rawText.length) {
                if (/\s/.test(rawText[k])) {
                    var runStart = k;
                    while (k < rawText.length && /\s/.test(rawText[k])) k++;
                    if (normText.length > 0 && normText[normText.length - 1] !== ' ') {
                        normToRaw.push(runStart); normText += ' ';
                    }
                } else {
                    normToRaw.push(k); normText += rawText[k]; k++;
                }
            }
            return {normText: normText, normToRaw: normToRaw, charMap: charMap};
        }

        function isSentenceEnd(text, pos) {
            var ch = text[pos];
            if (ch === '!' || ch === '?' || ch === '\u2026') return true;
            if (ch !== '.') return false;
            // Ellipsis: .. or ...
            if (pos > 0 && text[pos-1] === '.') return false;
            if (pos < text.length-1 && text[pos+1] === '.') return false;
            // Period ends a sentence only if followed by whitespace + uppercase letter
            var i = pos + 1;
            while (i < text.length && text[i] === ' ') i++;
            if (i >= text.length) return true;
            return text[i] !== text[i].toLowerCase(); // Unicode-aware uppercase check
        }

        function sentenceAround(text, pos) {
            // Walk backward to find start (after previous sentence-ending punctuation)
            var start = pos;
            while (start > 0) {
                if (isSentenceEnd(text, start - 1)) break;
                start--;
            }
            // Skip leading whitespace
            while (start < pos && /\s/.test(text[start])) start++;
            // Walk forward to find end (inclusive of sentence-ending punctuation)
            var end = pos;
            while (end < text.length) {
                if (isSentenceEnd(text, end)) { end++; break; }
                end++;
            }
            return text.slice(start, end).trim().replace(/\s+/g, ' ');
        }

        // Visually select the sentence in the DOM using a pre-computed map from buildDOMTextMap().
        function selectSentenceInDOM(map, sentence) {
            var normText = map.normText, normToRaw = map.normToRaw, charMap = map.charMap;
            var s = sentence.trim().replace(/\s+/g, ' ');
            var idx = normText.indexOf(s);
            if (idx < 0) idx = normText.toLowerCase().indexOf(s.toLowerCase());
            if (idx < 0) return;
            var normEnd = idx + s.length - 1;
            if (normEnd >= normToRaw.length) normEnd = normToRaw.length - 1;
            var rawStart = normToRaw[idx];
            while (rawStart < charMap.length && charMap[rawStart] === null) rawStart++;
            if (rawStart >= charMap.length) return;
            var rawEnd = normToRaw[normEnd];
            while (rawEnd >= 0 && charMap[rawEnd] === null) rawEnd--;
            if (rawEnd < 0) return;
            try {
                var range = document.createRange();
                range.setStart(charMap[rawStart].node, charMap[rawStart].offset);
                range.setEnd(charMap[rawEnd].node, charMap[rawEnd].offset + 1);
                var sel = window.getSelection();
                sel.removeAllRanges();
                sel.addRange(range);
            } catch(err) {}
        }

        function translatePhrase(phrase) {
            if (!phrase || phrase.length < 2) return;
            var selected = document.querySelector('input[name="translatorId"]:checked');
            if (!selected) return;
            document.getElementById('result').innerHTML =
                '<i style="color:#555">' + phrase + '</i><br><span style="color:#999">Translating…</span>';
            fetch('/text/view/${dict.id}/translate-phrase', {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: csrfParam + '=' + encodeURIComponent(csrfToken)
                    + '&text=' + encodeURIComponent(phrase)
                    + '&translatorId=' + encodeURIComponent(selected.value)
            })
            .then(r => r.json())
            .then(data => {
                document.getElementById('result').innerHTML = data.translation
                    ? '<i style="color:#555">' + phrase + '</i><br><br>' + data.translation
                    : '<i style="color:#555">' + phrase + '</i><br><span style="color:#999">No translation</span>';
            })
            .catch(() => {});
        }

        function showSentencePanel(sentence) {
            resultOwner = 'sentence';
            var escaped = sentence.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
            document.getElementById('result').innerHTML =
                '<i style="color:#333">' + escaped + '</i>' +
                '<br><br><button class="btn btn-sm btn-primary" onclick="translateCurrentSentence()">Translate</button>';
            document.getElementById('result')._pendingSentence = sentence;
        }

        function translateCurrentSentence() {
            var sentence = document.getElementById('result')._pendingSentence;
            if (sentence) {
                resultOwner = 'sentence'; // keep ownership during translation
                translatePhrase(sentence);
            }
        }

        // Double-click: select word and translate
        document.addEventListener('dblclick', function(e) {
            if (!e.target.closest('#bookText')) return;
            var span = e.target.closest('span[id]');
            if (span && /^\d+$/.test(span.id)) {
                highlight(parseInt(span.id));
            }
        });

        // Triple-click: highlight sentence and show Translate button
        document.addEventListener('click', function(e) {
            if (e.detail !== 3) return;
            if (!e.target.closest('#bookText')) return;
            var bookText = document.getElementById('bookText');
            var map = buildDOMTextMap(bookText);
            var approxPos = 0;

            // Find position from the clicked word span's text node
            var span = e.target.closest('span[id]');
            if (span && span.firstChild && span.firstChild.nodeType === 3) {
                var tn = span.firstChild;
                for (var ni = 0; ni < map.normToRaw.length; ni++) {
                    var en = map.charMap[map.normToRaw[ni]];
                    if (en && en.node === tn) {
                        approxPos = ni + Math.floor((span.textContent.length || 1) / 2);
                        break;
                    }
                }
            } else {
                // Fallback: use selection position
                var sel = window.getSelection();
                if (sel && sel.rangeCount) {
                    var cn = sel.getRangeAt(0).startContainer;
                    var co = sel.getRangeAt(0).startOffset;
                    for (var ni2 = 0; ni2 < map.normToRaw.length; ni2++) {
                        var en2 = map.charMap[map.normToRaw[ni2]];
                        if (en2 && en2.node === cn && en2.offset >= co) {
                            approxPos = ni2; break;
                        }
                    }
                }
            }

            var sentence = sentenceAround(map.normText, approxPos);
            if (sentence && sentence.length > 3) {
                highlightSentenceSpans(map, sentence);
                showSentencePanel(sentence);
            }
        });

        function renderWord(data, word) {
            var html = wordHeader(word);
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
            if (html) {
                document.getElementById('result').innerHTML = html;
                attachSpeakButton(word);
            }
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
        <div class="col-6 mb-5" id="bookText">
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
