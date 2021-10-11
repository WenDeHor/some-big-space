package com.myhome.service;


import com.myhome.forms.UserForm;
import com.myhome.models.Role;
import com.myhome.models.State;
import com.myhome.models.User;
import com.myhome.models.UserPhoto;
import com.myhome.repository.UserPhotoRepository;
import com.myhome.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoRepository userPhotoRepository;
    private final static Integer counter = 100;

    public RegistrationServiceImpl(UserRepository usersRepository, PasswordEncoder passwordEncoder, UserPhotoRepository userPhotoRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.userPhotoRepository = userPhotoRepository;
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
            createUserPhotoOnMinePage(address);


        }
    }

    private void createUserPhotoOnMinePage(String address) {
        byte[] inputBytes = new byte[0];
        try {
            inputBytes = Files.readAllBytes(Paths.get("src/main/resources/static/img/mine-photo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserPhoto userFirst = new UserPhoto();
        userFirst.setFullText("Tell us about yourself and your family");
        userFirst.setImage(inputBytes);
        userFirst.setAddress(address);
        userFirst.setType("image/jpg");
        userFirst.setName("mine-photo.jpg");
        userPhotoRepository.save(userFirst);
    }


    private Integer counter() {
        List<User> allByCounter = usersRepository.findAll();
        int size = allByCounter.size();
        return counter + size + 1;
    }
}
