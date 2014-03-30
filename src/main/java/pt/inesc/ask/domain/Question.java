package pt.inesc.ask.domain;

import java.util.LinkedList;
import java.util.List;

public class Question {
    public final String id;
    public final String title;
    public final String[] tags;
    public final String questionAnswerID;
    public final List<String> answersIDs;
    public final String question_id;

    public Question(String title, String[] tags) {
        super();
        this.id = "quest_" + title;
        this.title = title;
        this.tags = tags;
        // TODO criar a answer e colocar o id
        this.questionAnswerID = "answer";
        this.answersIDs = new LinkedList<String>();
        this.question_id = "test";
    }




}
