package com.example.inote;

public class Note {
    private String title, content, date, kind, id, userEmail, backgroundColorString;
    private boolean isPressed;

    public Note() {
    }

    public Note(String title, String content, String date, String kind, String id, String userEmail, String backgroundColorString, boolean isPressed) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.kind = kind;
        this.id = id;
        this.userEmail = userEmail;
        this.isPressed = isPressed;
        this.backgroundColorString = backgroundColorString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }

    public String getBackgroundColorString() {
        return backgroundColorString;
    }

    public void setBackgroundColorString(String backgroundColorString) {
        this.backgroundColorString = backgroundColorString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    // Ensure to override toString() method if needed for debugging or serialization purposes
    @Override
    public String toString() {
        return "Note{" + "title='" + title + '\'' + ", content='" + content + '\'' + ", date='" + date + '\'' + ", kind='" + kind + '\'' + '}';
    }

}

