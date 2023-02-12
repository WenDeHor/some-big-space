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
//        String address = "CC:"+currency.getCurrencyCode() + "-UN:" + userForm.getLogin()+"-SB:" +counter();
        String address = userForm.getLogin().trim() + " from " + userForm.getSettlement();
        String hashPassword = passwordEncoder.encode(userForm.getPassword());
        Optional<User> adminPresent = usersRepository.findOneByRole(Role.ADMIN);

        Date date = new Date();
        if (!adminPresent.isPresent()) {
            User user = new User();
            user.setLogin(userForm.getLogin());
            user.setSettlement(userForm.getSettlement());
            user.setEmail(userForm.getEmail());
            user.setCounter(counter);
            user.setPassword(hashPassword);
            user.setCurrencyCode(currency.getCurrencyCode());
            user.setAddress(address);
            user.setRole(Role.ADMIN);
            user.setState(State.ACTIVE);
            user.setDate(date);
            usersRepository.save(user);

        } else {
            User user = new User();
            user.setLogin(userForm.getLogin());
            user.setSettlement(userForm.getSettlement());
            user.setEmail(userForm.getEmail());
            user.setCounter(counter());
            user.setPassword(hashPassword);
            user.setCurrencyCode(currency.getCurrencyCode());
            user.setAddress(address);
            user.setRole(Role.USER);
            user.setState(State.ACTIVE);
            user.setDate(date);
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
