package pt.inesc.ask.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.inesc.ask.dao.VoldemortTestDAO;
import pt.inesc.ask.domain.AskException;
import voldemort.undoTracker.RUD;




@Controller
public class RootController {

    public static String DATABASE_SERVER = "localhost";

    private static final Logger log = LogManager.getLogger(RootController.class.getName());

    AskService s = new AskService();

    @RequestMapping(value = "/voldemort", method = RequestMethod.GET)
    public @ResponseBody
    String voldemortTest() {
        VoldemortTestDAO dao = new VoldemortTestDAO();
        dao.put("test", "voldemort");
        return dao.get("test");
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        log.info("GET /test");
        return "hello";
    }

    @RequestMapping(value = "/tags/{tag}", method = RequestMethod.GET)
    public String categoryIndex(HttpServletRequest r, @PathVariable String tag, Model model) throws AskException {
        model.addAttribute("questionList", s.getListQuestions(extractRid(r), tag));
        return "index";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest r, Model model) throws AskException {
        log.info("GET / " + extractRid(r));
        model.addAttribute("tags", s.getTags().toArray());
        return "tags";
    }

    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    String handleAnyException(Throwable ex, HttpServletRequest request) {
        log.error("Handled exception", ex);
        return ex.getMessage();
    }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(HttpServletRequest r, Model model) {
        log.info("GET /new-question" + extractRid(r));
        model.addAttribute("tags", s.getTags());
        return "newQuestion";
    }

    @RequestMapping(value = "/new-question", method = RequestMethod.POST)
    public String postNewQuestion(HttpServletRequest r, Model model) throws AskException, DecoderException {
        String title = r.getParameter("title");
        String text = r.getParameter("text");
        String[] tags = r.getParameter("tags").split(",");


        String author = getParameterDefault(r, "author", "author");
        String views = getParameterDefault(r, "views", "1");
        String answers = getParameterDefault(r, "answers", "1");

        List<String> tagList;
        tagList = (tags == null) ? new LinkedList<String>() : Arrays.asList(tags);
        String encoded = AskService.encodeTitle(title);
        s.newQuestion(encoded, text, tagList, author, views, answers, extractRid(r));
        return "redirect:/question/" + title;
    }



    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        log.info("GET /question/" + questionTitle);
        questionTitle = AskService.encodeTitle(questionTitle);
        Map<String, Object> attributes = s.getQuestionData(questionTitle, extractRid(r));
        model.addAllAttributes(attributes);
        return "question";
    }


    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        // log.info("DELETE /question/" + questionTitle + " " + extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteQuestion(questionTitle, extractRid(r));
        return "success";
    }




    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // log.info("POST /question/" + questionTitle + "/answer" +
        // extractRid(r));
        String author = getParameterDefault(r, "author", "author");
        String encoded = AskService.encodeTitle(questionTitle);
        s.newAnswer(encoded, author, p.get("text"), extractRid(r));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public @ResponseBody
    String updateAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        // log.info("PUT /question/" + questionTitle + "/answer" +
        // extractRid(r));
        s.updateAnswer(p.get("answerID"), p.get("text"), extractRid(r));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(HttpServletRequest r, @PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException, DecoderException {
        // log.info("DELETE /question/" + questionTitle + "/answer" +
        // extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteAnswer(questionTitle, p.get("answerID"), extractRid(r));
        return "success";
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.POST)
    public String newComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // log.info("POST /question/" + questionTitle + "/comment" +
        // extractRid(r));
        String author = getParameterDefault(r, "author", "author");
        s.newComment(questionTitle, p.get("answerID"), p.get("text"), author, extractRid(r));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.PUT)
    public @ResponseBody
    String putComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // log.info("PUT /question/" + questionTitle + "/comment" +
        // extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.updateComment(questionTitle, p.get("answerID"), p.get("commentID"), p.get("text"), extractRid(r));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            IOException {
        // log.info("DELETE /question/" + questionTitle + "/comment" +
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

    private RUD extractRid(HttpServletRequest r) {
        return (RUD) r.getAttribute("rud");
    }

    private String getParameterDefault(HttpServletRequest request, String field, String defaultValue) {
        String str = request.getParameter(field);
        if (str == null) {
            return defaultValue;
        } else {
            return str;
        }
    }


}
