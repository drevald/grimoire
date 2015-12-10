package org.helico.web;

import org.apache.log4j.Logger;
import org.helico.domain.User;
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
        String userName;
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }

//        User user = userService.findUser(userName);
//
//        if (user == null) {
//            try {
//                request.getRequestDispatcher("login.jsp").forward(request, response);
//            } catch (Exception e) {
//                LOG.error("Could not redirect to login", e);
//            }
//
//        } else {
//
//        }

        return userName;


    }


    public void setApplicationContext(ApplicationContext appContext) {
        LOG.debug("setting application context: " + appContext);
        this.appContext = appContext;
    }

}
