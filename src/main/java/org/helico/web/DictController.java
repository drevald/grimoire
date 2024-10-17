package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.*;
import org.helico.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DictController extends AbstractController {

    private static final Logger LOG = Logger.getLogger(DictController.class);

    private ApplicationContext appContext;

    @Autowired
    private DictService dictService;

    @Autowired
    private LangService langService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private JobService jobService;

    @Autowired
    private DictWordService dictWordService;

    @Autowired
    private TranslationService translationService;

    @RequestMapping("/dict")
    public String listDicts(Map<String, Object> map) {
        Account currAccount = accountService.findAccount(getCurrentAccount());
        map.put("currAccount", currAccount);
        List<DictHelper> helperList = new ArrayList<DictHelper>();
        List<Dict> dicts = dictService.listDicts(currAccount.getId());
        for (Dict dict : dicts) {
            DictHelper dictHelper = new DictHelper();
            dictHelper.setDict(dict);
            //List<Job> jobs = jobService.getActiveJobs(dict.getId());
            Job job = jobService.getLastOrActive(dict.getId());
            List<Job> jobs = new ArrayList<Job>();
            jobs.add(job);
            dictHelper.setJobs(jobs);
            helperList.add(dictHelper);
        }
        map.put("helperList", helperList);
        map.put("langs", langService.list());
        return "listDict";
    }

    @RequestMapping(value = "/dict/upload", method = RequestMethod.POST)
    public String handleFormUpload(@RequestParam("file") MultipartFile multipartFile,
                                   @RequestParam("langId") String langId,
                                   Map<String, Object> map,
                                   @ModelAttribute("dict") Dict dict,
                                   Errors errors) {
        Account account = accountService.findAccount(getCurrentAccount());
        if (!multipartFile.isEmpty()) {
            try {
                long dictId = dictService.saveOriginal(
                        account.getId(),
                        langId,
                        multipartFile.getInputStream(),
                        multipartFile.getOriginalFilename(),
                        System.getenv("LOCAL_STORAGE"));
                if(multipartFile.getOriginalFilename() != null
                        && multipartFile.getOriginalFilename().contains(".pdf")) {
                    return "redirect:/dict";
                }
                return "redirect:/dict/preview/" + dictId + "?langId=" + langId;
            } catch (IOException e) {
                LOG.error(e, e);
                errors.reject("error.reading.file");
                return "redirect:/dict";
            }
        } else {
            errors.reject("error.file.notfound");
            return "redirect:/dict";
        }
    }

    @RequestMapping(value = "/dict/edit/save", method = RequestMethod.POST)
    public String addDict(
            @RequestParam("id") Long id,
            @RequestParam("langId") String langId,
            @RequestParam("encoding") String encoding) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(id, account.getId());
        dict.setEncoding(encoding);
        dictService.saveDict(dict);
        return "redirect:/dict/edit/" + dict.getId() + "?langId=" + langId;
    }

    /**
     * Stores uploaded text after preview
     * @param id
     * @param encoding
     * @param langId
     * @return
     */
    @RequestMapping(value = "/dict/store", method = RequestMethod.POST)
    public String storeDict(
            @RequestParam("id") Long id,
            @RequestParam("encoding") String encoding,
            @RequestParam("langId") String langId) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(id, account.getId());
        dict.setEncoding(encoding);
        dict.setLangId(langId);
        dictService.saveDict(dict);
        LOG.info("Dict#" + id + " encoding=" + encoding + " dict.getEncoding=" + dict.getEncoding());
        dictService.storeDict(dict);
        return "redirect:/dict";
    }

    @RequestMapping("/")
    public String home() {
        return "redirect:/dict";
    }

    @RequestMapping(value = "/dict/edit/{dictId}")
    public String editDict(@PathVariable("dictId") Long dictId,
                           @RequestParam("langId") String langId,
                           Map<String, Object> map) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        map.put("dict", dict);
        map.put("langId", langId);
        map.put("encodings", langService.getEncodings(langId));
        try {
            String enc = dict.getEncoding() == null ? langService.getEncodings(langId)[0] : dict.getEncoding();
            map.put("preview", new String(dict.getPreview(), enc));
            map.put("encoding", enc);
        } catch (Exception e) {
            map.put("preview", new String("preview stub"));
        }
        return "saveDict";
    }

    @RequestMapping(value = "/dict/preview/{dictId}")
    public String previewDict(@PathVariable("dictId") Long dictId,
                           Map<String, Object> map) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        String[] encodings = langService.getEncodings(dict.getLangId());
        Map<String, String> previewMap = new HashMap<>();
        for (String encoding : encodings) {
            String previewString  = dictService.getPreview(dictId, encoding);
            previewMap.put(encoding, previewString);
        }
        map.put("dict", dict);
        map.put("langId", dict.getLangId());
        map.put("encodings", encodings);
        map.put("previews", previewMap);
        map.put("encoding", previewMap.keySet().iterator().next());
        map.put("preview", previewMap.values().iterator().next());
        return "previewDict";
    }

    @RequestMapping("/dict/view/{dictId}")
    public String viewDict(@PathVariable("dictId") Long dictId, Map<String, Object> map) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        map.put("dict", dict);
        try {
            map.put("preview", new String(dict.getPreview(), dict.getEncoding()));
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return "viewDict";
    }

    @RequestMapping("/dict/delete/{dictId}")
    public String deleteAccount(@PathVariable("dictId") Long dictId) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        if (dict != null) {
            dictService.removeDict(dictId);
        }
        return "redirect:/dict";
    }

    @RequestMapping("/dict/view/generate")
    public String parseDict(@RequestParam("dictId") Long id) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(id, account.getId());
        if (dict != null) {
            dictService.parseText(id);
        }
        return "redirect:/dict/view/" + id;
    }

    @RequestMapping("/dict/words/{dictId}")
    public String viewWords(
            @PathVariable("dictId") Long dictId,
            @RequestParam("offset") Integer offset,
            Map<String, Object> map) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        if (dict != null) {
            Integer size = 12;
            offset = (offset == null) ? size : offset;
            List<DictWord> words = dictWordService.getWords(dictId, offset, size);
            Long wordsNum = dictWordService.countWords(dictId);
            List<Translator> translators = translationService.listTranslators(dict.getLangId());
            List<TranslatorProvider> providers = translationService.listProviders();
            map.put("dict", dictService.findDict(dictId, account.getId()));
            map.put("wordsNum", wordsNum);
            map.put("words", words);
            map.put("translators", translators);
            map.put("offset", offset);
            map.put("maxOffset", wordsNum - (wordsNum % size));
            map.put("size", size);
            map.put("currPage", 1 + (int) (offset / size));
            map.put("totalPage", 1 + (int) (wordsNum / size));
            return "viewWords";
        } else {
            return "/dict";
        }
    }

    @RequestMapping(value = "/dict/words/translate", method = RequestMethod.POST)
    public String translateDict(@RequestParam("dictId") Long dictId,
                                @RequestParam("translatorId") Long translatorId) {
        Account account = accountService.findAccount(getCurrentAccount());
        Dict dict = dictService.findDict(dictId, account.getId());
        if (dict != null) {
            translationService.translateText(dictId, translatorId);
        }
        return "redirect:/dict";
    }

    @RequestMapping(value = "/upload")
    public String uploadDict(Map<String, Object> map) {
        Account currAccount = accountService.findAccount(getCurrentAccount());
        map.put("currAccount", currAccount);
        map.put("langs", currAccount.getAccountLangs());
        return "uploadDict";
    }

    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

}