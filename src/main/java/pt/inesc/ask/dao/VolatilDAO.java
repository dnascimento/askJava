package pt.inesc.ask.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;
import voldemort.versioning.Version;

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
        Question q = new Question("onde estou", tags, a.getId());
        Comment c = new Comment(a.getId(), "oi", "kiko");
        a.addComment(c.getId());
        save(a, 0L);
        save(q, 0L);
        save(c, 0L);
    }

    // Save
    @Override
    public Version save(Question quest, long rid) {
        questions.put(quest.getId(), quest);
        return null;
    }

    @Override
    public Version save(Answer answer, long rid) {
        answers.put(answer.getId(), answer);
        return null;
    }

    @Override
    public Version save(Comment comment, long rid) {
        comments.put(comment.getId(), comment);
        return null;
    }

    // Delete
    @Override
    public boolean deleteQuestion(String questionId, long rid) {
        Question q = questions.remove(questionId);
        if (q == null) {
            System.err.println("Question not exists:" + questionId);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAnswer(String answerId, long rid) {
        Answer a = answers.remove(answerId);
        if (a == null) {
            System.err.println("Answer not exists:" + answerId);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteComment(String commentId, long rid) {
        Comment c = comments.remove(commentId);
        if (c == null) {
            System.err.println("Comment not exists:" + commentId);
            return false;
        }
        return true;
    }

    // Gets
    @Override
    public Question getQuestion(String questionTitle, long rid) {
        Question q = questions.get(questionTitle);
        if (q == null) {
            System.err.println("Question not exists: " + questionTitle);
        }
        return q;
    }

    @Override
    public Answer getAnswer(String answerId, long rid) {
        Answer a = answers.get(answerId);
        if (a == null) {
            System.err.println("Answer not exists: " + answerId);
        }
        return a;
    }

    @Override
    public Comment getComment(String commentId, long rid) {
        Comment c = comments.get(commentId);
        if (c == null) {
            System.err.println("Comment not exists: " + commentId);
        }
        return c;
    }


    @Override
    public LinkedList<Question> getListQuestions(long rid) {
        return new LinkedList<Question>(questions.values());
    }

    @Override
    public Version saveNew(Question quest, long rid) {
        return save(quest, rid);
    }

    @Override
    public void cleanIndex(long rid) {
        questions.clear();
    }
}
