package pt.inesc.ask;

import java.util.List;

import pt.inesc.ask.domain.Question;

public class AskService {
    private final long reqId = 0;


    public void addNewQuestion(
            String title,
                String text,
                List<String> tags,
                String author,
                long rid) {

    }

    public Question getQuestion(String title, long rid) {
        // return db.getQuestion("quest_" + title, rid);
        return null;

    }

    public void deleteQuestion(String title, long rid) {
        // db.delete("quest_" + title, rid);
    }

    public List<Question> getQuestionList(int maxQuestion, long rid) {
        return null;
    }

    // Answers
    public void addAnswer(String questionTitle, String text, String author, long rid) {

    }

    public void updateAnswer(String questionTitle, String answerID, String text, long rid) {

    }

    public void delAnswer(String questionTitle, String answerID, long rid) {

    }

    // Comment
    public void addComment(String questionTitle, String answerID, String author, long rid) {

    }

    public void deleteComment(
            String questionTitle,
                String answerID,
                String commentID,
                long rid) {

    }

    public void updateComment(
            String questionTitle,
                String answerID,
                String commentID,
                String text,
                long rid) {

    }

    // Vote
    public void voteUp(String questionTitle, String answerID, long rid) {

    }

    public void voteDowm(String questionTitle, String answerID, long rid) {

    }


}
