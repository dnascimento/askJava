package pt.inesc.ask.domain;

import java.util.List;

import pt.inesc.ask.proto.AskProto;
import pt.inesc.ask.proto.AskProto.Question.Builder;

public class Question {
    private String id;
    AskProto.Question data;

    public Question(String title, List<String> tags, String questionAnswerID) {
        Builder b = AskProto.Question.newBuilder();
        this.id = title;
        this.data = b.setTitle(title).addAllTags(tags).addAnswerIds(questionAnswerID).build();
        this.id = title;
    }

    public Question(String id, AskProto.Question data) {
        this.id = id;
        this.data = data;
    }



    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return data.getTitle();
    }


    public List<String> getTags() {
        return data.getTagsList();
    }

    public List<String> getAnswersIDs() {
        return data.getAnswerIdsList();
    }

    public void addAnswer(String answerId) {
        Builder b = AskProto.Question.newBuilder(data);
        b.addAnswerIds(answerId);
        data = b.build();
    }


    public boolean removeAnswer(String answerId) {
        Builder b = AskProto.Question.newBuilder();
        b.setTitle(data.getTitle()).addAllTags(data.getTagsList());
        boolean found = false;
        for (String a : data.getAnswerIdsList()) {
            if (!a.equals(answerId))
                b.addAnswerIds(a);
            else
                found = true;
        }
        data = b.build();
        return found;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Question other = (Question) obj;
        if (!getAnswersIDs().equals(other.getAnswersIDs()))
            return false;
        if (!id.equals(other.id))
            return false;
        if (!getTags().equals(other.getTags()))
            return false;
        if (!getTitle().equals(other.getTitle()))
            return false;
        return true;
    }

    public AskProto.Question getData() {
        return data;
    }


}
