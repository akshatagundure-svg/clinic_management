package com.example.springcrud.model;

public class ChatResponse {
    private String aiName;
    private String reply;
    private String model;

    public ChatResponse() {}

    public ChatResponse(String aiName, String reply, String model) {
        this.aiName = aiName;
        this.reply = reply;
        this.model = model;
    }

    public String getAiName() { return aiName; }
    public void setAiName(String aiName) { this.aiName = aiName; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}