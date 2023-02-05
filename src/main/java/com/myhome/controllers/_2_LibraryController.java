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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.myhome.controllers.factory.MarkFactory.getMarkFromFactory;

@Controller
public class _2_LibraryController {
    private final MyFriendsRepository myFriendsRepository;
    private final UserRepository userRepository;
    private final CompositionRepository compositionRepository;
    private final CompressorImgToJpg compressorImgToJpg;
    private final EvaluateRepository evaluateRepository;
    private final CommentsRepository commentsRepository;
    private final MetricsService metricsService;

    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_titleText = 150; //chars
    private int limit_shortText = 1000; //chars
    private int limit_fullText = 20000; //chars

    public _2_LibraryController(MyFriendsRepository myFriendsRepository, UserRepository userRepository, CompositionRepository compositionRepository, CompressorImgToJpg compressorImgToJpg, EvaluateRepository evaluateRepository, CommentsRepository commentsRepository, MetricsService metricsService) {
        this.myFriendsRepository = myFriendsRepository;
        this.userRepository = userRepository;
        this.compositionRepository = compositionRepository;
        this.compressorImgToJpg = compressorImgToJpg;
        this.evaluateRepository = evaluateRepository;
        this.commentsRepository = commentsRepository;
        this.metricsService = metricsService;
    }

    @Transactional
    @GetMapping("/library/read-all-competitive-composition")
    public String readCompetitiveComposition(Model model, HttpServletRequest request) {
        metricsService.startMetricsCheck(request, "/users/read-all-competitive-composition");
        List<Composition> compositionList = compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE);
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());

        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "Read competitive composition");

        return "library-read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/library/read-all-my-composition")
    public String readAllComposition(Authentication authentication, Model model) {
        List<Composition> compositionList = compositionRepository.findAllByEmail(getUserEmail(authentication));
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "Read Composition");
        return "library-read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/read-competitive-one-composition/{id}")
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
                    return "library-read-competitive-one-composition-off-mark";
                }
            }
            model.addAttribute("compositionOne", oneById.get());
            model.addAttribute("title", "Read Composition");
            return "library-read-competitive-one-composition-on-mark";
        }
        return "redirect:/library/read-all-competitive-composition";
    }

    @PostMapping("/library/evaluation-one-composition")
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
        return "redirect:/library/read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/library/read-friends-composition")
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
        return "library-read-all-friend-composition";
    }

    @Transactional
    @GetMapping("/library/read-friend-one-composition/{id}")
    public String readFriendOneComposition(@PathVariable(value = "id") Long id, Authentication authentication, Model model) {
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            model.addAttribute("title", "Робота товариша");
            model.addAttribute("compositionOne", oneById.get());
            return "library-read-one-friend-composition";
        }
        return "redirect:/library/read-friends-composition";
    }


    @Transactional
    @GetMapping("/library/wright-composition")
    public String wrightComposition(Model model) {
        model.addAttribute("title", "Wright Composition");
        return "library-write-composition";
    }

    @Transactional
    @PostMapping("/library/save-composition")
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
            return "redirect:/library/read-all-my-composition";
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

        return "library-write-error-save-page";
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

    private Genre getGenre(String genreString) {
        Map<String, Genre> genreMap = new HashMap<>();
        genreMap.put("FANTASY", Genre.FANTASY);
        genreMap.put("NOVEL", Genre.NOVEL);
        genreMap.put("TALE", Genre.TALE);
        genreMap.put("STORIES", Genre.STORIES);
        return genreMap.get(genreString);
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
