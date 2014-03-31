package pt.inesc.ask.dao;

import java.util.HashMap;
import java.util.LinkedList;

import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;

// Data access object: access to database and convertion
public class DAO {
    HashMap<String, Question> questions = new HashMap<String, Question>();
    HashMap<String, Answer> answers = new HashMap<String, Answer>();
    HashMap<String, Comment> comments = new HashMap<String, Comment>();

    public DAO() {
        save(new Question("onde estou", new String[] { "perdido" }, "lost"));
        Answer ans = new Answer("dario", "Ondes estou pah", true, "lost");
        ans.addComment("com");
        save(ans);
        save(new Comment("com", "esta fixe", "dario"));
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
    public Boolean deleteQuestion(String questionId) {
        Question q = questions.remove(questionId);
        return (q == null) ? false : true;
    }

    public void deleteAnswer(String answerId) {
        answers.remove(answerId);
    }

    public void deleteComment(String commentId) {
        comments.remove(commentId);
    }

    // Gets
    public Question getQuestion(String questionTitle) {
        return questions.get(questionTitle);
    }

    public Answer getAnswer(String answerId) {
        return answers.get(answerId);
    }

    public Comment getComment(String commentId) {
        return comments.get(commentId);
    }


    public LinkedList<Question> getListQuestions() {
        return new LinkedList<Question>(questions.values());
    }
}
