package pt.inesc.ask.servlet;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;



@Controller
public class RootController {

    String[] tags = new String[] { "nice", "fixe" };
    DAO dao = new DAO();

    @Autowired(required = true)
    HttpServletRequest request;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String sayHelloToOpenshift() {
        return "hello";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletRequest req, Model model) {
        List<Question> list = dao.getListQuestions();
        model.addAttribute("questionList", list);
        return "index";
    }

    //
    // @ExceptionHandler(Throwable.class)
    // public ModelAndView handleAnyException(Throwable ex, HttpServletRequest request) {
    // ModelAndView model = new ModelAndView("error");
    // model.addObject("error", ex.getStackTrace());
    // return model;
    // }

    // ########## Question ################

    @RequestMapping(value = "/new-question", method = RequestMethod.GET)
    public String getNewQuestion(Model model) {
        model.addAttribute("question", new NewQuestionForm());
        model.addAttribute("tags", tags);
        return "newQuestion";
    }

    @RequestMapping(value = "/new-question", method = RequestMethod.POST)
    public String postNewQuestion(@ModelAttribute NewQuestionForm q, Model model) throws AskException {
        Answer ans = new Answer("author", q.text, true);
        Question quest = new Question(q.title, q.tags, ans.id);
        dao.save(quest);
        dao.save(ans);

        System.out.println("New question:" + q.getTitle());
        return getQuestion(q.title, model);
    }

    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.GET)
    public String getQuestion(@PathVariable String questionTitle, Model model) throws AskException {
        Question question = dao.getQuestion(questionTitle);
        if (question == null) {
            throw new AskException("Question does not exists");
        }
        model.addAttribute("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId);
            answers.add(ans);
            for (String commentId : ans.commentsIds) {
                ans.addComment(dao.getComment(commentId));
            }
        }
        model.addAttribute("question", answers.removeFirst());
        model.addAttribute("answers", answers);
        return "question";
    }

    @RequestMapping(value = "/question/{questionTitle}", method = RequestMethod.DELETE)
    public String deleteQuestion(HttpServletRequest req, @PathVariable String questionTitle, Model model) throws AskException {
        Question quest = dao.getQuestion(questionTitle);
        if (quest == null) {
            throw new AskException("Question not exists");
        }
        // Delete all answers and comments
        for (String answerId : quest.answersIDs) {
            Answer answer = dao.getAnswer(answerId);
            if (answer == null) {
                throw new AskException("Answer not exists");
            }
            for (String commentId : answer.commentsIds) {
                dao.deleteComment(commentId);
            }
            dao.deleteAnswer(answerId);
        }


        dao.deleteQuestion(quest.id);
        return index(req, model);
    }


    // ########## Answers ##########
    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.POST)
    public String newAnswer(@PathVariable String questionTitle, @ModelAttribute Answer answer, Model model) throws AskException {
        Question question = dao.getQuestion(questionTitle);
        question.addAnswer(answer.id);
        dao.save(answer);
        dao.save(question);
        return getQuestion(questionTitle, model);
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.PUT)
    public String updateAnswer(@PathVariable String questionTitle, @ModelAttribute Answer answer, Model model) throws AskException {
        dao.save(answer);
        return getQuestion(questionTitle, model);
    }

    @RequestMapping(value = "/question/{questionTitle}/answer", method = RequestMethod.DELETE)
    public String deleteAnswer(@PathVariable String questionTitle, @ModelAttribute Answer answer, Model model) throws AskException {
        Question question = dao.getQuestion(questionTitle);
        Boolean exists = question.removeAnswer(answer.id);
        if (!exists) {
            throw new AskException("Answer not exists");
        }
        // Delete all comments
        for (String commentId : answer.commentsIds) {
            dao.deleteComment(commentId);
        }
        dao.deleteAnswer(answer.id);
        dao.save(question);
        return getQuestion(questionTitle, model);
    }

    // ############ comments #########################
    @RequestMapping(value = "/question/{questionTitle}/{answerId}/comment", method = RequestMethod.POST)
    public String newComment(
            @PathVariable String questionTitle,
                @PathVariable String answerId,
                @ModelAttribute Comment comment,
                Model model) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.addComment(comment.id);
        dao.save(comment);
        dao.save(answer);
        return getQuestion(questionTitle, model);
    }

    @RequestMapping(value = "/question/{questionTitle}/{answerId}/comment", method = RequestMethod.PUT)
    public String putComment(
            @PathVariable String questionTitle,
                @PathVariable String answerId,
                @ModelAttribute Comment comment,
                Model model) throws AskException {
        dao.save(comment);
        return getQuestion(questionTitle, model);
    }

    @RequestMapping(value = "/question/{questionTitle}/{answerId}/comment", method = RequestMethod.DELETE)
    public String deleteComment(
            @PathVariable String questionTitle,
                @PathVariable String answerId,
                @ModelAttribute Comment comment,
                Model model) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        Boolean exists = answer.removeComment(comment.id);
        if (!exists) {
            throw new AskException("Comment not exists");
        }
        dao.deleteComment(comment.id);
        dao.save(answer);
        return getQuestion(questionTitle, model);
    }

    // ######## Vote ##########
    @RequestMapping(value = "/question/{questionTitle}/{answerId}/up", method = RequestMethod.POST)
    public String voteUp(@PathVariable String questionTitle, @PathVariable String answerId, Model model) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.voteUp();
        dao.save(answer);
        return getQuestion(questionTitle, model);
    }

    @RequestMapping(value = "/question/{questionTitle}/{answerId}/down", method = RequestMethod.POST)
    public String voteDown(@PathVariable String questionTitle, @PathVariable String answerId, Model model) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.voteDown();
        dao.save(answer);
        return getQuestion(questionTitle, model);
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
