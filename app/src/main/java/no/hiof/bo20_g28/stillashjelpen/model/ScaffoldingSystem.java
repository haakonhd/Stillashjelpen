package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class ScaffoldingSystem implements Serializable {

    private String scaffoldingSystemId;
    private String scaffoldingSystemName;
    private int bayWidth;
    private int bayLength;
    private int scaffoldLoadClass;

    private Date dateCreated;
    private Date dateChanged;


    public ScaffoldingSystem() {

    }


    public String getScaffoldingSystemId() {
        return scaffoldingSystemId;
    }

    public void setScaffoldingSystemId(String scaffoldingSystemId) {
        this.scaffoldingSystemId = scaffoldingSystemId;
    }

    public String getScaffoldingSystemName() {
        return scaffoldingSystemName;
    }

    public void setScaffoldingSystemName(String scaffoldingSystemName) {
        this.scaffoldingSystemName = scaffoldingSystemName;
    }

    public int getBayWidth() {
        return bayWidth;
    }

    public void setBayWidth(int bayWidth) {
        this.bayWidth = bayWidth;
    }

    public int getBayLength() {
        return bayLength;
    }

    public void setBayLength(int bayLength) {
        this.bayLength = bayLength;
    }

    public int getScaffoldLoadClass() {
        return scaffoldLoadClass;
    }

    public void setScaffoldLoadClass(int scaffoldLoadClass) {
        this.scaffoldLoadClass = scaffoldLoadClass;
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
