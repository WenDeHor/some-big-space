package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.ConvertFile;
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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class AdminBlogControllers {
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final LetterToUSERRepository letterToUSERRepository;
    private final CompositionRepository compositionRepository;
    private final ImageRepository imageRepository;
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;
    private final CompressorImgToJpg compressorImgToJpg;

    private final String ADMIN = "ADMIN from New_Apple";


    private final String YOUTUBE = "https://www.youtube.com/embed/";


    public AdminBlogControllers(UserRepository userRepository, PublicationRepository publicationRepository, PublicationPostAdminRepository publicationPostAdminRepository, VideoBoxAdminRepository videoBoxAdminRepository, LetterToUSERRepository letterToUSERRepository, CompositionRepository compositionRepository, ImageRepository imageRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService, CompressorImgToJpg compressorImgToJpg) {
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.letterToUSERRepository = letterToUSERRepository;
        this.compositionRepository = compositionRepository;
        this.imageRepository = imageRepository;
        this.letterToADMINRepository = letterToADMINRepository;
        this.metricsService = metricsService;
        this.compressorImgToJpg = compressorImgToJpg;
    }

    @GetMapping("/admin-mine/users")
    public String adminUsers(Model model) {
        List<User> allUsers = userRepository.findAll();
        long countUsers = allUsers.size();
        model.addAttribute("countUsers", countUsers);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("title", "Admin Page");
        return "admin-page";
    }

    //TODO competitive
    @Transactional
    @GetMapping("/admin-mine/users-competitive")
    public String adminUsersCompetitive(Model model) {
        List<Composition> compositionList = compositionRepository
                .findAllByPublicationType(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN).stream()
                .sorted(Comparator.comparing(Composition::getLocalDate).reversed())
                .collect(Collectors.toList());
        long countComposition = compositionList.size();
        model.addAttribute("countComposition", countComposition);
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "Competitive Composition");
        return "admin-competitive";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/competitive")
    public String userCompetitiveOK(@PathVariable(value = "id") int id) {
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            Composition composition = oneById.get();
            composition.setPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE);
            compositionRepository.save(composition);
        }
        return "redirect:/admin-mine/users-competitive";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/delete")
    public String userCompetitiveDelete(@PathVariable(value = "id") int id) {
        Optional<Composition> oneById = compositionRepository.findOneById(id);
        if (oneById.isPresent()) {
            Composition composition = oneById.get();
            composition.setPublicationType(PublicationType.PUBLIC_TO_DELETE);
            compositionRepository.save(composition);
        }
        return "redirect:/admin-mine/users-competitive";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/read")
    public String competitiveReadOne(@PathVariable(value = "id") int id,
                                     Model model) {
        Optional<Composition> compositionOne = compositionRepository.findOneById(id);
        if (!compositionOne.isPresent()) {
            return "redirect:/admin-mine/users-competitive";
        }
        model.addAttribute("compositionOne", compositionOne.get());
        return "admin-read-one-composition";
    }


    //TODO USER Composition
    @Transactional
    @GetMapping("/admin-mine/users-composition")
    public String adminUsersComposition(Model model) {
        List<Composition> compositionList = compositionRepository.findAll();
        long countComposition = compositionList.size();
        model.addAttribute("countComposition", countComposition);
        model.addAttribute("compositionList", compositionList);
        model.addAttribute("title", "All Composition");
        return "admin-read-all-user-composition";
    }

    @Transactional
    @GetMapping("/admin-mine/users-publication/{id}/read")
    public String publicationReadOne(@PathVariable(value = "id") int id,
                                     Model model) {
        Optional<Composition> compositionOne = compositionRepository.findOneById(id);
        if (!compositionOne.isPresent()) {
            return "redirect:/admin-mine/users-competitive";
        }
        model.addAttribute("compositionOne", compositionOne.get());
        return "admin-read-one-composition";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-publications")
    public String saveAdminPublications(Authentication authentication,
                                        MultipartFile file,
                                        @RequestParam("titleText") String titleText,
                                        @RequestParam("fullText") String fullText) throws IOException {
        User user = getUser(authentication);
        int count = compositionRepository.findAllByIdUser(user.getId()).size();
        ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail(), countId(count));
        Date date = new Date();
        PublicationPostAdmin publicationPostAdmin = new PublicationPostAdmin();
        publicationPostAdmin.setDate(date);
        publicationPostAdmin.setTitleText(titleText);
        publicationPostAdmin.setFullText(convertTextWithFormatToSave(fullText));
        publicationPostAdmin.setImage(convert.img);
        publicationPostAdminRepository.save(publicationPostAdmin);
        compressorImgToJpg.deleteImage(convert.nameStart);
        compressorImgToJpg.deleteImage(convert.nameEnd);
        return "redirect:/admin-mine/admin-publications";
    }

    private int countId(int count) {
        return ++count;
    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/edit")
    public String adminPublicationEditOne(@PathVariable(value = "id") int id,
                                          Model model) {
        if (!publicationPostAdminRepository.existsById(id)) {
            return "redirect:/admin-mine/admin-publications";
        }
        Optional<PublicationPostAdmin> byIdPublication = publicationPostAdminRepository.findById(id);
        List<PublicationPostAdmin> publicationPostAdminList = new ArrayList<>();
        byIdPublication.ifPresent(publicationPostAdminList::add);
        publicationPostAdminList.sort(Comparator.comparing(PublicationPostAdmin::getId).reversed());
        model.addAttribute("publicationPostAdminList", convertTextWithFormatEdit(publicationPostAdminList));

        return "admin-page-admin-edit-publication";
    }

    //TODO img
    @GetMapping("/image/display/admin/{id}")
    @ResponseBody
    void showImageComposition(@PathVariable("id") int id,
                              HttpServletResponse response,
                              Optional<Composition> composition) throws IOException {
        Optional<PublicationPostAdmin> composition2 = publicationPostAdminRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(composition2.get().getImage());
        response.getOutputStream().close();
    }


    //TODO UserPublications
    @GetMapping("/admin-mine/users-publications")
    public String adminUserPublications(Model model) {
        List<PublicationUser> allPublications = publicationRepository.findAll();
        long countPublications = publicationRepository.count();
        model.addAttribute("countPublications", countPublications);
        model.addAttribute("allPublications", allPublications);
        model.addAttribute("title", "Admin Page");
        return "admin-page-user-publication";
    }

    @GetMapping("/admin-mine/users-publications/{idUserPublication}/read")
    public String publicationEdit(@PathVariable(value = "idUserPublication") int idUserPublication, Model model) {
        if (!publicationRepository.existsById(idUserPublication)) {
            return "redirect:/admin-mine/admin-publications";
        }
        Optional<PublicationUser> post = publicationRepository.findById(idUserPublication);
        List<PublicationUser> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("publication", res);
        return "admin-mine-read-user-publication";
    }


    //TODO adminPublications
    @Transactional
    @GetMapping("/admin-mine/admin-publications")
    public String adminPublications(Model model) {
        List<PublicationPostAdmin> publicationPostAdminRepositoryAll = publicationPostAdminRepository.findAll();
        publicationPostAdminRepositoryAll.sort(Comparator.comparing(PublicationPostAdmin::getId).reversed());
        long countPublications = publicationPostAdminRepository.count();

        model.addAttribute("countPublications", countPublications);
        model.addAttribute("publicationPostAdminRepositoryAll", publicationPostAdminRepositoryAll);
        model.addAttribute("title", "Admin Page");
        return "admin-page-admin-publication";
    }

    //TODO SAVE
    private String convertTextWithFormatToSave(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/remove")
    public String adminPublicationRemove(@PathVariable(value = "id") int id, Model model) {
        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findById(id).orElseThrow(null);
        publicationPostAdminRepository.delete(publicationPostAdmin);
        return "redirect:/admin-mine/admin-publications";
    }

    //TODO READ EDIT
    private List<PublicationPostAdmin> convertTextWithFormatEdit(List<PublicationPostAdmin> publicationPostAdminList) {
        List<PublicationPostAdmin> list = new ArrayList<>();
        for (PublicationPostAdmin publicationPostAdmin : publicationPostAdminList) {
            String fullText = publicationPostAdmin.getFullText();
            String trim1 = fullText.replace("&#160&#160 ", "");
            String trim2 = trim1.replace("<br>", "");
            publicationPostAdmin.setFullText(trim2);
            list.add(publicationPostAdmin);
        }
        return list;
    }

    @Transactional
    @PostMapping("/admin-mine/admin-publications/{id}/edit")
    public String adminPublicationUpdate(@PathVariable(value = "id") int id,
                                         MultipartFile file,
                                         @RequestParam String titleText,
                                         @RequestParam String fullText) throws IOException {
        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findById(id).orElseThrow(null);
        publicationPostAdmin.setTitleText(titleText);
        publicationPostAdmin.setFullText(convertTextWithFormatToSave(fullText));
        publicationPostAdmin.setDate(new Date());
        if (Objects.equals(file.getContentType(), "application/octet-stream")) {
            Optional<PublicationPostAdmin> byId = publicationPostAdminRepository.findById(id);
            byte[] image = byId.get().getImage();
            publicationPostAdmin.setImage(image);
        } else {
            publicationPostAdmin.setImage(file.getBytes());
        }
        publicationPostAdminRepository.save(publicationPostAdmin);
        return "redirect:/admin-mine/admin-publications";
    }

    @GetMapping("/admin-mine/admin-video")
    public String adminNewsGetVideo(Authentication authentication, Model model) {
        User user = getUser(authentication);
        List<VideoBoxAdmin> allByAddressAdmin = videoBoxAdminRepository.findAllById(user.getId());
        allByAddressAdmin.sort(Comparator.comparing(VideoBoxAdmin::getId).reversed());
        model.addAttribute("videoBoxAdminList", allByAddressAdmin);
        model.addAttribute("title", "Admin Page");
        return "admin-page-video-publication";
    }

    @PostMapping("/admin-mine/admin-video")
    public String adminNewsAddVideo(Authentication authentication,
                                    @RequestParam("linkToVideo") String linkToVideo,
                                    @RequestParam("titleText") String titleText) {
        String url = createURL(parseNormalURL(linkToVideo));
        VideoBoxAdmin videoBox = new VideoBoxAdmin();
        videoBox.setLinkToVideo(url);
        videoBox.setTitleText(titleText);
        videoBox.setDate(new Date());
        videoBoxAdminRepository.save(videoBox);
        return "redirect:/admin-mine/admin-video";
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

    @GetMapping("/admin-mine/admin-video/{idVideoBox}/remove")
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") int idVideoBox) {
        VideoBoxAdmin videoBoxAdmin = videoBoxAdminRepository.findById(idVideoBox).orElseThrow(null);
        videoBoxAdminRepository.delete(videoBoxAdmin);
        return "redirect:/admin-mine/admin-video";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoPublicationEdit(@PathVariable(value = "id") int id,
                                            Model model) {
        if (!videoBoxAdminRepository.existsById(id)) {
            return "redirect:/admin-mine/admin-video";
        }
        Optional<VideoBoxAdmin> videoBoxAdminRepositoryById = videoBoxAdminRepository.findById(id);
        List<VideoBoxAdmin> videoBoxAdminArrayList = new ArrayList<>();
        videoBoxAdminRepositoryById.ifPresent(videoBoxAdminArrayList::add);
        videoBoxAdminArrayList.sort(Comparator.comparing(VideoBoxAdmin::getId).reversed());
        model.addAttribute("videoBoxAdminArrayList", videoBoxAdminArrayList);

        return "admin-page-edit-video-publication";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoUpdate(@PathVariable(value = "id") int id,
                                   @RequestParam String titleText,
                                   @RequestParam String linkToVideo) {
        VideoBoxAdmin videoBoxAdmin = videoBoxAdminRepository.findById(id).orElseThrow(null);
        videoBoxAdmin.setTitleText(titleText);
        int length = linkToVideo.length();
        if (length == 11) {
            String url = "https://www.youtube.com/embed/" + linkToVideo + "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
            videoBoxAdmin.setLinkToVideo(url);
        }
        videoBoxAdminRepository.save(videoBoxAdmin);
        return "redirect:/admin-mine/admin-video";
    }

    //TODO LETTER
    @GetMapping("/admin-mine/read-letters/enter-letters")
    public String adminReadLettersOfUserEnter(Authentication authentication, Model model) {
        String adminAddress = findUserAddress(authentication);
        List<LetterToADMIN> letterToADMINS = letterToADMINRepository.findAll().stream()
                .sorted(Comparator.comparing(LetterToADMIN::getDate).reversed())
                .collect(Collectors.toList());
        model.addAttribute("adminLetters", letterToADMINS);
        model.addAttribute("title", "Admin Page");
        return "admin-page-read-user-letters-enter";
    }

    @GetMapping("/admin-mine/read-letters/outer-letters")
    public String adminReadLettersOfUserOuter(Model model) {
        List<LetterToUSER> letterFromADMINS = letterToUSERRepository.findAll().stream()
                .filter(el -> el.getSenderAddress().equals(ADMIN))
                .sorted(Comparator.comparing(LetterToUSER::getDate).reversed())
                .collect(Collectors.toList());
        model.addAttribute("adminLetters", letterFromADMINS);
        model.addAttribute("title", "Admin Page");
        return "admin-page-read-user-letters-outer";
    }

    @GetMapping("/admin-mine/write-letters/send")
    public String adminWriteLetter(Authentication authentication, Model model) {
        String adminAddress = findUserAddress(authentication);
        model.addAttribute("adminAddress", adminAddress);
        model.addAttribute("title", "Admin Page");
        return "admin-page-write-to-user-letters";
    }

    @PostMapping("/admin-mine/write-letters/send")
    public String adminLetterSend(LetterToUSER letterToUSER,
                                  @RequestParam String titleText,
                                  @RequestParam String fullText,
                                  @RequestParam String senderAddress,
                                  @RequestParam String recipientAddress,
                                  Authentication authentication) {

        User user = getUser(authentication);
        LetterToUSER adminLetterToUSER = new LetterToUSER();
        adminLetterToUSER.setDate(new Date());
        adminLetterToUSER.setTitleText(titleText);
        adminLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        adminLetterToUSER.setSenderAddress(user.getAddress());
        adminLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(adminLetterToUSER);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @GetMapping("/admin-mine/read-letters/enter-letters/{id}/read")
    public String adminReadLetterEnter(@PathVariable(value = "id") int id,
                                       Model model) {
        Optional<LetterToADMIN> letter1 = letterToADMINRepository.findById(id);
        model.addAttribute("letter", letter1.get());
        return "admin-page-read-user-letters-fulltext-enter";
    }

    @GetMapping("/admin-mine/read-letters/outer-letters/{id}/read")
    public String adminReadLetterOuter(@PathVariable(value = "id") int id,
                                       Model model) {
        Optional<LetterToUSER> letter1 = letterToUSERRepository.findById(id);
        model.addAttribute("letter", letter1.get());
        return "admin-page-read-user-letters-fulltext-outer";
    }

    //TODO answer
    @GetMapping("/admin-mine/write-letters/{id}/answer")
    public String adminAnswerWriteLetter(Authentication authentication,
                                         @PathVariable(value = "id") int id,
                                         Model model) {
        Optional<LetterToADMIN> letter = letterToADMINRepository.findById(id);
        User user = getUser(authentication);
        if (letter.get().getAddressUser() == null) {
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        model.addAttribute("senderAddress", letter.get().getAddressUser());
        model.addAttribute("adminAddress", user.getAddress());
        model.addAttribute("title", "Admin Page");
        return "admin-page-write-to-user-letters-answer";
    }

    @PostMapping("/admin-mine/write-letters/answer")
    public String adminAnswerLetterSend(LetterToUSER letterToUSER,
                                        @RequestParam String titleText,
                                        @RequestParam String fullText,
                                        @RequestParam String senderAddress,
                                        @RequestParam String recipientAddress,
                                        Authentication authentication) {

        User user = getUser(authentication);
        LetterToUSER adminLetterToUSER = new LetterToUSER();
        adminLetterToUSER.setDate(new Date());
        adminLetterToUSER.setTitleText(titleText);
        adminLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        adminLetterToUSER.setSenderAddress(user.getAddress());
        adminLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(adminLetterToUSER);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/delete")
    public String adminDeleteLetter(@PathVariable(value = "idLetter") int idLetter) {
        Optional<LetterToADMIN> byId = letterToADMINRepository.findById(idLetter);
        byId.ifPresent(letterToADMINRepository::delete);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @Transactional
    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/read")
    public String adminReadOneLetter(@PathVariable(value = "idLetter") int idLetter,
                                     Model model) {
        Optional<LetterToADMIN> letterOptional = letterToADMINRepository.findById(idLetter);
        letterOptional.ifPresent(letterToADMIN -> model.addAttribute("letterReadOne", letterToADMIN));
        model.addAttribute("title", "Read User Letter");
        return "admin-read-one-letter";
    }

    //TODO METRICS
    @GetMapping("/admin-mine/read-metrics")
    public String getMetricsData(Model model) {
        metricsService.filterDays();
        List<MetricsDTO> allMetricsDTOs = metricsService.findAllMetricsDTOs().stream()
                .sorted(Comparator.comparing(MetricsDTO::getDate).reversed())
                .collect(Collectors.toList());
        model.addAttribute("metrics", allMetricsDTOs);
        model.addAttribute("title", "Metrics");
        return "admin-page-metrics";
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }

}
