package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

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
}
