package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.Buttons;
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
import static java.util.stream.Collectors.toList;

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
    private int limit_composition_titleText = 100; //chars
    private int limit_composition_shortText = 500; //chars
    private int limit_composition_fullText = 10000; //chars
    private int limit_composition_comment = 500; //chars
    private final static int LIMIT_LIST = 10;
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
        List<CompositionDTO> dtos = getAllCompetitiveComposition(idUser);
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<CompositionDTO> basePage = dtos.stream()
                .limit(LIMIT_LIST)
                .collect(toList());
        model.addAttribute("compositionList", basePage);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-competitive-composition";
    }

    @Transactional
    @GetMapping("/library/read-all-competitive-composition/{offset}")
    public String readCompetitiveCompositionByPages(@PathVariable(value = "offset") int offset,
                                                    Model model,
                                                    Authentication authentication,
                                                    HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<CompositionDTO> dtos = getAllCompetitiveComposition(idUser);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<CompositionDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("compositionList", offsetList);
            model.addAttribute("title", LIBRARY);
            return "library-read-all-competitive-composition";
        }
        List<CompositionDTO> offsetList = getOffsetListCompositionDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("compositionList", offsetList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-competitive-composition";
    }

    private String getCountPage(int size) {
        if (size < 60) {
            char[] chars = String.valueOf(size).toCharArray();
            if (chars.length < 2) {
                return "10";
            }
            return Integer.parseInt(String.valueOf(chars[0])) + 1 + "0";
        }
        return "60";
    }

    private List<CompositionDTO> getOffsetListCompositionDTO(List<CompositionDTO> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    private List<CompositionDTO> getAllCompetitiveComposition(int idUser) {
        return compositionRepository
                .findAllByPublicationAndIdUserNot(PublicationType.PUBLIC_TO_COMPETITIVE, idUser).stream()
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
    }

    @Transactional
    @GetMapping("/library/read-all-my-composition")
    public String readAllMyComposition(Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<CompositionDTO> dtos = getAllMyComposition(user);
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<CompositionDTO> basePage = dtos.stream()
                .limit(LIMIT_LIST)
                .collect(toList());
        model.addAttribute("compositionList", basePage);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-my-composition";
    }

    @Transactional
    @GetMapping("/library/read-all-my-composition/{offset}")
    public String readAllMyCompositionByPages(@PathVariable(value = "offset") int offset,
                                              Authentication authentication,
                                              Model model,
                                              HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<CompositionDTO> dtos = getAllMyComposition(user);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<CompositionDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("compositionList", offsetList);
            model.addAttribute("title", LIBRARY);
            return "library-read-all-my-composition";
        }
        List<CompositionDTO> offsetList = getOffsetListCompositionDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("compositionList", offsetList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-my-composition";
    }

    private List<CompositionDTO> getAllMyComposition(User user) {
        return compositionRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new CompositionDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getShortText(),
                        el.getGenre(),
                        HOST_NAME + "/library/read-my-one-composition/" + el.getId(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(CompositionDTO::getId).reversed())
                .collect(Collectors.toList());
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
            if (composition.getPublication().equals(PublicationType.NO_PUBLIC)
                    || composition.getPublication().equals(PublicationType.PUBLIC_TO_DELETE)) {
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
        if (compositionOptional.isPresent()) {
            Composition composition = compositionOptional.get();
            CompositionDTO compositionDTO = new CompositionDTO(
                    composition.getId(),
                    composition.getTitleText(),
                    convertTextToEdit(composition.getShortText()),
                    convertTextToEdit(composition.getFullText()),
                    converterImage(composition.getImage()));
            model.addAttribute("composition", compositionDTO);
            model.addAttribute("title", LIBRARY);
            return "library-edit-one-composition";
        }
        return "redirect:/library/read-all-my-composition";
    }

    @Transactional
    @PostMapping("/library/edit-composition/{id}/edit")
    public String compositionUpdate(@PathVariable(value = "id") int id,
                                    MultipartFile file,
                                    Authentication authentication,
                                    @RequestParam String genre,
                                    @RequestParam String titleText,
                                    @RequestParam String shortText,
                                    @RequestParam String fullText,
                                    Model model) throws IOException {
        if (checkData(file, titleText, shortText, fullText)) {
            return getErrorPage(file, titleText, shortText, fullText, model);
        } else {
            User user = getUser(authentication);
            Optional<Composition> compositionOptional = compositionRepository.findOneByIdUserAndId(user.getId(), id);
            if (compositionOptional.isPresent()) {
                Composition composition = compositionOptional.get();
                composition.setGenre(getGenre(genre));
                composition.setTitleText(titleText);
                composition.setShortText(convertTextToSave(shortText));
                composition.setFullText(convertTextToSave(fullText));
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
        }
        return "redirect:/library/read-all-my-composition";
    }

    private boolean isNotPresentImage(MultipartFile file) {
        return !Objects.equals(file.getContentType(), "application/octet-stream");
    }

    @Transactional
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
            if (composition.getPublication().equals(PublicationType.PUBLIC_TO_DELETE)) {
                return "redirect:/library/read-all-my-composition";
            }
            composition.setPublication(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN);
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
            compositionOptional.get().setPublication(PublicationType.PUBLIC_TO_FRIENDS);
            compositionRepository.save(compositionOptional.get());
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
                && compositionOptional.get().getPublication().equals(PublicationType.PUBLIC_TO_COMPETITIVE)) {
            Composition composition = compositionOptional.get();
            CompositionDTO compositionDTO = new CompositionDTO(
                    composition.getId(),
                    composition.getTitleText(),
                    composition.getFullText(),
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
                                 @RequestParam("comments") String comments,
                                 Model model) {
        if (checkEvaluationComments(comments)) {
            return getErrorPageByEvaluation(comments, model);
        } else {
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
            commentsOfComposition.setComments(convertTextToSave(comments));

            evaluateRepository.save(evaluate);
            commentsRepository.save(commentsOfComposition);
            return "redirect:/library/read-all-competitive-composition";
        }
    }

    private boolean checkEvaluationComments(String comments) {
        return comments.toCharArray().length > limit_composition_comment;
    }

    private String getErrorPageByEvaluation(String comments,
                                            Model model) {
        int commentsTextSize = comments.toCharArray().length;
        model.addAttribute("comments", comments);
        model.addAttribute("commentsTextSize", commentsTextSize);
        model.addAttribute("title", LIBRARY);
        return "library-write-error-evalue-page";
    }

    @Transactional
    @GetMapping("/library/read-friends-composition")
    public String readFriendComposition(Authentication authentication,
                                        Model model,
                                        HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<CompositionDTO> dtos = getFriendComposition(idUser);
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<CompositionDTO> basePage = dtos.stream()
                .limit(LIMIT_LIST)
                .collect(toList());
        model.addAttribute("friendsPublications", basePage);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-friend-composition";
    }

    @Transactional
    @GetMapping("/library/read-friends-composition/{offset}")
    public String readFriendCompositionByPages(@PathVariable(value = "offset") int offset,
                                               Authentication authentication,
                                               Model model,
                                               HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<CompositionDTO> dtos = getFriendComposition(idUser);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<CompositionDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("friendsPublications", offsetList);
            model.addAttribute("title", LIBRARY);
            return "library-read-all-friend-composition";
        }
        List<CompositionDTO> offsetList = getOffsetListCompositionDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("friendsPublications", offsetList);
        model.addAttribute("title", LIBRARY);
        return "library-read-all-friend-composition";
    }

    private List<CompositionDTO> getFriendComposition(int idUser) {
        List<Integer> integerList = Stream
                .concat(friendsRepository.findAllByIdUser(idUser).stream().map(Friends::getIdFriend),
                        familyRepository.findAllByIdUser(idUser).stream().map(Family::getIdFamily))
                .collect(Collectors.toList());
        return compositionRepository.findAllByIdUserIn(integerList).stream()
                .filter(a -> a.getPublication().equals(PublicationType.PUBLIC_TO_FRIENDS))
                .map(composition -> new CompositionDTO(
                        composition.getId(),
                        composition.getTitleText(),
                        composition.getShortText(),
                        composition.getGenre(),
                        HOST_NAME + "/library/read-friend-one-composition/" + composition.getId(),
                        converterImage(composition.getImage())))
                .collect(Collectors.toList());
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
                && compositionOptional.get().getPublication().equals(PublicationType.PUBLIC_TO_FRIENDS)) {
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
            composition.setPublication(PublicationType.NO_PUBLIC);
            composition.setTitleText(titleText);
            composition.setShortText(convertTextToSave(shortText));
            composition.setFullText(convertTextToSave(fullText));
            composition.setIdUser(user.getId());
            if (isNotPresentImage(file)) {// new photo
                ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
                composition.setImage(convert.img);
                compressorImgToJpg.deleteImage(convert.nameStart);
                compressorImgToJpg.deleteImage(convert.nameEnd);
            }
            compositionRepository.save(composition);
        }
        return "redirect:/library/read-all-my-composition";
    }

    private boolean checkData(MultipartFile file,
                              String titleText,
                              String shortText,
                              String fullText) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_composition_titleText
                || shortText.toCharArray().length > limit_composition_shortText
                || fullText.toCharArray().length > limit_composition_fullText;
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

        model.addAttribute("titleText", titleText);
        model.addAttribute("titleTextSize", titleTextSize);

        model.addAttribute("short", shortText);
        model.addAttribute("shortTextSize", shortTextSize);

        model.addAttribute("full", fullText);
        model.addAttribute("fullTextSize", fullTextSize);

        model.addAttribute("title", LIBRARY);
        return "library-write-error-save-page";
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

    private String convertTextToSave(String fullText) {
        String text = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text);
    }

    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private String convertTextToEdit(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }
}
