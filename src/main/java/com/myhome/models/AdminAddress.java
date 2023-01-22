package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "admin_address")
public class AdminAddress {
    public AdminAddress() {
    }

    public AdminAddress(String settlement, String numberLimit) {
        this.settlement = settlement;
        this.numberLimit = numberLimit;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_address_id")
    private Long idAdminAddress;

    @Column(name = "settlement")
    String settlement;

    @Column(name = "number_limit")
    String numberLimit;

    public Long getIdAdminAddress() {
        return idAdminAddress;
    }

    public void setIdAdminAddress(Long idAdminAddress) {
        this.idAdminAddress = idAdminAddress;
    }

    public String getSettlement() {
        return settlement;
    }

    public void setSettlement(String settlement) {
        this.settlement = settlement;
    }

    public String getNumberLimit() {
        return numberLimit;
    }

    public void setNumberLimit(String numberLimit) {
        this.numberLimit = numberLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminAddress that = (AdminAddress) o;
        return Objects.equals(idAdminAddress, that.idAdminAddress) &&
                Objects.equals(settlement, that.settlement) &&
                Objects.equals(numberLimit, that.numberLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAdminAddress, settlement, numberLimit);
    }

    @Override
    public String toString() {
        return "AdminAddress{" +
                "idAdminAddress=" + idAdminAddress +
                ", settlement='" + settlement + '\'' +
                ", numberLimit='" + numberLimit + '\'' +
                '}';
    }
}
