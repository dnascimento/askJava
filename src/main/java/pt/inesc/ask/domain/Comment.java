package pt.inesc.ask.domain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.appengine.repackaged.com.google.common.io.BaseEncoding;

public class Comment {
    public String id;
    public String text;
    public String author;

    public Comment(String answerID, String text, String author) {
        this.text = text;
        this.author = author;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((answerID + author + text).getBytes());
            this.id = BaseEncoding.base64().encode(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


}
