package pt.inesc.ask.dao;

import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;

// Data access object: access to database and conversion
public interface DAO {

    // Save
    public void save(Question quest, long rid);

    public void save(Answer answer, long rid);

    public void save(Comment comment, long rid);

    // Delete
    public void deleteQuestion(String questionId, long rid) throws AskException;

    public void deleteAnswer(String answerId, long rid) throws AskException;

    public void deleteComment(String commentId, long rid) throws AskException;

    // Gets
    public Question getQuestion(String questionTitle, long rid) throws AskException;

    public Answer getAnswer(String answerId, long rid) throws AskException;

    public Comment getComment(String commentId, long rid) throws AskException;

    public List<Question> getListQuestions(long rid) throws AskException;

    void saveNew(Question quest, long rid);
}
