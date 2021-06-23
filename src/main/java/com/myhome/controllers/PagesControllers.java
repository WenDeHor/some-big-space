package com.myhome.controllers;


import com.myhome.models.CookBook;
import com.myhome.models.MyFriends;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.repository.MyFriendsRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PagesControllers {
    //    Don't let the sun go down until you keep your promises
    // Картінки підгружати на різні кімнати
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final MyFriendsRepository myFriendsRepository;

    public PagesControllers(PublicationRepository publicationRepository, UserRepository userRepository, MyFriendsRepository myFriendsRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.myFriendsRepository = myFriendsRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Iterable<PublicationUser> publications = publicationRepository.findAll();
        model.addAttribute("publications", publications);
        model.addAttribute("title", "MYHOME");
        return "mine-page";
    }


    @GetMapping("/user-page")
    public String userPage() {
        return "userPage";
    }

    @GetMapping("/study")
    public String studyWritePublication() {
        return "study-write-publication";
    }

    @GetMapping("/kitchen")
    public String kitchenReadCookbook() {
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/living")
    public String livingReadPublications(Authentication authentication, Model model) {
//        Iterable<PublicationUser> publications = publicationRepository.findAll();
//        String userAddress = findUserAddress(authentication);
//       Optional<MyFriends> allByAddressUser = myFriendsRepository.findAllByAddressUser(userAddress);
//        List<MyFriends> myFriendsList = new ArrayList<>();
//        allByAddressUser.ifPresent(myFriendsList::add);

//        //TODO make class iterable MyFriendsRepository . ERROR
//        Iterable<PublicationUser> allByAddress = publicationRepository.findAllByAddress(allByAddressUser);
//        List<PublicationUser> publicationUserList = new ArrayList<>();
//        allByAddress.forEach(publicationUserList::add);
//        model.addAttribute("publications", publications);
        model.addAttribute("title", "LIVING ROOM");
        return "redirect:/living/publications";
    }

    @GetMapping("/safe")
    public String safeReadCookbook() {
        return "redirect:/safe/read-save-diary";
    }


    @GetMapping("/registration-error")
    public String registrationError(Model model) {
        model.addAttribute("title", "Registration-error");
        return "registration-error";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
