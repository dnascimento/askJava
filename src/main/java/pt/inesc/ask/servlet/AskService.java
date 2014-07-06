package pt.inesc.ask.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import voldemort.undoTracker.RUD;


public class AskService {
    private static final Logger log = LogManager.getLogger(AskService.class.getName());

    DAO dao = new VoldemortDAO();

    public void newQuestion(String title, String text, List<String> tags, String author, String views, String answers, RUD rud) throws AskException {
        log.info("New Question: " + title + " " + text + " " + tags + " " + author + " rud:" + rud);
        Answer ans = new Answer(title, author, text, true);
        Question quest = new Question(title, tags, views, answers, ans.getId());
        dao.saveNew(quest, rud);
        dao.save(ans, rud);
        log.info("New question:" + title);

    }

    public List<Question> getListQuestions(RUD rud, String tag) throws AskException {
        log.info("Get Question List" + " rud:" + rud);
        return dao.getListQuestions(rud, tag);
    }

    public Map<String, Object> getQuestionData(String questionTitle, RUD rud) throws AskException {
        log.info("Get question: " + questionTitle + " rud:" + rud);
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        Question question = dao.getQuestion(questionTitle, rud);
        attributes.put("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId, rud);
            ans.cleanComments();
            for (String commentId : ans.getCommentsIds()) {
                ans.addComment(dao.getComment(commentId, rud));
            }
            answers.addLast(ans);
        }
        attributes.put("question", answers.removeFirst());
        attributes.put("answers", answers);
        return attributes;
    }

    public boolean deleteQuestion(String questionTitle, RUD rud) throws AskException {
        log.info("Delete Question: " + questionTitle + " rud:" + rud);
        boolean c = true, a = true, q = true;
        Question quest = dao.getQuestion(questionTitle, rud);

        // Delete all answers and comments
        for (String answerId : quest.getAnswersIDs()) {
            try {
                Answer answer = dao.getAnswer(answerId, rud);
                for (String commentId : answer.getCommentsIds()) {
                    c = c && dao.deleteComment(commentId, rud);
                }
                a = a && dao.deleteAnswer(answerId, rud);
            } catch (AskException e) {
                a = false;
                System.err.println(e);
            }
        }
        q = dao.deleteQuestion(quest.getId(), rud);
        return c && a && q;
    }

    public String newAnswer(String questionTitle, String author, String text, RUD rud) throws AskException {
        log.info("New Answer: " + questionTitle + " " + author + " " + text + " rud:" + rud);
        Question question = dao.getQuestion(questionTitle, rud);
        Answer ans = new Answer(questionTitle, author, text, false);
        question.addAnswer(ans.getId());
        dao.save(ans, rud);
        dao.save(question, rud);
        return ans.getId();
    }

    public void updateAnswer(String answerId, String text, RUD rud) throws AskException {
        log.info("Update Answer: " + answerId + " " + text + " rud:" + rud);
        Answer answer = dao.getAnswer(answerId, rud);
        if (answer == null) {
            throw new AskException("Update Answer: answer not exists:" + answerId);
        }
        answer.setText(text);
        dao.save(answer, rud);
    }

    public boolean deleteAnswer(String questionTitle, String answerId, RUD rud) throws AskException {
        log.info("Delete Answer: " + questionTitle + " " + answerId + " rud:" + rud);
        Question question = dao.getQuestion(questionTitle, rud);
        Answer answer = dao.getAnswer(answerId, rud);
        boolean c = true, a = true;
        a = question.removeAnswer(answerId);
        // Delete all comments
        for (String commentId : answer.getCommentsIds()) {
            c = c && dao.deleteComment(commentId, rud);
        }
        a = a && dao.deleteAnswer(answer.getId(), rud);
        dao.save(question, rud);
        return a && c;
    }

    public String newComment(String questionTitle, String answerID, String text, String author, RUD rud) throws AskException {
        log.info("New Comment: " + questionTitle + " " + answerID + " " + text + " " + author + " rud:" + rud);
        Answer answer = dao.getAnswer(answerID, rud);
        Comment comment = new Comment(answerID, text, author);
        answer.addComment(comment.getId());
        dao.save(comment, rud);
        dao.save(answer, rud);
        return comment.getId();
    }

    public void updateComment(String questionTitle, String answerID, String commentID, String text, RUD rud) throws AskException {
        log.info("Update Comment" + questionTitle + " " + answerID + " " + commentID + " " + text + " rud:" + rud);
        Comment com = dao.getComment(commentID, rud);
        com.setText(text);
        dao.save(com, rud);
    }

    public void deleteComment(String commentID, String answerID, RUD rud) throws AskException {
        log.info("Delete Comment: " + commentID + " " + answerID + " rud:" + rud);
        Answer answer = dao.getAnswer(answerID, rud);
        Boolean exists = answer.removeComment(commentID);
        if (!exists) {
            throw new AskException("Comment not exists");
        }
        dao.deleteComment(commentID, rud);
        dao.save(answer, rud);

    }

    public void voteUp(String questionTitle, String answerId, RUD rud) throws AskException {
        log.info("VoteUp: " + questionTitle + " " + answerId + " rud:" + rud);
        Answer answer = dao.getAnswer(answerId, rud);
        answer.voteUp();
        dao.save(answer, rud);
    }

    public void voteDown(String questionTitle, String answerId, RUD rud) throws AskException {
        log.info("VoteDown: " + questionTitle + " " + answerId + " rud:" + rud);
        Answer answer = dao.getAnswer(answerId, rud);
        answer.voteDown();
        dao.save(answer, rud);
    }

    public DAO getDao() {
        return dao;
    }

    public List<String> getTags() {
        return dao.getTags();
    }

    public static String encodeTitle(String title) {
        try {
            return new URLCodec().encode(title);
        } catch (EncoderException e) {
            log.error(e);
            return title;
        }
    }

    public static String decodeTitle(String title) {
        try {
            return new URLCodec().decode(title);
        } catch (DecoderException e) {
            log.error(e);
            return title;
        }
    }
}
