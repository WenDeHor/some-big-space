package com.myhome.controllers;


import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.models.VideoBoxAdmin;
import com.myhome.repository.MyFriendsRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.repository.VideoBoxAdminRepository;
import com.myhome.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class PagesControllers {
    //    Don't let the sun go down until you keep your promises
    // Картінки підгружати на різні кімнати
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final MyFriendsRepository myFriendsRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;

    public PagesControllers(PublicationRepository publicationRepository, UserRepository userRepository, MyFriendsRepository myFriendsRepository, VideoBoxAdminRepository videoBoxAdminRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.myFriendsRepository = myFriendsRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
    }

//    @GetMapping("/admin-mine/admin-video")
//    public String adminNewVideo(Authentication authentication, Model model) {
//
//        List<VideoBoxAdmin> videoBoxAdminList = new ArrayList<>();
//        allByAddressAdmin.forEach(videoBoxAdminList::add);
//        videoBoxAdminList.sort(Comparator.comparing(VideoBoxAdmin::getIdVideoBox).reversed());
//
//        model.addAttribute("videoBoxAdminList", videoBoxAdminList);
//        model.addAttribute("title", "Admin Page");
//        return "admin-page-video-publication";
//    }

    @GetMapping("/")
    public String home(Model model) {
        Date date = new Date();
        Iterable<VideoBoxAdmin> firstByDate = videoBoxAdminRepository.findAll();
        List<VideoBoxAdmin> video = new ArrayList<>((Collection<? extends VideoBoxAdmin>) firstByDate);
        int size = video.size();
        VideoBoxAdmin videoBoxAdmin = video.get(size - 1);

        Iterable<PublicationUser> publications = publicationRepository.findAll();
        System.out.println(size);
        model.addAttribute("videoBoxAdmin", videoBoxAdmin);
        model.addAttribute("publications", publications);
        model.addAttribute("title", "MYHOME");
        return "mine-page";
    }


    @GetMapping("/admin-page")
    public String userPage() {
        return "redirect:/admin-mine/users";
    }

    @GetMapping("/user-page")
    public String adminPage() {
        return "userPage";
    }

    @GetMapping("/study")
    public String studyWritePublication() {
        return "redirect:/study/read-publications";
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
