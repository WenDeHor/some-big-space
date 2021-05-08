package com.myhome.service;


import com.myhome.forms.UserForm;
import com.myhome.models.Role;
import com.myhome.models.State;
import com.myhome.models.User;
import com.myhome.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private Integer counter=100;

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
        String address = "CC:"+currency.getCurrencyCode() + "-UN:" + userForm.getLogin()+"-SB:" +counter();

        String hashPassword = passwordEncoder.encode(userForm.getPassword());
        Optional<User> adminPresent = usersRepository.findOneByRole(Role.ADMIN);

        if (!adminPresent.isPresent()) {
            User user = User.builder()
                    .password(hashPassword)
                    .login(userForm.getLogin())
                    .email(userForm.getEmail())
                    .counter(counter)
                    .address(address)
                    .role(Role.ADMIN)
                    .state(State.ACTIVE)

                    .build();
            System.out.println(user.toString());
            usersRepository.save(user);

        } else {
            User user = User.builder()
                    .password(hashPassword)
                    .login(userForm.getLogin())
                    .email(userForm.getEmail())
                    .counter(counter())
                    .address(address)
                    .role(Role.USER)
                    .state(State.ACTIVE)
                    .build();
            System.out.println(user.toString());
            usersRepository.save(user);
        }
    }

    private Integer counter() {
        List<User> allByCounter =  usersRepository.findAll();
        int size = allByCounter.size();
        return counter + size + 1;
    }
}
