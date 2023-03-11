package com.myhome.db;

import com.myhome.models.Role;
import com.myhome.models.State;
import com.myhome.models.User;
import com.myhome.models.UserPhoto;
import com.myhome.repository.UserPhotoRepository;
import com.myhome.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

@Controller
public class DbBuilder {
    private final String settlement = "New_Apple";
    private final UserRepository usersRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final static Integer counter = 100;

    public DbBuilder(UserRepository usersRepository, UserPhotoRepository userPhotoRepository) {
        this.usersRepository = usersRepository;
        this.userPhotoRepository = userPhotoRepository;
    }

    @GetMapping("/insert-db")
    private String insertDb() {
        for (int i = 0; i < 1000; i++) {
            createUser(i + 1);
        }
        return "mine-page";
    }

    private void createUser(int id) {
        String string = Integer.valueOf(id).toString();
        User user = new User();
        user.setLogin(string);
        user.setSettlement(settlement);
        user.setEmail(string + "@gmail.com");
        user.setPassword("$2a$12$5H7yKftLTxCHv8HttZKZE./FCLcAhrYUgY99WqpsjcDlXn7cVKKIW");
        user.setCurrencyCode("UA");
        user.setAddress(string + " form " + settlement);
        user.setRole(Role.USER);
        user.setState(State.ACTIVE);
        user.setDate(new Date());
        usersRepository.save(user);
        createUserPhotoOnMinePage(string + " form " + settlement);
    }
    private void createUserPhotoOnMinePage(String address) {
        byte[] inputBytes = new byte[0];
        try {
            inputBytes = Files.readAllBytes(Paths.get("src/main/resources/static/img/mine-photo.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        UserPhoto userFirst = new UserPhoto();
        userFirst.setFullText("Розкажіть про свою сім’ю");
        userFirst.setImage(inputBytes);
        userFirst.setAddress(address);
        userPhotoRepository.save(userFirst);
    }
}
