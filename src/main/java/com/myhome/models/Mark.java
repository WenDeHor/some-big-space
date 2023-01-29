package com.myhome.models;

public enum Mark {
    ENVIRONMENT_0("Не має оцінки"),
    ENVIRONMENT_1("Описані в достатній мірі"),
    ENVIRONMENT_2("Описані не детально"),
    ENVIRONMENT_3("Опис відсутній"),
    CHARACTERS_0("Не має оцінки"),
    CHARACTERS_1("Описані в достатній мірі"),
    CHARACTERS_2("Описані не детально"),
    CHARACTERS_3("Опис відсутній"),
    ATMOSPHERE_0("Не має оцінки"),
    ATMOSPHERE_1("Детально прописано світ"),
    ATMOSPHERE_2("Вибірково описано"),
    ATMOSPHERE_3("Описи відсутні"),
    PLOT_0("Не має оцінки"),
    PLOT_1("Сюжет складний (багато загадок)"),
    PLOT_2("Багато не розкритих подій"),
    PLOT_3("Є нерозкриті деталі"),
    PLOT_4("Сюжет простий"),
    PLOT_5("Повністю не завершений"),
    IMPRESSION_0("Не має оцінки"),
    IMPRESSION_1("Відчуваю захоплення та співпереживання"),
    IMPRESSION_2("Відчуття розгубленості"),
    IMPRESSION_3("Хочеться плакати"),
    IMPRESSION_4("Довелося задуматись"),
    IMPRESSION_5("Немаю вражень");

    String name;

    Mark(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "name='" + name + '\'' +
                '}';
    }
}
