package no.hiof.bo20_g28.stillashjelpen.model;

import java.io.Serializable;

public class ChecklistItem implements Serializable {
    private int id;
    private String text;
    private int parentId;
    private int checkedChildrenCounter;
    private int numberOfChildren ;
    private boolean isParent;
    private boolean isChecked;


    public ChecklistItem(int id, String text, int parentId, int checkedChildrenCounter, int numberOfChildren, boolean isParent, boolean isChecked) {
        this.id = id;
        this.text = text;
        this.parentId = parentId;
        this.checkedChildrenCounter = checkedChildrenCounter;
        this.numberOfChildren = numberOfChildren;
        this.isParent = isParent;
        this.isChecked = isChecked;
    }


    public int getCheckedChildrenCounter() {
        return checkedChildrenCounter;
    }

    public void setCheckedChildrenCounter(int checkedChildrenCounter) {
        this.checkedChildrenCounter = checkedChildrenCounter;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public ChecklistItem(int id, String text, int parentId){
        this.id = id;
        this.text = text;
        this.parentId = parentId;
        this.isParent = false;
    }

    public ChecklistItem(boolean isParent, int id, String text, int numberOfChildren){
        this.id = id;
        this.text = text;
        this.parentId = 1000;
        this.numberOfChildren = numberOfChildren;
        this.isParent = isParent;

    }

    public ChecklistItem(){}

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void incrementCheckedChildrenCounter() {
        this.checkedChildrenCounter++;
    }

    public void decrementCheckedChildrenCounter() {
        this.checkedChildrenCounter--;
    }

    public boolean allChildrenAreChecked(){
        return this.checkedChildrenCounter == this.numberOfChildren;
    }

    public boolean getIsParent() {
        return this.isParent;
    }

    public void setIsParent(boolean parent) {
        this.isParent = parent;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
    }
}
