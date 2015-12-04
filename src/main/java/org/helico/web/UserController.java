package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.User;
import org.helico.service.LangService;
import org.helico.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Controller
public class UserController {

	private static final Logger LOG = Logger.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private LangService langService;
	private Set<String> learnedLangId;


	@RequestMapping(value="/form", method = RequestMethod.POST)
	public String handleFormUpload(@RequestParam("name") String name,
				       @RequestParam("file") MultipartFile file) {
    	
	LOG.info("!! handleFormUpload");
        if (!file.isEmpty()) {
	    try {
		// store the bytes somewhere
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	return "redirect:/index";

    }

	@RequestMapping("/index")
	public String listUsers(Map<String, Object> map) {
		map.put("user", new User());
		map.put("userList", userService.listUsers());
		return "user";
	}
	
//	@RequestMapping("/")
//	public String home() {
//		return "redirect:/index";
//	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String adduser(@ModelAttribute("user") User user,
			BindingResult result) {
		userService.addUser(user);
		return "redirect:/index";
	}

	@RequestMapping("/delete/{userId}")
	public String deleteUser(@PathVariable("userId") Long userId) {
		userService.removeUser(userId);
		return "redirect:/index";
	}

	@RequestMapping("/registerUser")
	public String registerUser(
            @RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("nativeLangId") String nativeLangId,
			@RequestParam("learnedLangId") Set<String> learnedLangId,
            @ModelAttribute("user") User dict,
            Errors errors
    ) {
		this.learnedLangId = learnedLangId;
		try {
		    userService.registerUser(username, password, nativeLangId, learnedLangId);
		    return "redirect:/dict";
        } catch (Exception e) {
            errors.reject("error.reading.file");
            return "register";
        }
	}

    @RequestMapping("/register")
	public String register(Map<String, Object> map) {
		map.put("langs", langService.list());
		return "register";
	}


}
