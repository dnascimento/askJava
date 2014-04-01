package pt.inesc.ask.domain;

import java.util.LinkedList;
import java.util.List;

public class Question {
    public String id;
    public String title;
    public List<String> tags;
    public LinkedList<String> answersIDs;


    public Question(String title, List<String> tags, String questionAnswerID) {
        super();
        this.id = title;
        this.title = title;
        this.tags = tags;
        this.answersIDs = new LinkedList<String>();
        this.answersIDs.addFirst(questionAnswerID);
    }


    public Question(String title, List<String> tags, LinkedList<String> answerIdsList) {
        super();
        this.id = title;
        this.title = title;
        this.tags = tags;
        this.answersIDs = answerIdsList;
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



    public List<String> getTags() {
        return tags;
    }


    public void setTags(List<String> tags) {
        this.tags = tags;
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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((answersIDs == null) ? 0 : answersIDs.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
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
        if (answersIDs == null) {
            if (other.answersIDs != null)
                return false;
        } else if (!answersIDs.equals(other.answersIDs))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (!tags.equals(other.tags))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }








}
