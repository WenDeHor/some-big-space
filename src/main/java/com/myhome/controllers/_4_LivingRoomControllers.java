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

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Date;
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
                                    HttpServletRequest request,
                                    Model model) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<Family> myFamilyList = familyRepository.findAllByIdUser(user.getId());
        model.addAttribute("myFamilyList", myFamilyList);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-family";
    }

    @GetMapping("/living/friends/friends")
    public String livingAllMyFriends(Authentication authentication,
                                     Model model,
                                     HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<Friends> myFriendsList = friendsRepository.findAllByIdUser(user.getId());
        model.addAttribute("myFriendsList", myFriendsList);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-friends";
    }

    @GetMapping("/living/friends/search")
    public String findFriendsPage(Model model,
                                  HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/search/find")
    public String findFriends(@RequestParam String userLogin,
                              Authentication authentication,
                              Model model,
                              HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int userId = user.getId();
        List<Integer> integerList = Stream
                .concat(friendsRepository.findAllByIdUser(userId).stream().map(Friends::getIdFriend),
                        familyRepository.findAllByIdUser(userId).stream().map(Family::getIdFamily))
                .collect(Collectors.toList());
        List<User> users = userRepository.findAllByLoginContainingIgnoreCase(userLogin).stream()
                .filter(u -> u.getId() != userId
                        && !integerList.contains(u.getId()))
                .collect(Collectors.toList());
        model.addAttribute("all", users);
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/friends/{id}/add")
    public String livingFriendsAdd(@PathVariable(value = "id") int id,
                                   Authentication authentication,
                                   Model model) {
        User user = getUser(authentication);
        Optional<Friends> userFriendOptional = friendsRepository.findByIdFriendAndIdUser(id, user.getId());
        if (!userFriendOptional.isPresent()) {//no present in my friends
            Optional<User> userOptional = userRepository.findOneById(id);
            if (userOptional.isPresent()) { //is user present in app
                User userFriend = userOptional.get();
                Friends newFriend = new Friends();
                newFriend.setIdUser(user.getId());
                newFriend.setIdFriend(id);
                newFriend.setAddressFriend(userFriend.getAddress());
                friendsRepository.save(newFriend);
            }
        }
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/family/{id}/add")
    public String livingFamilyAdd(@PathVariable(value = "id") int id,
                                  Authentication authentication,
                                  Model model) {
        User user = getUser(authentication);
        Optional<Family> userFamilyOptional = familyRepository.findByIdFamilyAndIdUser(id, user.getId());
        if (!userFamilyOptional.isPresent()) {//no present in my family
            Optional<User> userOptional = userRepository.findOneById(id);
            if (userOptional.isPresent()) { //is user present in app
                User userFamily = userOptional.get();
                Family newFamily = new Family();
                newFamily.setIdUser(user.getId());
                newFamily.setIdFamily(id);
                newFamily.setAddressFamily(userFamily.getAddress());
                familyRepository.save(newFamily);
            }
        }
        model.addAttribute("title", LIVING_ROOM);
        return "living-friends-search";
    }

    @GetMapping("/living/friends/family/{id}/remove")
    public String livingFamilyRemove(@PathVariable(value = "id") int id,
                                     Authentication authentication) {
        User user = getUser(authentication);
        Optional<Family> optionalFamily = familyRepository.findByIdFamilyAndIdUser(id, user.getId());
        optionalFamily.ifPresent(familyRepository::delete);
        return "redirect:/living/friends/family";
    }

    @GetMapping("/living/friends/friend/{id}/remove")
    public String livingFriendRemove(@PathVariable(value = "id") int id,
                                     Authentication authentication) {
        User user = getUser(authentication);
        Optional< Friends> friendsOptional = friendsRepository.findByIdFriendAndIdUser(id, user.getId());
        friendsOptional.ifPresent(friendsRepository::delete);
        return "redirect:/living/friends/friends";
    }

    @GetMapping("/living/news/video")
    public String livingNewVideo(Authentication authentication,
                                 Model model,
                                 HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<VideoBox> allVideoBox = videoBoxRepository.findAllByIdUser(user.getId()).stream()
                .sorted(Comparator.comparing(VideoBox::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("videoBoxList", allVideoBox);
        model.addAttribute("title", LIVING_ROOM);
        return "living-news-video";
    }

    @GetMapping("/living/news/video/{idVideoBox}/remove")
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") int idVideoBox,
                                        Authentication authentication) {
        User user = getUser(authentication);
        Optional<VideoBox> videoBoxOptional = videoBoxRepository.findByIdAndIdUser(idVideoBox, user.getId());
        videoBoxOptional.ifPresent(videoBoxRepository::delete);
        return "redirect:/living/news/video";
    }

    @GetMapping("/living/news/add")
    public String livingNewsAdd(Model model,
                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        model.addAttribute("title", LIVING_ROOM);
        return "living-news-add";
    }

    @PostMapping("/living/news/add/video")
    public String livingNewsAddVideo(Authentication authentication,
                                     @RequestParam("linkToVideo") String linkToVideo) {

        User user = getUser(authentication);
        String url = createURL(parseNormalURL(linkToVideo));

        VideoBox videoBox = new VideoBox();
        videoBox.setIdUser(user.getId());
        videoBox.setLinkToVideo(url);
        videoBox.setDate(new Date());
        videoBoxRepository.save(videoBox);
        return "redirect:/living/news/video";
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

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }
}
