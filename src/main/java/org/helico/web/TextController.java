package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.Account;
import org.helico.domain.Word;
import org.helico.service.*;
import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Reader;
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

    private static final Logger LOG = Logger.getLogger(TextController.class);

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
                        sb.append("<script>words[" + counter + "] = \"" + word.getTranslation() + "\";</script>");
                        counter++;
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
            LOG.error(e, e);
        }
        LOG.trace(String.format("MARKED STRING Dict #%d Offset %d \n ++++++++++ \n %s \n ----------\n"
                ,dict.getId(), offset, sb.toString()));
        map.put("text", sb.toString());
        map.put("dict", dict);
        map.put("offset", offset);
        map.put("size", TEXT_SIZE);
        return "viewText";
    }

}
