package pt.inesc.ask.domain;

import java.util.LinkedList;

public class Question {
    public String id;
    public String title;
    public String[] tags;
    public String questionAnswerID;
    public LinkedList<String> answersIDs;


    public Question(String title, String[] tags, String questionAnswerID) {
        super();
        this.id = title;
        this.title = title;
        this.tags = tags;
        this.answersIDs = new LinkedList<String>();
        this.answersIDs.addFirst(questionAnswerID);
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String[] getTags() {
        return tags;
    }


    public void setTags(String[] tags) {
        this.tags = tags;
    }


    public String getQuestionAnswerID() {
        return questionAnswerID;
    }


    public void setQuestionAnswerID(String questionAnswerID) {
        this.questionAnswerID = questionAnswerID;
    }


    public LinkedList<String> getAnswersIDs() {
        return answersIDs;
    }


    public void setAnswersIDs(LinkedList<String> answersIDs) {
        this.answersIDs = answersIDs;
    }


    public void addAnswer(String answerId) {
        answersIDs.addLast(answerId);
    }


    public boolean removeAnswer(String answerId) {
        return answersIDs.remove(answerId);
    }







}
