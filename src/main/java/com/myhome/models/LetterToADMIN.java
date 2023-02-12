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

    @Column(name = "address")
    @Size(min = 0, max = 100)
    private String address;

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

    public LetterToADMIN(String email,String address, Date localDate, String titleText, String fullText) {
        this.email = email;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        LetterToADMIN that = (LetterToADMIN) o;
        return Objects.equals(idLetter, that.idLetter) &&
                Objects.equals(email, that.email) &&
                Objects.equals(address, that.address) &&
                Objects.equals(localDate, that.localDate) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLetter, email, address, localDate, titleText, fullText);
    }

    @Override
    public String toString() {
        return "LetterToADMIN{" +
                "idLetter=" + idLetter +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", localDate=" + localDate +
                ", titleText='" + titleText + '\'' +
                ", fullText='" + fullText + '\'' +
                '}';
    }
}
