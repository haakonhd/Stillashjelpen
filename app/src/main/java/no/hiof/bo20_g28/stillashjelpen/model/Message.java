package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private String messageId;
    private String userId;
    private String projectId;
    private String content;
    private Date dateCreated;

    public Message() {

    }

    public Message(String messageId, String userId, String projectId, String content, Date dateCreated) {
        this.messageId = messageId;
        this.userId = userId;
        this.projectId = projectId;
        this.content = content;
        this.dateCreated = dateCreated;
    }


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
