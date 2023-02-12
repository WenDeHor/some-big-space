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
        long countUsers = userRepository.count();
        model.addAttribute("countUsers", countUsers);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("title", "Admin Page");
        return "admin-page";
    }

//    @GetMapping("/admin-mine/users")
//    public String adminMain() {
//        return "redirect:/admin-mine/users-registry";
//    }
//
//    @GetMapping("/admin-mine/users-registry")
//    public String adminUsers(Model model) {
//        List<User> sortedUsers = userRepository.findAll().stream()
//                .sorted(Comparator.comparing(User::getDate).reversed())
//                .collect(Collectors.toList());
//        long countUsers = userRepository.count();
//        model.addAttribute("countUsers", countUsers);
//        model.addAttribute("allUsers", sortedUsers);
//        model.addAttribute("title", "Admin Page");
//        return "admin-page";
//    }
//
//    @GetMapping("/admin-mine/users/{id}/block")
//    public String userLock(@PathVariable(value = "id") Long id) {
//        Optional<User> oneByIdUser = userRepository.findOneByIdUser(id);
//        if (oneByIdUser.isPresent() && oneByIdUser.get().getRole().equals(Role.USER)) {
//            User user = oneByIdUser.get();
//            user.setState(State.BANNED);
//            userRepository.save(user);
//        }
//        return "redirect:/admin-mine/users-registry";
//    }
//
//    @GetMapping("/admin-mine/users/{id}/unlock")
//    public String userUnLock(@PathVariable(value = "id") Long id) {
//        Optional<User> oneByIdUser = userRepository.findOneByIdUser(id);
//        if (oneByIdUser.isPresent() && oneByIdUser.get().getRole().equals(Role.USER)) {
//            User user = oneByIdUser.get();
//            user.setState(State.ACTIVE);
//            userRepository.save(user);
//        }
//        return "redirect:/admin-mine/users-registry";
//    }


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
    public String userCompetitiveOK(@PathVariable(value = "id") Long id) {
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
    public String userCompetitiveDelete(@PathVariable(value = "id") Long id) {
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
    public String competitiveReadOne(@PathVariable(value = "id") Long id,
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
    public String publicationReadOne(@PathVariable(value = "id") Long id,
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
        String userEmail = getUserEmail(authentication);
        int count = compositionRepository.findAllByEmail(userEmail).size();
        ConvertFile convert = compressorImgToJpg.convert(file, userEmail, countId(count));
        Date date = new Date();
        PublicationPostAdmin publicationPostAdmin = new PublicationPostAdmin();
        publicationPostAdmin.setDate(date);
        publicationPostAdmin.setTitleText(titleText);
        publicationPostAdmin.setFullText(convertTextWithFormatToSave(fullText));
        publicationPostAdmin.setAddress(userEmail);
        publicationPostAdmin.setName(file.getOriginalFilename());
        publicationPostAdmin.setType(file.getContentType());
//        publicationPostAdmin.setImage(file.getBytes());
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
    public String adminPublicationEditOne(@PathVariable(value = "id") Long id,
                                          Model model) {
        if (!publicationPostAdminRepository.existsById(id)) {
            return "redirect:/admin-mine/admin-publications";
        }
        Optional<PublicationPostAdmin> byIdPublication = publicationPostAdminRepository.findByIdPublication(id);
        List<PublicationPostAdmin> publicationPostAdminList = new ArrayList<>();
        byIdPublication.ifPresent(publicationPostAdminList::add);
        publicationPostAdminList.sort(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed());
        model.addAttribute("publicationPostAdminList", convertTextWithFormatEdit(publicationPostAdminList));

        return "admin-page-admin-edit-publication";
    }

    //TODO img
    @GetMapping("/image/display/admin/{id}")
    @ResponseBody
    void showImageComposition(@PathVariable("id") Long id,
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
    public String publicationEdit(@PathVariable(value = "idUserPublication") Long idUserPublication, Model model) {
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
        publicationPostAdminRepositoryAll.sort(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed());
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

    //    @Transactional
//    @GetMapping("/image/display/admin/{idPublication}")
//    @ResponseBody
//    void showImageAdmin(@PathVariable("idPublication") Long idPublication,
//                        HttpServletResponse response,
//                        Optional<PublicationPostAdmin> publicationPostAdmin) throws ServletException, IOException {
//
//        publicationPostAdmin = publicationPostAdminRepository.findById(idPublication);
//        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
//        response.getOutputStream().write(publicationPostAdmin.get().getImage());
//        response.getOutputStream().close();
//    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/remove")
    public String adminPublicationRemove(@PathVariable(value = "id") Long id, Model model) {
        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findByIdPublication(id).orElseThrow(null);
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
    public String adminPublicationUpdate(@PathVariable(value = "id") Long id,
                                         MultipartFile file,
                                         @RequestParam String titleText,
                                         @RequestParam String fullText,
                                         Model model) throws IOException {
        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findByIdPublication(id).orElseThrow(null);
        publicationPostAdmin.setTitleText(titleText);
        publicationPostAdmin.setFullText(convertTextWithFormatToSave(fullText));
        Date date = new Date();
        publicationPostAdmin.setDate(date);
        publicationPostAdmin.setName(file.getOriginalFilename());
        publicationPostAdmin.setType(file.getContentType());

        if (file.getContentType().equals("application/octet-stream")) {
            Optional<PublicationPostAdmin> byId = publicationPostAdminRepository.findById(id);
            byte[] image = byId.get().getImage();
            publicationPostAdmin.setImage(image);
        } else {
            publicationPostAdmin.setImage(file.getBytes());
        }
        publicationPostAdminRepository.save(publicationPostAdmin);

        return "redirect:/admin-mine/admin-publications";
    }

    //    @Transactional
//    @PostMapping("/admin-mine/admin-publications/{id}/edit")
//    public String adminPublicationUpdate(@PathVariable(value = "id") Long id,
//                                         MultipartFile file,
//                                         Authentication authentication,
//                                         @RequestParam String titleText,
//                                         @RequestParam String fullText) throws IOException {
//        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findByIdPublication(id).orElseThrow(null);
//        publicationPostAdmin.setTitleText(titleText);
//        publicationPostAdmin.setFullText(convertTextWithFormatToSave(fullText));
//        Date date = new Date();
//        publicationPostAdmin.setDate(date);
//        publicationPostAdmin.setName(file.getOriginalFilename());
//        publicationPostAdmin.setType(file.getContentType());
//
//        if (Objects.equals(file.getContentType(), "application/octet-stream")) {
//            Optional<PublicationPostAdmin> byId = publicationPostAdminRepository.findById(id);
//            byId.ifPresent(postAdmin -> publicationPostAdmin.setImage(postAdmin.getImage()));
//        } else {
//            String userEmail = getUserEmail(authentication);
//            int count = compositionRepository.findAllByEmail(userEmail).size();
//            ConvertFile convert = compressorImgToJpg.convert(file, userEmail, countId(count));
//            publicationPostAdmin.setImage(convert.img);
////            publicationPostAdmin.setImage(file.getBytes());
//            compressorImgToJpg.deleteImage(convert.nameStart);
//            compressorImgToJpg.deleteImage(convert.nameEnd);
//        }
//        publicationPostAdminRepository.save(publicationPostAdmin);
//
//        return "redirect:/admin-mine/admin-publications";
//    }

    @GetMapping("/admin-mine/admin-video")
    public String adminNewsGetVideo(Authentication authentication, Model model) {
        String userAddress = findUserAddress(authentication);
        Iterable<VideoBoxAdmin> allByAddressAdmin = videoBoxAdminRepository.findAllByAddressAdmin(userAddress);
        List<VideoBoxAdmin> videoBoxAdminList = new ArrayList<>();
        allByAddressAdmin.forEach(videoBoxAdminList::add);
        videoBoxAdminList.sort(Comparator.comparing(VideoBoxAdmin::getIdVideoBox).reversed());

        model.addAttribute("videoBoxAdminList", videoBoxAdminList);
        model.addAttribute("title", "Admin Page");
        return "admin-page-video-publication";
    }

    @PostMapping("/admin-mine/admin-video")
    public String adminNewsAddVideo(Authentication authentication,
                                    Model model,
                                    @RequestParam("linkToVideo") String linkToVideo,
                                    @RequestParam("titleText") String titleText) throws IOException {

        String userAddress = findUserAddress(authentication);
        String url = createURL(parseNormalURL(linkToVideo));
        VideoBoxAdmin videoBox = new VideoBoxAdmin();
        videoBox.setAddressAdmin(userAddress);
        videoBox.setLinkToVideo(url);
        videoBox.setTitleText(titleText);
        Date date = new Date();
        videoBox.setDate(date);
        videoBoxAdminRepository.save(videoBox);
        return "redirect:/admin-mine/admin-video";
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

    @GetMapping("/admin-mine/admin-video/{idVideoBox}/remove")
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") Long idVideoBox, Model model) {
        VideoBoxAdmin videoBoxAdmin = videoBoxAdminRepository.findById(idVideoBox).orElseThrow(null);
        videoBoxAdminRepository.delete(videoBoxAdmin);
        return "redirect:/admin-mine/admin-video";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoPublicationEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!videoBoxAdminRepository.existsById(id)) {
            return "redirect:/admin-mine/admin-video";
        }
        Optional<VideoBoxAdmin> videoBoxAdminRepositoryById = videoBoxAdminRepository.findById(id);
        List<VideoBoxAdmin> videoBoxAdminArrayList = new ArrayList<>();
        videoBoxAdminRepositoryById.ifPresent(videoBoxAdminArrayList::add);
        videoBoxAdminArrayList.sort(Comparator.comparing(VideoBoxAdmin::getIdVideoBox).reversed());
        model.addAttribute("videoBoxAdminArrayList", videoBoxAdminArrayList);

        return "admin-page-edit-video-publication";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoUpdate(@PathVariable(value = "id") Long id,

                                   @RequestParam String titleText,
                                   @RequestParam String linkToVideo,
                                   Model model) throws IOException {
        VideoBoxAdmin videoBoxAdmin = videoBoxAdminRepository.findByIdVideoBox(id).orElseThrow(null);
        videoBoxAdmin.setTitleText(titleText);
        int length = linkToVideo.length();
        if (length == 11) {
            String url = "https://www.youtube.com/embed/" + linkToVideo + "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
            videoBoxAdmin.setLinkToVideo(url);
        }
        videoBoxAdminRepository.save(videoBoxAdmin);

        return "redirect:/admin-mine/admin-video";
    }

//    @Transactional
//    @PostMapping("/admin-mine/admin-video/{id}/edit")
//    public String adminVideoUpdate(@PathVariable(value = "id") Long id,
//                                   @RequestParam String titleText,
//                                   @RequestParam String linkToVideo) {
//        VideoBoxAdmin videoBoxAdmin = videoBoxAdminRepository.findByIdVideoBox(id).orElseThrow(null);
//        videoBoxAdmin.setTitleText(titleText);
//        String url;
//        if (linkToVideo.split("=").length > 3) {
//            url = createURL(parseUpdateURL(linkToVideo));
//        } else {
//            url = createURL(parseNormalURL(linkToVideo));
//        }
//
//        videoBoxAdmin.setLinkToVideo(url);
//        videoBoxAdminRepository.save(videoBoxAdmin);
//        return "redirect:/admin-mine/admin-video";
//    }

    //TODO LETTER
    @GetMapping("/admin-mine/read-letters/enter-letters")
    public String adminReadLettersOfUserEnter(Authentication authentication, Model model) {
        String adminAddress = findUserAddress(authentication);
        List<LetterToADMIN> letterToADMINS = letterToADMINRepository.findAll().stream()
                .sorted(Comparator.comparing(LetterToADMIN::getLocalDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("adminLetters", letterToADMINS);
        model.addAttribute("title", "Admin Page");
        return "admin-page-read-user-letters-enter";
    }

    //    href="/admin-mine/read-letters/outer-letters">
    @GetMapping("/admin-mine/read-letters/outer-letters")
    public String adminReadLettersOfUserOuter(Authentication authentication, Model model) {
        List<LetterToUSER> letterFromADMINS = letterToUSERRepository.findAll().stream()
                .filter(el -> el.getSenderAddress().equals(ADMIN))
                .sorted(Comparator.comparing(LetterToUSER::getDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("adminLetters", letterFromADMINS);
        model.addAttribute("title", "Admin Page");
        return "admin-page-read-user-letters-outer";
    }

//    @GetMapping("/admin-mine/read-users-letters")
//    public String adminReadUsersLetters(Model model) {
//        List<LetterToADMIN> sortedLetterToADMINS = letterToADMINRepository.findAll().stream()
//                .sorted(Comparator.comparing(LetterToADMIN::getLocalDate).reversed())
//                .collect(Collectors.toList());
//        long countLetters = letterToADMINRepository.count();
//        model.addAttribute("countLetters", countLetters);
//        model.addAttribute("sortedLetters", sortedLetterToADMINS);
//        model.addAttribute("title", "Read Letters");
//        return "admin-read-users-letters";
//    }

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
                                  Model model, Authentication authentication) {

        String adminAddress = findUserAddress(authentication);
        Date date = new Date();
        LetterToUSER adminLetterToUSER = new LetterToUSER();
        adminLetterToUSER.setDate(date);
        adminLetterToUSER.setTitleText(titleText);
        adminLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        adminLetterToUSER.setSenderAddress(adminAddress);
        adminLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(adminLetterToUSER);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @GetMapping("/admin-mine/read-letters/enter-letters/{id}/read")
    public String adminReadLetterEnter(@PathVariable(value = "id") Long id, Model model) {
        Optional<LetterToADMIN> letter1 = letterToADMINRepository.findById(id);
        model.addAttribute("letter", letter1.get());
        return "admin-page-read-user-letters-fulltext-enter";
    }

// <td><a th:href="'/admin-mine/read-letters/outer-letters/'+${letterToUSER.idLetter}+'/read'"
@GetMapping("/admin-mine/read-letters/outer-letters/{id}/read")
public String adminReadLetterOuter(@PathVariable(value = "id") Long id, Model model) {
    Optional<LetterToUSER> letter1 = letterToUSERRepository.findById(id);
    model.addAttribute("letter", letter1.get());
    return "admin-page-read-user-letters-fulltext-outer";
}
    //TODO answer
    @GetMapping("/admin-mine/write-letters/{id}/answer")
    public String adminAnswerWriteLetter(Authentication authentication,
                                         @PathVariable(value = "id") Long id,
                                         Model model) {
        Optional<LetterToADMIN> letter = letterToADMINRepository.findById(id);
        String adminAddress = findUserAddress(authentication);
        if (letter.get().getAddress() == null) {
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        model.addAttribute("senderAddress", letter.get().getAddress());
        model.addAttribute("adminAddress", adminAddress);
        model.addAttribute("title", "Admin Page");
        return "admin-page-write-to-user-letters-answer";
    }

    @PostMapping("/admin-mine/write-letters/answer")
    public String adminAnswerLetterSend(LetterToUSER letterToUSER,
                                        @RequestParam String titleText,
                                        @RequestParam String fullText,
                                        @RequestParam String senderAddress,
                                        @RequestParam String recipientAddress,
                                        Model model, Authentication authentication) {

        String adminAddress = findUserAddress(authentication);
        Date date = new Date();
        LetterToUSER adminLetterToUSER = new LetterToUSER();
        adminLetterToUSER.setDate(date);
        adminLetterToUSER.setTitleText(titleText);
        adminLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        adminLetterToUSER.setSenderAddress(adminAddress);
        adminLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(adminLetterToUSER);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/delete")
    public String adminDeleteLetter(@PathVariable(value = "idLetter") Long idLetter) {
        Optional<LetterToADMIN> byId = letterToADMINRepository.findById(idLetter);
        byId.ifPresent(letterToADMINRepository::delete);
        return "redirect:/admin-mine/read-letters/enter-letters";
    }

    @Transactional
    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/read")
    public String adminReadOneLetter(@PathVariable(value = "idLetter") Long idLetter,
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

    private String getUserEmail(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);

        return oneByEmail.map(User::getEmail).orElse(null);
    }

}
