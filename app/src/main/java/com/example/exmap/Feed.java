package com.example.exmap;

import com.google.firebase.Timestamp;

public class Feed {

    String problem, explanation;
    Timestamp timestamp;

    public Feed() {
    }

    public Feed(String problem, String explanation, Timestamp timestamp) {
        this.problem = problem;
        this.explanation = explanation;
        this.timestamp = timestamp;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
