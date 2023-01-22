package com.myhome.models;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "users")
public class User {
    public User() {
    }

    public User(String login, String settlement, String email, String password, String address, String currencyCode, Role role, State state, Integer counter, Date date) {
        this.login = login;
        this.settlement = settlement;
        this.email = email;
        this.password = password;
        this.address = address;
        this.currencyCode = currencyCode;
        this.role = role;
        this.state = state;
        this.counter = counter;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long idUser;

    @Column(name = "login")
    private String login;//REGISTRATION PAGE

    @Column(name = "settlement")
    private String settlement;//REGISTRATION PAGE

    @Column(name = "email")
    private String email;//REGISTRATION PAGE

    @Column(name = "password")
    private String password;//REGISTRATION PAGE

    @Column(name = "address")
    private String address;

    @Column(name = "currency_code")
    private String currencyCode;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Column(name = "counter")
    private Integer counter;



    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(idUser, user.idUser) &&
                Objects.equals(login, user.login) &&
                Objects.equals(settlement, user.settlement) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(address, user.address) &&
                Objects.equals(currencyCode, user.currencyCode) &&
                role == user.role &&
                state == user.state &&
                Objects.equals(counter, user.counter) &&
                Objects.equals(date, user.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, login, settlement, email, password, address, currencyCode, role, state, counter, date);
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", login='" + login + '\'' +
                ", settlement='" + settlement + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", address='" + address + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", role=" + role +
                ", state=" + state +
                ", counter=" + counter +
                ", date=" + date +
                '}';
    }
}
