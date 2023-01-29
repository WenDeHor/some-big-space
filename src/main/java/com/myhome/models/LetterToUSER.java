package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "letter_user")
public class LetterToUSER {
    public LetterToUSER() {
    }

    public LetterToUSER(Date date, @Size(min = 0, max = 1000) String titleText, @Size(min = 0, max = 3000) String fullText, String senderAddress, String recipientAddress) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.senderAddress = senderAddress;
        this.recipientAddress = recipientAddress;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_letter")
    private Long idLetter;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(min = 0, max = 1000)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

//    @Column(name = "number_of_letter")
//    private Integer numberOfLetter;

    @Column(name = "sender_address")
    private String senderAddress;

    @Column(name = "recipient_address")
    private String recipientAddress;

    public Long getIdLetter() {
        return idLetter;
    }

    public void setIdLetter(Long idLetter) {
        this.idLetter = idLetter;
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

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getRecipientAddress() {
        return recipientAddress;
    }

    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LetterToUSER letterToUSER = (LetterToUSER) o;
        return Objects.equals(idLetter, letterToUSER.idLetter) &&
                Objects.equals(date, letterToUSER.date) &&
                Objects.equals(titleText, letterToUSER.titleText) &&
                Objects.equals(fullText, letterToUSER.fullText) &&
                Objects.equals(senderAddress, letterToUSER.senderAddress) &&
                Objects.equals(recipientAddress, letterToUSER.recipientAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLetter, date, titleText, fullText, senderAddress, recipientAddress);
    }

    @Override
    public String toString() {
        return "Letter{" +
                "idLetter=" + idLetter +
                ", date=" + date +
                ", titleText='" + titleText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", senderAddress='" + senderAddress + '\'' +
                ", recipientAddress='" + recipientAddress + '\'' +
                '}';
    }
}
