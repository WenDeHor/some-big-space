package com.myhome.test;

import java.util.regex.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Object {
    public static void main(String[] args) throws NoSuchFieldException {
//        Stream.generate(() -> Math.random()).forEach(System.out::println);
        Stream.concat(Stream.of("aaa", "bbb", "ccc"), Stream.of("111", "222", "333")).forEach(System.out::println);
//        List<String> myList = Arrays.asList("a1", "a2", "b1", "c2", "c1");
//
//        myList.stream() // (1)  return a Stream
//                .filter(s -> s.startsWith("a")) // (2)  return a new Stream
//                .map(String::toUpperCase)  // (3)  return a new Stream
//                .sorted()   // (4)  return a new Stream
//                .forEach(System.out::println); // (5)

//        Person tom = new Person("Tom",10);
//            System.out.println("class::::: "+ tom.getName()+tom.getAge()+tom.getClass().getSimpleName());

    }
}
