package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.Account;
import org.helico.service.LangService;
import org.helico.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;

@Controller
public class AccountController  extends AbstractController  {

	private static final Logger LOG = Logger.getLogger(AccountController.class);

	@Autowired
	private AccountService accountService;

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
	public String listAccounts(Map<String, Object> map) {
		map.put("account", new Account());
		map.put("accountList", accountService.listAccounts());
		return "account";
	}
	
//	@RequestMapping("/")
//	public String home() {
//		return "redirect:/index";
//	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addaccount(@ModelAttribute("account") Account account,
			BindingResult result) {
		accountService.addAccount(account);
		return "redirect:/index";
	}

	@RequestMapping("/delete/{accountId}")
	public String deleteAccount(@PathVariable("accountId") Long accountId) {
		accountService.removeAccount(accountId);
		return "redirect:/index";
	}

	@RequestMapping("/registerAccount")
	public String registerAccount(
            @RequestParam("accountname") String accountname,
			@RequestParam("password") String password,
			@RequestParam("nativeLangId") String nativeLangId,
			@RequestParam("learnedLangId") Set<String> learnedLangId,
            @ModelAttribute("account") Account dict,
            Errors errors
    ) {
		this.learnedLangId = learnedLangId;
		try {
		    accountService.registerAccount(accountname, password, nativeLangId, learnedLangId);
		    return "redirect:/dict";
        } catch (Exception e) {
            errors.reject("error.reading.file");
            return "register";
        }
	}

	@RequestMapping("/updateAccount")
	public String updateAccount(
			@RequestParam("accountId") Long accountId,
			@RequestParam("accountname") String accountname,
			@RequestParam("password") String password,
			@RequestParam("nativeLangId") String nativeLangId,
			@RequestParam("learnedLangId") Set<String> learnedLangId,
			@ModelAttribute("account") Account dict,
			Errors errors
	) {
		this.learnedLangId = learnedLangId;
		try {
			accountService.updateAccount(accountId, accountname, password, nativeLangId, learnedLangId);
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

	@RequestMapping("/preferences")
	public String viewAccount(Map<String, Object> map) {
		map.put("langs", langService.list());
		map.put("account", accountService.findAccount(getCurrentAccount()));
		return "viewAccount";
	}

}
