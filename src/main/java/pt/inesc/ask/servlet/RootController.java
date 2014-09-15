package pt.inesc.ask.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.inesc.ask.domain.AskException;
import voldemort.undoTracker.SRD;


@Controller
public class RootController {

    public static final String DATABASE_SERVER = "localhost";
    public static final String VOLDEMORT_PORT = "6666";

    private static final Logger log = Logger.getLogger(RootController.class.getName());


    AskService s = new AskService();

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        return "hello";
    }


    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    String handleAnyException(Throwable ex, HttpServletRequest request) {
        // LOG.error("Handled exception", ex);
        return ex.getMessage();
    }


    @RequestMapping(value = "/tags/{tag}", method = RequestMethod.GET)
    public String categoryIndex(HttpServletRequest r, @PathVariable String tag, Model model) throws AskException {
        model.addAttribute("questionList", s.getListQuestions(extractRid(r), tag));
        return "index";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest r, Model model) throws AskException {
        // LOG.info("GET / " + extractRid(r));
        model.addAttribute("tags", s.getTags().toArray());
        return "tags";
    }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(HttpServletRequest r, Model model) {
        // LOG.info("GET /new-question" + extractRid(r));
        model.addAttribute("tags", s.getTags());
        return "newQuestion";
    }

    @RequestMapping(value = "/new-question", method = RequestMethod.POST)
    public String postNewQuestion(HttpServletRequest r, Model model) throws AskException, DecoderException {
        String title = r.getParameter("title");
        return newQuestion(title, r);
    }

    @RequestMapping(value = "/new-question/{questionTitle}", method = RequestMethod.POST)
    public String postNewQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException,
            DecoderException {

        return newQuestion(questionTitle, r);
    }

    private String newQuestion(String title, HttpServletRequest r) throws AskException {
        String text = r.getParameter("text");
        String[] tags = r.getParameter("tags").split(",");


        String answerId = getParameterDefault(r, "answerId", null);
        String author = getParameterDefault(r, "author", "author");
        String views = getParameterDefault(r, "views", "1");
        String answers = getParameterDefault(r, "answers", "1");

        List<String> tagList;
        tagList = (tags == null) ? new LinkedList<String>() : Arrays.asList(tags);
        String encoded = AskService.encodeTitle(title);
        s.newQuestion(encoded, text, tagList, author, views, answers, extractRid(r), answerId);
        return "redirect:/question/" + title;
    }






    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        // LOG.info("GET /question/" + questionTitle);
        questionTitle = AskService.encodeTitle(questionTitle);
        Map<String, Object> attributes = s.getQuestionData(questionTitle, extractRid(r));
        model.addAllAttributes(attributes);
        return "question";
    }


    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        // //LOG.info("DELETE /question/" + questionTitle + " " + extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteQuestion(questionTitle, extractRid(r));
        return "success";
    }




    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // //LOG.info("POST /question/" + questionTitle + "/answer" +
        // extractRid(r));

        String answerId = getParameterDefault(p, "answerId", null);
        String author = getParameterDefault(p, "author", "author");
        String encoded = AskService.encodeTitle(questionTitle);
        String unescapedText = StringEscapeUtils.unescapeHtml(p.get("text"));
        s.newAnswer(encoded, author, unescapedText, extractRid(r), answerId);
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public @ResponseBody
    String updateAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        // //LOG.info("PUT /question/" + questionTitle + "/answer" +
        // extractRid(r));
        s.updateAnswer(p.get("answerID"), p.get("text"), extractRid(r));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(HttpServletRequest r, @PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException {
        // //LOG.info("DELETE /question/" + questionTitle + "/answer" +
        // extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteAnswer(questionTitle, p.get("answerID"), extractRid(r));
        return "success";
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.POST)
    public String newComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        // //LOG.info("POST /question/" + questionTitle + "/comment" +
        // extractRid(r));
        String author = getParameterDefault(p, "author", "author");
        String unescapedText = StringEscapeUtils.unescapeHtml(p.get("text"));
        s.newComment(questionTitle, p.get("answerID"), unescapedText, author, extractRid(r));
        return "redirect:/question/" + questionTitle;
    }


    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.PUT)
    public @ResponseBody
    String putComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // //LOG.info("PUT /question/" + questionTitle + "/comment" +
        // extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.updateComment(questionTitle, p.get("answerID"), p.get("commentID"), p.get("text"), extractRid(r));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            IOException {
        // //LOG.info("DELETE /question/" + questionTitle + "/comment" +
        // extractRid(r));
        s.deleteComment(p.get("commentID"), p.get("answerID"), extractRid(r));
        return "success";
    }

    // ######## Vote ##########
    @RequestMapping(value = "/question/{questionTitle}/up", method = RequestMethod.POST)
    public String voteUp(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        s.voteUp(questionTitle, p.get("answerID"), extractRid(r));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/down", method = RequestMethod.POST)
    public String voteDown(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        s.voteDown(questionTitle, p.get("answerID"), extractRid(r));
        return "redirect:/question/" + questionTitle;
    }

    // ######## Upload ################
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model) {
        return "upload";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadPost(Model model) {
        // TODO
        return "upload";
    }

    private SRD extractRid(HttpServletRequest r) {
        return (SRD) r.getAttribute("srd");
    }

    private String getParameterDefault(HttpServletRequest request, String field, String defaultValue) {
        String str = request.getParameter(field);
        if (str == null) {
            return defaultValue;
        } else {
            return str;
        }
    }


    private String getParameterDefault(Map<String, String> p, String field, String defaultValue) {
        String str = p.get(field);
        if (str == null) {
            return defaultValue;
        } else {
            return str;
        }
    }

}
