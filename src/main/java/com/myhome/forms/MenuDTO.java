package com.myhome.forms;

import java.util.Date;
import java.util.Objects;

public class MenuDTO {
    private int id;
    private Date date;
    private String breakfast;
    private String dinner;
    private String supper;
    private int idUser;

    public MenuDTO() {
    }

    public MenuDTO(int id, Date date, String breakfast, String dinner, String supper, int idUser) {
        this.id = id;
        this.date = date;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.supper = supper;
        this.idUser = idUser;
    }

    public MenuDTO(Date date, String breakfast, String dinner, String supper, int idUser) {
        this.date = date;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.supper = supper;
        this.idUser = idUser;
    }

    public MenuDTO(int id, Date date, String breakfast, String dinner, String supper) {
        this.id = id;
        this.date = date;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.supper = supper;
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
        MenuDTO menu = (MenuDTO) o;
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

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", date=" + date +
                ", breakfast='" + breakfast + '\'' +
                ", dinner='" + dinner + '\'' +
                ", supper='" + supper + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
