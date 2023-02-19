package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "shop_meals")
public class ShopMeals {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "full_text")
    @Size(max = 3000)
    private String fullText;

    @Column(name = "id_user")
    private int idUser;

    public ShopMeals() {
    }

    public ShopMeals(Date date, @Size(max = 3000) String fullText, int idUser) {
        this.date = date;
        this.fullText = fullText;
        this.idUser = idUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopMeals shopMeals = (ShopMeals) o;
        return id == shopMeals.id &&
                idUser == shopMeals.idUser &&
                Objects.equals(date, shopMeals.date) &&
                Objects.equals(fullText, shopMeals.fullText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, fullText, idUser);
    }
}
