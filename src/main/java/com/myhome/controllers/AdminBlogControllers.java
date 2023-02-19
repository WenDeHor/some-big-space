package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.CompositionDTO;
import com.myhome.forms.ConvertFile;
import com.myhome.forms.PublicationPostAdminDTO;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminBlogControllers {
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final LetterToUSERRepository letterToUSERRepository;
    private final CompositionRepository compositionRepository;
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;
    private final CompressorImgToJpg compressorImgToJpg;

    private final String ADMIN = "ADMIN from New_Apple";
    private final String ADMIN_PAGE = "ADMIN_PAGE";


    private final String YOUTUBE = "https://www.youtube.com/embed/";


    public AdminBlogControllers(UserRepository userRepository, PublicationRepository publicationRepository, PublicationPostAdminRepository publicationPostAdminRepository, VideoBoxAdminRepository videoBoxAdminRepository, LetterToUSERRepository letterToUSERRepository, CompositionRepository compositionRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService, CompressorImgToJpg compressorImgToJpg) {
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.letterToUSERRepository = letterToUSERRepository;
        this.compositionRepository = compositionRepository;
        this.letterToADMINRepository = letterToADMINRepository;
        this.metricsService = metricsService;
        this.compressorImgToJpg = compressorImgToJpg;
    }

    @GetMapping("/admin-mine/users")
    public String adminUsers(Model model,
                             Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<User> allUsers = userRepository.findAll();
            long countUsers = allUsers.size();
            model.addAttribute("countUsers", countUsers);
            model.addAttribute("allUsers", allUsers);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page";
        }
        return "redirect:/";
    }

    //TODO competitive
    @Transactional
    @GetMapping("/admin-mine/users-competitive")
    public String adminUsersCompetitive(Model model,
                                        Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<CompositionDTO> compositionDTOList = compositionRepository
                    .findAllByPublicationType(PublicationType.PUBLIC_TO_COORDINATION_OF_ADMIN).stream()
                    .map(composition -> new CompositionDTO(
                            composition.getId(),
                            composition.getTitleText()
                    ))
                    .sorted(Comparator.comparing(CompositionDTO::getId).reversed())
                    .collect(Collectors.toList());
            long countComposition = compositionDTOList.size();
            model.addAttribute("countComposition", countComposition);
            model.addAttribute("compositionList", compositionDTOList);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-competitive";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/competitive")
    public String userCompetitiveOK(@PathVariable(value = "id") int id,
                                    Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<Composition> oneById = compositionRepository.findOneById(id);
            if (oneById.isPresent()) {
                Composition composition = oneById.get();
                composition.setPublicationType(PublicationType.PUBLIC_TO_COMPETITIVE);
                compositionRepository.save(composition);
            }
            return "redirect:/admin-mine/users-competitive";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/delete")
    public String userCompetitiveDelete(@PathVariable(value = "id") int id,
                                        Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<Composition> oneById = compositionRepository.findOneById(id);
            if (oneById.isPresent()) {
                Composition composition = oneById.get();
                composition.setPublicationType(PublicationType.PUBLIC_TO_DELETE);
                compositionRepository.save(composition);
            }
            return "redirect:/admin-mine/users-competitive";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/users-competitive/{id}/read")
    public String competitiveReadOne(@PathVariable(value = "id") int id,
                                     Model model,
                                     Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<Composition> compositionOptional = compositionRepository.findOneById(id);
            if(compositionOptional.isPresent()){
                Composition composition = compositionOptional.get();
                CompositionDTO compositionDTO = new CompositionDTO(
                        composition.getId(),
                        composition.getTitleText(),
                        convertText(composition.getShortText()),
                        convertText(composition.getFullText()),
                        converterImage(composition.getImage()));
                model.addAttribute("title", ADMIN_PAGE);
                model.addAttribute("compositionOne", compositionDTO);
                return "admin-read-one-composition";
            }
            return "redirect:/admin-mine/users-competitive";
        }
        return "redirect:/";
    }

    private String converterImage(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    //TODO USER Composition
    @Transactional
    @GetMapping("/admin-mine/users-composition/{id}/read")
    public String publicationReadOne(@PathVariable(value = "id") int id,
                                     Model model,
                                     Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {

            Optional<Composition> compositionOptional = compositionRepository.findOneById(id);
            if (compositionOptional.isPresent()) {
                model.addAttribute("compositionOne", compositionOptional.get());
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-read-one-composition";
            }
            return "redirect:/admin-mine/users-competitive";
        }
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-publications")
    public String saveAdminPublications(Authentication authentication,
                                        MultipartFile file,
                                        @RequestParam("titleText") String titleText,
                                        @RequestParam("fullText") String fullText) throws IOException {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
            Date date = new Date();
            PublicationPostAdmin publicationPostAdmin = new PublicationPostAdmin();
            publicationPostAdmin.setDate(date);
            publicationPostAdmin.setTitleText(titleText);
            publicationPostAdmin.setFullText(convertText(fullText));
            publicationPostAdmin.setImage(convert.img);
            publicationPostAdminRepository.save(publicationPostAdmin);
            compressorImgToJpg.deleteImage(convert.nameStart);
            compressorImgToJpg.deleteImage(convert.nameEnd);
            return "redirect:/admin-mine/admin-publications";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/edit")
    public String adminPublicationEditOne(@PathVariable(value = "id") int id,
                                          Model model,
                                          Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<PublicationPostAdmin> publicationPostAdminOptional = publicationPostAdminRepository.findById(id);
            if (publicationPostAdminOptional.isPresent()) {
                PublicationPostAdmin publicationPostAdmin = publicationPostAdminOptional.get();
                PublicationPostAdminDTO publicationPostAdminDTO = new PublicationPostAdminDTO(
                        publicationPostAdmin.getId(),
                        convertText(publicationPostAdmin.getTitleText()),
                        convertText(publicationPostAdmin.getFullText()),
                        converterImage(publicationPostAdmin.getImage()));
                model.addAttribute("publicationPostAdminList", publicationPostAdminDTO);
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-page-admin-edit-publication";
            }
            return "redirect:/admin-mine/admin-publications";
        }
        return "redirect:/";
    }

    private String convertText(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }

    //TODO UserPublications
    @GetMapping("/admin-mine/users-publications")
    public String adminUserPublications(Model model,
                                        Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<PublicationUser> allPublications = publicationRepository.findAll();
            long countPublications = publicationRepository.count();
            model.addAttribute("countPublications", countPublications);
            model.addAttribute("allPublications", allPublications);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-user-publication";
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/users-publications/{idUserPublication}/read")
    public String publicationEdit(@PathVariable(value = "idUserPublication") int idUserPublication,
                                  Model model,
                                  Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<PublicationUser> post = publicationRepository.findById(idUserPublication);
            if (post.isPresent()) {
                model.addAttribute("title", ADMIN_PAGE);
                model.addAttribute("publication", post.get());
                return "admin-mine-read-user-publication";
            }
            return "redirect:/admin-mine/admin-publications";
        }
        return "redirect:/";
    }


    //TODO adminPublications
    @Transactional
    @GetMapping("/admin-mine/admin-publications")
    public String adminPublications(Model model,
                                    Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<PublicationPostAdminDTO> publicationPostAdminRepositoryAll = publicationPostAdminRepository.findAll().stream()
                    .map(el -> new PublicationPostAdminDTO(
                            el.getId(),
                            el.getDate(),
                            convertText(el.getTitleText()),
                            convertText(el.getFullText()),
                            converterImage(el.getImage())))
                    .sorted(Comparator.comparing(PublicationPostAdminDTO::getId).reversed())
                    .collect(Collectors.toList());
            long countPublications = publicationPostAdminRepository.count();
            model.addAttribute("countPublications", countPublications);
            model.addAttribute("publicationPostAdminRepositoryAll", publicationPostAdminRepositoryAll);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-admin-publication";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/remove")
    public String adminPublicationRemove(@PathVariable(value = "id") int id,
                                         Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<PublicationPostAdmin> publicationPostAdminOptional = publicationPostAdminRepository.findById(id);
            publicationPostAdminOptional.ifPresent(publicationPostAdminRepository::delete);
            return "redirect:/admin-mine/admin-publications";
        }
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-publications/{id}/edit")
    public String adminPublicationUpdate(@PathVariable(value = "id") int id,
                                         MultipartFile file,
                                         @RequestParam String titleText,
                                         @RequestParam String fullText,
                                         Authentication authentication) throws IOException {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<PublicationPostAdmin> publicationPostAdminOptional = publicationPostAdminRepository.findById(id);
            if (publicationPostAdminOptional.isPresent()) {
                PublicationPostAdmin publicationPostAdmin = publicationPostAdminOptional.get();
                publicationPostAdmin.setTitleText(titleText);
                publicationPostAdmin.setFullText(convertText(fullText));
                publicationPostAdmin.setDate(new Date());
                ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
                if (!Objects.equals(file.getContentType(), "application/octet-stream")) { //new photo
                    publicationPostAdmin.setImage(convert.img);
                }
                publicationPostAdminRepository.save(publicationPostAdmin);
                compressorImgToJpg.deleteImage(convert.nameStart);
                compressorImgToJpg.deleteImage(convert.nameEnd);
                return "redirect:/admin-mine/admin-publications";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/admin-video")
    public String adminNewsGetVideo(Authentication authentication,
                                    Model model) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<VideoBoxAdmin> allByAddressAdmin = videoBoxAdminRepository.findAllById(user.getId());
            allByAddressAdmin.sort(Comparator.comparing(VideoBoxAdmin::getId).reversed());
            model.addAttribute("videoBoxAdminList", allByAddressAdmin);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-video-publication";
        }
        return "redirect:/";
    }

    @PostMapping("/admin-mine/admin-video")
    public String adminNewsAddVideo(Authentication authentication,
                                    @RequestParam("linkToVideo") String linkToVideo,
                                    @RequestParam("titleText") String titleText) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            String url = createURL(parseNormalURL(linkToVideo));
            VideoBoxAdmin videoBox = new VideoBoxAdmin();
            videoBox.setLinkToVideo(url);
            videoBox.setTitleText(titleText);
            videoBox.setDate(new Date());
            videoBoxAdminRepository.save(videoBox);
            return "redirect:/admin-mine/admin-video";
        }
        return "redirect:/";
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
    public String livingNewsVideoRemove(@PathVariable(value = "idVideoBox") int idVideoBox,
                                        Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<VideoBoxAdmin> videoBoxAdminOptional = videoBoxAdminRepository.findById(idVideoBox);
            videoBoxAdminOptional.ifPresent(videoBoxAdminRepository::delete);
            return "redirect:/admin-mine/admin-video";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoPublicationEdit(@PathVariable(value = "id") int id,
                                            Model model,
                                            Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<VideoBoxAdmin> videoBoxAdminOptional = videoBoxAdminRepository.findById(id);
            if (videoBoxAdminOptional.isPresent()) {
                model.addAttribute("videoBoxAdminArrayList", videoBoxAdminOptional.get());
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-page-edit-video-publication";
            }
            return "redirect:/admin-mine/admin-video";
        }
        return "redirect:/";
    }

    @Transactional
    @PostMapping("/admin-mine/admin-video/{id}/edit")
    public String adminVideoUpdate(@PathVariable(value = "id") int id,
                                   @RequestParam String titleText,
                                   @RequestParam String linkToVideo,
                                   Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
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
        return "redirect:/";
    }

    //TODO LETTER
    @GetMapping("/admin-mine/read-letters/enter-letters")
    public String adminReadLettersOfUserEnter(Authentication authentication,
                                              Model model) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<LetterToADMIN> letterToADMINS = letterToADMINRepository.findAll().stream()
                    .sorted(Comparator.comparing(LetterToADMIN::getDate).reversed())
                    .collect(Collectors.toList());
            model.addAttribute("adminLetters", letterToADMINS);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-read-user-letters-enter";
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/read-letters/outer-letters")
    public String adminReadLettersOfUserOuter(Model model,
                                              Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            List<LetterToUSER> letterFromADMINS = letterToUSERRepository.findAll().stream()
                    .filter(el -> el.getSenderAddress().equals(ADMIN))
                    .sorted(Comparator.comparing(LetterToUSER::getDate).reversed())
                    .collect(Collectors.toList());
            model.addAttribute("adminLetters", letterFromADMINS);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-read-user-letters-outer";
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/write-letters/send")
    public String adminWriteLetter(Authentication authentication,
                                   Model model) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            model.addAttribute("adminAddress", user.getAddress());
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-write-to-user-letters";
        }
        return "redirect:/";
    }

    @PostMapping("/admin-mine/write-letters/send")
    public String adminLetterSend(@RequestParam String titleText,
                                  @RequestParam String fullText,
                                  @RequestParam String recipientAddress,
                                  Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            LetterToUSER adminLetterToUSER = createLetterToUSER(titleText, fullText, recipientAddress, user);
            letterToUSERRepository.save(adminLetterToUSER);
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        return "redirect:/";
    }

    private LetterToUSER createLetterToUSER(String titleText,
                                            String fullText,
                                            String recipientAddress,
                                            User user) {
        LetterToUSER userSendLetterToUSER = new LetterToUSER();
        userSendLetterToUSER.setDate(new Date());
        userSendLetterToUSER.setTitleText(titleText);
        userSendLetterToUSER.setFullText(convertText(fullText));
        userSendLetterToUSER.setSenderAddress(user.getAddress());
        userSendLetterToUSER.setRecipientAddress(recipientAddress);
        return userSendLetterToUSER;
    }

    @GetMapping("/admin-mine/read-letters/enter-letters/{id}/read")
    public String adminReadLetterEnter(@PathVariable(value = "id") int id,
                                       Model model,
                                       Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<LetterToADMIN> letterOptional = letterToADMINRepository.findById(id);
            if (letterOptional.isPresent()) {
                model.addAttribute("letter", letterOptional.get());
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-page-read-user-letters-fulltext-enter";
            }
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/read-letters/outer-letters/{id}/read")
    public String adminReadLetterOuter(@PathVariable(value = "id") int id,
                                       Model model,
                                       Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<LetterToUSER> letterOptional = letterToUSERRepository.findById(id);
            if (letterOptional.isPresent()) {
                model.addAttribute("letter", letterOptional.get());
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-page-read-user-letters-fulltext-outer";
            }
            return "redirect:/admin-mine/read-letters/outer-letters";
        }
        return "redirect:/";
    }

    //TODO answer
    @GetMapping("/admin-mine/write-letters/{id}/answer")
    public String adminAnswerWriteLetter(Authentication authentication,
                                         @PathVariable(value = "id") int id,
                                         Model model) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<LetterToADMIN> letterOptional = letterToADMINRepository.findById(id);
            if (letterOptional.isPresent()) {
                model.addAttribute("senderAddress", letterOptional.get().getAddressUser());
                model.addAttribute("adminAddress", user.getAddress());
                model.addAttribute("title", ADMIN_PAGE);
                return "admin-page-write-to-user-letters-answer";
            }
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        return "redirect:/";
    }

    @PostMapping("/admin-mine/write-letters/answer")
    public String adminAnswerLetterSend(LetterToUSER letterToUSER,
                                        @RequestParam String titleText,
                                        @RequestParam String fullText,
                                        @RequestParam String senderAddress,
                                        @RequestParam String recipientAddress,
                                        Authentication authentication) {

        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            LetterToUSER adminLetterToUSER = createLetterToUSER(titleText, fullText, recipientAddress, user);
            letterToUSERRepository.save(adminLetterToUSER);
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        return "redirect:/";
    }

    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/delete")
    public String adminDeleteLetter(@PathVariable(value = "idLetter") int idLetter,
                                    Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<LetterToADMIN> byId = letterToADMINRepository.findById(idLetter);
            byId.ifPresent(letterToADMINRepository::delete);
            return "redirect:/admin-mine/read-letters/enter-letters";
        }
        return "redirect:/";
    }

    @Transactional
    @GetMapping("/admin-mine/read-users-letters/enter-letters/{idLetter}/read")
    public String adminReadOneLetter(@PathVariable(value = "idLetter") int idLetter,
                                     Model model,
                                     Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            Optional<LetterToADMIN> letterOptional = letterToADMINRepository.findById(idLetter);
            letterOptional.ifPresent(letterToADMIN -> model.addAttribute("letterReadOne", letterToADMIN));
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-read-one-letter";
        }
        return "redirect:/";
    }

    //TODO METRICS
    @GetMapping("/admin-mine/read-metrics")
    public String getMetricsData(Model model,
                                 Authentication authentication) {
        User user = getUser(authentication);
        if (user.getRole().equals(Role.ADMIN)) {
            metricsService.filterDays();
            List<MetricsDTO> allMetricsDTOs = metricsService.findAllMetricsDTOs().stream()
                    .sorted(Comparator.comparing(MetricsDTO::getDate).reversed())
                    .collect(Collectors.toList());
            model.addAttribute("metrics", allMetricsDTOs);
            model.addAttribute("title", ADMIN_PAGE);
            return "admin-page-metrics";
        }
        return "redirect:/";
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
