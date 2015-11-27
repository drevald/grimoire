package org.helico;

import org.apache.log4j.Logger;

import javax.servlet.*;

/**
 * Created by helicobacter on 06.11.15.
 */
public class InspectionFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(InspectionFilter.class);


    public void	destroy() {
        LOG.debug("Destroying InspectionFilter");
    }

    public void	doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        LOG.debug("doFilter InspectionFilter" + request.getParameterNames());
    }

    public void	init(FilterConfig filterConfig) {
        LOG.debug("Initializing InspectionFilter");
    }

}
