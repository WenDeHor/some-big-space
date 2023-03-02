package com.myhome.forms;

import java.util.Objects;

public class Buttons {
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;

    public Buttons() {
    }

    public Buttons(String one) {
        this.one = one;
    }

    public Buttons(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public Buttons(String one, String two, String three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public Buttons(String one, String two, String three, String four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public Buttons(String one, String two, String three, String four, String five) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public String getFour() {
        return four;
    }

    public void setFour(String four) {
        this.four = four;
    }

    public String getFive() {
        return five;
    }

    public void setFive(String five) {
        this.five = five;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Buttons that = (Buttons) o;
        return Objects.equals(one, that.one) &&
                Objects.equals(two, that.two) &&
                Objects.equals(three, that.three) &&
                Objects.equals(four, that.four) &&
                Objects.equals(five, that.five);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two, three, four, five);
    }
}
