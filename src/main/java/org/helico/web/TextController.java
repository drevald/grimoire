package org.helico.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.helico.domain.Dict;
import org.helico.domain.Account;
import org.helico.domain.Translator;
import org.helico.domain.Word;
import org.helico.service.*;
import org.helico.sm.handler.TranslateHandler;
import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

/**
 * Created with IntelliJ IDEA.
 * Account: ddreval
 * Date: 05.06.14
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class TextController  extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(TextController.class);

    private static final int TEXT_SIZE = 1000;

    private static final String HIGHLIGHTED_WORD =
            "<span id=%d style='cursor:pointer'>%s</span>";

    private final DictService dictService;
    private final AccountService accountService;
    private final TextService textService;
    private final WordService wordService;
    private final TranslationService translationService;
    private final TranslateHandler translateHandler;

    TextController(DictService dictService,
                   AccountService accountService,
                   TextService textService,
                   WordService wordService,
                   TranslationService translationService,
                   TranslateHandler translateHandler) {
        this.dictService  = dictService;
        this.accountService = accountService;
        this.textService = textService;
        this.wordService = wordService;
        this.translationService = translationService;
        this.translateHandler = translateHandler;
    }


    @RequestMapping("/text/view/{textId}")
    public String viewDict(
            @PathVariable("textId") Long dictId, Map<String, Object> map,
            @RequestParam("offset") int offset,
            RedirectAttributes ra) {
        Account account = accountService.findAccount(getCurrentAccount());
        Long accountId = account.getId();
        Dict dict = dictService.findDict(dictId, accountId);
        try (Reader reader = textService.getTextReader(dictId, offset, TEXT_SIZE);) {

            WordReader wordReader = new WordReader(reader);

            List<WordReaderResult> allWords = wordReader.words().collect(Collectors.toList());

            Set<String> words = allWords.stream()
                    .filter(w -> w.isWord())
                    .map(w -> w.getResult())
                    .collect(toSet());

            Map<String, Word> knownWords = wordService.getWords(dict.getLangId(), words);

//            while (wordReader.ready()) {
//                WordReaderResult result = wordReader.readWord();
//                if (result!=null && result.isWord()) {
//                    String wordString = result.getResult();
//                    Word word = wordService.getWord(dict.getLangId(), wordString);
//                    if (word != null) {
//                        sb.append(String.format(HIGHLIGHTED_WORD, counter, wordString));
//                        String translation = word.getTranslation() != null ? word.getTranslation().replace("\"", "") : "";
//                        sb.append("<script>words[" + counter + "] = \"" + translation + "\";</script>");
//                        sb.append("<script>wordValues[" + counter + "] = \"" + wordString.replace("\"", "") + "\";</script>");
//                        counter++;
//                    } else {
//                        sb.append("<i>" + wordString + "</i> ");
//                    }
//                } else if (result != null) {
//                    sb.append(result.getResult().replaceAll("\n", "<br>"));
//                } else {
//
//                }
//            }

            List<Translator> translators = translationService.listTranslators(dict.getLangId(), account.getNativeLangId());
            map.put("tokens", allWords);
            map.put("knownWords", knownWords);
            map.put("dict", dict);
            map.put("offset", offset);
            map.put("size", TEXT_SIZE);
            map.put("translators", translators);
            return "viewText";

        } catch (Exception e) {
            LOG.error("Text of dict {} is unreadable", dictId, e);
            ra.addFlashAttribute("error",
                    "Текст словаря «" + dict.getName() + "» недоступен. Удалите словарь или загрузите файл заново.");
            return "redirect:/dict";
        }

    }

    @RequestMapping(value = "/text/view/{dictId}/audio", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getWordAudio(
            @PathVariable("dictId") Long dictId,
            @RequestParam("word") String word) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        for (Translator t : translationService.listTranslators(dict.getLangId())) {
            if ("Scriptorium".equalsIgnoreCase(t.getProvider().getTitle())) {
                byte[] audio = translateHandler.fetchAudio(word, t);
                if (audio != null) {
                    return ResponseEntity.ok()
                            .contentType(org.springframework.http.MediaType.parseMediaType("audio/ogg"))
                            .body(audio);
                }
                break;
            }
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/text/view/{dictId}/lookup", method = RequestMethod.GET,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> lookupWord(
            @PathVariable("dictId") Long dictId,
            @RequestParam("word") String word) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        List<Translator> translators = translationService.listTranslators(dict.getLangId(), account.getNativeLangId());
        for (Translator t : translators) {
            if ("Scriptorium".equalsIgnoreCase(t.getProvider().getTitle())) {
                String raw = translateHandler.fetchRaw(word, t.getId());
                if (raw != null) return ResponseEntity.ok(raw);
            }
        }
        return ResponseEntity.ok("{\"translations\":[],\"definitions\":[]}");
    }

    @RequestMapping(value = "/text/view/{dictId}/translate-phrase", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> translatePhrase(
            @PathVariable("dictId") Long dictId,
            @RequestParam("text") String text,
            @RequestParam("translatorId") Long translatorId) {
        Account account = accountService.findAccount(getCurrentAccount());
        dictService.findDict(dictId, account.getId()); // auth check
        String translation = translateHandler.translatePhrase(text, translatorId);
        String safe = translation != null ? translation.replace("\\", "\\\\").replace("\"", "\\\"") : null;
        return ResponseEntity.ok(safe != null ? "{\"translation\":\"" + safe + "\"}" : "{\"translation\":null}");
    }

    @RequestMapping(value = "/text/view/{dictId}/translate-ajax", method = RequestMethod.POST,
                    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> translateWordAjax(
            @PathVariable("dictId") Long dictId,
            @RequestParam("wordValue") String wordValue,
            @RequestParam("translatorId") Long translatorId) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        Word word = wordService.getWord(dict.getLangId(), wordValue);
        if (word == null) return ResponseEntity.ok("{\"translation\":null}");
        String translation = translateHandler.translateSingleWord(wordValue, translatorId);
        if (translation != null) {
            translationService.storeTranslation(word.getId(), translatorId, translation);
        }
        String safe = translation != null ? translation.replace("\\", "\\\\").replace("\"", "\\\"") : null;
        return ResponseEntity.ok(safe != null ? "{\"translation\":\"" + safe + "\"}" : "{\"translation\":null}");
    }

    @RequestMapping(value = "/text/edit/{dictId}", method = RequestMethod.GET)
    public String editText(@PathVariable("dictId") Long dictId, Map<String, Object> map) throws Exception {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        map.put("dict", dict);
        map.put("content", textService.getFullText(dictId));
        return "editText";
    }

    @RequestMapping(value = "/text/edit/{dictId}/save", method = RequestMethod.POST)
    public String saveText(@PathVariable("dictId") Long dictId,
                           @RequestParam("content") String content) throws Exception {
        Account account = accountService.findAccount(getCurrentAccount());
        dictService.findDict(dictId, account.getId()); // auth check
        textService.saveFullText(dictId, content);
        return "redirect:/text/view/" + dictId + "?offset=0";
    }

    @RequestMapping(value = "/text/view/{dictId}/translate", method = RequestMethod.POST)
    public String translateWord(
            @PathVariable("dictId") Long dictId,
            @RequestParam("wordValue") String wordValue,
            @RequestParam("translatorId") Long translatorId,
            @RequestParam("offset") int offset) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        Word word = wordService.getWord(dict.getLangId(), wordValue);
        if (word != null && !translationService.isTranslated(word.getId(), translatorId)) {
            String result = translateHandler.translateSingleWord(wordValue, translatorId);
            if (result != null) {
                translationService.storeTranslation(word.getId(), translatorId, result);
            }
        }
        return "redirect:/text/view/" + dictId + "?offset=" + offset;
    }

}
