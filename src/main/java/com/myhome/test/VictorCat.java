package com.myhome.test;


import java.util.ArrayList;
import java.util.List;

public class VictorCat {
    public static void main(String[] args) {
        List<String> classes = new ArrayList<>();
        classes.add("Victor Tkachuk");
        classes.add("Sofia");
        classes.add("Victoria Victorivna");
        System.out.println(classes.toString());

        for (int i = 0; i < 1; i++) {
            System.out.println(classes.get(1));
            System.out.println(classes.get(2));
            classes.add("Peta");
            System.out.println(classes.toString());
        }

        if(classes.get(0).equals("Victor Tkachuk")){
            System.out.println(classes.get(0)+" hello");
        }else {
            System.out.println("good by");
        }
    }
}
