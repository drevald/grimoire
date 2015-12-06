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

    private ApplicationContext appContext;

    protected String getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

}
