package no.hiof.bo20_g28.stillashjelpen.model;

import java.util.Date;

public class ControlSchemeDefectFixed {

    private Date controlDate;
    private Date defectFixedDate;
    private String signature;

    public ControlSchemeDefectFixed(Date controlDate, Date defectFoundDate, String signature) {
        this.controlDate = controlDate;
        this.defectFixedDate = defectFoundDate;
        this.signature = signature;
    }

    public Date getDefectFixedDate() {
        return defectFixedDate;
    }

    public void setDefectFixedDate(Date defectFoundDate) {
        this.defectFixedDate = defectFoundDate;
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
