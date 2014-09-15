package pt.inesc.ask.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.jboss.logging.Logger;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import voldemort.undoTracker.SRD;

public class AskService {
    private static final Logger LOG = Logger.getLogger(AskService.class.getName());

    DAO dao = new VoldemortDAO("tcp://" + RootController.DATABASE_SERVER + ":" + RootController.VOLDEMORT_PORT);

    public void newQuestion(
            String title,
                String text,
                List<String> tags,
                String author,
                String views,
                String answers,
                SRD srd,
                String answerId) throws AskException {
        LOG.info("New Question: " + title + " " + text + " " + tags + " " + author + " srd:" + srd);
        Answer ans = new Answer(title, author, text, true, answerId);
        Question quest = new Question(title, tags, views, answers, ans.getId());
        dao.saveNew(quest, srd);
        dao.save(ans, srd);
        LOG.info("New question:" + title);

    }

    public List<QuestionEntry> getListQuestions(SRD srd, String tag) throws AskException {
        LOG.info("Get Question List" + " srd:" + srd);
        return dao.getListQuestions(srd, tag);
    }

    public Map<String, Object> getQuestionData(String questionTitle, SRD srd) throws AskException {
        LOG.info("Get question: " + questionTitle + " srd:" + srd);
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        Question question = dao.getQuestion(questionTitle, srd);
        attributes.put("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId, srd);
            ans.cleanComments();
            for (String commentId : ans.getCommentsIds()) {
                ans.addComment(dao.getComment(commentId, srd));
            }
            answers.addLast(ans);
        }
        attributes.put("question", answers.removeFirst());
        attributes.put("answers", answers);
        return attributes;
    }

    public boolean deleteQuestion(String questionTitle, SRD srd) throws AskException {
        LOG.info("Delete Question: " + questionTitle + " srd:" + srd);
        boolean c = true, a = true, q = true;
        Question quest = dao.getQuestion(questionTitle, srd);

        // Delete all answers and comments
        for (String answerId : quest.getAnswersIDs()) {
            try {
                Answer answer = dao.getAnswer(answerId, srd);
                for (String commentId : answer.getCommentsIds()) {
                    c = c && dao.deleteComment(commentId, srd);
                }
                a = a && dao.deleteAnswer(answerId, srd);
            } catch (AskException e) {
                a = false;
                System.err.println(e);
            }
        }
        q = dao.deleteQuestion(quest.getId(), srd);
        return c && a && q;
    }

    public String newAnswer(String questionTitle, String author, String text, SRD srd, String answerId) throws AskException {
        Question question = dao.getQuestion(questionTitle, srd);
        Answer ans = new Answer(questionTitle, author, text, false, answerId);
        LOG.info("New Answer: " + questionTitle + " :author: " + author + " :text: " + text + " :srd:" + srd + " :id: " + ans.getId()
                + " : providedId:" + answerId);
        question.addAnswer(ans.getId());
        dao.save(ans, srd);
        dao.save(question, srd);
        return ans.getId();
    }

    public void updateAnswer(String answerId, String text, SRD srd) throws AskException {
        LOG.info("Update Answer: " + answerId + " :text: " + text + " :srd:" + srd);
        Answer answer = dao.getAnswer(answerId, srd);
        if (answer == null) {
            throw new AskException("Update Answer: answer not exists:" + answerId);
        }
        answer.setText(text);
        dao.save(answer, srd);
    }

    public boolean deleteAnswer(String questionTitle, String answerId, SRD srd) throws AskException {
        LOG.info("Delete Answer: " + questionTitle + " " + answerId + " srd:" + srd);
        Question question = dao.getQuestion(questionTitle, srd);
        Answer answer = dao.getAnswer(answerId, srd);
        boolean c = true, a = true;
        a = question.removeAnswer(answerId);
        // Delete all comments
        for (String commentId : answer.getCommentsIds()) {
            c = c && dao.deleteComment(commentId, srd);
        }
        a = a && dao.deleteAnswer(answer.getId(), srd);
        dao.save(question, srd);
        return a && c;
    }

    public String newComment(String questionTitle, String answerID, String text, String author, SRD srd) throws AskException {
        Answer answer = dao.getAnswer(answerID, srd);
        Comment comment = new Comment(answerID, text, author);
        LOG.info("New Comment: " + questionTitle + " :answerId: " + answerID + " :text: " + text + " :author: " + author + " :srd:" + srd);
        answer.addComment(comment.getId());
        dao.save(comment, srd);
        dao.save(answer, srd);
        return comment.getId();
    }

    public void updateComment(String questionTitle, String answerID, String commentID, String text, SRD srd) throws AskException {
        LOG.info("Update Comment" + questionTitle + " " + answerID + " " + commentID + " " + text + " srd:" + srd);
        Comment com = dao.getComment(commentID, srd);
        com.setText(text);
        dao.save(com, srd);
    }

    public void deleteComment(String commentID, String answerID, SRD srd) throws AskException {
        LOG.info("Delete Comment: " + commentID + " " + answerID + " srd:" + srd);
        Answer answer = dao.getAnswer(answerID, srd);
        Boolean exists = answer.removeComment(commentID);
        if (!exists) {
            throw new AskException("Comment not exists");
        }
        dao.deleteComment(commentID, srd);
        dao.save(answer, srd);

    }

    public void voteUp(String questionTitle, String answerId, SRD srd) throws AskException {
        LOG.info("VoteUp: " + questionTitle + " " + answerId + " srd:" + srd);
        Answer answer = dao.getAnswer(answerId, srd);
        answer.voteUp();
        dao.save(answer, srd);
    }

    public void voteDown(String questionTitle, String answerId, SRD srd) throws AskException {
        LOG.info("VoteDown: " + questionTitle + " " + answerId + " srd:" + srd);
        Answer answer = dao.getAnswer(answerId, srd);
        answer.voteDown();
        dao.save(answer, srd);
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
            // LOG.error(e);
            return title;
        }
    }

    public static String decodeTitle(String title) {
        try {
            return new URLCodec().decode(title);
        } catch (DecoderException e) {
            // LOG.error(e);
            return title;
        }
    }
}
