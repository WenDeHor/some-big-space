package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.models.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        String url = createURL(parseNormalURL(linkToVideo));
        VideoBox videoBox = new VideoBox();
        videoBox.setAddressUser(userAddress);
        videoBox.setLinkToVideo(url);
        LocalDate localDate = LocalDate.now();
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

    private List<Composition> convertText(List<Composition> publication) {
        List<Composition> composition = new ArrayList<>(publication);
        for (int i = 0; i < publication.size(); i++) {
            //ShortText
            String shortText = publication.get(i).getShortText();
            String trim3 = shortText.replace("&#160&#160 ", "");
            String trim4 = trim3.replace("<br>", "");
            composition.get(i).setShortText(trim4);
            //FullText
            String fullText = publication.get(i).getFullText();
            String trim1 = fullText.replace("&#160&#160 ", "");
            String trim2 = trim1.replace("<br>", "");
            composition.get(i).setFullText(trim2);
        }
        return composition;
    }

    private Genre getGenre(String genreString) {
        Map<String, Genre> genreMap = new HashMap<>();
        genreMap.put("FANTASY", Genre.FANTASY);
        genreMap.put("NOVEL", Genre.NOVEL);
        genreMap.put("TALE", Genre.TALE);
        genreMap.put("STORIES", Genre.STORIES);
        return genreMap.get(genreString);
    }

    private String convertTextWithFormatToCompositionSaveAndEdit(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private int countId(int count) {
        return ++count;
    }

    private boolean checkData(MultipartFile file, String titleText, String shortText, String fullText) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_titleText
                || shortText.toCharArray().length > limit_shortText
                || fullText.toCharArray().length > limit_fullText;
    }

    private String getErrorPage(MultipartFile file, String titleText, String shortText, String fullText, Model model) throws IOException {
        String stile = "crimson";
        String originalFilename = file.getOriginalFilename();
        int fileSize = file.getBytes().length / constant;
        int titleTextSize = titleText.toCharArray().length;
        int shortTextSize = shortText.toCharArray().length;
        int fullTextSize = fullText.toCharArray().length;
        if (file.getBytes().length / constant > limit_photo) {
            model.addAttribute("stile", stile);
        }

        model.addAttribute("originalFilename", originalFilename);
        model.addAttribute("fileSize", fileSize);

        model.addAttribute("title", titleText);
        model.addAttribute("titleTextSize", titleTextSize);

        model.addAttribute("short", shortText);
        model.addAttribute("shortTextSize", shortTextSize);

        model.addAttribute("full", fullText);
        model.addAttribute("fullTextSize", fullTextSize);
//        return "redirect:/users/wright-composition";

        return "living-write-error-save-page";
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

    private String parseUpdateURL(String url) {
        String[] cutLink = url.split(YOUTUBE);
        String[] cutName = cutLink[1].split("\\?");
        return cutName[0];
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
