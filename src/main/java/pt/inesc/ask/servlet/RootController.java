package pt.inesc.ask.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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




@Controller
public class RootController {

    String[] tags = new String[] { "nice", "fixe" };
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
        System.out.println("GET /test");
        return "hello";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest r, Model model) throws AskException {
        System.out.println("GET / " + Long.parseLong(r.getHeader("Id")));
        model.addAttribute("questionList", s.getListQuestions(Long.parseLong(r.getHeader("Id"))));
        return "index";
    }

    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    String handleAnyException(Throwable ex, HttpServletRequest request) {
        return ex.getMessage();
    }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(HttpServletRequest r, Model model) {
        System.out.println("ID" + Long.parseLong(r.getHeader("Id")));
        System.out.println("GET /new-question");
        model.addAttribute("tags", tags);
        return "newQuestion";
    }

    @RequestMapping(value = "/new-question", method = RequestMethod.POST)
    public String postNewQuestion(HttpServletRequest r, Model model) throws AskException {
        System.out.println("ID" + Long.parseLong(r.getHeader("Id")));
        String title = r.getParameter("title");
        String text = r.getParameter("text");
        String[] tags = r.getParameterValues("tags");
        s.newQuestion(title, text, Arrays.asList(tags), "author", Long.parseLong(r.getHeader("Id")));
        return "redirect:/question/" + title;
    }

    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException {
        System.out.println("GET /question/" + questionTitle + " " + Long.parseLong(r.getHeader("Id")));

        Map<String, Object> attributes = s.getQuestionData(questionTitle, Long.parseLong(r.getHeader("Id")));
        model.addAllAttributes(attributes);
        return "question";
    }


    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException {
        s.deleteQuestion(questionTitle, Long.parseLong(r.getHeader("Id")));
        return "success";
    }




    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.newAnswer(questionTitle, "author", p.get("text"), Long.parseLong(r.getHeader("Id")));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public @ResponseBody
    String updateAnswer(HttpServletRequest r, @PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException {
        s.updateAnswer(p.get("answerID"), p.get("text"), Long.parseLong(r.getHeader("Id")));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(HttpServletRequest r, @PathVariable String questionTitle,

    @RequestBody Map<String, String> p) throws AskException {

        s.deleteAnswer(questionTitle, p.get("answerID"), Long.parseLong(r.getHeader("Id")));
        return "success";
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.POST)
    public String newComment(
            HttpServletRequest r,
                @PathVariable String questionTitle,
                @RequestBody Map<String, String> p) throws AskException {
        s.newComment(questionTitle, p.get("answerID"), p.get("text"), "author", Long.parseLong(r.getHeader("Id")));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.PUT)
    public @ResponseBody
    String putComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {

        s.updateComment(questionTitle,
                        p.get("answerID"),
                        p.get("commentID"),
                        p.get("text"),
                        Long.parseLong(r.getHeader("Id")));
        return "success";
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            IOException {
        s.deleteComment(p.get("commentID"), p.get("answerID"), Long.parseLong(r.getHeader("Id")));
        return "success";
    }

    // ######## Vote ##########
    @RequestMapping(value = "/question/{questionTitle}/up", method = RequestMethod.POST)
    public String voteUp(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.voteUp(questionTitle, p.get("answerID"), Long.parseLong(r.getHeader("Id")));
        return "redirect:/question/" + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/down", method = RequestMethod.POST)
    public String voteDown(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        s.voteDown(questionTitle, p.get("answerID"), Long.parseLong(r.getHeader("Id")));
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
