package pt.inesc.ask.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import pt.inesc.ask.domain.AskException;




@Controller
public class RootController {

    String[] tags = new String[] { "nice", "fixe" };
    AskService s = new AskService();

    @Autowired(required = true)
    HttpServletRequest request;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        return "hello";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest req, Model model) {
        model.addAttribute("questionList", s.getListQuestions());
        return "index";
    }


    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    String handleAnyException(Throwable ex, HttpServletRequest request) {
        // ModelAndView model = new ModelAndView("error");
        // model.addObject("error", e);
        return ex.getMessage();
    }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(Model model) {
        model.addAttribute("tags", tags);
        return "newQuestion";
    }

    @RequestMapping(value = "/new-question", method = RequestMethod.POST)
    public String postNewQuestion(HttpServletRequest request, Model model) throws AskException {
        String title = request.getParameter("title");
        String text = request.getParameter("text");
        String[] tags = request.getParameterValues("tags");
        s.newQuestion(title, text, tags, "author");
        return "redirect:/question/" + title;
    }

    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(@PathVariable String questionTitle, Model model) throws AskException {
        Map<String, Object> attributes = s.getQuestionData(questionTitle);
        model.addAllAttributes(attributes);
        return "question";
    }


    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteQuestion(HttpServletRequest req, @PathVariable String questionTitle, Model model) throws AskException {
        s.deleteQuestion(questionTitle);
        return "success";
    }




    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.newAnswer(questionTitle, "author", p.get("text"));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public @ResponseBody
    String updateAnswer(@PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException {
        s.updateAnswer(p.get("answerID"), p.get("text"));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(@PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException {

        s.deleteAnswer(questionTitle, p.get("answerID"));
        return "success";
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.POST)
    public String newComment(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.newComment(questionTitle, p.get("answerID"), p.get("text"), "author");
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.PUT)
    public @ResponseBody
    String putComment(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {

        s.updateComment(questionTitle, p.get("answerID"), p.get("commentID"), p.get("text"));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            IOException {
        s.deleteComment(p.get("commentID"), p.get("answerID"));
        return "success";
    }

    // ######## Vote ##########
    @RequestMapping(value = "/question/{questionTitle}/up", method = RequestMethod.POST)
    public String voteUp(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.voteUp(questionTitle, p.get("answerID"));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/down", method = RequestMethod.POST)
    public String voteDown(@PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.voteDown(questionTitle, p.get("answerID"));
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


}
