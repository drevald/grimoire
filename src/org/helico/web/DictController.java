package org.helico.web;

import java.io.IOException;
import java.util.Map;

import org.helico.domain.Dict;
import org.helico.domain.User;
import org.helico.service.DictService;
import org.helico.service.UserService;
import org.helico.service.LangService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.apache.log4j.Logger;


@Controller
public class DictController {

    private static final Logger LOG = Logger.getLogger(DictController.class);

    private static final int PREVIEW_SIZE = 1000;

    @Autowired
    private DictService dictService;

    @Autowired
	private LangService langService;

    @Autowired
    private UserService userService;	
	
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
    	map.put("currUser", getCurrentUser());
    	map.put("dictList", dictService.listDicts());
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
			    dict = dictService.loadPreviewFile(user.getId(), 
								    file.getInputStream(), 
								    file.getOriginalFilename());
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
            @RequestParam("langId") Long langId,
			@RequestParam("encoding") String encoding)
	{
		Dict dict = dictService.findDict(id);
		dict.setEncoding(encoding);
		dictService.saveDict(dict);
	    return "redirect:/dict/edit/"+dict.getId() + "?langId=" + langId;
	}

	@RequestMapping(value = "/dict/edit/store", method = RequestMethod.POST)
	public String storeDict(
			@RequestParam("id") Long id,
			@RequestParam("encoding") String encoding)
	{
		Dict dict = dictService.findDict(id);
		dict.setEncoding(encoding);
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
		Dict dict = dictService.findDict(dictId);
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
		Dict dict = dictService.findDict(dictId);
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
		dictService.removeDict(dictId);
		return "redirect:/dict";
	}

	@RequestMapping("/dict/view/generate")
	public String parseDict(@RequestParam("dictId") Long id) {
		dictService.parseText(id);
		return "redirect:/dict/view/" +  id;
	}

	
}
