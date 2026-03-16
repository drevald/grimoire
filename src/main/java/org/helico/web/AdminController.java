package org.helico.web;

import org.helico.domain.Transition;
import org.helico.domain.TranslatorProvider;
import org.helico.service.TransitionService;
import org.helico.service.TranslatorProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

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

    @RequestMapping(value = "/admin/providers", method = RequestMethod.GET)
    public String listProviders(Map<String, Object> map) {
        List<TranslatorProvider> providers = providerService.listProviders();
        map.put("providers", providers);
        return "providerList";
    }

    @RequestMapping(value = "/admin/providers/new", method = RequestMethod.GET)
    public String newProvider(Map<String, Object> map) {
        TranslatorProvider provider = new TranslatorProvider();
        provider.setMethod("GET");
        provider.setContentType("application/json");
        provider.setCharset("UTF-8");
        map.put("provider", provider);
        map.put("isNew", true);
        return "providerForm";
    }

    @RequestMapping(value = "/admin/providers/edit/{id}", method = RequestMethod.GET)
    public String editProvider(@PathVariable Long id, Map<String, Object> map) {
        TranslatorProvider provider = providerService.getProvider(id);
        if (provider == null) {
            return "redirect:/admin/providers";
        }
        map.put("provider", provider);
        map.put("isNew", false);
        return "providerForm";
    }

    @RequestMapping(value = "/admin/providers/save", method = RequestMethod.POST)
    public String saveProvider(@ModelAttribute TranslatorProvider provider) {
        providerService.saveProvider(provider);
        return "redirect:/admin/providers";
    }

    @RequestMapping(value = "/admin/providers/api", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<TranslatorProvider>> listProvidersApi() {
        List<TranslatorProvider> providers = providerService.listProviders();
        return ResponseEntity.ok(providers);
    }

    @RequestMapping(value = "/admin/providers/api/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<TranslatorProvider> getProvider(@PathVariable Long id) {
        TranslatorProvider provider = providerService.getProvider(id);
        if (provider == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(provider);
    }

    @RequestMapping(value = "/admin/providers/api", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TranslatorProvider> createProvider(@RequestBody TranslatorProvider provider) {
        provider.setId(null); // Ensure it's a new entity
        providerService.saveProvider(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(provider);
    }

    @RequestMapping(value = "/admin/providers/api/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TranslatorProvider> updateProvider(@PathVariable Long id, @RequestBody TranslatorProvider provider) {
        TranslatorProvider existing = providerService.getProvider(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        provider.setId(id);
        providerService.saveProvider(provider);
        return ResponseEntity.ok(provider);
    }

    @RequestMapping(value = "/admin/providers/api/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        TranslatorProvider existing = providerService.getProvider(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

}
