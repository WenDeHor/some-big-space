package com.myhome.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class show {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("Віктор Ткачук=0");
        list.add("Вікторія Вікторівна=1");
        list.add("Богдан=2");
        list.add("Софія=3");

        System.out.println("Віктор Привіт це я твій Татко");
        System.out.println(list);

        list.sort(Comparator.naturalOrder());
        System.out.println(list);

    }
}
