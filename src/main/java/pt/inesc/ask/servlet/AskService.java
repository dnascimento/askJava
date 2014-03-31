package pt.inesc.ask.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;


public class AskService {
    DAO dao = DAO.getDAO();

    public void newQuestion(String title, String text, String[] tags, String string) {
        Answer ans = new Answer(title, "author", text, true);
        Question quest = new Question(title, tags, ans.id);
        dao.save(quest);
        dao.save(ans);
        System.out.println("New question:" + title);

    }

    public List<Question> getListQuestions() {
        return dao.getListQuestions();
    }

    public Map<String, Object> getQuestionData(String questionTitle) throws AskException {
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        Question question = dao.getQuestion(questionTitle);
        if (question == null) {
            throw new AskException("Question does not exists");
        }
        attributes.put("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId);
            ans.cleanComments();
            for (String commentId : ans.commentsIds) {
                ans.addComment(dao.getComment(commentId));
            }
            answers.addLast(ans);
        }
        attributes.put("question", answers.removeFirst());
        attributes.put("answers", answers);
        return attributes;
    }

    public void deleteQuestion(String questionTitle) throws AskException {
        Question quest = dao.getQuestion(questionTitle);
        // Delete all answers and comments
        for (String answerId : quest.answersIDs) {
            Answer answer = dao.getAnswer(answerId);
            for (String commentId : answer.commentsIds) {
                dao.deleteComment(commentId);
            }
            dao.deleteAnswer(answerId);
        }
        dao.deleteQuestion(quest.id);
    }

    public void newAnswer(String questionTitle, String string, String text) throws AskException {
        Question question = dao.getQuestion(questionTitle);
        Answer ans = new Answer(questionTitle, "author", text, false);
        question.addAnswer(ans.id);
        dao.save(ans);
        dao.save(question);
    }

    public void updateAnswer(String answerId, String text) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.text = text;
        dao.save(answer);
    }

    public void deleteAnswer(String questionTitle, String answerId) throws AskException {
        Question question = dao.getQuestion(questionTitle);
        Answer answer = dao.getAnswer(answerId);
        Boolean exists = question.removeAnswer(answerId);
        if (!exists) {
            throw new AskException("Answer not exists");
        }
        // Delete all comments
        for (String commentId : answer.commentsIds) {
            dao.deleteComment(commentId);
        }
        dao.deleteAnswer(answer.id);
        dao.save(question);
    }

    public void newComment(String questionTitle, String answerID, String text, String author) throws AskException {
        Answer answer = dao.getAnswer(answerID);
        Comment comment = new Comment(answerID, text, author);
        answer.addComment(comment.id);
        dao.save(comment);
        dao.save(answer);
    }

    public void updateComment(String questionTitle, String answerID, String commentID, String text) throws AskException {
        System.out.println("Update Comment" + questionTitle + " " + answerID + " " + commentID + " " + text);
        Comment com = dao.getComment(commentID);
        com.text = text;
        dao.save(com);
    }

    public void deleteComment(String commentID, String answerID) throws AskException {
        Answer answer = dao.getAnswer(answerID);
        Boolean exists = answer.removeComment(commentID);
        if (!exists) {
            throw new AskException("Comment not exists");
        }
        dao.deleteComment(commentID);
        dao.save(answer);

    }

    public void voteUp(String questionTitle, String answerId) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.voteUp();
        dao.save(answer);
    }

    public void voteDown(String questionTitle, String answerId) throws AskException {
        Answer answer = dao.getAnswer(answerId);
        answer.voteDown();
        dao.save(answer);
    }
}
