package pt.inesc.ask.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang.RandomStringUtils;
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

    public static final String DATABASE_SERVER = "database";
    public static final String VOLDEMORT_PORT = "6666";

    public static final String UPLOAD = "upload";
    public static final String SUCCESS = "success";
    public static final String REDIRECT_TO_QUESTIONS = "redirect:/question/";

    private static final Logger log = Logger.getLogger(RootController.class.getName());


    AskService s = new AskService();

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        // // System.out.println("Request");
        return "hello";
    }


    @ExceptionHandler(Throwable.class)
    public @ResponseBody
    String handleAnyException(Throwable ex, HttpServletRequest request) {
        // System.out.println("ERROR: " + ex.getMessage());
        // ex.printStackTrace();
        return "ERROR:" + ex.getMessage();
    }


    @RequestMapping(value = "/tags/{tag}", method = RequestMethod.GET)
    public String categoryIndex(HttpServletRequest r, @PathVariable String tag, Model model) throws AskException {
        model.addAttribute("questionList", s.getListQuestions(extractRid(r), tag));
        return "index";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest r, Model model) throws AskException {
        model.addAttribute("tags", s.getTags().toArray());
        return "tags";
    }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(HttpServletRequest r, Model model) {
        // System.out.println("GET /new-question" + extractRid(r));
        model.addAttribute("tags", s.getTags());
        return "newQuestion";
    }

    @RequestMapping(value = "/randomQuestion", method = RequestMethod.POST)
    public String randomQuestion(HttpServletRequest r, Model model) throws AskException, DecoderException {
        SRD srd = extractRid(r);
        String rand = RandomStringUtils.random(100);
        s.newQuestion(rand, rand, Arrays.asList(rand), rand, "0", "1", srd, null);
        return REDIRECT_TO_QUESTIONS + rand;
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
        SRD srd = extractRid(r);
        // System.out.println("New question: author: " + author + " ; rid: " + srd.rid);

        s.newQuestion(encoded, text, tagList, author, views, answers, srd, answerId);
        return REDIRECT_TO_QUESTIONS + title;
    }





    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        // System.out.println("GET /question/" + questionTitle);
        questionTitle = AskService.encodeTitle(questionTitle);
        Map<String, Object> attributes = s.getQuestionData(questionTitle, extractRid(r));
        model.addAllAttributes(attributes);
        return "question";
    }


    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteQuestion(HttpServletRequest r, @PathVariable String questionTitle, Model model) throws AskException, DecoderException {
        // System.out.println("DELETE /question/" + questionTitle + " " + extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteQuestion(questionTitle, extractRid(r));
        return SUCCESS;
    }




    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // System.out.println("POST /question/" + questionTitle + "/answer" +
        // extractRid(r));
        String answerId = getParameterDefault(p, "answerId", null);
        String author = getParameterDefault(p, "author", "author");
        String encoded = AskService.encodeTitle(questionTitle);
        String unescapedText = StringEscapeUtils.unescapeHtml(p.get("text"));

        SRD srd = extractRid(r);
        // System.out.println("New answer: author: " + author + " ; rid: " + srd.rid);

        s.newAnswer(encoded, author, unescapedText, srd, answerId);
        return REDIRECT_TO_QUESTIONS + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public @ResponseBody
    String updateAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        // System.out.println("PUT /question/" + questionTitle + "/answer" +
        // extractRid(r));
        s.updateAnswer(p.get("answerID"), p.get("text"), extractRid(r));
        return SUCCESS;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        s.deleteAnswer(questionTitle, p.get("answerID"), extractRid(r));
        return SUCCESS;
    }

    @RequestMapping(value = "/question/{questionTitle}/answer/{answerID}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteAnswer(HttpServletRequest r, @PathVariable String questionTitle, @PathVariable String answerID) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        answerID = decodeParameter(answerID);
        s.deleteAnswer(questionTitle, answerID, extractRid(r));
        return SUCCESS;
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.POST)
    public String newComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        // System.out.println("POST /question/" + questionTitle + "/comment" +
        // extractRid(r));
        String author = getParameterDefault(p, "author", "author");
        String unescapedText = StringEscapeUtils.unescapeHtml(p.get("text"));

        SRD srd = extractRid(r);
        // System.out.println("New comment: author: " + author + " ; rid: " + srd.rid);

        s.newComment(questionTitle, p.get("answerID"), unescapedText, author, srd);
        return REDIRECT_TO_QUESTIONS + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.PUT)
    public @ResponseBody
    String putComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            DecoderException {
        // System.out.println("PUT /question/" + questionTitle + "/comment" +
        // extractRid(r));
        questionTitle = AskService.encodeTitle(questionTitle);
        s.updateComment(questionTitle, p.get("answerID"), p.get("commentID"), p.get("text"), extractRid(r));
        return SUCCESS;
    }

    @RequestMapping(value = "/question/{questionTitle}/comment", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException,
            IOException {
        s.deleteComment(p.get("commentID"), p.get("answerID"), extractRid(r));
        return SUCCESS;
    }



    @RequestMapping(value = "/question/{questionTitle}/comment/{answerID}/{commentID}", method = RequestMethod.DELETE)
    public @ResponseBody
    String deleteComment(
            HttpServletRequest r,
                @PathVariable String questionTitle,
                @PathVariable String answerID,
                @PathVariable String commentID,
                @RequestBody Map<String, String> p) throws AskException, IOException {
        answerID = decodeParameter(answerID);
        commentID = decodeParameter(commentID);
        s.deleteComment(commentID, answerID, extractRid(r));
        return SUCCESS;
    }



    // ######## Vote ##########
    @RequestMapping(value = "/question/{questionTitle}/up", method = RequestMethod.POST)
    public String voteUp(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        s.voteUp(questionTitle, p.get("answerID"), extractRid(r));
        return REDIRECT_TO_QUESTIONS + questionTitle;
    }

    @RequestMapping(value = "/question/{questionTitle}/down", method = RequestMethod.POST)
    public String voteDown(HttpServletRequest r, @PathVariable String questionTitle, @RequestBody Map<String, String> p) throws AskException {
        questionTitle = AskService.encodeTitle(questionTitle);
        s.voteDown(questionTitle, p.get("answerID"), extractRid(r));
        return REDIRECT_TO_QUESTIONS + questionTitle;
    }

    // ######## Upload ################
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model) {
        return UPLOAD;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadPost(Model model) {
        return UPLOAD;
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



    private String decodeParameter(String text) {
        return text.replace("%2B", "+").replace("%2F", "/").replace("%3D", "=");
    }
}
