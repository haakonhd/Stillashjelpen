package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import no.hiof.bo20_g28.stillashjelpen.ControlSchemeActivity;

public class ControlScheme implements Serializable {

    private String controlSchemeId;

    private Date dateCreated;
    private Date dateChanged;

    private String builderCompanyName;
    private String refNum;
    private String placeName;
    private String builtByName;
    private String userCompanyName;
    private String userPhoneNum;
    private String contactPersonName;
    private String builderNameControlName;
    private String userNameControlName;
    private String builderControlDate;
    private String userControlDate;

    private String scaffoldType;
    private String scaffoldLength;
    private String scaffoldWidth;
    private String scaffoldHeight;
    private String numOfWallAnchors;
    private String numOfWallAnchorTests;
    private String wallAnchorHolds;
    private String wallAnchorTestResult;

    private String lastEmailSentTo;

    private ArrayList<ChecklistItem> checklistItems;
    private ArrayList<ControlSchemeDefect> controlSchemeDefects = new ArrayList<>();
    private ArrayList<ControlSchemeDefectFixed> controlSchemeDefectFixed = new ArrayList<>();

    public ArrayList<ControlSchemeDefect> getControlSchemeDefects() {
        return controlSchemeDefects;
    }
    public ArrayList<ControlSchemeDefectFixed> getControlSchemeDefectFixed() {
        return controlSchemeDefectFixed;
    }

    public void setControlSchemeDefects(ArrayList<ControlSchemeDefect> controlSchemeDefects) {
        this.controlSchemeDefects = controlSchemeDefects;
    }

    public void setControlSchemeDefectFixed(ArrayList<ControlSchemeDefectFixed> controlSchemeDefects) {
        this.controlSchemeDefectFixed = controlSchemeDefects;
    }

    public void addControlSchemeDefect(ControlSchemeDefect controlSchemeDefect){
        this.controlSchemeDefects.add(controlSchemeDefect);
    }

    public void addControlSchemeDefectFixed(ControlSchemeDefectFixed controlSchemeDefectFixed) {
        this.controlSchemeDefectFixed.add(controlSchemeDefectFixed);
    }


    public ControlScheme(String controlSchemeId) {
        this.controlSchemeId = controlSchemeId;
    }

    public ControlScheme() {

    }

    public String getControlSchemeId() {
        return controlSchemeId;
    }

    public void setControlSchemeId(String controlSchemeId) {
        this.controlSchemeId = controlSchemeId;
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

    public String getBuilderCompanyName() {
        return builderCompanyName;
    }

    public void setBuilderCompanyName(String builderCompanyName) {
        this.builderCompanyName = builderCompanyName;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getBuiltByName() {
        return builtByName;
    }

    public void setBuiltByName(String builtByName) {
        this.builtByName = builtByName;
    }

    public String getUserCompanyName() {
        return userCompanyName;
    }

    public void setUserCompanyName(String userCompanyName) {
        this.userCompanyName = userCompanyName;
    }

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getBuilderNameControlName() {
        return builderNameControlName;
    }

    public void setBuilderNameControlName(String builderNameControlName) {
        this.builderNameControlName = builderNameControlName;
    }

    public String getUserNameControlName() {
        return userNameControlName;
    }

    public void setUserNameControlName(String userNameControlName) {
        this.userNameControlName = userNameControlName;
    }

    public String getBuilderControlDate() {
        return builderControlDate;
    }

    public void setBuilderControlDate(String builderControlDate) {
        this.builderControlDate = builderControlDate;
    }

    public String getUserControlDate() {
        return userControlDate;
    }

    public void setUserControlDate(String userControlDate) {
        this.userControlDate = userControlDate;
    }

    public String getScaffoldType() {
        return scaffoldType;
    }

    public void setScaffoldType(String scaffoldType) {
        this.scaffoldType = scaffoldType;
    }

    public String getScaffoldLength() {
        return scaffoldLength;
    }

    public void setScaffoldLength(String scaffoldLength) {
        this.scaffoldLength = scaffoldLength;
    }

    public String getScaffoldWidth() {
        return scaffoldWidth;
    }

    public void setScaffoldWidth(String scaffoldWidth) {
        this.scaffoldWidth = scaffoldWidth;
    }

    public String getScaffoldHeight() {
        return scaffoldHeight;
    }

    public void setScaffoldHeight(String scaffoldHeight) {
        this.scaffoldHeight = scaffoldHeight;
    }

    public String getNumOfWallAnchors() {
        return numOfWallAnchors;
    }

    public void setNumOfWallAnchors(String numOfWallAnchors) {
        this.numOfWallAnchors = numOfWallAnchors;
    }

    public String getNumOfWallAnchorTests() {
        return numOfWallAnchorTests;
    }

    public void setNumOfWallAnchorTests(String numOfWallAnchorTests) {
        this.numOfWallAnchorTests = numOfWallAnchorTests;
    }

    public String getWallAnchorHolds() {
        return wallAnchorHolds;
    }

    public void setWallAnchorHolds(String wallAnchorHolds) {
        this.wallAnchorHolds = wallAnchorHolds;
    }

    public String getWallAnchorTestResult() {
        return wallAnchorTestResult;
    }

    public void setWallAnchorTestResult(String wallAnchorTestResult) {
        this.wallAnchorTestResult = wallAnchorTestResult;
    }

    public ArrayList<ChecklistItem> getChecklistItems() {
        return checklistItems;
    }

    public void setChecklistItems(ArrayList<ChecklistItem> checklistItems) {
        this.checklistItems = checklistItems;
    }

    public String getLastEmailSentTo() {
        return lastEmailSentTo;
    }

    public void setLastEmailSentTo(String lastEmailSentTo) {
        this.lastEmailSentTo = lastEmailSentTo;
    }
}
