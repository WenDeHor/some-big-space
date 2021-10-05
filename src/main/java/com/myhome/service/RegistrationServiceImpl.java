package com.myhome.service;


import com.myhome.forms.UserForm;
import com.myhome.models.Role;
import com.myhome.models.State;
import com.myhome.models.User;
import com.myhome.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final static Integer counter = 100;

    public RegistrationServiceImpl(UserRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void signUp(UserForm userForm) {
        Locale defaultLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(defaultLocale);
        System.out.println("Currency Code: " + currency.getCurrencyCode());
        System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
//        String address = "CC:"+currency.getCurrencyCode() + "-UN:" + userForm.getLogin()+"-SB:" +counter();
        String address = "[" + userForm.getLogin() + "]" + userForm.getSettlement() + ":" + userForm.getEmail();
        String hashPassword = passwordEncoder.encode(userForm.getPassword());
        Optional<User> adminPresent = usersRepository.findOneByRole(Role.ADMIN);

//        private String login;
//        private String settlement;
//        private String email;
//        private String password;

        Date date = new Date();
        if (!adminPresent.isPresent()) {
            User user = User.builder()
                    .login(userForm.getLogin())
                    .settlement(userForm.getSettlement())
                    .email(userForm.getEmail())
                    .counter(counter)
                    .password(hashPassword)
                    .currencyCode(currency.getCurrencyCode())
                    .address(address)
                    .role(Role.ADMIN)
                    .state(State.ACTIVE)
                    .date(date)
                    .build();
            System.out.println(user.toString());
            usersRepository.save(user);

        } else {
            User user = User.builder()
                    .login(userForm.getLogin())
                    .settlement(userForm.getSettlement())
                    .email(userForm.getEmail())
                    .password(hashPassword)
                    .counter(counter())
                    .address(address)
                    .currencyCode(currency.getCurrencyCode())
                    .date(date)
                    .role(Role.USER)
                    .state(State.ACTIVE)
                    .build();
            System.out.println(user.toString());
            usersRepository.save(user);
        }
    }

    private Integer counter() {
        List<User> allByCounter = usersRepository.findAll();
        int size = allByCounter.size();
        return counter + size + 1;
    }
}
