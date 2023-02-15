package com.myhome.controllers;


import com.myhome.models.*;
import com.myhome.repository.*;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.MetricsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class PagesControllers {
    //    Don't let the sun go down until you keep your promises
    // Картінки підгружати на різні кімнати
    private final CompositionRepository compositionRepository;
    private final UserRepository userRepository;
    //    private final MyFriendsRepository myFriendsRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final String LINK_BASE = "https://www.youtube.com/embed/GYrwebcKoxE?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;
    private final CommentsRepository commentsRepository;

    private final String MY_HOME = "Мій дім";
    private final String STUDY_ROOM = "Моя кімната";
    private final String LIBRARY_ROOM = "Читальня";
    private final String SAFE_ROOM = "Робоча кімната";
    private final String LIVING_ROOM = "Вітальня";
    private final String KITCHEN_ROOM = "Кухня";

    public PagesControllers(CompositionRepository compositionRepository, UserRepository userRepository, VideoBoxAdminRepository videoBoxAdminRepository, UserPhotoRepository userPhotoRepository, PublicationPostAdminRepository publicationPostAdminRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService, CommentsRepository commentsRepository) {
        this.compositionRepository = compositionRepository;
        this.userRepository = userRepository;
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
        List<Composition> compositionWithComments = getCompositionWithComments();
        List<PublicationPostAdmin> publications = publicationPostAdminRepository.findAll();
        List<PublicationPostAdmin> publicationPostAdminListTreeElements = getPublicationPostAdminList(publications);

        if (publicationPostAdminListTreeElements.isEmpty()) {
            model.addAttribute("publications", new ArrayList<>());
        } else {
            model.addAttribute("publications", publicationPostAdminListTreeElements);
        }

        if (compositionWithComments.isEmpty()) {
            model.addAttribute("compositionWithComments", new ArrayList<>());
        } else {
            model.addAttribute("compositionWithComments", compositionWithComments);
        }

        if (videoBoxAdmins.isEmpty()) {
            model.addAttribute("videoBoxAdmin", new ArrayList<>());
        } else {
            model.addAttribute("videoBoxAdmin", videoBoxAdmins.get(sizeVideoList - 1));
        }
        model.addAttribute("title", MY_HOME);
        return "mine-page";
    }

    //    public PublicationPostAdmin(int id, Date date, String titleText, String fullText, byte[] image, String convert) {
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
    public List<Composition> getCompositionWithComments() {
        return compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE).stream()
                .map(el -> new Composition(
                        el.getId(),
                        el.getLocalDate(),
                        el.getGenre(),
                        el.getTitleText(),
                        el.getShortText(),
                        el.getFullText(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(Composition::getLocalDate).reversed())
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
            Composition composition = new Composition(
                    compositionOne.get().getTitleText(),
                    compositionOne.get().getFullText(),
                    Base64.getMimeEncoder().encodeToString(compositionOne.get().getImage()));
            model.addAttribute("compositionOne", composition);
        }
        model.addAttribute("title", MY_HOME);
        model.addAttribute("comments", findCommentsByIdComposition(id));
        return "users-read-competitive-one-composition-index";
    }

    private List<String> findCommentsByIdComposition(int id) {
        List<String> allByIdComposition = commentsRepository.findAllById(id).stream()
                .map(Comments::getComments)
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
            model.addAttribute("photos", userPhotoFind.get());
            model.addAttribute("title", MY_HOME);
            return "userPage";
        }
        Optional<UserPhoto> oneByAddress = userPhotoRepository.findOneByAddress(user.getAddress());
        model.addAttribute("photos", oneByAddress.get());
        model.addAttribute("title", MY_HOME);
        return "userPage";
    }

    @GetMapping("/change/photo")
    public String userPageChangePhotoEdit(Model model) {
        model.addAttribute("title", MY_HOME);
        return "userPage-updatePhoto";
    }

    @Transactional
    @PostMapping("/change/photo") //TODO zipping photo
    public String userPageChangePhoto(Authentication authentication,
                                      MultipartFile file,
                                      @RequestParam("fullText") String fullText) throws IOException {
        User user = getUser(authentication);
        UserPhoto userPhoto = new UserPhoto();
        userPhoto.setIdUser(user.getId());
        userPhoto.setImage(file.getBytes());
        userPhoto.setFullText(fullText);
        userPhotoRepository.deleteByIdUser(user.getId());
        userPhotoRepository.save(userPhoto);
        return "redirect:/user-page";
    }

    @GetMapping("/user/page/photo/display/{id}")
    @ResponseBody
    void showUserPagePhoto(@PathVariable("id") int id,
                           HttpServletResponse response,
                           Optional<UserPhoto> userPhoto) throws ServletException, IOException {
        userPhoto = userPhotoRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(userPhoto.get().getImage());
        response.getOutputStream().close();
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
        if (userPost.toCharArray().length > 100 || titleText.toCharArray().length > 100 || fullText.toCharArray().length > 20000) {
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
