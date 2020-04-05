package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Project implements Serializable {

    private String projectId, userId, projectName;
    private ArrayList<Message> messages;
    private ArrayList<Wall> walls;
    private ControlScheme controlScheme;
    private Date dateCreated;


    public Project() {

    }


    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public void setWalls(ArrayList<Wall> walls) {
        this.walls = walls;
    }

    public ControlScheme getControlScheme() {
        return controlScheme;
    }

    public void setControlScheme(ControlScheme controlScheme) {
        this.controlScheme = controlScheme;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
