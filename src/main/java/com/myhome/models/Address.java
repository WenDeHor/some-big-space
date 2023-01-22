package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "address")
public class Address {
    public Address() {
    }

    public Address(String settlement, String currencyCode, Long idUser) {
        this.settlement = settlement;
        this.currencyCode = currencyCode;
        this.idUser = idUser;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long idAddress;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number")
    int number;

    @Column(name = "settlement")
    String settlement;

    @Column(name = "currency_code")
    String currencyCode; //settlement+number

    @Column(name = "user_id")
       private Long idUser;

    public Long getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Long idAddress) {
        this.idAddress = idAddress;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return number == address.number &&
                Objects.equals(idAddress, address.idAddress) &&
                Objects.equals(settlement, address.settlement) &&
                Objects.equals(currencyCode, address.currencyCode) &&
                Objects.equals(idUser, address.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAddress, number, settlement, currencyCode, idUser);
    }

    @Override
    public String toString() {
        return "Address{" +
                "idAddress=" + idAddress +
                ", number=" + number +
                ", settlement='" + settlement + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
