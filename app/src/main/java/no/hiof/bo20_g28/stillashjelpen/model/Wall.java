package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class Wall implements Serializable {

    private String wallId;
    private String userId;
    private String projectId;
    private String scaffoldType;
    private String wallName;
    private String pictureId;
    private String wallDescription;
    private int soleBoardArea;
    private float wallAnchorDistance;
    private Date dateCreated;
    private Date dateChanged;


    public Wall(String wallId, String userId, String projectId, String scaffoldType, String wallName, Date dateCreated) {
        this.wallId = wallId;
        this.userId = userId;
        this.projectId = projectId;
        this.scaffoldType = scaffoldType;
        this.wallName = wallName;
        this.dateCreated = dateCreated;
    }

    public Wall() {

    }


    public String getWallId() {
        return wallId;
    }

    public void setWallId(String wallId) {
        this.wallId = wallId;
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

    public String getScaffoldType() {
        return scaffoldType;
    }

    public void setScaffoldType(String scaffoldType) {
        this.scaffoldType = scaffoldType;
    }

    public String getWallName() {
        return wallName;
    }

    public void setWallName(String wallName) {
        this.wallName = wallName;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getWallDescription() {
        return wallDescription;
    }

    public void setWallDescription(String wallDescription) {
        this.wallDescription = wallDescription;
    }

    public int getSoleBoardArea() {
        return soleBoardArea;
    }

    public void setSoleBoardArea(int soleBoardArea) {
        this.soleBoardArea = soleBoardArea;
    }

    public float getWallAnchorDistance() {
        return wallAnchorDistance;
    }

    public void setWallAnchorDistance(float wallAnchorDistance) {
        this.wallAnchorDistance = wallAnchorDistance;
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
