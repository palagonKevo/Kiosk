package com.example.kiosk;

public class Product {

    private String descr;
    private Long color;
    private short type;
    private String codi;
    private boolean hasData;
    private Short tag;
    private String tagComment;

    public Product() {
    }

    public Product( String descr, Long color, short type, String codi, boolean hasData, Short tag, String tagComment) {
        this.descr = descr;
        this.color = color;
        this.type = type;
        this.codi = codi;
        this.hasData = hasData;
        this.tag = tag;
        this.tagComment = tagComment;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Long getColor() {
        return color;
    }

    public void setColor(Long color) {
        this.color = color;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public Short getTag() {
        return tag;
    }

    public void setTag(Short tag) {
        this.tag = tag;
    }

    public String getTagComment() {
        return tagComment;
    }

    public void setTagComment(String tagComment) {
        this.tagComment = tagComment;
    }
}
