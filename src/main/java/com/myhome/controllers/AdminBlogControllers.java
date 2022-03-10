package com.myhome.controllers;

import com.myhome.models.*;
import com.myhome.repository.*;
import com.myhome.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
public class AdminBlogControllers {
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final LetterRepository letterRepository;


    public AdminBlogControllers(UserRepository userRepository, PublicationRepository publicationRepository, PublicationPostAdminRepository publicationPostAdminRepository, VideoBoxAdminRepository videoBoxAdminRepository, LetterRepository letterRepository) {
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.letterRepository = letterRepository;
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

    @Transactional
    @PostMapping("/admin-mine/admin-publications")
    public String saveAdminPublications(Authentication authentication,
                                        Model model,
                                        MultipartFile file,
                                        @RequestParam("titleText") String titleText,
                                        @RequestParam("fullText") String fullText) throws IOException {

        String address = findUserAddress(authentication);
        Date date = new Date();

        PublicationPostAdmin publicationPostAdmin = new PublicationPostAdmin();
        publicationPostAdmin.setDate(date);
        publicationPostAdmin.setTitleText(titleText);
        publicationPostAdmin.setFullText(fullText);
        publicationPostAdmin.setAddress(address);
        publicationPostAdmin.setName(file.getOriginalFilename());
        publicationPostAdmin.setType(file.getContentType());
        publicationPostAdmin.setImage(file.getBytes());

        publicationPostAdminRepository.save(publicationPostAdmin);
        return "redirect:/admin-mine/admin-publications";
    }

    //    @Transactional
    @GetMapping("/image/display/admin/{idPublication}")
    @ResponseBody
    void showImageAdmin(@PathVariable("idPublication") Long idPublication,
                        HttpServletResponse response,
                        Optional<PublicationPostAdmin> publicationPostAdmin) throws ServletException, IOException {

        publicationPostAdmin = publicationPostAdminRepository.findById(idPublication);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(publicationPostAdmin.get().getImage());
        response.getOutputStream().close();
    }

//    @GetMapping("/user/page/photo/display/{id}")
//    @ResponseBody
//    void showUserPagePhoto(@PathVariable("id") Long id,
//                           HttpServletResponse response,
//                           Optional<UserPhoto> userPhoto) throws ServletException, IOException {
//        userPhoto = userPhotoRepository.findById(id);
//        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
//        response.getOutputStream().write(userPhoto.get().getImage());
//        response.getOutputStream().close();
//    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/remove")
    public String adminPublicationRemove(@PathVariable(value = "id") Long id, Model model) {
        PublicationPostAdmin publicationPostAdmin = publicationPostAdminRepository.findByIdPublication(id).orElseThrow(null);
        publicationPostAdminRepository.delete(publicationPostAdmin);
        return "redirect:/admin-mine/admin-publications";
    }

    @Transactional
    @GetMapping("/admin-mine/admin-publications/{id}/edit")
    public String adminPublicationEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!publicationPostAdminRepository.existsById(id)) {
            return "redirect:/admin-mine/admin-publications";
        }
        Optional<PublicationPostAdmin> byIdPublication = publicationPostAdminRepository.findByIdPublication(id);
        List<PublicationPostAdmin> publicationPostAdminList = new ArrayList<>();
        byIdPublication.ifPresent(publicationPostAdminList::add);
        publicationPostAdminList.sort(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed());
        model.addAttribute("publicationPostAdminList", publicationPostAdminList);

        return "admin-page-admin-edit-publication";
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
        publicationPostAdmin.setFullText(fullText);
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

