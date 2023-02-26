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
    @Column(name = "id")
    private int id;

    @Column(name = "address_user")
    @Size(max = 50)
    private String addressUser;

    @Column(name = "email")
    @Size(max = 100)
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(max = 100)
    private String titleText;

    @Column(name = "full_text")
    @Size(max = 3000)
    private String fullText;

    public LetterToADMIN() {
    }

    public LetterToADMIN(@Size(max = 50) String addressUser, @Size(max = 100) String email, Date date, @Size(max = 100) String titleText, @Size(max = 20000) String fullText) {
        this.addressUser = addressUser;
        this.email = email;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetterToADMIN that = (LetterToADMIN) o;
        return id == that.id &&
                Objects.equals(addressUser, that.addressUser) &&
                Objects.equals(email, that.email) &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressUser, email, date, titleText, fullText);
    }
}
