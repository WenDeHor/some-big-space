package com.myhome.test;

import com.myhome.models.Role;
import com.myhome.models.State;
import com.myhome.models.User;
import com.myhome.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Locale;

public class maintest {
    public static void main(String[] args) {
        UserRepository usersRepository = null;
//        if (usersRepository.findTopByCounter() == null) {
            int count = 100;
            User user = User.builder()
                    .password("sesedf")
                    .login("userForm.getLogin()")
                    .email("userForm.getEmail()")
//                    .counter(usersRepository.countByCounter())
                    .address("address")
                    .role(Role.ADMIN)
                    .state(State.ACTIVE)
                    .build();
            System.out.println(user.toString());
            usersRepository.save(user);
//        }
//        System.out.println(userRepository.findTopByCounter());;
//        Locale defaultLocale = Locale.getDefault();
//        System.out.println("Locale: " + defaultLocale.getDisplayName());
//        Currency currency = Currency.getInstance(defaultLocale);
//        System.out.println("Currency Code: " + currency.getCurrencyCode());
//        System.out.println("Symbol: " + currency.getSymbol());
//        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
//        System.out.println();

//        LocalDate date = LocalDate.now(); // получаем текущую дату
//        int year = date.getYear();
//        String month = String.valueOf(date.getMonthValue());
//
//        int dayOfMonth = date.getDayOfMonth();
//        DayOfWeek dayOfWeek = date.getDayOfWeek();
//        String format = date.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
//        System.out.println(format);
//        System.out.println(date);
//        System.out.println(dayOfWeek);
//
//        if (month.equals("4")) {
//            month = "match";
//        }
//
//        System.out.printf("%d.%s.%d \n", dayOfMonth, month, year);
//
//        Month month2 = date.getMonth();
//        System.out.printf("%d.%s.%d \n", dayOfMonth, month2, year);
//
//        String s = month2.toString().toLowerCase();
//        System.out.printf("%d.%s.%d \n", dayOfMonth, s, year);
    }
}
