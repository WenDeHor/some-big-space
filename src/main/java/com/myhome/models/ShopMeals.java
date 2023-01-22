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
@Table(name = "shop_meals")
public class ShopMeals {
    public ShopMeals() {
    }

    public ShopMeals(Date date, @Size(min = 0, max = 3000) String fullText, String address) {
        this.date = date;
        this.fullText = fullText;
        this.address = address;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

    @Column(name = "address")
    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopMeals shopMeals = (ShopMeals) o;
        return Objects.equals(id, shopMeals.id) &&
                Objects.equals(date, shopMeals.date) &&
                Objects.equals(fullText, shopMeals.fullText) &&
                Objects.equals(address, shopMeals.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, fullText, address);
    }

    @Override
    public String toString() {
        return "ShopMeals{" +
                "id=" + id +
                ", date=" + date +
                ", fullText='" + fullText + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
