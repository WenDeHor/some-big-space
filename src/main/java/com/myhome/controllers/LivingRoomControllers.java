package com.myhome.controllers;

import com.myhome.models.*;
import com.myhome.repository.MyFriendsRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LivingRoomControllers {
    private final MyFriendsRepository myFriendsRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    public LivingRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, MyFriendsRepository myFriendsRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.myFriendsRepository = myFriendsRepository;
    }

    @GetMapping("/living/publications")
    public String livingReadPublications(Authentication authentication, Model model) {
        Iterable<PublicationUser> publications = publicationRepository.findAll();
        String userAddress = findUserAddress(authentication);
        Iterable<MyFriends> allByAddressMyFriends = myFriendsRepository.findAllByAddressUser(userAddress);
        List<MyFriends> myFriendsList = new ArrayList<>();
        allByAddressMyFriends.forEach(myFriendsList::add);
        int size = myFriendsList.size();

        List<PublicationUser> publicationUserList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            MyFriends myFriends = myFriendsList.get(0);
            String addressMyFriends = myFriends.getAddressMyFriends();
            Iterable<PublicationUser> allByAddress = publicationRepository.findAllByAddress(addressMyFriends);
            allByAddress.forEach(publicationUserList::add);
        }
        Set<PublicationUser>publicationUserSet=new HashSet<>(publicationUserList);

        model.addAttribute("publications", publicationUserSet);
        model.addAttribute("title", "LIVING ROOM");
        return "living-read-publications";
    }

    @GetMapping("/living/friends")
    public String livingFriends(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);
        Iterable<MyFriends> allByAddressMyFriends = myFriendsRepository.findAllByAddressUser(userAddress);
        List<MyFriends> myFriendsList = new ArrayList<>();
        allByAddressMyFriends.forEach(myFriendsList::add);
        model.addAttribute("myFriendsList", myFriendsList);
        model.addAttribute("title", "LIVING ROOM");
        return "living-friends";
    }

    @GetMapping("/living/friends/add")
    public String cookBookUpdate(@RequestParam String userNameOrEmail,
                                 Model model) throws IOException {
        List<User> all = userRepository.findAll();
        List<User> collectLogin = all.stream()
                .filter(p -> p.getLogin().equals(userNameOrEmail))
                .collect(Collectors.toList());
        List<User> collectEmail = all.stream()
                .filter(p -> p.getEmail().equals(userNameOrEmail))
                .collect(Collectors.toList());

        List<User> userList = new ArrayList<>();
        userList.addAll(collectLogin);
        userList.addAll(collectEmail);
        Set<User> userSetUsers = new HashSet<>(userList);
        model.addAttribute("all", userSetUsers);

        model.addAttribute("title", "LIVING ROOM");
        return "living-friends";
    }

    @GetMapping("/living/friends/{id}/add")
    public String livingFriendsAdd(@PathVariable(value = "id") Long id, Authentication authentication, Model model) {
        Optional<User> oneByIdUser = userRepository.findOneByIdUser(id);
        String addressMyFriends = oneByIdUser.get().getAddress();
        String userAddress = findUserAddress(authentication);
        Optional<MyFriends> allByAddressUserAndAddressMyFriends = myFriendsRepository.findAllByAddressUserAndAddressMyFriends(userAddress, addressMyFriends);
        if (userAddress.equals(addressMyFriends) || allByAddressUserAndAddressMyFriends.isPresent()) {
            return "redirect:/living/friends";
        } else {
            MyFriends myFriends = new MyFriends();
            myFriends.setAddressUser(userAddress);
            myFriends.setAddressMyFriends(addressMyFriends);
            myFriendsRepository.save(myFriends);
        }
        return "redirect:/living/friends";
    }

    @GetMapping("/living/friends/{id}/remove")
    public String livingFriendsRemove(@PathVariable(value = "id") Long id, Model model) {
        MyFriends myFriends = myFriendsRepository.findById(id).orElseThrow(null);
        myFriendsRepository.delete(myFriends);
        return "redirect:/living/friends";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
