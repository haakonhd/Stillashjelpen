package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Project implements Serializable {

    private String projectId;
    private String userId;
    private String projectName;
    private String scaffoldType;
    private ArrayList<Message> messages;
    private ArrayList<Wall> walls;
    private ControlScheme controlScheme;
    private Date dateCreated;
    private Date dateChanged;

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

    public String getScaffoldType() {
        return scaffoldType;
    }

    public void setScaffoldType(String scaffoldType) {
        this.scaffoldType = scaffoldType;
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

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

}
