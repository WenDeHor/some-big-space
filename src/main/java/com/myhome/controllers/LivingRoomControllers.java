package com.myhome.controllers;

import com.myhome.models.*;
import com.myhome.repository.*;
import com.myhome.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LivingRoomControllers {
    private final MyFriendsRepository myFriendsRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final NewsBoxRepository newsBoxRepository;
    private final VideoBoxRepository videoBoxRepository;

    public LivingRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, MyFriendsRepository myFriendsRepository, NewsBoxRepository newsBoxRepository, VideoBoxRepository videoBoxRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.myFriendsRepository = myFriendsRepository;
        this.newsBoxRepository = newsBoxRepository;
        this.videoBoxRepository = videoBoxRepository;
    }

    @GetMapping("/living/publications")
    public String livingReadPublications(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);
        List<MyFriends> allByAddressMyFriends = myFriendsRepository.findAllByAddressUser(userAddress);
//        List<MyFriends> myFriendsList = new ArrayList<>(allByAddressMyFriends);
        List<PublicationUser> publicationUserList = new ArrayList<>();
        for (MyFriends myFriends : allByAddressMyFriends) {
            String addressMyFriends = myFriends.getAddressMyFriends();
            List<PublicationUser> allByAddress = publicationRepository.findAllByAddress(addressMyFriends);
            publicationUserList.addAll(allByAddress);
        }
        publicationUserList.sort(Comparator.comparing(PublicationUser::getDate).reversed());

        model.addAttribute("publications", publicationUserList);
        model.addAttribute("title", "LIVING ROOM");
        return "living-read-publications";
    }

    @GetMapping("/living/friends")
    public String livingFriends(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);
        List<MyFriends> myFriendsList = myFriendsRepository.findAllByAddressUser(userAddress);
        model.addAttribute("myFriendsList", myFriendsList);
        model.addAttribute("title", "LIVING ROOM");
        return "living-friends";
    }

    @GetMapping("/living/friends/add")
    public String cookBookUpdate(@RequestParam String userNameOrEmail,
                                 Model model) {
        List<User> all = userRepository.findAll();
        List<User> collectLogin = all.stream()
                .filter(p -> p.getLogin().equals(userNameOrEmail))
                .collect(Collectors.toList());
        List<User> collectEmail = all.stream()
                .filter(p -> p.getEmail().equals(userNameOrEmail))
                .collect(Collectors.toList());

        Set<User> userSetUsers = new HashSet<>();
        userSetUsers.addAll(collectLogin);
        userSetUsers.addAll(collectEmail);
//        Set<User> userSetUsers = new HashSet<>(userList);
        model.addAttribute("all", userSetUsers);
        model.addAttribute("title", "LIVING ROOM");
        return "living-friends";
    }

    @GetMapping("/living/friends/{id}/add")
    public String livingFriendsAdd(@PathVariable(value = "id") Long id, Authentication authentication) {
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


    @GetMapping("/living/news/site")
    public String livingNewsSite(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);

        Iterable<NewsBox> allNewsBox = newsBoxRepository.findAllByAddressUser(userAddress);
        List<NewsBox> newsBoxList = new ArrayList<>();
        allNewsBox.forEach(newsBoxList::add);
        newsBoxList.sort(Comparator.comparing(NewsBox::getIdNewsBox).reversed());

        model.addAttribute("newsBoxList", newsBoxList);
        model.addAttribute("title", "LIVING ROOM");
        return "living-news-site";
    }

    @GetMapping("/living/news/site/{idNewsBox}/remove")
    public String livingNewsSiteRemove(@PathVariable(value = "idNewsBox") Long idNewsBox, Model model) {
        NewsBox newsBox = newsBoxRepository.findById(idNewsBox).orElseThrow(null);
        newsBoxRepository.delete(newsBox);
        return "redirect:/living/news/site";
    }

    @GetMapping("/living/news/video")
    public String livingNewVideo(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);

        Iterable<VideoBox> allVideoBox = videoBoxRepository.findAllByAddressUser(userAddress);
        List<VideoBox> videoBoxList = new ArrayList<>();
        allVideoBox.forEach(videoBoxList::add);
        videoBoxList.sort(Comparator.comparing(VideoBox::getIdVideoBox).reversed());

        model.addAttribute("videoBoxList", videoBoxList);
        model.addAttribute("title", "LIVING ROOM");
        return "living-news-video";
    }

    @GetMapping("/living/news/video/{idVideoBox}/remove")
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") Long idVideoBox, Model model) {
        VideoBox videoBox = videoBoxRepository.findById(idVideoBox).orElseThrow(null);
        videoBoxRepository.delete(videoBox);
        return "redirect:/living/news/video";
    }

    @GetMapping("/living/news/add")
    public String livingNewsAdd(Model model) {
        model.addAttribute("title", "LIVING ROOM");
        return "living-news-add";
    }

    @PostMapping("/living/news/add/video")
    public String livingNewsAddVideo(Authentication authentication,
                                     Model model,
                                     @RequestParam("linkToVideo") String linkToVideo) throws IOException {

        String userAddress = findUserAddress(authentication);
        String url = "https://www.youtube.com/embed/" + linkToVideo + "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";

        LocalDate localDate = LocalDate.now();

        VideoBox videoBox = new VideoBox();
        videoBox.setAddressUser(userAddress);
        videoBox.setLinkToVideo(url);
        videoBox.setLocalDate(localDate);

        videoBoxRepository.save(videoBox);
        return "redirect:/living/news/video";
    }

    @PostMapping("/living/news/add/site")
    public String livingNewsAddSite(Authentication authentication,
                                    Model model,
                                    @RequestParam("linkToNews") String linkToNews) throws IOException {

        String userAddress = findUserAddress(authentication);
        LocalDate localDate = LocalDate.now();

        NewsBox newsBox = new NewsBox();
        newsBox.setAddressUser(userAddress);
        newsBox.setLinkToNews(linkToNews);
        newsBox.setLocalDate(localDate);

        newsBoxRepository.save(newsBox);
        return "redirect:/living/news/site";
    }





    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
