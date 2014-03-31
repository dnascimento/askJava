package pt.inesc.ask.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.repackaged.com.google.common.io.BaseEncoding;

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









}
