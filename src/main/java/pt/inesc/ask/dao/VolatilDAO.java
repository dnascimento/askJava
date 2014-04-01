package pt.inesc.ask.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;

// Data access object: access to database and convertion
public class VolatilDAO
        implements DAO {
    HashMap<String, Question> questions = new HashMap<String, Question>();
    HashMap<String, Answer> answers = new HashMap<String, Answer>();
    HashMap<String, Comment> comments = new HashMap<String, Comment>();



    public VolatilDAO() {
        Answer a = new Answer("onde estou", "dario", "o que e isto", true);
        ArrayList<String> tags = new ArrayList<String>();
        tags.add("perdido");
        Question q = new Question("onde estou", tags, a.id);
        Comment c = new Comment(a.id, "parvo", "maike");
        a.addComment(c.id);
        save(a, "0");
        save(q, "0");
        save(c, "0");
    }

    // Save
    @Override
    public void save(Question quest, String rid) {
        questions.put(quest.id, quest);
    }

    @Override
    public void save(Answer answer, String rid) {
        answers.put(answer.id, answer);
    }

    @Override
    public void save(Comment comment, String rid) {
        comments.put(comment.id, comment);
    }

    // Delete
    @Override
    public void deleteQuestion(String questionId, String rid) throws AskException {
        Question q = questions.remove(questionId);
        if (q == null) {
            throw new AskException("Question not exists:" + questionId);
        }
    }

    @Override
    public void deleteAnswer(String answerId, String rid) throws AskException {
        Answer a = answers.remove(answerId);
        if (a == null) {
            throw new AskException("Answer not exists:" + answerId);
        }
    }

    @Override
    public void deleteComment(String commentId, String rid) throws AskException {
        Comment c = comments.remove(commentId);
        if (c == null) {
            throw new AskException("Comment not exists:" + commentId);
        }
    }

    // Gets
    @Override
    public Question getQuestion(String questionTitle, String rid) throws AskException {
        Question q = questions.get(questionTitle);
        if (q == null) {
            throw new AskException("Question not exists: " + questionTitle);
        }
        return q;
    }

    @Override
    public Answer getAnswer(String answerId, String rid) throws AskException {
        Answer a = answers.get(answerId);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return a;
    }

    @Override
    public Comment getComment(String commentId, String rid) throws AskException {
        Comment c = comments.get(commentId);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return c;
    }


    @Override
    public LinkedList<Question> getListQuestions(String rid) {
        return new LinkedList<Question>(questions.values());
    }

    @Override
    public void saveNew(Question quest, String rid) {
        save(quest, rid);
    }
}
