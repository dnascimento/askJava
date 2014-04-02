package pt.inesc.ask.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.google.common.io.BaseEncoding;
 
public class Answer {
    public String id;
    public String author;
    public String text;
    public Boolean isQuestion;
    public int votes = 0;
    public LinkedList<String> commentsIds;
    private final LinkedList<Comment> comments = new LinkedList<Comment>();

    public Answer(String questionTitle, String author, String text, Boolean isQuestion) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((questionTitle + author + text).getBytes());
            this.id = BaseEncoding.base64().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.author = author;
        this.text = text;
        this.isQuestion = isQuestion;
        this.commentsIds = new LinkedList<String>();
    }

    public Answer(String id, String author, String text, Boolean isQuestion, int votes, LinkedList<String> commentsIds) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.isQuestion = isQuestion;
        this.commentsIds = commentsIds;
        this.votes = votes;
    }

    public void addComment(Comment comment) {
        if (comment != null)
            comments.addLast(comment);
    }


    public void addComment(String commentId) {
        commentsIds.addLast(commentId);
    }

    public Boolean removeComment(String commentId) {
        return commentsIds.remove(commentId);
    }

    public void voteUp() {
        this.votes++;

    }

    public void voteDown() {
        this.votes--;

    }



    @Override
    public String toString() {
        return "Answer [id=" + id + ", author=" + author + ", text=" + text + ", isQuestion=" + isQuestion + ", votes="
                + votes + ", commentsIds=" + commentsIds + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getIsQuestion() {
        return isQuestion;
    }

    public void setIsQuestion(Boolean isQuestion) {
        this.isQuestion = isQuestion;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public List<String> getCommentsIds() {
        return commentsIds;
    }

    public void setCommentsIds(LinkedList<String> commentsIds) {
        this.commentsIds = commentsIds;
    }

    public LinkedList<Comment> getComments() {
        return comments;
    }

    public void cleanComments() {
        comments.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((commentsIds == null) ? 0 : commentsIds.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((isQuestion == null) ? 0 : isQuestion.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + votes;
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
        Answer other = (Answer) obj;
        if (author == null) {
            if (other.author != null)
                return false;
        } else if (!author.equals(other.author))
            return false;
        if (comments == null) {
            if (other.comments != null)
                return false;
        } else if (!comments.equals(other.comments))
            return false;
        if (commentsIds == null) {
            if (other.commentsIds != null)
                return false;
        } else if (!commentsIds.equals(other.commentsIds))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (isQuestion == null) {
            if (other.isQuestion != null)
                return false;
        } else if (!isQuestion.equals(other.isQuestion))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (votes != other.votes)
            return false;
        return true;
    }









}
