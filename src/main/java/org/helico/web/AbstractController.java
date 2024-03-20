package org.helico.web;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

/**
 * Created by Denis on 12/6/15.
 */
@Controller
public class AbstractController implements ApplicationContextAware {

    private static final Logger LOG = Logger.getLogger(AbstractController.class);

    protected String getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String accountName;
        if (principal instanceof UserDetails) {
            accountName = ((UserDetails) principal).getUsername();
        } else {
            accountName = principal.toString();
        }
        return accountName;
    }


    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
    }

}