    @GetMapping("/admin-mine/admin-video")
    public String adminNewVideo(Authentication authentication, Model model) {
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
        String url = "https://www.youtube.com/embed/" + linkToVideo + "?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
//        https://www.youtube.com/embed/ZV9qvauLlmo&t?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent
        VideoBoxAdmin videoBox = new VideoBoxAdmin();
        videoBox.setAddressAdmin(userAddress);
        videoBox.setLinkToVideo(url);
        videoBox.setTitleText(titleText);
        Date date = new Date();
        videoBox.setDate(date);
        videoBoxAdminRepository.save(videoBox);
        return "redirect:/admin-mine/admin-video";
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

    //TODO LETTER
    @GetMapping("/admin-mine/read-letters")
    public String adminReadLettersOfUser(Authentication authentication, Model model) {
        String adminAddress = findUserAddress(authentication);

        Iterable<Letter> allByRecipientAddress = letterRepository.findAllByRecipientAddress(adminAddress);
        Iterable<Letter> allBySenderAddress = letterRepository.findAllBySenderAddress(adminAddress);
        ArrayList<Letter> adminLetters = new ArrayList<>();
        allByRecipientAddress.forEach(adminLetters::add);
        allBySenderAddress.forEach(adminLetters::add);
        adminLetters.sort(Comparator.comparing(Letter::getDate).reversed());

        model.addAttribute("adminLetters", adminLetters);
        model.addAttribute("title", "Admin Page");
        return "admin-page-read-user-letters";
    }

    @GetMapping("/admin-mine/write-letters/send")
    public String adminWriteLetter(Authentication authentication, Model model) {
        String adminAddress = findUserAddress(authentication);

        model.addAttribute("adminAddress", adminAddress);
        model.addAttribute("title", "Admin Page");
        return "admin-page-write-to-user-letters";
    }

    @PostMapping("/admin-mine/write-letters/send")
    public String adminLetterSend(Letter letter,
                                  @RequestParam String titleText,
                                  @RequestParam String fullText,
                                  @RequestParam String senderAddress,
                                  @RequestParam String recipientAddress,
                                  Model model, Authentication authentication) {

        String adminAddress = findUserAddress(authentication);
        Date date = new Date();
        Letter adminLetter = Letter.builder()
                .date(date)
                .titleText(titleText)
                .fullText(fullText)
                .senderAddress(adminAddress)
                .recipientAddress(recipientAddress)
                .build();

        letterRepository.save(adminLetter);
        return "redirect:/admin-mine/read-letters";
    }

    //  <td><a th:href="'/admin-mine/read-letters/'+${adminLetter.fullText}+'/read'"
//    class="btn btn-warning">fullText</a></td>
    @GetMapping("/admin-mine/read-letters/{id}/read")
    public String adminReadLetter(@PathVariable(value = "id") Long id, Model model) {
        if (!letterRepository.existsById(id)) {
            return "redirect:/admin-mine/read-letters";
        }
        Optional<Letter> letter = letterRepository.findById(id);
        List<Letter> res = new ArrayList<>();
        letter.ifPresent(res::add);
        model.addAttribute("letter", res);
        return "admin-page-read-user-letters-fulltext";
    }

    //     <td><a th:href="'/admin-mine/read-letters/'+${adminLetter.idLetter}+'/answer'"
//    class="btn btn-warning">answer</a></td>
//TODO answer
    @GetMapping("/admin-mine/write-letters/{id}/answer")
    public String adminAnswerWriteLetter(Authentication authentication,
                                         @PathVariable(value = "id") Long id,
                                         Model model) {
        String adminAddress = findUserAddress(authentication);
        if (!letterRepository.existsById(id)) {
            return "redirect:/admin-mine/read-letters";
        }
        Optional<Letter> byId = letterRepository.findById(id);
        String senderAddress = byId.get().getSenderAddress();

        model.addAttribute("senderAddress", senderAddress);
        model.addAttribute("adminAddress", adminAddress);
        model.addAttribute("title", "Admin Page");
        return "admin-page-write-to-user-letters-answer";
    }

    @PostMapping("/admin-mine/write-letters/answer")
    public String adminAnswerLetterSend(Letter letter,
                                        @RequestParam String titleText,
                                        @RequestParam String fullText,
                                        @RequestParam String senderAddress,
                                        @RequestParam String recipientAddress,
                                        Model model, Authentication authentication) {

        String adminAddress = findUserAddress(authentication);
        Date date = new Date();
        Letter adminLetter = Letter.builder()
                .date(date)
                .titleText(titleText)
                .fullText(fullText)
                .senderAddress(adminAddress)
                .recipientAddress(recipientAddress)
                .build();

        letterRepository.save(adminLetter);
        return "redirect:/admin-mine/read-letters";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }

}
