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

import java.io.Reader;
import java.util.List;
import java.util.Map;

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
            "<span id=%d onclick='javascript:highlight(%d);' style='cursor:pointer'>%s</span>";

    private ApplicationContext appContext;

    @Autowired
    private DictService dictService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TextService textService;

    @Autowired
    private WordService wordService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private TranslateHandler translateHandler;

    @RequestMapping("/text/view/{textId}")
    public String viewDict(
            @PathVariable("textId") Long dictId, Map<String, Object> map,
            @RequestParam("offset") int offset) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = textService.getTextReader(dictId, offset, TEXT_SIZE);
            WordReader wordReader = new WordReader(reader);
            int counter = 0;
            while (wordReader.ready()) {
                WordReaderResult result = wordReader.readWord();
                if (result!=null && result.isWord()) {
                    String wordString = result.getResult();
                    Word word = wordService.getWord(dict.getLangId(), wordString);
                    if (word != null) {
                        sb.append(String.format(HIGHLIGHTED_WORD, counter, counter, wordString));
                        String translation = word.getTranslation() != null ? word.getTranslation().replace("\"", "") : "";
                        sb.append("<script>words[" + counter + "] = \"" + translation + "\";</script>");
                        sb.append("<script>wordValues[" + counter + "] = \"" + wordString.replace("\"", "") + "\";</script>");
                        counter++;
                    } else {
                        sb.append("<i>" + wordString + "</i> ");
                    }
                } else if (result != null) {
                    sb.append(result.getResult().replaceAll("\n", "<br>"));
                } else {

                }
            }
        } catch (Exception e) {
            if(e.getMessage()!=null) {
                sb.append(String.format("<br><b>%s</b>", e.getMessage()));
            }
            LOG.error("Error occurred", e);
        }
        LOG.trace(String.format("MARKED STRING Dict #%d Offset %d \n ++++++++++ \n %s \n ----------\n"
                ,dict.getId(), offset, sb.toString()));
        List<Translator> translators = translationService.listTranslators(dict.getLangId(), account.getNativeLangId());
        map.put("text", sb.toString());
        map.put("dict", dict);
        map.put("offset", offset);
        map.put("size", TEXT_SIZE);
        map.put("translators", translators);
        return "viewText";
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
