package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;

public class ScaffoldingSystem implements Serializable {

    private String scaffoldingSystemId;
    private String scaffoldingSystemName;
    private int bayWidth;
    private int bayLength;


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
}
