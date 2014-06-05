package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.Dict;
import org.helico.domain.User;
import org.helico.domain.Word;
import org.helico.service.*;
import org.helico.util.WordReader;
import org.helico.util.WordReaderResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Reader;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ddreval
 * Date: 05.06.14
 * Time: 15:19
 * To change this template use File | Settings | File Templates.
 */

@Controller
public class TextController  implements ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(TextController.class);

    private ApplicationContext appContext;

    @Autowired
    private DictService dictService;

    @Autowired
    private UserService userService;

    @Autowired
    private TextService textService;

    @Autowired
    private WordService wordService;

    @RequestMapping("/text/view/{textId}")
    public String viewDict(@PathVariable("textId") Long dictId, Map<String, Object> map) {
        User user = userService.findUser(getCurrentUser());
        Dict dict = dictService.findDict(dictId, user.getId());
        StringBuilder sb = new StringBuilder();
        try {
            Reader reader = textService.getTextReader(dictId, 0, 10000);
            WordReader wordReader = new WordReader(reader);
            while (wordReader.ready()) {
                WordReaderResult result = wordReader.readWord();
                if (result.isWord()) {
                    String wordString = result.getResult();
                    Word word = wordService.getWord(dict.getLangId(), wordString);
                    sb.append("<a title=\"" + word.getTranslation() + "\" href=\"#\">");
                    sb.append(wordString);
                    sb.append("</a>");
                } else {
                    sb.append(result.getResult());
                }
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        map.put("text", sb.toString());
        return "viewText";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }
//
//    public void setApplicationContext(ApplicationContext appContext) {
//        LOG.debug("setting application context: " + appContext);
//        this.appContext = appContext;
//    }

    private String getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails)principal).getUsername();
        } else {
            return principal.toString();
        }
    }

}
