package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.ConvertFile;
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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.myhome.controllers.factory.MarkFactory.getMarkFromFactory;

@Controller
public class LivingRoomControllers {
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

    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_titleText = 150; //chars
    private int limit_shortText = 1000; //chars
    private int limit_fullText = 20000; //chars

    public LivingRoomControllers(MyFriendsRepository myFriendsRepository, UserRepository userRepository, PublicationRepository publicationRepository, NewsBoxRepository newsBoxRepository, VideoBoxRepository videoBoxRepository, CompositionRepository compositionRepository, CompressorImgToJpg compressorImgToJpg, EvaluateRepository evaluateRepository, CommentsRepository commentsRepository, MetricsService metricsService) {
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

    @Transactional
    @GetMapping("/users/read-all-my-composition")
    public String readAllComposition(Authentication authentication, Model model) {
        List<Composition> compositionList = compositionRepository.findAllByEmail(getUserEmail(authentication));
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "Read Composition");
        return "living-read-all-my-composition";
    }

    @Transactional
    @GetMapping("/users/read-one-composition/{id}")
    public String readOneComposition(@PathVariable(value = "id") Long id, Authentication authentication, Model model) {
        List<Composition> compositionList = compositionRepository.findAllByEmail(getUserEmail(authentication));
        Optional<Composition> composition = compositionList.stream().filter(f -> f.getId().equals(id)).findFirst();
        if (composition.isPresent() && composition.get().getPublicationType().equals(PublicationType.NO_PUBLIC)
                || composition.isPresent() && composition.get().getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
            model.addAttribute("compositionOne", composition.get());
            model.addAttribute("title", "Одна з моїх робіт");
            return "living-read-one-my-composition-all-button";
        } else {
            model.addAttribute("compositionOne", composition.get());
            model.addAttribute("title", "Одна з моїх робіт");
            return "living-read-one-my-composition-off-button";
        }
//        return "redirect:/users/read-all-my-composition";
    }

    @Transactional
    @GetMapping("/users/edit-composition/{id}/edit")
    public String editOneComposition(@PathVariable(value = "id") Long id, Model model) {
        if (!compositionRepository.existsById(id)) {
            return "redirect:/users/edit-composition";
        }
        Optional<Composition> compositionFind = compositionRepository.findById(id);
        List<Composition> compositionList = new ArrayList<>();
        compositionFind.ifPresent(compositionList::add);

        if (compositionFind.isPresent()) {
            Composition composition = convertText(compositionList).get(0);
            model.addAttribute("compositionList", composition);
        }
        return "living-edit-one-composition";
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

    @Transactional
    @PostMapping("/users/edit-composition/{id}/edit")
    public String compositionUpdate(@PathVariable(value = "id") Long id,
                                    MultipartFile file,
                                    Authentication authentication,
                                    @RequestParam String genre,
                                    @RequestParam String titleText,
                                    @RequestParam String shortText,
                                    @RequestParam String fullText) throws IOException {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        composition.setGenre(getGenre(genre));
        composition.setTitleText(titleText);
        composition.setShortText(convertTextWithFormatToCompositionSaveAndEdit(shortText));
        composition.setFullText(convertTextWithFormatToCompositionSaveAndEdit(fullText));
        composition.setLocalDate(composition.getLocalDate());
        composition.setName(file.getOriginalFilename());
        composition.setType(file.getContentType());

        if (file.getContentType().equals("application/octet-stream")) {
            Optional<Composition> byId = compositionRepository.findById(id);
            byte[] image = byId.get().getImage();
            composition.setImage(image);
        } else {// нове фото
            String userEmail = getUserEmail(authentication);
            int count = compositionRepository.findAllByEmail(userEmail).size();
            ConvertFile convert = compressorImgToJpg.convert(file, userEmail, countId(count));
            composition.setImage(convert.img);
//            composition.setImage(file.getBytes());
            compressorImgToJpg.deleteImage(convert.nameStart);
            compressorImgToJpg.deleteImage(convert.nameEnd);
        }
        compositionRepository.save(composition);
        return "redirect:/users/read-all-my-composition";
    }

    private Genre getGenre(String genreString) {
        Map<String, Genre> genreMap = new HashMap<>();
        genreMap.put("FANTASY", Genre.FANTASY);
        genreMap.put("NOVEL", Genre.NOVEL);
        genreMap.put("TALE", Genre.TALE);
        genreMap.put("STORIES", Genre.STORIES);
        return genreMap.get(genreString);
    }

    @GetMapping("/users/edit-composition/{id}/remove")
    public String compositionRemove(@PathVariable(value = "id") Long id, Model model) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        compositionRepository.delete(composition);
        return "redirect:/users/read-all-my-composition";
    }

