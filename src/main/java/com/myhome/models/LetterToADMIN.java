package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "letter_admin")
public class LetterToADMIN {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long idLetter;

    @Column(name = "email")
    @Size(min = 0, max = 100)
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "local_date", nullable = false)
    private Date localDate;

    @Column(name = "title_text")
    @Size(min = 0, max = 100)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 20000)
    private String fullText;

    public LetterToADMIN() {
    }

    public LetterToADMIN(@Size(min = 0, max = 100) String email, Date localDate, @Size(min = 0, max = 100) String titleText, @Size(min = 0, max = 20000) String fullText) {
        this.email = email;
        this.localDate = localDate;
        this.titleText = titleText;
        this.fullText = fullText;
    }

    public Long getIdLetter() {
        return idLetter;
    }

    public void setIdLetter(Long idLetter) {
        this.idLetter = idLetter;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetterToADMIN letterToADMIN = (LetterToADMIN) o;
        return Objects.equals(idLetter, letterToADMIN.idLetter) &&
                Objects.equals(email, letterToADMIN.email) &&
                Objects.equals(localDate, letterToADMIN.localDate) &&
                Objects.equals(titleText, letterToADMIN.titleText) &&
                Objects.equals(fullText, letterToADMIN.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLetter, email, localDate, titleText, fullText);
    }
}
