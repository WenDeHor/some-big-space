package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.models.MyFriends;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.models.VideoBox;
import com.myhome.repository.*;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.MetricsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class _4_LivingRoomControllers {
    private final MyFriendsRepository myFriendsRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final NewsBoxRepository newsBoxRepository;
    private final VideoBoxRepository videoBoxRepository;
    private final CompositionRepository compositionRepository;
    private final CompressorImgToJpg compressorImgToJpg;
    private final EvaluateRepository evaluateRepository;

    private final CommentsRepository commentsRepository;
    private final MetricsService metricsService;

    private final long PERIOD_METRICS = 30L;
    private static final String INCOGNITO = "Incognito";
    private static final String LOCAL_UI = "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";

    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_titleText = 150; //chars
    private int limit_shortText = 1000; //chars
    private int limit_fullText = 20000; //chars

    private final String YOUTUBE = "https://www.youtube.com/embed/";

    public _4_LivingRoomControllers(MyFriendsRepository myFriendsRepository, UserRepository userRepository, PublicationRepository publicationRepository, NewsBoxRepository newsBoxRepository, VideoBoxRepository videoBoxRepository, CompositionRepository compositionRepository, CompressorImgToJpg compressorImgToJpg, EvaluateRepository evaluateRepository, CommentsRepository commentsRepository, MetricsService metricsService) {
        this.myFriendsRepository = myFriendsRepository;
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.newsBoxRepository = newsBoxRepository;
        this.videoBoxRepository = videoBoxRepository;
        this.compositionRepository = compositionRepository;
        this.compressorImgToJpg = compressorImgToJpg;
        this.evaluateRepository = evaluateRepository;
        this.commentsRepository = commentsRepository;
        this.metricsService = metricsService;
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
        String url = createURL(parseNormalURL(linkToVideo));
        VideoBox videoBox = new VideoBox();
        videoBox.setAddressUser(userAddress);
        videoBox.setLinkToVideo(url);
        LocalDate localDate = LocalDate.now();
        videoBox.setLocalDate(localDate);
        videoBoxRepository.save(videoBox);
        return "redirect:/living/news/video";
    }


    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }

    private String createURL(String nameURL) {
        return YOUTUBE + nameURL + "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
    }

    private String parseNormalURL(String url) {
        String[] split = url.split("=");
        StringBuilder sb = new StringBuilder();
        char[] chars = split[1].trim().toCharArray();
        for (int i = 0; i < 11; i++) {
            sb.append(chars[i]);
        }
        return sb.toString();
    }
}
