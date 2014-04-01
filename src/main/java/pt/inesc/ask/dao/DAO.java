package pt.inesc.ask.dao;

import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;

// Data access object: access to database and conversion
public interface DAO {

    // Save
    public void save(Question quest, String rid);

    public void save(Answer answer, String rid);

    public void save(Comment comment, String rid);

    // Delete
    public void deleteQuestion(String questionId, String rid) throws AskException;

    public void deleteAnswer(String answerId, String rid) throws AskException;

    public void deleteComment(String commentId, String rid) throws AskException;

    // Gets
    public Question getQuestion(String questionTitle, String rid) throws AskException;

    public Answer getAnswer(String answerId, String rid) throws AskException;

    public Comment getComment(String commentId, String rid) throws AskException;

    public List<Question> getListQuestions(String rid) throws AskException;

    void saveNew(Question quest, String rid);
}
