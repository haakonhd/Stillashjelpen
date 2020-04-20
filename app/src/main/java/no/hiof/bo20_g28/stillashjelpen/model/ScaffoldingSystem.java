package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;
import java.util.Date;

public class ScaffoldingSystem implements Serializable {

    private String scaffoldingSystemId;
    private String scaffoldingSystemName;
    private double bayWidth;
    private double bayLength;
    private int weight;
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

    public double getBayWidth() {
        return bayWidth;
    }

    public void setBayWidth(double bayWidth) {
        this.bayWidth = bayWidth;
    }

    public double getBayLength() {
        return bayLength;
    }

    public void setBayLength(double bayLength) {
        this.bayLength = bayLength;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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
