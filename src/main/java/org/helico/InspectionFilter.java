package org.helico;

import org.apache.log4j.Logger;
import org.helico.domain.User;
import org.helico.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.*;

/**
 * Created by helicobacter on 06.11.15.
 */
public class InspectionFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(InspectionFilter.class);

    @Autowired
    private UserService userService;

    public void	destroy() {
        LOG.debug("Destroying InspectionFilter");
    }

    public void	doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String userName;
//        if (principal instanceof UserDetails) {
//            userName = ((UserDetails) principal).getUsername();
//        } else {
//            userName = principal.toString();
//        }
//        User user = userService.findUser(userName);
//        if (user == null) {
//            try {
//                request.getRequestDispatcher("login.jsp").forward(request, response);
//            } catch (Exception e) {
//                LOG.error("Could not redirect to login", e);
//            }
//        }
    }

    public void	init(FilterConfig filterConfig) {
        LOG.debug("Initializing InspectionFilter");
    }

}
