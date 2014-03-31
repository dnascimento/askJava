package pt.inesc.ask.dao;

import java.util.HashMap;
import java.util.LinkedList;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;

// Data access object: access to database and convertion
public class DAO {
    HashMap<String, Question> questions = new HashMap<String, Question>();
    HashMap<String, Answer> answers = new HashMap<String, Answer>();
    HashMap<String, Comment> comments = new HashMap<String, Comment>();
    public static DAO singleton = new DAO();

    public static DAO getDAO() {
        return singleton;
    }

    private DAO() {
        Answer a = new Answer("onde estou", "dario", "o que e isto", true);
        Question q = new Question("onde estou", new String[] { "perdido" }, a.id);
        Comment c = new Comment(a.id, "parvo", "maike");
        a.addComment(c.id);
        save(a);
        save(q);
        save(c);
        // TODO
        // // In real life this stuff would get wired in
        // String bootstrapUrl = "tcp://localhost:6666";
        // StoreClientFactory factory = new SocketStoreClientFactory(new
        // ClientConfig().setBootstrapUrls(bootstrapUrl));
        //
        // StoreClient<String, Question> client = factory.getStoreClient("my_store_name");
        //
        // // get the value
        // Versioned<Question> version = client.get("some_key");
        //
        // // modify the value
        // // version.setObject(new Questionion());
        //
        // // update the value
        // client.put("some_key", version);
    }

    // Save
    public void save(Question quest) {
        questions.put(quest.id, quest);
    }

    public void save(Answer answer) {
        answers.put(answer.id, answer);
    }

    public void save(Comment comment) {
        comments.put(comment.id, comment);
    }

    // Delete
    public void deleteQuestion(String questionId) throws AskException {
        Question q = questions.remove(questionId);
        if (q == null) {
            throw new AskException("Question not exists:" + questionId);
        }
    }

    public void deleteAnswer(String answerId) throws AskException {
        Answer a = answers.remove(answerId);
        if (a == null) {
            throw new AskException("Answer not exists:" + answerId);
        }
    }

    public void deleteComment(String commentId) throws AskException {
        Comment c = comments.remove(commentId);
        if (c == null) {
            throw new AskException("Comment not exists:" + commentId);
        }
    }

    // Gets
    public Question getQuestion(String questionTitle) throws AskException {
        Question q = questions.get(questionTitle);
        if (q == null) {
            throw new AskException("Question not exists: " + questionTitle);
        }
        return q;
    }

    public Answer getAnswer(String answerId) throws AskException {
        Answer a = answers.get(answerId);
        if (a == null) {
            throw new AskException("Answer not exists: " + answerId);
        }
        return a;
    }

    public Comment getComment(String commentId) throws AskException {
        Comment c = comments.get(commentId);
        if (c == null) {
            throw new AskException("Comment not exists: " + commentId);
        }
        return c;
    }


    public LinkedList<Question> getListQuestions() {
        return new LinkedList<Question>(questions.values());
    }
}
