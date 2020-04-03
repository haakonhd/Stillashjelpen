package no.hiof.bo20_g28.stillashjelpen.model;

public class ControlScheme {

    private String controlSchemeId;

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
}
