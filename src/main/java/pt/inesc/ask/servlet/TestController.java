package pt.inesc.ask.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestController extends
        HttpServlet {

    private static final long serialVersionUID = 1L;
    String redirect;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String contextPath = config.getServletContext().getContextPath();
        if (contextPath.startsWith("/")) {
            redirect = contextPath;
        } else {
            redirect = "/" + contextPath;
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/html");
        response.setStatus(200);
        response.getOutputStream().println("Hello Ask");
    }

}
