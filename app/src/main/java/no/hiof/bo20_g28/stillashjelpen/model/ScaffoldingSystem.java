package no.hiof.bo20_g28.stillashjelpen.model;

public class ScaffoldingSystem {

    private String scaffoldingSystemId;
    private String scaffoldingSystemName;
    private int fagbredde;
    private int faglengde;


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

    public int getFagbredde() {
        return fagbredde;
    }

    public void setFagbredde(int fagbredde) {
        this.fagbredde = fagbredde;
    }

    public int getFaglengde() {
        return faglengde;
    }

    public void setFaglengde(int faglengde) {
        this.faglengde = faglengde;
    }
}
