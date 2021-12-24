package org.helico.web;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    protected static final Logger LOG = LogManager.getLogger(AbstractController.class);

    private ApplicationContext appContext;

    protected String getCurrentAccount() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String accountName;
        if (principal instanceof UserDetails) {
            accountName = ((UserDetails) principal).getUsername();
        } else {
            accountName = principal.toString();
        }

//        Account account = accountService.findAccount(accountName);
//
//        if (account == null) {
//            try {
//                request.getRequestDispatcher("login.jsp").forward(request, response);
//            } catch (Exception e) {
//                LOG.error("Could not redirect to login", e);
//            }
//
//        } else {
//
//        }

        return accountName;


    }


    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

}
