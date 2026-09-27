package com.studypod.studypod_backend.ai;

public class AIGenerateRequest {
    private String noteContent;
    private String type; // summary, flashcards, quiz, flowchart

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}