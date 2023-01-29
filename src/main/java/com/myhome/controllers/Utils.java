package com.myhome.controllers;

import com.myhome.models.User;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
public class Utils {

    public static String getUserName(Authentication authentication, UserRepository userRepository) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getLogin();
    }

    static String errorIMG() {
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(new File("src\\main\\resources\\templates\\images\\errorIMGLetter.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }


}
