package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.*;
import org.helico.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class DictController implements ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(DictController.class);

    private ApplicationContext appContext;

    @Autowired
    private DictService dictService;

    @Autowired
	private LangService langService;

    @Autowired
    private UserService userService;

    @Autowired
	private JobService jobService;

    @Autowired
	private DictWordService dictWordService;

    @Autowired
	private TranslationService translationService;
	
    private String getCurrentUser() {
	Object principal = SecurityContextHolder.getContext()
	    .getAuthentication().getPrincipal();
	if (principal instanceof UserDetails) {
	    return ((UserDetails)principal).getUsername();
	} else {
	    return principal.toString();
	}
    }

    @RequestMapping("/dict")
	public String listDicts(Map<String, Object> map) {
	User currUser = userService.findUser(getCurrentUser());
	map.put("currUser", currUser);
    	List<DictHelper> helperList = new ArrayList<DictHelper>();
	List<Dict> dicts = dictService.listDicts(currUser.getId());
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
    	return "dictList";
    }
	
    @RequestMapping(value="/dict/upload", method = RequestMethod.POST)
	public String handleFormUpload(@RequestParam("file") MultipartFile file,
                        @RequestParam("langId") String langId,
			Map<String, Object> map,
			@ModelAttribute("dict") Dict dict,
			Errors errors)
	{
	    User user = userService.findUser(getCurrentUser());
	    if (!file.isEmpty()) {
			try {
                WebApplicationContext webApplicationContext = (WebApplicationContext)appContext;
                String storagePath = webApplicationContext.getServletContext().getInitParameter("localStorage");
			    dict = dictService.loadPreviewFile(user.getId(),
                                    langId,
								    file.getInputStream(), 
								    file.getOriginalFilename(),
                                    storagePath);
			    dict.setEncoding("UTF-8");
			    dict.setLangId(langId);
			    map.put("dict", dict);
			    map.put("preview", new String(dict.getPreview()));
			    return "redirect:/dict/edit/" + dict.getId() + "?langId=" + langId;
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
			@RequestParam("encoding") String encoding)
	{
        User user = userService.findUser(getCurrentUser());
		Dict dict = dictService.findDict(id, user.getId());
		dict.setEncoding(encoding);
		dictService.saveDict(dict);
	    return "redirect:/dict/edit/"+dict.getId() + "?langId=" + langId;
	}

	@RequestMapping(value = "/dict/edit/store", method = RequestMethod.POST)
	public String storeDict(
			@RequestParam("id") Long id,
			@RequestParam("encoding") String encoding,
			@RequestParam("langId") String langId)
	{
        User user = userService.findUser(getCurrentUser());
		Dict dict = dictService.findDict(id, user.getId());
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

	@RequestMapping(value="/dict/edit/{dictId}")
	public String editDict(@PathVariable("dictId") Long dictId,
                           @RequestParam("langId") String langId,
			       Map<String, Object> map) {
        User user = userService.findUser(getCurrentUser());
		Dict dict = dictService.findDict(dictId, user.getId());
		map.put("dict", dict);
        map.put("langId", langId);
        map.put("encodings", langService.getEncodings(langId));
		try {
            String enc = dict.getEncoding() == null ? langService.getEncodings(langId)[0] : dict.getEncoding();
		    map.put("preview", new String(dict.getPreview(), enc));
            map.put("encoding", enc);
		} catch (Exception e) {
		    map.put("preview", new String(dict.getPreview()));
		}
		return "saveDict";
	}	
	
	@RequestMapping("/dict/view/{dictId}")
	public String viewDict(@PathVariable("dictId") Long dictId, Map<String, Object> map) {
        User user = userService.findUser(getCurrentUser());
		Dict dict = dictService.findDict(dictId, user.getId());
		map.put("dict", dict);
        try {
	    map.put("preview", new String(dict.getPreview(), dict.getEncoding()));
        } catch (Exception e) {
            LOG.error(e, e);
        }
	return "viewDict";
	}
	
	@RequestMapping("/dict/delete/{dictId}")
	public String deleteUser(@PathVariable("dictId") Long dictId) {
        User user = userService.findUser(getCurrentUser());
        Dict dict = dictService.findDict(dictId, user.getId());
        if (dict != null) {
		    dictService.removeDict(dictId);
        }
		return "redirect:/dict";
	}

	@RequestMapping("/dict/view/generate")
	public String parseDict(@RequestParam("dictId") Long id) {
        User user = userService.findUser(getCurrentUser());
        Dict dict = dictService.findDict(id, user.getId());
        if (dict != null) {
		    dictService.parseText(id);
        }
		return "redirect:/dict/view/" +  id;
	}

	@RequestMapping("/dict/words/{dictId}")
	public String viewWords(
				@PathVariable("dictId") Long dictId,
				@RequestParam("offset") Integer offset, 
				Map<String, Object> map) {
        User user = userService.findUser(getCurrentUser());
        Dict dict = dictService.findDict(dictId, user.getId());
        if (dict != null) {
            Integer size = 12;
            offset = (offset == null) ? size : offset;
            List<DictWord> words = dictWordService.getWords(dictId, offset, size);
            Long wordsNum = dictWordService.countWords(dictId);
            List<TranslatorProvider> providers = translationService.listProviders();
            map.put("dict", dictService.findDict(dictId, user.getId()));
            map.put("wordsNum", wordsNum);
            map.put("words", words);
            map.put("providers", providers);
            map.put("offset", offset);
            map.put("maxOffset", wordsNum-(wordsNum%size));
            map.put("size", size);
            map.put("currPage", 1+(int)(offset/size));
            map.put("totalPage", 1+(int)(wordsNum/size));
            return "viewWords";
        } else {
            return "/dict";
        }
	}

    @RequestMapping(value = "/dict/words/translate", method = RequestMethod.POST)
	public String translateDict(@RequestParam("dictId") Long id) {
        User user = userService.findUser(getCurrentUser());
        Dict dict = dictService.findDict(id, user.getId());
        if (dict != null) {
		    translationService.translateText(id);
        }
		return "redirect:/dict";
	}

    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

}