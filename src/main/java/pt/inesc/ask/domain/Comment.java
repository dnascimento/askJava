package pt.inesc.ask.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang.StringEscapeUtils;

import pt.inesc.ask.proto.AskProto;

import com.google.common.io.BaseEncoding;

public class Comment {
    AskProto.Comment data;
    private String id;

    public Comment(String answerID, String text, String author) {
        AskProto.Comment.Builder b = AskProto.Comment.newBuilder();
        b.setText(text).setAuthor(author);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((answerID + author + text).getBytes());
            this.id = BaseEncoding.base64().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        data = b.build();
    }

    public void setText(String text) {
        AskProto.Comment.Builder b = AskProto.Comment.newBuilder();
        b.setAuthor(data.getAuthor()).setText(text);
        data = b.build();
    }


    public Comment(String id, AskProto.Comment data) {
        this.id = id;
        this.data = data;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return StringEscapeUtils.unescapeHtml(data.getText());
    }


    public String getAuthor() {
        return data.getAuthor();
    }




    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        String author = data.getAuthor();
        String text = data.getText();
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
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
        Comment other = (Comment) obj;
        if (!getAuthor().equals(other.getAuthor()))
            return false;
        if (!id.equals(other.id))
            return false;
        if (!getText().equals(other.getText()))
            return false;
        return true;
    }

    public AskProto.Comment getData() {
        return data;
    }





}
