package com.myhome.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Objects;


@Entity
@Table(name = "metrics_data")
public class MetricsData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "currency_code")
    private Currency currencyCode; //UAH

    @Column(name = "user_name")
    private String userName; //adminOfMyHome@storyflow.link or Incognito

    @Column(name = "date")
    private LocalDate date; //2022-07-02 18:39:31.024000

    @Column(name = "accept_language")
    private String acceptLanguage; //en-US,en;q=0.9,uk;q=0.8

    @Column(name = "url_name")
    private String URLName; ///users/read-competitive-one-composition-index/{id}

    @Column(name = "remote_addr")
    private String remoteAddr; //0:0:0:0:0:0:0:1

    @Column(name = "marker")
    private String marker;

    public MetricsData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currency currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    public String getURLName() {
        return URLName;
    }

    public void setURLName(String URLName) {
        this.URLName = URLName;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricsData that = (MetricsData) o;
        return id == that.id &&
                Objects.equals(currencyCode, that.currencyCode) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(date, that.date) &&
                Objects.equals(acceptLanguage, that.acceptLanguage) &&
                Objects.equals(URLName, that.URLName) &&
                Objects.equals(remoteAddr, that.remoteAddr) &&
                Objects.equals(marker, that.marker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyCode, userName, date, acceptLanguage, URLName, remoteAddr, marker);
    }
}
