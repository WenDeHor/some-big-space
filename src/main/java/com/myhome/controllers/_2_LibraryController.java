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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.myhome.controllers.factory.MarkFactory.getMarkFromFactory;

@Controller
public class _2_LibraryController {
    private final UserRepository userRepository;
    private final CompositionRepository compositionRepository;
    private final CompressorImgToJpg compressorImgToJpg;
    private final EvaluateRepository evaluateRepository;
    private final CommentsRepository commentsRepository;
    private final MetricsService metricsService;
    private final FamilyRepository familyRepository;
    private final FriendsRepository friendsRepository;

    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_titleText = 150; //chars
    private int limit_shortText = 1000; //chars
    private int limit_fullText = 20000; //chars
    private final String LIBRARY = "Читальня";

    public _2_LibraryController(UserRepository userRepository, CompositionRepository compositionRepository, CompressorImgToJpg compressorImgToJpg, EvaluateRepository evaluateRepository, CommentsRepository commentsRepository, MetricsService metricsService, FamilyRepository familyRepository, FriendsRepository friendsRepository) {
        this.userRepository = userRepository;
        this.compositionRepository = compositionRepository;
        this.compressorImgToJpg = compressorImgToJpg;
        this.evaluateRepository = evaluateRepository;
        this.commentsRepository = commentsRepository;
        this.metricsService = metricsService;
        this.familyRepository = familyRepository;
        this.friendsRepository = friendsRepository;
    }

