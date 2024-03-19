package org.helico;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by helicobacter on 06.11.15.
 */
public class MyServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(MyServlet.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OutputStream os = resp.getOutputStream();
        os.write("Hi All. Serving".getBytes());
        os.close();
        LOG.debug("Hi All. Serving");
    }

    @Override
    protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OutputStream os = resp.getOutputStream();
        os.write("Hi All. Getting".getBytes());
        os.close();
        LOG.debug("Hi All. Getting");
    }

    @Override
    protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OutputStream os = resp.getOutputStream();
        os.write("Hi All. Posting".getBytes());
        os.close();
        LOG.debug("Hi All. Posting");
    }

}
