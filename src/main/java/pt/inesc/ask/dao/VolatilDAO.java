package pt.inesc.ask.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import pt.inesc.ask.domain.QuestionEntry;
import voldemort.undoTracker.SRD;
import voldemort.versioning.Version;

// Data access object: access to database and conversation
public class VolatilDAO
        implements DAO {
    HashMap<String, Question> questions = new HashMap<String, Question>();
    HashMap<String, Answer> answers = new HashMap<String, Answer>();
    HashMap<String, Comment> comments = new HashMap<String, Comment>();
    ArrayList<String> tags = new ArrayList<String>();



    public VolatilDAO() {
        Answer a = new Answer("onde estou", "dario", "o que e isto", true, null);
        tags.add("perdido");
        Question q = new Question("onde estou", tags, "1", "1", a.getId());
        Comment c = new Comment(a.getId(), "oi", "kiko");
        a.addComment(c.getId());
        save(a, new SRD());
        save(q, new SRD());
        save(c, new SRD());
    }

    // Save
    @Override
    public Version save(Question quest, SRD srd) {
        questions.put(quest.getId(), quest);
        return null;
    }

    @Override
    public Version save(Answer answer, SRD srd) {
        answers.put(answer.getId(), answer);
        return null;
    }

    @Override
    public Version save(Comment comment, SRD srd) {
        comments.put(comment.getId(), comment);
        return null;
    }

    // Delete
    @Override
    public boolean deleteQuestion(String questionId, SRD srd) {
        Question q = questions.remove(questionId);
        if (q == null) {
            System.err.println("Question not exists:" + questionId);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAnswer(String answerId, SRD srd) {
        Answer a = answers.remove(answerId);
        if (a == null) {
            System.err.println("Answer not exists:" + answerId);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteComment(String commentId, SRD srd) {
        Comment c = comments.remove(commentId);
        if (c == null) {
            System.err.println("Comment not exists:" + commentId);
            return false;
        }
        return true;
    }

    // Gets
    @Override
    public Question getQuestion(String questionTitle, SRD srd) {
        Question q = questions.get(questionTitle);
        if (q == null) {
            System.err.println("Question not exists: " + questionTitle);
        }
        return q;
    }

    @Override
    public Answer getAnswer(String answerId, SRD srd) {
        Answer a = answers.get(answerId);
        if (a == null) {
            System.err.println("Answer not exists: " + answerId);
        }
        return a;
    }

    @Override
    public Comment getComment(String commentId, SRD srd) {
        Comment c = comments.get(commentId);
        if (c == null) {
            System.err.println("Comment not exists: " + commentId);
        }
        return c;
    }


    @Override
    public List<QuestionEntry> getListQuestions(SRD srd, String tag) {
        LinkedList<QuestionEntry> list = new LinkedList<QuestionEntry>();
        for (Question e : questions.values()) {
            list.add(new QuestionEntry(e.getTitle()));
        }
        return list;
    }

    @Override
    public Version saveNew(Question quest, SRD srd) {
        return save(quest, srd);
    }

    @Override
    public void cleanIndex(SRD srd) {
        questions.clear();
    }

    @Override
    public List<String> getTags() {
        return tags;
    }
}