    @Transactional
    @GetMapping("/library/read-all-competitive-composition")
    public String readCompetitiveComposition(Model model,
                                             HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        List<Composition> compositionList = compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE);
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());

        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/library/read-all-my-composition")
    public String readAllComposition(Authentication authentication,
                                     Model model,
                                     HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        List<Composition> compositionList = compositionRepository.findAllByEmail(getUserEmail(authentication));
        compositionList.sort(Comparator.comparing(Composition::getId).reversed());
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/read-my-one-composition/{id}")
    public String readMyOneComposition(@PathVariable(value = "id") Long id,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> composition = compositionRepository.findOneById(id);
        if (composition.isPresent() && composition.get().getPublicationType().equals(PublicationType.NO_PUBLIC)
                || composition.isPresent() && composition.get().getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
            model.addAttribute("compositionOne", composition.get());
            model.addAttribute("title", LIBRARY);
            return "library-read-one-my-composition-all-button";
        } else {
            model.addAttribute("compositionOne", composition.get());
            model.addAttribute("title", LIBRARY);
            return "library-read-one-my-composition-off-button";
        }
    }

    @Transactional
    @GetMapping("/library/edit-composition/{id}/edit")
    public String editOneComposition(@PathVariable(value = "id") Long id,
                                     Model model) {
        Optional<Composition> compositionFind = compositionRepository.findById(id);
        if (!compositionFind.isPresent()) {
            return "redirect:/library/read-all-my-composition";
        } else {
            Composition composition = convertText(compositionFind.get());
            model.addAttribute("composition", composition);
            model.addAttribute("title", LIBRARY);
        }
        return "living-edit-one-composition";
    }

    private Composition convertText(Composition composition) {
        //ShortText
        String shortText = composition.getShortText();
        String trim3 = shortText.replace("&#160&#160 ", "");
        String trim4 = trim3.replace("<br>", "");
        composition.setShortText(trim4);
        //FullText
        String fullText = composition.getFullText();
        String trim1 = fullText.replace("&#160&#160 ", "");
        String trim2 = trim1.replace("<br>", "");
        composition.setFullText(trim2);
        return composition;
    }

    @Transactional
    @PostMapping("/library/edit-composition/{id}/edit")
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
        return "redirect:/library/read-all-my-composition";
    }

    @GetMapping("/library/edit-composition/{id}/remove")
    public String compositionRemove(@PathVariable(value = "id") Long id) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        compositionRepository.delete(composition);
        return "redirect:/library/read-all-my-composition";
    }

    @GetMapping("/library/public-composition/{id}/to-coordination")
    public String compositionToCompetitive(@PathVariable(value = "id") Long id) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        if (composition.getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
            return "redirect:/library/read-all-my-composition";
        }
        composition.setPublicationType(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN);
        compositionRepository.save(composition);
        return "redirect:/library/read-all-my-composition";
    }

    @GetMapping("/library/public-composition/{id}/to-friends")
    public String compositionToFriends(@PathVariable(value = "id") Long id) {
        Composition composition = compositionRepository.findById(id).orElseThrow(null);
        composition.setPublicationType(PublicationType.PUBLIC_TO_FRIENDS);
        compositionRepository.save(composition);
        return "redirect:/library/read-all-my-composition";
    }


    @GetMapping("/image/display/composition/{id}")
    @ResponseBody
    void showUserPagePhoto(@PathVariable("id") Long id,
                           HttpServletResponse response,
                           Optional<Composition> composition) throws ServletException, IOException {
        composition = compositionRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(composition.get().getImage());
        response.getOutputStream().close();
    }

    @Transactional
    @GetMapping("/library/read-competitive-one-composition/{id}")
    public String readCompetitiveOneComposition(@PathVariable(value = "id") Long id,
                                                Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            if (oneById.get().getPublicationType().equals(PublicationType.PUBLIC_TO_COMPETITIVE)) {
                Optional<Evaluate> byEmailAppraiser = evaluateRepository.findByEmailAppraiserAndIdComposition(getUserEmail(authentication), id);
                if (byEmailAppraiser.isPresent()) {
                    model.addAttribute("compositionOne", oneById.get());
                    model.addAttribute("title", LIBRARY);
                    return "library-read-competitive-one-composition-off-mark";
                }
            }
            model.addAttribute("compositionOne", oneById.get());
            model.addAttribute("title", LIBRARY);
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
    public String readFriendComposition(Authentication authentication,
                                        Model model,
                                        HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Long userId = findUserId(authentication);
        List<Long> integerList = Stream
                .concat(friendsRepository.findAllByIdUser(userId).stream().map(Friends::getIdFriend),
                        familyRepository.findAllByIdUser(userId).stream().map(Family::getIdFamily))
                .collect(Collectors.toList());
        List<Composition> friendCompositions = compositionRepository.findAllByUserIdIn(integerList)
                .stream()
                .filter(a -> a.getPublicationType().equals(PublicationType.PUBLIC_TO_FRIENDS))
                .collect(Collectors.toList());

        model.addAttribute("friendsPublications", friendCompositions);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-friend-composition";
    }

    private Long findUserId(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getIdUser();
    }

    @Transactional
    @GetMapping("/library/read-friend-one-composition/{id}")
    public String readFriendOneComposition(@PathVariable(value = "id") Long id,
                                           Model model,
                                           HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            model.addAttribute("title", LIBRARY);
            model.addAttribute("compositionOne", oneById.get());
            return "library-read-one-friend-composition";
        }
        return "redirect:/library/read-friends-composition";
    }


    @Transactional
    @GetMapping("/library/wright-composition")
    public String wrightComposition(Model model) {
        model.addAttribute("title", LIBRARY);
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
            User user = getUser(authentication);
            int count = compositionRepository.findAllByUserId(user.getIdUser()).size();
            Composition composition = new Composition();
            composition.setLocalDate(new Date());
            composition.setGenre(getGenre(genre));
            composition.setPublicationType(PublicationType.NO_PUBLIC);
            composition.setTitleText(titleText);
            composition.setShortText(convertTextWithFormatToCompositionSaveAndEdit(shortText));
            composition.setFullText(convertTextWithFormatToCompositionSaveAndEdit(fullText));
            composition.setEmail(user.getEmail());
            composition.setAddress(user.getAddress());
            composition.setUserId(user.getIdUser());
            composition.setName(file.getOriginalFilename());
            composition.setType(file.getContentType());
            ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail(), countId(count));
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

    private boolean checkData(MultipartFile file,
                              String titleText,
                              String shortText,
                              String fullText) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_titleText
                || shortText.toCharArray().length > limit_shortText
                || fullText.toCharArray().length > limit_fullText;
    }

    private String getErrorPage(MultipartFile file,
                                String titleText,
                                String shortText,
                                String fullText,
                                Model model) throws IOException {
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
        return "library-write-error-save-page";
    }

    private String convertTextWithFormatToCompositionSaveAndEdit(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    //TODO REGEX
    private String REGEX(String patternRegex,
                         String replace,
                         String text) {
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

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }
}
