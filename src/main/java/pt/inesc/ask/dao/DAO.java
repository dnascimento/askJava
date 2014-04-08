package pt.inesc.ask.dao;

import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import voldemort.versioning.Version;

// Data access object: access to database and conversion
public interface DAO {

    // Save
    public Version save(Question quest, long rid);

    public Version save(Answer answer, long rid);

    public Version save(Comment comment, long rid);

    // Delete
    public boolean deleteQuestion(String questionId, long rid);

    public boolean deleteAnswer(String answerId, long rid);

    public boolean deleteComment(String commentId, long rid);

    // Gets
    public Question getQuestion(String questionTitle, long rid) throws AskException;

    public Answer getAnswer(String answerId, long rid) throws AskException;

    public Comment getComment(String commentId, long rid) throws AskException;

    public List<Question> getListQuestions(long rid, String tag) throws AskException;

    Version saveNew(Question quest, long rid) throws AskException;

    void cleanIndex(long rid);
}
