package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class ControlScheme implements Serializable {

    private String controlSchemeId;

    private Date dateCreated;
    private Date dateChanged;

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
}
