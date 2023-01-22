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
@Table(name = "menu")
public class Menu {
    public Menu() {
    }

    public Menu(Date date, @Size(min = 0, max = 1000) String breakfast, @Size(min = 0, max = 1000) String dinner, @Size(min = 0, max = 1000) String supper, String address) {
        this.date = date;
        this.breakfast = breakfast;
        this.dinner = dinner;
        this.supper = supper;
        this.address = address;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "breakfast")
    @Size(min = 0, max = 1000)
    private String breakfast;

    @Column(name = "dinner")
    @Size(min = 0, max = 1000)
    private String dinner;

    @Column(name = "supper")
    @Size(min = 0, max = 1000)
    private String supper;

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
        Menu menu = (Menu) o;
        return Objects.equals(id, menu.id) &&
                Objects.equals(date, menu.date) &&
                Objects.equals(breakfast, menu.breakfast) &&
                Objects.equals(dinner, menu.dinner) &&
                Objects.equals(supper, menu.supper) &&
                Objects.equals(address, menu.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, breakfast, dinner, supper, address);
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id=" + id +
                ", date=" + date +
                ", breakfast='" + breakfast + '\'' +
                ", dinner='" + dinner + '\'' +
                ", supper='" + supper + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
