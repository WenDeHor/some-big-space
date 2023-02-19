package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.CompositionDTO;
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
    private final String HOST_NAME = "http://localhost:8080";


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
                                             Authentication authentication,
                                             HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<CompositionDTO> compositionList = compositionRepository
                .findAllByPublicationTypeAndIdUserNot(PublicationType.PUBLIC_TO_COMPETITIVE, idUser).stream()
                .map(el -> new CompositionDTO(
                        el.getId(),
                        el.getDate(),
                        el.getTitleText(),
                        el.getShortText(),
                        HOST_NAME + "/library/read-competitive-one-composition/" + el.getId(),
                        Base64.getMimeEncoder().encodeToString(el.getImage()),
                        el.getGenre()))
                .sorted(Comparator.comparing(CompositionDTO::getId).reversed())
                .collect(Collectors.toList());

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
        User user = getUser(authentication);
        List<CompositionDTO> compositionList = compositionRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new CompositionDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getShortText(),
                        el.getGenre(),
                        HOST_NAME + "/library/read-my-one-composition/" + el.getId(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(CompositionDTO::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/read-my-one-composition/{id}")
    public String readMyOneComposition(@PathVariable(value = "id") int id,
                                       Model model,
                                       Authentication authentication,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        Optional<Composition> compositionOptional = compositionRepository.findOneByIdUserAndId(user.getId(), id);
        if (compositionOptional.isPresent()) {
            Composition composition = compositionOptional.get();
            if (composition.getPublicationType().equals(PublicationType.NO_PUBLIC)
                    || composition.getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
                CompositionDTO compositionDTO = convertToCompositionDTO(composition);
                model.addAttribute("compositionOne", compositionDTO);
                model.addAttribute("title", LIBRARY);
                return "library-read-one-my-composition-all-button";
            } else {
                CompositionDTO compositionDTO = convertToCompositionDTO(composition);
                model.addAttribute("compositionOne", compositionDTO);
                model.addAttribute("title", LIBRARY);
                return "library-read-one-my-composition-off-button";
            }
        }
        return "redirect:/library/read-all-my-composition";
    }

    private CompositionDTO convertToCompositionDTO(Composition composition) {
        return new CompositionDTO(
                composition.getId(),
                composition.getTitleText(),
                composition.getFullText(),
                converterImage(composition.getImage()));
    }

    private String converterImage(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @Transactional
    @GetMapping("/library/edit-composition/{id}/edit")
    public String editOneComposition(@PathVariable(value = "id") int id,
                                     Authentication authentication,
                                     Model model) {
        User user = getUser(authentication);
        Optional<Composition> compositionOptional = compositionRepository.findAllByIdUserAndId(user.getId(), id);
        if (!compositionOptional.isPresent()) {
            return "redirect:/library/read-all-my-composition";
        } else {
            Composition composition = compositionOptional.get();
            CompositionDTO compositionDTO = new CompositionDTO(
                    composition.getId(),
                    composition.getTitleText(),
                    convertText(composition.getShortText()),
                    convertText(composition.getFullText()),
                    converterImage(composition.getImage()));
            model.addAttribute("composition", compositionDTO);
            model.addAttribute("title", LIBRARY);
            return "library-edit-one-composition";
        }
    }

    private String convertText(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }

    @Transactional
    @PostMapping("/library/edit-composition/{id}/edit")
    public String compositionUpdate(@PathVariable(value = "id") int id,
                                    MultipartFile file,
                                    Authentication authentication,
                                    @RequestParam String genre,
                                    @RequestParam String titleText,
                                    @RequestParam String shortText,
                                    @RequestParam String fullText) throws IOException {
        User user = getUser(authentication);
        Optional<Composition> compositionOptional = compositionRepository.findOneByIdUserAndId(user.getId(), id);
        if (compositionOptional.isPresent()) {
            Composition composition = compositionOptional.get();
            composition.setGenre(getGenre(genre));
            composition.setTitleText(titleText);
            composition.setShortText(convertTextWithFormatToCompositionSaveAndEdit(shortText));
            composition.setFullText(convertTextWithFormatToCompositionSaveAndEdit(fullText));
            composition.setDate(composition.getDate());

            if (isNotPresentImage(file)) {// new photo
                ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
                composition.setImage(convert.img);
                compressorImgToJpg.deleteImage(convert.nameStart);
                compressorImgToJpg.deleteImage(convert.nameEnd);
            }
            compositionRepository.save(composition);
            return "redirect:/library/read-all-my-composition";
        }
        return "redirect:/library/read-all-my-composition";
    }

    private boolean isNotPresentImage(MultipartFile file) {
        return !Objects.equals(file.getContentType(), "application/octet-stream");
    }

    @GetMapping("/library/edit-composition/{id}/remove")
    public String compositionRemove(@PathVariable(value = "id") Integer id,
                                    Authentication authentication) {
        User user = getUser(authentication);
        Optional<Composition> composition = compositionRepository.findAllByIdUserAndId(user.getId(), id);
        composition.ifPresent(compositionRepository::delete);
        return "redirect:/library/read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/public-composition/{id}/to-coordination")
    public String compositionToCompetitive(@PathVariable(value = "id") Integer id,
                                           Authentication authentication) {
        User user = getUser(authentication);
        Optional<Composition> compositionOptional = compositionRepository.findAllByIdUserAndId(user.getId(), id);
        if (compositionOptional.isPresent()) {
            Composition composition = compositionOptional.get();
            if (composition.getPublicationType().equals(PublicationType.PUBLIC_TO_DELETE)) {
                return "redirect:/library/read-all-my-composition";
            }
            composition.setPublicationType(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN);
            compositionRepository.save(composition);
            return "redirect:/library/read-all-my-composition";
        }
        return "redirect:/library/read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/public-composition/{id}/to-friends")
    public String compositionToFriends(@PathVariable(value = "id") Integer id,
                                       Authentication authentication) {
        User user = getUser(authentication);
        Optional<Composition> compositionOptional = compositionRepository.findAllByIdUserAndId(user.getId(), id);
        if (compositionOptional.isPresent()) {
            compositionOptional.get().setPublicationType(PublicationType.PUBLIC_TO_FRIENDS);
            compositionRepository.save(compositionOptional.get());
            return "redirect:/library/read-all-my-composition";
        }
        return "redirect:/library/read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/read-competitive-one-composition/{id}")
    public String readCompetitiveOneComposition(@PathVariable(value = "id") Integer id,
                                                Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> compositionOptional = compositionRepository.findOneById(id);
        if (compositionOptional.isPresent()
                && compositionOptional.get().getPublicationType().equals(PublicationType.PUBLIC_TO_COMPETITIVE)) {
            Composition composition = compositionOptional.get();
            CompositionDTO compositionDTO = new CompositionDTO(
                    composition.getId(),
                    composition.getTitleText(),
                    convertText(composition.getFullText()),
                    converterImage(composition.getImage()));
            User user = getUser(authentication);
            Optional<Evaluate> byIdAppraiser = evaluateRepository.findByIdAppraiserAndIdComposition(user.getId(), id);
            if (byIdAppraiser.isPresent()) {
                model.addAttribute("title", LIBRARY);
                model.addAttribute("compositionOne", compositionDTO);
                model.addAttribute("comments", findCommentsByIdComposition(id));
                return "library-read-competitive-one-composition-off-mark";
            }
            model.addAttribute("compositionOne", compositionDTO);
            model.addAttribute("title", LIBRARY);
            return "library-read-competitive-one-composition-on-mark";
        }
        return "redirect:/library/read-all-competitive-composition";
    }

    private List<String> findCommentsByIdComposition(int id) {
        List<String> allByIdComposition = commentsRepository.findAllByIdComposition(id).stream()
                .map(Comments::getComments)
                .collect(Collectors.toList());
        if (allByIdComposition.isEmpty()) {
            return new ArrayList<>();
        } else {
            return allByIdComposition;
        }
    }

    @PostMapping("/library/evaluation-one-composition")
    public String saveEvaluation(Authentication authentication,
                                 @RequestParam("id") int id,
                                 @RequestParam("environment") String environment,
                                 @RequestParam("characters") String characters,
                                 @RequestParam("atmosphere") String atmosphere,
                                 @RequestParam("plot") String plot,
                                 @RequestParam("impression") String impression,
                                 @RequestParam("comments") String comments) {
        User user = getUser(authentication);

        Evaluate evaluate = new Evaluate();
        evaluate.setDate(new Date());
        evaluate.setIdComposition(id);
        evaluate.setIdAppraiser(user.getId());
        evaluate.setEnvironment(getMarkFromFactory("ENVIRONMENT", environment));
        evaluate.setCharacters(getMarkFromFactory("CHARACTERS", characters));
        evaluate.setAtmosphere(getMarkFromFactory("ATMOSPHERE", atmosphere));
        evaluate.setPlot(getMarkFromFactory("PLOT", plot));
        evaluate.setImpression(getMarkFromFactory("IMPRESSION", impression));

        Comments commentsOfComposition = new Comments();
        commentsOfComposition.setIdComposition(id);
        commentsOfComposition.setIdUser(user.getId());
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
        User user = getUser(authentication);
        int idUser = user.getId();
        List<Integer> integerList = Stream
                .concat(friendsRepository.findAllByIdUser(idUser).stream().map(Friends::getIdFriend),
                        familyRepository.findAllByIdUser(idUser).stream().map(Family::getIdFamily))
                .collect(Collectors.toList());
        List<CompositionDTO> friendCompositions = compositionRepository.findAllByIdUserIn(integerList).stream()
                .filter(a -> a.getPublicationType().equals(PublicationType.PUBLIC_TO_FRIENDS))
                .map(composition -> new CompositionDTO(
                        composition.getId(),
                        composition.getTitleText(),
                        convertText(composition.getShortText()),
                        composition.getGenre(),
                        HOST_NAME + "/library/read-friend-one-composition/" + composition.getId(),
                        converterImage(composition.getImage())))
                .collect(Collectors.toList());
        model.addAttribute("friendsPublications", friendCompositions);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-friend-composition";
    }

    @Transactional
    @GetMapping("/library/read-friend-one-composition/{id}")
    public String readFriendOneComposition(@PathVariable(value = "id") int id,
                                           Model model,
                                           Authentication authentication,
                                           HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> compositionOptional = compositionRepository.findOneById(id);
        if (compositionOptional.isPresent()
                && compositionOptional.get().getPublicationType().equals(PublicationType.PUBLIC_TO_FRIENDS)) {
            int idUserComposition = compositionOptional.get().getIdUser();
            User user = getUser(authentication);
            int idUser = user.getId();
            List<Integer> integerList = Stream
                    .concat(friendsRepository.findAllByIdUser(idUser).stream().map(Friends::getIdFriend),
                            familyRepository.findAllByIdUser(idUser).stream().map(Family::getIdFamily))
                    .filter(el -> el.equals(idUserComposition))
                    .collect(Collectors.toList());
            if (integerList.size() > 0) {
                Composition composition = compositionOptional.get();
                CompositionDTO compositionDTO = new CompositionDTO(
                        composition.getId(),
                        composition.getTitleText(),
                        composition.getFullText(),
                        converterImage(composition.getImage()));
                model.addAttribute("title", LIBRARY);
                model.addAttribute("compositionOne", compositionDTO);
                return "library-read-one-friend-composition";
            }
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
            Composition composition = new Composition();
            composition.setDate(new Date());
            composition.setGenre(getGenre(genre));
            composition.setPublicationType(PublicationType.NO_PUBLIC);
            composition.setTitleText(titleText);
            composition.setShortText(convertTextWithFormatToCompositionSaveAndEdit(shortText));
            composition.setFullText(convertTextWithFormatToCompositionSaveAndEdit(fullText));
            composition.setIdUser(user.getId());
            ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
            composition.setImage(convert.img);
            compositionRepository.save(composition);
            compressorImgToJpg.deleteImage(convert.nameStart);
            compressorImgToJpg.deleteImage(convert.nameEnd);
            return "redirect:/library/read-all-my-composition";
        }
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

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }
}
