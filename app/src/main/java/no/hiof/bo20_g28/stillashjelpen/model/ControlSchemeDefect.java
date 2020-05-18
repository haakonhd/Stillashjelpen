package no.hiof.bo20_g28.stillashjelpen.model;

import java.util.Date;

public class ControlSchemeDefect {

    private Date foundDate;
    private String defectDescription;

    public ControlSchemeDefect(Date foundDate, String defectDescription) {
        this.foundDate = foundDate;
        this.defectDescription = defectDescription;
    }

    public Date getfoundDate() {
        return foundDate;
    }

    public void setfoundDate(Date foundDate) {
        this.foundDate = foundDate;
    }

    public String getDefectDescription() {
        return defectDescription;
    }

    public void setDefectDescription(String defectDescription) {
        this.defectDescription = defectDescription;
    }
}
