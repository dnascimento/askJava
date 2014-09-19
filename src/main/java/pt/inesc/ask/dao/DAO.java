package pt.inesc.ask.dao;

import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import voldemort.undoTracker.SRD;
import voldemort.versioning.Version;

// Data access object: access to database and conversion
public interface DAO {

    // Save
    public Version save(Question quest, SRD srd);

    public Version save(Answer answer, SRD srd);

    public Version save(Comment comment, SRD srd);

    // Delete
    public boolean deleteQuestion(String questionId, SRD srd);

    public boolean deleteAnswer(String answerId, SRD srd) throws AskException;

    public boolean deleteComment(String commentId, SRD srd) throws AskException;

    // Gets
    public Question getQuestion(String questionTitle, SRD srd) throws AskException;

    public Answer getAnswer(String answerId, SRD srd) throws AskException;

    public Comment getComment(String commentId, SRD srd) throws AskException;

    public List<QuestionEntry> getListQuestions(SRD srd, String tag) throws AskException;

    Version saveNew(Question quest, SRD srd) throws AskException;

    void cleanIndex(SRD srd);

    public List<String> getTags();
}
