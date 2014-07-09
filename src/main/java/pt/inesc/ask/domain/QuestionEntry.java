package pt.inesc.ask.domain;

import pt.inesc.ask.servlet.AskService;

public class QuestionEntry {

    public final String id;

    public QuestionEntry(String str) {
        this.id = str;
    }

    public String getTitle() {
        return AskService.decodeTitle(id);
    }

    public String getUrl() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        QuestionEntry other = (QuestionEntry) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "QuestionEntry [" + id + "]";
    }






}