    @GetMapping("/users/public-composition/{id}/to-coordination")
    public String compositionToCompetitive(@PathVariable(value = "id") Long id, Model model) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        if (composition.getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
            return "redirect:/users/read-all-my-composition";
        }
        composition.setPublicationType(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN);
        compositionRepository.save(composition);
        return "redirect:/users/read-all-my-composition";
    }

    @GetMapping("/users/public-composition/{id}/to-friends")
    public String compositionToFriends(@PathVariable(value = "id") Long id, Model model) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        composition.setPublicationType(PublicationType.PUBLIC_TO_FRIENDS);
        compositionRepository.save(composition);
        return "redirect:/users/read-all-my-composition";
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

    @Transactional
    @GetMapping("/users/read-all-competitive-composition")
    public String readCompetitiveComposition(Model model, HttpServletRequest request) {
        metricsService.startMetricsCheck(request, "/users/read-all-competitive-composition");
        List<Composition> compositionList = compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE);
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());

        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "Read competitive composition");

        return "living-read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/users/read-competitive-one-composition/{id}")
    public String readCompetitiveOneComposition(@PathVariable(value = "id") Long id,
                                                Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, "/users/read-competitive-one-composition/" + id);
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            if (oneById.get().getPublicationType().equals(PublicationType.PUBLIC_TO_COMPETITIVE)) {
                Optional<Evaluate> byEmailAppraiser = evaluateRepository.findByEmailAppraiserAndIdComposition(getUserEmail(authentication), id);

                if (byEmailAppraiser.isPresent()) {
                    model.addAttribute("compositionOne", oneById.get());
                    return "living-read-competitive-one-composition-off-mark";
                }
            }
            model.addAttribute("compositionOne", oneById.get());
            model.addAttribute("title", "Read Composition");
            return "living-read-competitive-one-composition-on-mark";
        }
        return "redirect:/users/read-all-competitive-composition";
    }

    @PostMapping("/users/evaluation-one-composition")
    public String saveEvaluation(Authentication authentication,
                                 @RequestParam("id") Long id,
                                 @RequestParam("environment") String environment,
                                 @RequestParam("characters") String characters,
                                 @RequestParam("atmosphere") String atmosphere,
                                 @RequestParam("plot") String plot,
                                 @RequestParam("impression") String impression,
                                 @RequestParam("comments") String comments) {
        String userEmail = getUserEmail(authentication);

        Evaluate evaluate = new Evaluate();
        evaluate.setLocalDate(new Date());
        evaluate.setIdComposition(id);
        evaluate.setEmailAppraiser(userEmail);
        evaluate.setEnvironment(getMarkFromFactory("ENVIRONMENT", environment));
        evaluate.setCharacters(getMarkFromFactory("CHARACTERS", characters));
        evaluate.setAtmosphere(getMarkFromFactory("ATMOSPHERE", atmosphere));
        evaluate.setPlot(getMarkFromFactory("PLOT", plot));
        evaluate.setImpression(getMarkFromFactory("IMPRESSION", impression));

        Comments commentsOfComposition = new Comments();
        commentsOfComposition.setEmail(userEmail);
        commentsOfComposition.setIdComposition(id);
        commentsOfComposition.setComments(convertTextWithFormatToCompositionSaveAndEdit(comments));

        evaluateRepository.save(evaluate);
        commentsRepository.save(commentsOfComposition);
        return "redirect:/users/read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/users/read-friends-composition")
    public String readFriendComposition(Authentication authentication, Model model) {
        String userEmail = getUserEmail(authentication);

        List<Optional<User>> optionalList = myFriendsRepository.findAllByAddressUser(userEmail).stream()
                .map(el -> userRepository.findOneByEmail(el.getAddressMyFriends()))
                .collect(Collectors.toList());
        List<User> userList = optionalList.stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        List<List<Composition>> compositionList = userList.stream()
                .map(el -> compositionRepository.findAllByEmail(el.getEmail()))
                .collect(Collectors.toList());
        List<Composition> allPublicToFriends = new ArrayList<>();
        for (List<Composition> compositions : compositionList) {
            List<Composition> collect = compositions.stream()
                    .filter(a -> a.getPublicationType().equals(PublicationType.PUBLIC_TO_FRIENDS))
                    .collect(Collectors.toList());
            allPublicToFriends.addAll(collect);
        }
        model.addAttribute("friendsPublications", allPublicToFriends);
        model.addAttribute("title", "Публікації друзів");
        return "living-read-all-friend-composition";
    }

    @Transactional
    @GetMapping("/users/read-friend-one-composition/{id}")
    public String readFriendOneComposition(@PathVariable(value = "id") Long id, Authentication authentication, Model model) {
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            model.addAttribute("title", "Робота товариша");
            model.addAttribute("compositionOne", oneById.get());
            return "living-read-one-friend-composition";
        }
        return "redirect:/users/read-friends-composition";
    }


    @Transactional
    @GetMapping("/users/wright-composition")
    public String wrightComposition(Model model) {
        model.addAttribute("title", "Wright Composition");
        return "living-write-composition";
    }

    @Transactional
    @PostMapping("/users/save-composition")
    public String saveComposition(Authentication authentication,
                                  Model model,
                                  MultipartFile file,
                                  @RequestParam("genre") String genre,
                                  @RequestParam("titleText") String titleText,
                                  @RequestParam("shortText") String shortText,
                                  @RequestParam("fullText") String fullText) throws IOException {

        if (checkData(file, titleText, shortText, fullText)) {
            return getErrorPage(file, titleText, shortText, fullText, model);
        } else {
            String userEmail = getUserEmail(authentication);
            int count = compositionRepository.findAllByEmail(userEmail).size();
            Composition composition = new Composition();
            composition.setLocalDate(new Date());
            composition.setGenre(getGenre(genre));
            composition.setPublicationType(PublicationType.NO_PUBLIC);
            composition.setTitleText(titleText);
            composition.setShortText(convertTextWithFormatToCompositionSaveAndEdit(shortText));
            composition.setFullText(convertTextWithFormatToCompositionSaveAndEdit(fullText));
            composition.setEmail(userEmail);
            composition.setName(file.getOriginalFilename());
            composition.setType(file.getContentType());
            ConvertFile convert = compressorImgToJpg.convert(file, userEmail, countId(count));
            composition.setImage(convert.img);
            compositionRepository.save(composition);
            compressorImgToJpg.deleteImage(convert.nameStart);
            compressorImgToJpg.deleteImage(convert.nameEnd);
            return "redirect:/users/read-all-my-composition";
        }
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

    private String getUserEmail(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getEmail();
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
