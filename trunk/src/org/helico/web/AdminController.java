package org.helico.web;

import org.helico.domain.Transition;
import org.helico.domain.TranslatorProvider;
import org.helico.service.TransitionService;
import org.helico.service.TranslatorProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private TransitionService transitionService;

    @Autowired
    private TranslatorProviderService providerService;

    @RequestMapping(value = "/admin/transitions")
	public String listTransitions(Map<String, Object> map) {
        List<Transition> transitions = transitionService.list();
        map.put("transitions", transitions);
        return "transList";
    }

    @RequestMapping(value = "/admin/providers")
	public String listProviders(Map<String, Object> map) {
        List<TranslatorProvider> providers = providerService.listProviders();
        map.put("providers", providers);
        return "providerList";
    }


}
