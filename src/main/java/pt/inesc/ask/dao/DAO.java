package pt.inesc.ask.dao;

import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import voldemort.undoTracker.RUD;
import voldemort.versioning.Version;

// Data access object: access to database and conversion
public interface DAO {

    // Save
    public Version save(Question quest, RUD rud);

    public Version save(Answer answer, RUD rud);

    public Version save(Comment comment, RUD rud);

    // Delete
    public boolean deleteQuestion(String questionId, RUD rud);

    public boolean deleteAnswer(String answerId, RUD rud);

    public boolean deleteComment(String commentId, RUD rud);

    // Gets
    public Question getQuestion(String questionTitle, RUD rud) throws AskException;

    public Answer getAnswer(String answerId, RUD rud) throws AskException;

    public Comment getComment(String commentId, RUD rud) throws AskException;

    public List<Question> getListQuestions(RUD rud, String tag) throws AskException;

    Version saveNew(Question quest, RUD rud) throws AskException;

    void cleanIndex(RUD rud);
}
