package com.myhome.controllers;


import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.CompositionDTO;
import com.myhome.forms.ConvertFile;
import com.myhome.forms.UserPhotoDTO;
import com.myhome.models.*;
import com.myhome.repository.*;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.MetricsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class PagesControllers {
    private final CompositionRepository compositionRepository;
    private final UserRepository userRepository;
    private final CompressorImgToJpg compressorImgToJpg;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;
    private final CommentsRepository commentsRepository;


    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_fullText = 3000; //chars

    private final String HOST_NAME = "http://localhost:8080";

    private final String MY_HOME = "Мій дім";
    private final String STUDY_ROOM = "Моя кімната";
    private final String LIBRARY_ROOM = "Читальня";
    private final String SAFE_ROOM = "Робоча кімната";
    private final String LIVING_ROOM = "Вітальня";
    private final String KITCHEN_ROOM = "Кухня";

    private final String LINK_BASE = "https://www.youtube.com/embed/GYrwebcKoxE?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";

    public PagesControllers(CompositionRepository compositionRepository, UserRepository userRepository, CompressorImgToJpg compressorImgToJpg, VideoBoxAdminRepository videoBoxAdminRepository, UserPhotoRepository userPhotoRepository, PublicationPostAdminRepository publicationPostAdminRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService, CommentsRepository commentsRepository) {
        this.compositionRepository = compositionRepository;
        this.userRepository = userRepository;
        this.compressorImgToJpg = compressorImgToJpg;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.userPhotoRepository = userPhotoRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
        this.letterToADMINRepository = letterToADMINRepository;
        this.metricsService = metricsService;
        this.commentsRepository = commentsRepository;
    }

    @Transactional
    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        List<VideoBoxAdmin> videoBoxAdmins = videoBoxAdminRepository.findAll();
        int sizeVideoList = videoBoxAdmins.size();
        List<CompositionDTO> compositionDTO = getCompositionWithComments();
        List<PublicationPostAdmin> publications = publicationPostAdminRepository.findAll();
        List<PublicationPostAdmin> publicationPostAdminListTreeElements = getPublicationPostAdminList(publications);

        if (publicationPostAdminListTreeElements.isEmpty()) {
            model.addAttribute("publications", new ArrayList<>());
        } else {
            model.addAttribute("publications", publicationPostAdminListTreeElements);
        }

        if (compositionDTO.isEmpty()) {
            model.addAttribute("compositionWithComments", new ArrayList<>());
        } else {
            model.addAttribute("compositionWithComments", compositionDTO);
        }

        if (videoBoxAdmins.isEmpty()) {
            model.addAttribute("videoBoxAdmin", new ArrayList<>());
        } else {
            model.addAttribute("videoBoxAdmin", videoBoxAdmins.get(sizeVideoList - 1));
        }
        model.addAttribute("title", MY_HOME);
        return "mine-page";
    }

    private List<PublicationPostAdmin> getPublicationPostAdminList(List<PublicationPostAdmin> publicationList) {
        return publicationList.stream()
                .map(el -> new PublicationPostAdmin(
                        el.getId(),
                        el.getDate(),
                        el.getTitleText(),
                        el.getFullText(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(PublicationPostAdmin::getId).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CompositionDTO> getCompositionWithComments() {
        return compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE).stream()
                .map(el -> new CompositionDTO(
                        el.getDate(),
                        el.getTitleText(),
                        el.getShortText(),
                        HOST_NAME + "/users/read-competitive-one-composition-index/" + el.getId(),
                        converter(el.getImage())))
                .sorted(Comparator.comparing(CompositionDTO::getDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional
    @GetMapping("/users/read-competitive-one-composition-index/{id}")
    public String displayCommentsOfOneComposition(@PathVariable("id") int id,
                                                  Model model,
                                                  HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        Optional<Composition> compositionOne = compositionRepository.findOneById(id);
        if (compositionOne.isPresent()) {
            Composition composition = compositionOne
                    .map(value -> new Composition(
                            value.getPublicationType(),
                            value.getTitleText(),
                            value.getFullText(),
                            converter(value.getImage())))
                    .orElseGet(Composition::new);
            if (composition.getPublicationType().equals(PublicationType.PUBLIC_TO_COMPETITIVE)
                    || composition.getPublicationType().equals(PublicationType.PUBLIC_FOR_ALL)) {
                model.addAttribute("compositionOne", composition);
            } else {
                return "redirect:/";
            }
            model.addAttribute("title", MY_HOME);
            model.addAttribute("comments", findCommentsByIdComposition(id));
            return "users-read-competitive-one-composition-index";
        }
        return "redirect:/";
    }

    private List<String> findCommentsByIdComposition(int id) {
        List<String> allByIdComposition = commentsRepository.findAllByIdComposition(id).stream()
                .map(Comments::getComments)
                .sorted()
                .collect(Collectors.toList());
        if (allByIdComposition.isEmpty()) {
            return new ArrayList<>();
        } else {
            return allByIdComposition;
        }
    }

    @GetMapping("/admin-page")
    public String userPage() {
        return "redirect:/admin-mine/users";
    }

    @Transactional
    @GetMapping("/user-page")
    public String userSecondLoad(Authentication authentication,
                                 Model model,
                                 HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        Optional<UserPhoto> userPhotoFind = userPhotoRepository.findOneByIdUser(user.getId());
        if (userPhotoFind.isPresent()) {
            UserPhoto userPhoto = userPhotoFind.get();
            UserPhotoDTO userPhotoDTO = new UserPhotoDTO(userPhoto.getFullText(), converter(userPhoto.getImage()));
            model.addAttribute("photos", userPhotoDTO);
            model.addAttribute("title", MY_HOME);
            return "userPage";
        }
        Optional<UserPhoto> oneByAddress = userPhotoRepository.findOneByIdUser(user.getId());
        model.addAttribute("photos", oneByAddress.get());
        model.addAttribute("title", MY_HOME);
        return "userPage";
    }

    private String converter(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @Transactional
    @GetMapping("/change/photo")
    public String userPageChangePhotoEdit(Model model,
                                          Authentication authentication) {
        User user = getUser(authentication);
        Optional<UserPhoto> userPhotoOptional = userPhotoRepository.findOneByIdUser(user.getId());
        if (userPhotoOptional.isPresent()) {
            UserPhoto userPhoto = userPhotoOptional.get();
            model.addAttribute("userPhoto", createUserPhotoDTO(userPhoto));
            model.addAttribute("title", MY_HOME);
            return "userPage-updatePhoto";
        }
        return "redirect:/user-page";
    }

    private UserPhotoDTO createUserPhotoDTO(UserPhoto userPhoto) {
        UserPhotoDTO userPhotoDTO = new UserPhotoDTO();
        userPhotoDTO.setFullText(convertTextWithFormatFullText(userPhoto.getFullText()));
        userPhotoDTO.setImage(converter(userPhoto.getImage()));
        return userPhotoDTO;
    }

    private String convertTextWithFormatFullText(String fullText) {
        return fullText
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }

    @Transactional
    @PostMapping("/change/photo")
    public String userPageChangePhoto(Authentication authentication,
                                      MultipartFile file,
                                      @RequestParam("fullText") String fullText,
                                      Model model) throws IOException {
        if (checkData(file, fullText)) {
            return getErrorPage(file, fullText, model);
        } else {
            User user = getUser(authentication);
            Optional<UserPhoto> userPhotoOptional = userPhotoRepository.findOneByIdUser(user.getId());
            if (userPhotoOptional.isPresent()) {
                UserPhoto userPhoto = userPhotoOptional.get();
                userPhoto.setFullText(convertTextWithFormatToSave(fullText));
                if (isNotPresentImage(file)) { //new Photo
                    ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
                    userPhoto.setImage(convert.img);
                    compressorImgToJpg.deleteImage(convert.nameStart);
                    compressorImgToJpg.deleteImage(convert.nameEnd);
                }
                userPhotoRepository.save(userPhoto);
            }
        }
        return "redirect:/user-page";
    }

    private boolean isNotPresentImage(MultipartFile file) {
        return !Objects.equals(file.getContentType(), "application/octet-stream");
    }

    private String convertTextWithFormatToSave(String fullText) {
        String text = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text);
    }

    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private boolean checkData(MultipartFile file,
                              String fullText) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || fullText.toCharArray().length > limit_fullText;
    }

    private String getErrorPage(MultipartFile file,
                                String fullText,
                                Model model) throws IOException {
        String stile = "crimson";
        String originalFilename = file.getOriginalFilename();
        int fileSize = file.getBytes().length / constant;
        int fullTextSize = fullText.toCharArray().length;
        if (file.getBytes().length / constant > limit_photo) {
            model.addAttribute("stile", stile);
        }

        model.addAttribute("originalFilename", originalFilename);
        model.addAttribute("fileSize", fileSize);

        model.addAttribute("full", fullText);
        model.addAttribute("fullTextSize", fullTextSize);
        return "userPage-updatePhoto-error";
    }

    @GetMapping("/study")
    public String studyWritePublication() {
        return "redirect:/study/read-all-publications";
    }

    @GetMapping("/kitchen")
    public String kitchenReadCookbook() {
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/library")
    public String library() {
        return "redirect:/library/read-all-competitive-composition";
    }

    @GetMapping("/living")
    public String livingReadPublications(Model model) {
        return "redirect:/living/news/video";
    }

    @GetMapping("/safe")
    public String safeReadCookbook() {
        return "redirect:/safe/read-save-diary";
    }

    @Transactional
    @GetMapping("/users/write/letter")
    public String userWriteLetter(HttpServletRequest request,
                                  Model model) {
        model.addAttribute("title", MY_HOME);
        metricsService.startMetricsCheck(request, request.getRequestURI());
        return "users-write-letter";
    }

    @PostMapping("/users/send-letter")
    public String sendLetter(@RequestParam("userPost") String userPost,
                             @RequestParam("titleText") String titleText,
                             @RequestParam("fullText") String fullText) {
        if (userPost.toCharArray().length > 100
                || titleText.toCharArray().length > 100
                || fullText.toCharArray().length > 20000) {
            return "redirect:/error-letter";
        } else {
            LetterToADMIN letterToADMIN = new LetterToADMIN();
            letterToADMIN.setDate(new Date());
            letterToADMIN.setEmail(userPost);
            letterToADMIN.setTitleText(titleText);
            letterToADMIN.setFullText(fullText);
            letterToADMINRepository.save(letterToADMIN);
            return "redirect:/";
        }
    }

    @GetMapping("/registration-error")
    public String registrationError(Model model,
                                    HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        model.addAttribute("title", MY_HOME);
        model.addAttribute("title", "Registration-error");
        return "registration-error";
    }

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }
}
