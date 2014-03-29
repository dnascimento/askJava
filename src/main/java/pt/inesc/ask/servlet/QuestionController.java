package pt.inesc.ask.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.inesc.ask.dao.QuestionDAO;


public class QuestionController extends
        HttpServlet {
    private static final long serialVersionUID = 1L;
    String redirect;
    private QuestionDAO questionDao;

    public QuestionController() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String contextPath = config.getServletContext().getContextPath();
        if (contextPath.startsWith("/")) {
            redirect = contextPath;
        } else {
            redirect = "/" + contextPath;
        }
        questionDao = new QuestionDAO();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // InputStream requestBodyInput = request.getInputStream();
        //
        // String param1 = request.getParameter("param1");
        // String contentLength = request.getHeader("Content-Length");
        // HttpSession session = request.getSession();
        // session.setAttribute("userName", "theUserName");
        // String userName = (String) session.getAttribute("userName");
        //
        //
        // response.setHeader("Header-Name", "Header Value");
        // response.setHeader("Content-Type", "text/html");
        // response.sendRedirect("http://jenkov.com");

        // The RequestDispatcher class enables your servlet to "call" another servlet from
        // inside another servlet. The other servlet is called as if an HTTP request was
        // sent to it by a browser.
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("/anotherURL.simple");
        // The include() method merges the response written by the calling servlet, and
        // the activated servlet. This way you can achieve "server side includes" using
        // the include().

        // RequestDispatcher view = request.getRequestDispatcher("viewName");
        // view.forward(request, response);
        //
        // requestDispatcher.forward(request, response);
        //
        // response.getWriter().print("Hello Google");
    }





    // @RequestMapping(value = "/newQuestion", method = RequestMethod.GET)
    // @ResponseBody
    // public String getNewQuestion(HttpServletRequest req, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/newQuestion", method = RequestMethod.POST)
    // @ResponseBody
    // public String postNewQuestion(Model model) {
    // return "dario";
    // }
    //
    //
    // // ////////////////// Edit Methods ////////////////////
    // @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    // @ResponseBody
    // public String getQuestion(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/{questionTitle}", method = RequestMethod.DELETE)
    // @ResponseBody
    // public String deleteQuestion(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    //
    //
    //
    // // ------------- Answers -------------------------
    // @RequestMapping(value = "/question/{questionTitle}/answer", method =
    // RequestMethod.POST)
    // @ResponseBody
    // public String postAnswer(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/question/{questionTitle}/answer", method =
    // RequestMethod.PUT)
    // @ResponseBody
    // public String putAnswer(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/question/{questionTitle}/answer", method =
    // RequestMethod.DELETE)
    // @ResponseBody
    // public String deleteAnswer(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // // ------------- comments -------------------------
    // @RequestMapping(value = "/question/{questionTitle}/comment", method =
    // RequestMethod.POST)
    // @ResponseBody
    // public String postComment(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/question/{questionTitle}/comment", method =
    // RequestMethod.PUT)
    // @ResponseBody
    // public String putComment(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/question/{questionTitle}/comment", method =
    // RequestMethod.DELETE)
    // @ResponseBody
    // public String deleteComment(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    //
    //
    //
    //
    // // ------------- votes -------------------------
    // @RequestMapping(value = "/question/{questionTitle}/up", method =
    // RequestMethod.POST)
    // @ResponseBody
    // public String voteUp(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/question/{questionTitle}/down", method =
    // RequestMethod.POST)
    // @ResponseBody
    // public String voteDown(@PathVariable String questionTitle, Model model) {
    // return "dario";
    // }
    //
    //
    //
    // // ------------- upload -------------------------
    // @RequestMapping(value = "/upload", method = RequestMethod.GET)
    // @ResponseBody
    // public String getUpload(Model model) {
    // return "dario";
    // }
    //
    // @RequestMapping(value = "/upload", method = RequestMethod.POST)
    // @ResponseBody
    // public String postUpload(Model model) {
    // return "dario";
    // }
    //


}
