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

    public void newQuestion(String title, String text, List<String> tags, String author, long rid) throws AskException {
        System.out.println("New Question: " + title + " " + text + " " + tags + " " + author + " rid:" + rid);
        Answer ans = new Answer(title, author, text, true);
        Question quest = new Question(title, tags, ans.getId());
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
        attributes.put("questionData", question);
        List<String> answerIds = question.getAnswersIDs();
        LinkedList<Answer> answers = new LinkedList<Answer>();
        for (String answerId : answerIds) {
            Answer ans = dao.getAnswer(answerId, rid);
            ans.cleanComments();
            for (String commentId : ans.getCommentsIds()) {
                ans.addComment(dao.getComment(commentId, rid));
            }
            answers.addLast(ans);
        }
        attributes.put("question", answers.removeFirst());
        attributes.put("answers", answers);
        return attributes;
    }

    public boolean deleteQuestion(String questionTitle, long rid) throws AskException {
        System.out.println("Delete Question: " + questionTitle + " rid:" + rid);
        boolean c = true, a = true, q = true;
        Question quest = dao.getQuestion(questionTitle, rid);

        // Delete all answers and comments
        for (String answerId : quest.getAnswersIDs()) {
            try {
                Answer answer = dao.getAnswer(answerId, rid);
                for (String commentId : answer.getCommentsIds()) {
                    c = c && dao.deleteComment(commentId, rid);
                }
                a = a && dao.deleteAnswer(answerId, rid);
            } catch (AskException e) {
                a = false;
                System.err.println(e);
            }
        }
        q = dao.deleteQuestion(quest.getId(), rid);
        return c && a && q;
    }

    public String newAnswer(String questionTitle, String author, String text, long rid) throws AskException {
        System.out.println("New Answer: " + questionTitle + " " + author + " " + text + " rid:" + rid);
        Question question = dao.getQuestion(questionTitle, rid);
        Answer ans = new Answer(questionTitle, author, text, false);
        question.addAnswer(ans.getId());
        dao.save(ans, rid);
        dao.save(question, rid);
        return ans.getId();
    }

    public void updateAnswer(String answerId, String text, long rid) throws AskException {
        System.out.println("Update Answer: " + answerId + " " + text + " rid:" + rid);
        Answer answer = dao.getAnswer(answerId, rid);
        if (answer == null) {
            throw new AskException("Update Answer: answer not exists:" + answerId);
        }
        answer.setText(text);
        dao.save(answer, rid);
    }

    public boolean deleteAnswer(String questionTitle, String answerId, long rid) throws AskException {
        System.out.println("Delete Answer: " + questionTitle + " " + answerId + " rid:" + rid);
        Question question = dao.getQuestion(questionTitle, rid);
        Answer answer = dao.getAnswer(answerId, rid);
        boolean c = true, a = true;
        a = question.removeAnswer(answerId);
        // Delete all comments
        for (String commentId : answer.getCommentsIds()) {
            c = c && dao.deleteComment(commentId, rid);
        }
        a = a && dao.deleteAnswer(answer.getId(), rid);
        dao.save(question, rid);
        return a && c;
    }

    public String newComment(String questionTitle, String answerID, String text, String author, long rid) throws AskException {
        System.out.println("New Comment: " + questionTitle + " " + answerID + " " + text + " " + author + " rid:" + rid);
        Answer answer = dao.getAnswer(answerID, rid);
        Comment comment = new Comment(answerID, text, author);
        answer.addComment(comment.getId());
        dao.save(comment, rid);
        dao.save(answer, rid);
        return comment.getId();
    }

    public void updateComment(String questionTitle, String answerID, String commentID, String text, long rid) throws AskException {
        System.out.println("Update Comment" + questionTitle + " " + answerID + " " + commentID + " " + text + " rid:"
                + rid);
        Comment com = dao.getComment(commentID, rid);
        com.setText(text);
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

    public DAO getDao() {
        return dao;
    }
}
