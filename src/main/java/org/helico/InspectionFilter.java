package org.helico;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Created by helicobacter on 06.11.15.
 */
public class InspectionFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(InspectionFilter.class);

    public void	destroy() {
        LOG.debug("Destroying InspectionFilter");
    }

    public void	doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            LOG.info("Account principal is " + ((HttpServletRequest)request).getUserPrincipal());
            Principal principal = ((HttpServletRequest)request).getUserPrincipal();
//            if (principal == null) {
//                request.getRequestDispatcher("/login.jsp").forward(request, response);
//            } else {
                chain.doFilter(request, response);
//            }
        } catch (Exception e) {
             LOG.error(e, e);
        }
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String accountName;
//        if (principal instanceof AccountDetails) {
//            accountName = ((AccountDetails) principal).getAccountname();
//        } else {
//            accountName = principal.toString();
//        }
//        Account account = accountService.findAccount(accountName);
//        if (account == null) {
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
