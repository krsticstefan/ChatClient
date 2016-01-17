package client;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private String author;
    private Date date;
    private String text;

    public Message() {
        date = new Date();
    }
    
    public Message(String text) {
        this.text = text;
        date = new Date();
    }

    public Message(String author, String text) {
        this.author = author;
        this.text = text;
        date = new Date();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
//        StringBuilder sbuf = new StringBuilder();
//        sbuf.append(author);
//        sbuf.append(" [");
//        sbuf.append(date);
//        sbuf.append("]:");
//        sbuf.append(text);
//        return sbuf.toString();
        return author + " [" + date + "]: " + text;
    }

    
}
