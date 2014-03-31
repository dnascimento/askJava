package pt.inesc.ask.domain;

import java.util.LinkedList;
import java.util.List;

public class Answer {
    public String id;
    public String author;
    public String text;
    public Boolean isQuestion;
    public int votes = 0;
    public List<String> commentsIds;
    private final LinkedList<Comment> comments = new LinkedList<Comment>();

    public Answer(String author, String text, Boolean isQuestion) {
        this(author, text, isQuestion, text + author);
    }


    public LinkedList<Comment> getComments() {
        return comments;
    }

    public Answer() {

    }

    public Answer(String author, String text, Boolean isQuestion, String id) {
        super();
        // TODO
        /**
         * m = hashlib.md5()
         * m.update(text+author+answerID)
         * self._id = "com_"+m.hexdigest()
         */
        this.id = id;
        this.author = author;
        this.text = text;
        this.isQuestion = isQuestion;
        this.commentsIds = new LinkedList<String>();
    }



    public void addComment(String commentId) {
        commentsIds.add(commentId);
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


    public int getVotes() {
        return votes;
    }


    public void setVotes(int votes) {
        this.votes = votes;
    }


    public String getId() {
        return id;
    }


    public String getAuthor() {
        return author;
    }


    public String getText() {
        return text;
    }


    public Boolean getIsQuestion() {
        return isQuestion;
    }


    public List<String> getCommentsIds() {
        return commentsIds;
    }


    @Override
    public String toString() {
        return "Answer [id=" + id + ", author=" + author + ", text=" + text + ", isQuestion=" + isQuestion + ", votes="
                + votes + ", commentsIds=" + commentsIds + "]";
    }


    public void addComment(Comment comment) {
        if (comment != null)
            comments.addFirst(comment);
    }







}
