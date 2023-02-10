package com.myhome.forms;

import java.util.Objects;

public class LettersDTO {
    private Long idLetter;
    private String titleText;
    private String fullText;
    private String info;

    public LettersDTO() {
    }

    public LettersDTO(Long idLetter, String titleText, String fullText, String info) {
        this.idLetter = idLetter;
        this.titleText = titleText;
        this.fullText = fullText;
        this.info = info;
    }

    public Long getIdLetter() {
        return idLetter;
    }

    public void setIdLetter(Long idLetter) {
        this.idLetter = idLetter;
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
        return Objects.equals(idLetter, that.idLetter) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLetter, titleText, fullText, info);
    }

    @Override
    public String toString() {
        return "LettersDTO{" +
                "idLetter=" + idLetter +
                ", titleText='" + titleText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
