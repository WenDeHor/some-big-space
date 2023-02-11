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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Controller
public class PagesControllers {
    //    Don't let the sun go down until you keep your promises
    // Картінки підгружати на різні кімнати
    private final PublicationRepository publicationRepository;
    private final CompositionRepository compositionRepository;
    private final UserRepository userRepository;
    //    private final MyFriendsRepository myFriendsRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final String LINK_BASE = "https://www.youtube.com/embed/GYrwebcKoxE?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;

    public PagesControllers(PublicationRepository publicationRepository, CompositionRepository compositionRepository, UserRepository userRepository, VideoBoxAdminRepository videoBoxAdminRepository, UserPhotoRepository userPhotoRepository, PublicationPostAdminRepository publicationPostAdminRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService) {
        this.publicationRepository = publicationRepository;
        this.compositionRepository = compositionRepository;
        this.userRepository = userRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.userPhotoRepository = userPhotoRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
        this.letterToADMINRepository = letterToADMINRepository;
        this.metricsService = metricsService;
    }

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        metricsService.startMetricsCheck(request, "/");
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

        model.addAttribute("title", "STORY FLOW");
        return "mine-page";
    }

    private List<PublicationPostAdmin> getPublicationPostAdminList(List<PublicationPostAdmin> publicationList) {
        return publicationList.stream()
                .map(el -> new PublicationPostAdmin(
                        el.getIdPublication(),
                        el.getDate(),
                        el.getTitleText(),
                        el.getFullText(),
                        el.getAddress(),
                        el.getName(),
                        el.getType(),
                        el.getImage(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Composition> getCompositionWithComments() {
        return compositionRepository.findAllByPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE).stream()
                .map(el -> new Composition(
                        el.getLocalDate(),
                        el.getGenre(),
                        el.getPublicationType(),
                        el.getTitleText(),
                        el.getShortText(),
                        el.getFullText(),
                        el.getEmail(),
                        el.getAddress(),
                        el.getUserId(),
                        el.getName(),
                        el.getType(),
                        el.getImage(),
                        Base64.getMimeEncoder().encodeToString(el.getImage())))
                .sorted(Comparator.comparing(Composition::getLocalDate).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }

    @GetMapping("/admin-page")
    public String userPage() {
        return "redirect:/admin-mine/users";
    }

    @Transactional
    @GetMapping("/user-page")
    public String userSecondLoad(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);
        Optional<UserPhoto> userPhotoFind = userPhotoRepository.findOneByAddress(userAddress);
        List<UserPhoto> photos = new CopyOnWriteArrayList<>();
        userPhotoFind.ifPresent(photos::add);

        model.addAttribute("photos", photos);
        model.addAttribute("title", "Mine Page");
        return "userPage";
    }

    @GetMapping("/change/photo")
    public String userPageChangePhotoEdit() {

        return "userPage-updatePhoto";
    }

    @Transactional
    @PostMapping("/change/photo")
    public String userPageChangePhoto(Authentication authentication,
                                      MultipartFile file,
                                      @RequestParam("fullText") String fullText) throws IOException {
        String userAddress = findUserAddress(authentication);

        UserPhoto userPhoto = new UserPhoto();
        userPhoto.setAddress(userAddress);
        userPhoto.setImage(file.getBytes());
        userPhoto.setName(file.getOriginalFilename());
        userPhoto.setType(file.getContentType());
        userPhoto.setFullText(fullText);
        userPhotoRepository.deleteByAddress(userAddress);
        userPhotoRepository.save(userPhoto);
        return "redirect:/user-page";
    }

    @GetMapping("/user/page/photo/display/{id}")
    @ResponseBody
    void showUserPagePhoto(@PathVariable("id") Long id,
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
    public String livingReadPublications(Authentication authentication, Model model) {
        model.addAttribute("title", "LIVING ROOM");
        return "redirect:/living/news/video";
    }

    @GetMapping("/safe")
    public String safeReadCookbook() {
        return "redirect:/safe/read-save-diary";
    }

    @Transactional
    @GetMapping("/users/write/letter")
    public String userWriteLetter(HttpServletRequest request) {
        metricsService.startMetricsCheck(request, "/users/write/letter");
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
            letterToADMIN.setLocalDate(new Date());
            letterToADMIN.setEmail(userPost);
            letterToADMIN.setTitleText(titleText);
            letterToADMIN.setFullText(fullText);
            letterToADMINRepository.save(letterToADMIN);
            return "redirect:/";
        }
    }


    @GetMapping("/registration-error")
    public String registrationError(Model model) {
        model.addAttribute("title", "Registration-error");
        return "registration-error";
    }

//    private TargetDataLine microphone;
//    private SourceDataLine speakers;
//
//    @GetMapping("/call")
//    public void startUserVolume() {
//        for (int i = 0; i < 100; i++) {
//            AudioFormat format = new AudioFormat(8000.0f, 8, 2, true, true);
//
//            try {
//                microphone = AudioSystem.getTargetDataLine(format);
//
//                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//                microphone = (TargetDataLine) AudioSystem.getLine(info);
//                microphone.open(format);
//
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                int numBytesRead;
//                int CHUNK_SIZE = 1024;
//                byte[] data = new byte[microphone.getBufferSize() / 5];
//                microphone.start();
//
//                int bytesRead = 0;
//                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
//                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//                speakers.open(format);
//                speakers.start();
//                while (bytesRead < 100000) {
//                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
//                    bytesRead += numBytesRead;
//                    // write the mic data to a stream for use later
//                    out.write(data, 0, numBytesRead);
//                    // write mic data to stream for immediate playback
//                    speakers.write(data, 0, numBytesRead);
//                }
//                speakers.drain();
//                speakers.close();
//                microphone.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
////        return "redirect:/user-page";
//    }
//
//    @GetMapping("/stop")
//    public void stopUserVolume() {
//        speakers.close();
//        microphone.close();
////        return "redirect:/user-page";
//    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
