package com.myhome.forms;

import java.util.Date;
import java.util.Objects;

public class LettersDTO {
    private int id;
    private Date date;
    private String titleText;
    private String fullText;
    private String info;

    public LettersDTO() {
    }

    public LettersDTO(int id, String titleText, String fullText, String info) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
        this.info = info;
    }

    public LettersDTO(int id, Date date, String titleText, String fullText, String info) {
        this.id = id;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        LettersDTO that = (LettersDTO) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, titleText, fullText, info);
    }
}
