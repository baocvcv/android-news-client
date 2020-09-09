package com.java.baohan.backend;

public class KnowledgeRelation {
    public String relation;
    public String url;
    public String label;
    public boolean forward;

    public KnowledgeRelation(String relation, String url, String label, boolean forward) {
        this.relation = relation;
        this.url = url;
        this.label = label;
        this.forward = forward;
    }
}
