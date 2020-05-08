package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

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
    private boolean isFirstTimeCreatingAnchorDistance;
    private boolean isFirstTimeCreatingSoleBoardArea;

    private Cover cover;
    private ForceFactor forceFactor;
    private double anchorForce;
    private int scaffoldHeight;
    private double bayLength;

    private int load;
    private int nrOfFloors;
    private double groundKiloNewton;
    private double bayWidth;
    private int weight;
    private int groundSpinnerPosition;
    private int loadSeekerPosition;


    public Wall(String wallId, String userId, String projectId, String scaffoldType, String wallName, Date dateCreated) {
        this.wallId = wallId;
        this.userId = userId;
        this.projectId = projectId;
        this.scaffoldType = scaffoldType;
        this.wallName = wallName;
        this.dateCreated = dateCreated;
        this.isFirstTimeCreatingAnchorDistance = true;
        this.isFirstTimeCreatingSoleBoardArea = true;
    }

    public Wall() {
        this.isFirstTimeCreatingAnchorDistance = true;
        this.isFirstTimeCreatingSoleBoardArea = true;
    }

    public double getBayLength() {
        return bayLength;
    }

    public void setBayLength(double bayLength) {
        this.bayLength = bayLength;
    }

    public boolean isFirstTimeCreatingSoleBoardArea() {
        return isFirstTimeCreatingSoleBoardArea;
    }

    public void setFirstTimeCreatingSoleBoardArea(boolean firstTimeCreatingSoleBoardArea) {
        isFirstTimeCreatingSoleBoardArea = firstTimeCreatingSoleBoardArea;
    }

    public void setFirstTimeCreatingAnchorDistance(boolean firstTimeCreatingAnchorDistance) {
        isFirstTimeCreatingAnchorDistance = firstTimeCreatingAnchorDistance;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getNrOfFloors() {
        return nrOfFloors;
    }

    public void setNrOfFloors(int nrOfFloors) {
        this.nrOfFloors = nrOfFloors;
    }

    public double getGroundKiloNewton() {
        return groundKiloNewton;
    }

    public void setGroundKiloNewton(double groundKiloNewton) {
        this.groundKiloNewton = groundKiloNewton;
    }

    public double getBayWidth() {
        return bayWidth;
    }

    public void setBayWidth(double bayWidth) {
        this.bayWidth = bayWidth;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getLoadSeekerPosition() {
        return loadSeekerPosition;
    }

    public void setLoadSeekerPosition(int loadSeekerPosition) {
        this.loadSeekerPosition = loadSeekerPosition;
    }

    public int getGroundSpinnerPosition() {
        return groundSpinnerPosition;
    }

    public void setGroundSpinnerPosition(int groundSpinnerPosition) {
        this.groundSpinnerPosition = groundSpinnerPosition;
    }

    public enum Cover {
        UNCOVERED,
        NET,
        TARP;
    }

    public enum ForceFactor {
        NORMAL,
        PARALLEL;
    }

    public boolean isFirstTimeCreatingAnchorDistance() {
        return isFirstTimeCreatingAnchorDistance;
    }


    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public ForceFactor getForceFactor() {
        return forceFactor;
    }

    public void setForceFactor(ForceFactor forceFactor) {
        this.forceFactor = forceFactor;
    }

    public double getAnchorForce() {
        return anchorForce;
    }

    public void setAnchorForce(double anchorForce) {
        this.anchorForce = anchorForce;
    }

    public int getScaffoldHeight() {
        return scaffoldHeight;
    }

    public void setScaffoldHeight(int scaffoldHeight) {
        this.scaffoldHeight = scaffoldHeight;
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
