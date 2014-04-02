package pt.inesc.ask.servlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.inesc.ask.dao.DAO;
import pt.inesc.ask.dao.VoldemortDAO;
import pt.inesc.ask.domain.Answer;
import pt.inesc.ask.domain.AskException;
import pt.inesc.ask.domain.Comment;
import pt.inesc.ask.domain.Question;


public class AskService {
    // DAO dao = new VolatilDAO();
    DAO dao = new VoldemortDAO();

    public void newQuestion(String title, String text, List<String> tags, String author, long rid) {
        System.out.println("New Question: " + title + " " + text + " " + tags + " " + author + " rid:" + rid);

        Answer ans = new Answer(title, author, text, true);
        Question quest = new Question(title, tags, ans.id);
        dao.saveNew(quest, rid);
        dao.save(ans, rid);
        System.out.println("New question:" + title);

    }

    public List<Question> getListQuestions(long rid) throws AskException {
        System.out.println("Get Question List" + " rid:" + rid);

        return dao.getListQuestions(rid);
    }

    public Map<String, Object> getQuestionData(String questionTitle, long rid) throws AskException {
        System.out.println("Get question: " + questionTitle + " rid:" + rid);

        HashMap<String, Object> attributes = new HashMap<String, Object>();
        Question question = dao.getQuestion(questionTitle, rid);
        if (question == null) {
            throw new AskException("Question does not exists");
        }
        attributes.put("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId, rid);
            ans.cleanComments();
            for (String commentId : ans.commentsIds) {
                ans.addComment(dao.getComment(commentId, rid));
            }
            answers.addLast(ans);
        }
        attributes.put("question", answers.removeFirst());
        attributes.put("answers", answers);
        return attributes;
    }

    public void deleteQuestion(String questionTitle, long rid) throws AskException {
        System.out.println("Delete Question: " + questionTitle + " rid:" + rid);

        Question quest = dao.getQuestion(questionTitle, rid);
        // Delete all answers and comments
        for (String answerId : quest.answersIDs) {
            Answer answer = dao.getAnswer(answerId, rid);
            for (String commentId : answer.commentsIds) {
                dao.deleteComment(commentId, rid);
            }
            dao.deleteAnswer(answerId, rid);
        }
        dao.deleteQuestion(quest.id, rid);
    }

    public void newAnswer(String questionTitle, String author, String text, long rid) throws AskException {
        System.out.println("New Answer: " + questionTitle + " " + author + " " + text + " rid:" + rid);

        Question question = dao.getQuestion(questionTitle, rid);
        Answer ans = new Answer(questionTitle, author, text, false);
        question.addAnswer(ans.id);
        dao.save(ans, rid);
        dao.save(question, rid);
    }

    public void updateAnswer(String answerId, String text, long rid) throws AskException {
        System.out.println("Update Answer: " + answerId + " " + text + " rid:" + rid);

        Answer answer = dao.getAnswer(answerId, rid);
        answer.text = text;
        dao.save(answer, rid);
    }

    public void deleteAnswer(String questionTitle, String answerId, long rid) throws AskException {
        System.out.println("Delete Answer: " + questionTitle + " " + answerId + " rid:" + rid);

        Question question = dao.getQuestion(questionTitle, rid);
        Answer answer = dao.getAnswer(answerId, rid);
        Boolean exists = question.removeAnswer(answerId);
        if (!exists) {
            throw new AskException("Answer not exists");
        }
        // Delete all comments
        for (String commentId : answer.commentsIds) {
            dao.deleteComment(commentId, rid);
        }
        dao.deleteAnswer(answer.id, rid);
        dao.save(question, rid);
    }

    public void newComment(String questionTitle, String answerID, String text, String author, long rid) throws AskException {
        System.out.println("New Comment: " + questionTitle + " " + answerID + " " + text + " " + author + " rid:" + rid);
        Answer answer = dao.getAnswer(answerID, rid);
        Comment comment = new Comment(answerID, text, author);
        answer.addComment(comment.id);
        dao.save(comment, rid);
        dao.save(answer, rid);
    }

    public void updateComment(String questionTitle, String answerID, String commentID, String text, long rid) throws AskException {
        System.out.println("Update Comment" + questionTitle + " " + answerID + " " + commentID + " " + text + " rid:"
                + rid);
        Comment com = dao.getComment(commentID, rid);
        com.text = text;
        dao.save(com, rid);
    }

    public void deleteComment(String commentID, String answerID, long rid) throws AskException {
        System.out.println("Delete Comment: " + commentID + " " + answerID + " rid:" + rid);
        Answer answer = dao.getAnswer(answerID, rid);
        Boolean exists = answer.removeComment(commentID);
        if (!exists) {
            throw new AskException("Comment not exists");
        }
        dao.deleteComment(commentID, rid);
        dao.save(answer, rid);

    }

    public void voteUp(String questionTitle, String answerId, long rid) throws AskException {
        System.out.println("VoteUp: " + questionTitle + " " + answerId + " rid:" + rid);
        Answer answer = dao.getAnswer(answerId, rid);
        answer.voteUp();
        dao.save(answer, rid);
    }

    public void voteDown(String questionTitle, String answerId, long rid) throws AskException {
        System.out.println("VoteDown: " + questionTitle + " " + answerId + " rid:" + rid);
        Answer answer = dao.getAnswer(answerId, rid);
        answer.voteDown();
        dao.save(answer, rid);
    }
}
