package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "my_friends")
public class MyFriends {
    public MyFriends() {
    }

    public MyFriends(String addressUser, String addressMyFriends) {
        this.addressUser = addressUser;
        this.addressMyFriends = addressMyFriends;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_my_friends")
    private Long idMyFriends;

    @Column(name = "address_user")
    private String addressUser;

    @Column(name = "address_my_friends")
    private String addressMyFriends;

    public Long getIdMyFriends() {
        return idMyFriends;
    }

    public void setIdMyFriends(Long idMyFriends) {
        this.idMyFriends = idMyFriends;
    }

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public String getAddressMyFriends() {
        return addressMyFriends;
    }

    public void setAddressMyFriends(String addressMyFriends) {
        this.addressMyFriends = addressMyFriends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyFriends myFriends = (MyFriends) o;
        return Objects.equals(idMyFriends, myFriends.idMyFriends) &&
                Objects.equals(addressUser, myFriends.addressUser) &&
                Objects.equals(addressMyFriends, myFriends.addressMyFriends);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMyFriends, addressUser, addressMyFriends);
    }

    @Override
    public String toString() {
        return "MyFriends{" +
                "idMyFriends=" + idMyFriends +
                ", addressUser='" + addressUser + '\'' +
                ", addressMyFriends='" + addressMyFriends + '\'' +
                '}';
    }
}
