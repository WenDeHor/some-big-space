package com.myhome.forms;

import java.util.Objects;

public class PublicationDTO {
    private int id;
    private String titleText;
    private String fullText;
    private String info;

    public PublicationDTO() {
    }

    public PublicationDTO(int id, String titleText, String fullText, String info) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
        this.info = info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicationDTO that = (PublicationDTO) o;
        return id == that.id &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titleText, fullText, info);
    }
}
