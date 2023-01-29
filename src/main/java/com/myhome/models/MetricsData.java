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
    private Long id;

    @Column(name = "currency_code")
    private Currency currencyCode; //UAH

    @Column(name = "user_name")
    private String userName; //adminOfMyHome@storyflow.link or Incognito

//    @Column(name = "count_per_day")
//    private Long countPerDay; //0 - 1000...

    //    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private LocalDate date; //2022-07-02 18:39:31.024000

//    @Column(name = "user_agent")
//    @Size(min = 0, max = 2000)
//    private String userAgent; //Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36

    @Column(name = "accept_language")
    private String acceptLanguage; //en-US,en;q=0.9,uk;q=0.8

    @Column(name = "url_name")
    private String URLName; ///users/read-competitive-one-composition-index/{id}

//    @Column(name = "user_principal")
//    @Size(min = 0, max = 2000)
//    private String userPrincipal; //UsernamePasswordAuthenticationToken [Principal=com.studio.stories.security.details.UserDetailsImpl@618c5414,
    // Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null], Granted Authorities=[ADMIN]]

    @Column(name = "remote_addr")
    private String remoteAddr; //0:0:0:0:0:0:0:1

    @Column(name = "marker")
    private String marker;

    public MetricsData() {
    }

    public MetricsData(Currency currencyCode, String userName, LocalDate date, String acceptLanguage, String URLName, String remoteAddr, String marker) {
        this.currencyCode = currencyCode;
        this.userName = userName;
        this.date = date;
        this.acceptLanguage = acceptLanguage;
        this.URLName = URLName;
        this.remoteAddr = remoteAddr;
        this.marker = marker;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        return Objects.equals(id, that.id) &&
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
