package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class ControlSchemeDefectFixed implements Serializable {

    private Date controlDate;
    private Date defectFoundDate;
    private String signature;

    public ControlSchemeDefectFixed(Date controlDate, Date defectFoundDate, String signature) {
        this.controlDate = controlDate;
        this.defectFoundDate = defectFoundDate;
        this.signature = signature;
    }

    public Date getDefectFoundDate() {
        return defectFoundDate;
    }

    public void setDefectFoundDate(Date defectFoundDate) {
        this.defectFoundDate = defectFoundDate;
    }

    public Date getControlDate() {
        return controlDate;
    }

    public void setControlDate(Date controlDate) {
        this.controlDate = controlDate;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
