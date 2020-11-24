package com.sr.datasync;

public class EntryClass {
    private String title;
    private String data;

    public EntryClass(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public EntryClass() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean equals(EntryClass entryClass){
        if(entryClass.getTitle().equals(this.title)){
            return entryClass.getData().equals(this.data);
        }
        return false;
    }
}