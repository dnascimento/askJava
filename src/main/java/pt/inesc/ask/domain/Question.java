package pt.inesc.ask.domain;

import java.util.LinkedList;
import java.util.List;

import pt.inesc.ask.proto.AskProto;
import pt.inesc.ask.proto.AskProto.Question.Builder;
import pt.inesc.ask.servlet.AskService;

public class Question {
    private String id;
    AskProto.Question data;

    public Question(String title, List<String> tags, String views, String answers, String questionAnswerID) {
        Builder b = AskProto.Question.newBuilder();
        this.id = title;
        this.data = b.setTitle(title).addAllTags(tags).addAnswerIds(questionAnswerID).setViews(views).setAnswers(answers).build();
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
        return AskService.decodeTitle(data.getTitle());
    }

    public String getUrl() {
        return data.getTitle();
    }


    public String getViews() {
        return data.getViews();
    }

    public String getAnswers() {
        return data.getAnswers();
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
        b.setTitle(data.getTitle()).addAllTags(data.getTagsList()).setViews(data.getViews());

        boolean found = false;
        b.addAllAnswerIds(new LinkedList<String>());
        if (data.getAnswerIdsList() != null) {
            for (String e : data.getAnswerIdsList()) {
                if (e == answerId) {
                    found = true;
                } else {
                    b.addAnswerIds(e);
                }
            }
        }
        b.setAnswers(data.getAnswers());

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
        if (!getUrl().equals(other.getUrl()))
            return false;
        return true;
    }

    public AskProto.Question getData() {
        return data;
    }


    @Override
    public String toString() {
        return getUrl();
    }



}
