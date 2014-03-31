package pt.inesc.ask;

import java.util.List;

import pt.inesc.ask.domain.Question;


public class AskService {
    private final long reqId = 0;


    public void addNewQuestionion(
            String title,
                String text,
                List<String> tags,
                String author,
                long rid) {

    }

    public Question getQuestionion(String title, long rid) {
        // return db.getQuestionion("Question_" + title, rid);
        return null;

    }

    public void deleteQuestionion(String title, long rid) {
        // db.delete("Question_" + title, rid);
    }

    public List<Question> getQuestionionList(int maxQuestionion, long rid) {
        return null;
    }

    // Answers
    public void addAnswer(String QuestionionTitle, String text, String author, long rid) {

    }

    public void updateAnswer(
            String QuestionionTitle,
                String answerID,
                String text,
                long rid) {

    }

    public void delAnswer(String QuestionionTitle, String answerID, long rid) {

    }

    // Comment
    public void addComment(
            String QuestionionTitle,
                String answerID,
                String author,
                long rid) {

    }

    public void deleteComment(
            String QuestionionTitle,
                String answerID,
                String commentID,
                long rid) {

    }

    public void updateComment(
            String QuestionionTitle,
                String answerID,
                String commentID,
                String text,
                long rid) {

    }

    // Vote
    public void voteUp(String QuestionionTitle, String answerID, long rid) {

    }

    public void voteDowm(String QuestionionTitle, String answerID, long rid) {

    }


}
