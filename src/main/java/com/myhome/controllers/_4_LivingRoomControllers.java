package com.myhome.controllers;

import com.myhome.models.Family;
import com.myhome.models.Friends;
import com.myhome.models.User;
import com.myhome.models.VideoBox;
import com.myhome.repository.FamilyRepository;
import com.myhome.repository.FriendsRepository;
import com.myhome.repository.UserRepository;
import com.myhome.repository.VideoBoxRepository;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.MetricsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class _4_LivingRoomControllers {
    private final UserRepository userRepository;
    private final VideoBoxRepository videoBoxRepository;
    private final FriendsRepository friendsRepository;
    private final FamilyRepository familyRepository;

    private final String LIVING_ROOM = "Вітальня";

    private final MetricsService metricsService;

    public _4_LivingRoomControllers(UserRepository userRepository, VideoBoxRepository videoBoxRepository, FriendsRepository friendsRepository, FamilyRepository familyRepository, MetricsService metricsService) {
        this.userRepository = userRepository;
        this.videoBoxRepository = videoBoxRepository;
        this.friendsRepository = friendsRepository;
        this.familyRepository = familyRepository;
        this.metricsService = metricsService;
    }

    private final String YOUTUBE = "https://www.youtube.com/embed/";

    @GetMapping("/living/friends/family")
    public String livingAllMyFamily(Authentication authentication,
                                    Model model) {
        Long userId = findUserId(authentication);
        List<Family> myFamilyList = familyRepository.findAllByIdUser(userId);
        model.addAttribute("myFamilyList", myFamilyList);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-family";
    }

    @GetMapping("/living/friends/friends")
    public String livingAllMyFriends(Authentication authentication,
                                     Model model) {
        Long userId = findUserId(authentication);
        List<Friends> myFriendsList = friendsRepository.findAllByIdUser(userId);
        model.addAttribute("myFriendsList", myFriendsList);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-friends";
    }

    @GetMapping("/living/friends/search")
    public String findFriendsPage(Model model) {
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/search/find")
    public String findFriends(@RequestParam String userLogin,
                              Authentication authentication,
                              Model model) {
        Long userId = findUserId(authentication);
        List<Long> integerList = Stream
                .concat(friendsRepository.findAllByIdUser(userId).stream().map(Friends::getIdFriend),
                        familyRepository.findAllByIdUser(userId).stream().map(Family::getIdFamily))
                .collect(Collectors.toList());
        List<User> users = userRepository.findAllByLoginContainingIgnoreCase(userLogin).stream()
                .filter(user -> !user.getIdUser().equals(userId))
                .filter(friend -> !integerList.contains(friend.getIdUser()))
                .collect(Collectors.toList());
        model.addAttribute("all", users);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/friends/{id}/add")
    public String livingFriendsAdd(@PathVariable(value = "id") Long id,
                                   Authentication authentication,
                                   Model model) {
        Long userId = findUserId(authentication);

        Friends newFriend = new Friends();
        newFriend.setIdUser(userId);
        newFriend.setIdFriend(id);
        newFriend.setAddressFriend(getAddress(id));
        friendsRepository.save(newFriend);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/family/{id}/add")
    public String livingFamilyAdd(@PathVariable(value = "id") Long id,
                                  Authentication authentication,
                                  Model model) {
        Long userId = findUserId(authentication);

        Family newFamily = new Family();
        newFamily.setIdUser(userId);
        newFamily.setIdFamily(id);
        newFamily.setAddressFamily(getAddress(id));
        familyRepository.save(newFamily);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    private String getAddress(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get().getAddress();
        }
        return "";
    }

    @GetMapping("/living/friends/family/{id}/remove")
    public String livingFamilyRemove(@PathVariable(value = "id") Long id) {
        Family family = familyRepository.findById(id).orElseThrow(null);
        familyRepository.delete(family);
        return "redirect:/living/friends/family";
    }

    @GetMapping("/living/friends/friend/{id}/remove")
    public String livingFriendRemove(@PathVariable(value = "id") Long id) {
        Friends friend = friendsRepository.findById(id).orElseThrow(null);
        friendsRepository.delete(friend);
        return "redirect:/living/friends/friends";
    }

    @GetMapping("/living/news/video")
    public String livingNewVideo(Authentication authentication,
                                 Model model) {
        String userAddress = findUserAddress(authentication);

        Iterable<VideoBox> allVideoBox = videoBoxRepository.findAllByAddressUser(userAddress);
        List<VideoBox> videoBoxList = new ArrayList<>();
        allVideoBox.forEach(videoBoxList::add);
        videoBoxList.sort(Comparator.comparing(VideoBox::getIdVideoBox).reversed());

        model.addAttribute("videoBoxList", videoBoxList);
        model.addAttribute("title", LIVING_ROOM);
        return "living-news-video";
    }

    @GetMapping("/living/news/video/{idVideoBox}/remove")
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") Long idVideoBox) {
        VideoBox videoBox = videoBoxRepository.findById(idVideoBox).orElseThrow(null);
        videoBoxRepository.delete(videoBox);
        return "redirect:/living/news/video";
    }

    @GetMapping("/living/news/add")
    public String livingNewsAdd(Model model) {
        model.addAttribute("title", LIVING_ROOM);
        return "living-news-add";
    }

    @PostMapping("/living/news/add/video")
    public String livingNewsAddVideo(Authentication authentication,
                                     @RequestParam("linkToVideo") String linkToVideo) {

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

    private Long findUserId(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getIdUser();
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
