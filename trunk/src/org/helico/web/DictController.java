package org.helico.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.helico.domain.Dict;
import org.helico.domain.User;
import org.helico.service.DictService;
import org.helico.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


@Controller
public class DictController {

    private static final Logger LOG = Logger.getLogger(DictController.class);

    private static final int PREVIEW_SIZE = 1000;

    @Autowired
    private DictService dictService;

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
    	return "dictList";
    }
	
    @RequestMapping(value="/dict/upload", method = RequestMethod.POST)
	public String handleFormUpload(@RequestParam("name") String name, 
				       @RequestParam("file") MultipartFile file,
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
			    dict.setEncoding("utf-8");
			    map.put("dict", dict);
			    map.put("preview", new String(dict.getPreview()));
			    return "redirect:/dict/edit/" + dict.getId();
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
			@RequestParam("name") String name,
			@RequestParam("encoding") String encoding) 
	{
		Dict dict = dictService.findDict(id);
		dict.setEncoding(encoding);
		dict.setName(name);
		dictService.saveDict(dict);
	    return "redirect:/dict/edit/"+dict.getId();
	}

	@RequestMapping("/")
	public String home() {
		return "redirect:/dict";
	}	

	@RequestMapping("/dict/edit/{dictId}")
	public String editDict(@PathVariable("dictId") Long dictId, 
			       Map<String, Object> map,
			       HttpServletRequest request) {
		Dict dict = dictService.findDict(dictId);
		Locale locale = RequestContextUtils.getLocale(request);
		map.put("dict", dict);
		try {
		    map.put("preview", new String(dict.getPreview(), dict.getEncoding()));
		} catch (Exception e) {
		    map.put("preview", new String(dict.getPreview()));
		}
		ArrayList<String> charsetList = new ArrayList<String>();
//		for (Charset c : Charset.availableCharsets().values()) {
//		    charsetList.add(c.name());
//		}
		charsetList.add("windows-1251");
		charsetList.add("cp866");
		charsetList.add("KOI8-R");
		charsetList.add("UTF-8");
		charsetList.add("ISO-8859-1");

		map.put("charsetList", charsetList);
		return "saveDict";
	}	
	
	@RequestMapping("/dict/view/{dictId}")
	public String viewDict(@PathVariable("dictId") Long dictId, Map<String, Object> map) {
		Dict dict = dictService.findDict(dictId);
		map.put("dict", dict);
		map.put("preview", new String(dict.getPreview()));
		return "viewDict";
	}
	
	@RequestMapping("/dict/delete/{dictId}")
	public String deleteUser(@PathVariable("dictId") Long dictId) {
		dictService.removeDict(dictId);
		return "redirect:/dict";
	}

	
}
