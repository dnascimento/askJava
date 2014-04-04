package pt.inesc.ask.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import pt.inesc.ask.proto.AskProto;

import com.google.common.io.BaseEncoding;

public class Answer {
    AskProto.Answer data;
    private String id;
    // To display the comments
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
        AskProto.Answer.Builder b = AskProto.Answer.newBuilder();
        b.setAuthor(author).setText(text).setVotes(0).setIsQuestion(isQuestion);
        data = b.build();
    }

    public Answer(String id, AskProto.Answer data) {
        this.id = id;
        this.data = data;
    }

    // To display
    public void addComment(Comment comment) {
        if (comment != null)
            comments.addLast(comment);
    }

    // To list of comments
    public void addComment(String commentId) {
        AskProto.Answer.Builder b = AskProto.Answer.newBuilder();
        b.setAuthor(data.getAuthor())
         .setText(data.getText())
         .setVotes(data.getVotes())
         .setIsQuestion(data.getIsQuestion())
         .addAllCommentIds(data.getCommentIdsList());
        b.addCommentIds(commentId);
        data = b.build();
    }

    public Boolean removeComment(String commentId) {
        List<String> oldList = data.getCommentIdsList();
        boolean found = false;
        AskProto.Answer.Builder b = AskProto.Answer.newBuilder();
        b.setAuthor(data.getAuthor())
         .setText(data.getText())
         .setVotes(data.getVotes())
         .setIsQuestion(data.getIsQuestion());

        for (String e : oldList) {
            if (e.equals(commentId)) {
                found = true;
            } else {
                b.addCommentIds(e);
            }
        }
        data = b.build();
        return found;
    }

    public void setText(String text) {
        newData(data.getAuthor(), text, data.getVotes(), data.getIsQuestion(), data.getCommentIdsList());
    }

    private void vote(int votes) {
        data.getVotes();
        newData(data.getAuthor(),
                data.getText(),
                data.getVotes() + votes,
                data.getIsQuestion(),
                data.getCommentIdsList());
    }

    private void newData(String author, String text, int votes, boolean isQuestion, List<String> commentIds) {
        AskProto.Answer.Builder b = AskProto.Answer.newBuilder();
        b.setAuthor(author).setText(text).setVotes(votes).setIsQuestion(isQuestion).addAllCommentIds(commentIds);
        data = b.build();
    }





    public void voteUp() {
        vote(+1);
    }

    public void voteDown() {
        vote(-1);
    }



    @Override
    public String toString() {
        return "Answer [id=" + id + ", author=" + getAuthor() + ", text=" + getText() + ", isQuestion="
                + getIsQuestion() + ", votes=" + getVotes() + ", commentsIds=" + getCommentsIds() + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return data.getAuthor();
    }


    public String getText() {
        return data.getText();
    }



    public Boolean getIsQuestion() {
        return data.getIsQuestion();
    }


    public int getVotes() {
        return data.getVotes();
    }

    public List<String> getCommentsIds() {
        return data.getCommentIdsList();
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
        String author = getAuthor();
        List<String> commentsIds = getCommentsIds();
        Boolean isQuestion = getIsQuestion();
        String text = getText();
        int votes = getVotes();
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

        if (!getAuthor().equals(other.getAuthor()))
            return false;
        if (!comments.equals(other.comments))
            return false;
        if (!getCommentsIds().equals(other.getCommentsIds()))
            return false;
        if (!id.equals(other.id))
            return false;

        if (!getIsQuestion().equals(other.getIsQuestion()))
            return false;
        if (!getText().equals(other.getText()))
            return false;
        if (getVotes() != other.getVotes())
            return false;
        return true;
    }

    public AskProto.Answer getData() {
        return data;
    }


}
