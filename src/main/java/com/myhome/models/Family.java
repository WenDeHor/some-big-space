package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "family")
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "id_family")
    private int idFamily;

    @Column(name = "address_family")
    private String addressFamily;

    public Family() {
    }

    public Family(int idUser, int idFamily, String addressFamily) {
        this.idUser = idUser;
        this.idFamily = idFamily;
        this.addressFamily = addressFamily;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(int idFamily) {
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
        return id == family.id &&
                idUser == family.idUser &&
                idFamily == family.idFamily &&
                Objects.equals(addressFamily, family.addressFamily);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idFamily, addressFamily);
    }
}
