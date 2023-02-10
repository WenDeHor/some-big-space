package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "family")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "id_family")
    private Long idFamily;

    @Column(name = "address_family")
    private String addressFamily;

    public Family() {
    }

    public Family(Long idUser, Long idFamily, String addressFamily) {
        this.idUser = idUser;
        this.idFamily = idFamily;
        this.addressFamily = addressFamily;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(Long idFamily) {
        this.idFamily = idFamily;
    }

    public String getAddressFamily() {
        return addressFamily;
    }

    public void setAddressFamily(String addressFamily) {
        this.addressFamily = addressFamily;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Family family = (Family) o;
        return Objects.equals(id, family.id) &&
                Objects.equals(idUser, family.idUser) &&
                Objects.equals(idFamily, family.idFamily) &&
                Objects.equals(addressFamily, family.addressFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idFamily, addressFamily);
    }

    @Override
    public String toString() {
        return "Family{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idFamily=" + idFamily +
                ", addressFamily='" + addressFamily + '\'' +
                '}';
    }
}
