package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "breakfast")
    @Size(max = 1000)
    private String breakfast;

    @Column(name = "dinner")
    @Size(max = 1000)
    private String dinner;

    @Column(name = "supper")
    @Size(max = 1000)
    private String supper;

    @Column(name = "id_user")
    private int idUser;

    public Menu() {
    }

    public Menu(Date date, String breakfast, String dinner, String supper, int idUser) {
        this.date = date;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.supper = supper;
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

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public String getSupper() {
        return supper;
    }

    public void setSupper(String supper) {
        this.supper = supper;
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
        Menu menu = (Menu) o;
        return id == menu.id &&
                idUser == menu.idUser &&
                Objects.equals(date, menu.date) &&
                Objects.equals(breakfast, menu.breakfast) &&
                Objects.equals(dinner, menu.dinner) &&
                Objects.equals(supper, menu.supper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, breakfast, dinner, supper, idUser);
    }
}
