package org.helico.web;

import java.util.Map;

import org.helico.domain.User;
import org.helico.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.apache.log4j.Logger;

@Controller
public class UserController {

	private static final Logger LOG = Logger.getLogger(UserController.class);


	@Autowired
	private UserService userService;


    @RequestMapping(value="/form", method = RequestMethod.POST)
	public String handleFormUpload(@RequestParam("name") String name,
				       @RequestParam("file") MultipartFile file) {
    	
	LOG.info("!! handleFormUpload");
        if (!file.isEmpty()) {
	    try {
		userService.addFile(file.getInputStream());
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


}
