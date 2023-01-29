package com.myhome.forms;

import lombok.Data;

import java.util.Objects;

@Data
public class OneElement {
    String element;

    public OneElement() {
    }

    public OneElement(String element) {
        this.element = element;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneElement that = (OneElement) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }
}
