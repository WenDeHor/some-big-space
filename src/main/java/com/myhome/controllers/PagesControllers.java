package com.myhome.controllers;


import com.myhome.models.PublicationPostAdmin;
import com.myhome.models.User;
import com.myhome.models.UserPhoto;
import com.myhome.models.VideoBoxAdmin;
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
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class PagesControllers {
    //    Don't let the sun go down until you keep your promises
    // Картінки підгружати на різні кімнати
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final MyFriendsRepository myFriendsRepository;
    private final VideoBoxAdminRepository videoBoxAdminRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final PublicationPostAdminRepository publicationPostAdminRepository;

    public PagesControllers(PublicationRepository publicationRepository, UserRepository userRepository, MyFriendsRepository myFriendsRepository, VideoBoxAdminRepository videoBoxAdminRepository, UserPhotoRepository userPhotoRepository, PublicationPostAdminRepository publicationPostAdminRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.myFriendsRepository = myFriendsRepository;
        this.videoBoxAdminRepository = videoBoxAdminRepository;
        this.userPhotoRepository = userPhotoRepository;
        this.publicationPostAdminRepository = publicationPostAdminRepository;
    }


    @GetMapping("/")
    public String home(Model model) {

        Iterable<VideoBoxAdmin> firstByDate = videoBoxAdminRepository.findAll();
        List<VideoBoxAdmin> video = new ArrayList<>((Collection<? extends VideoBoxAdmin>) firstByDate);
        int size = video.size();
        VideoBoxAdmin videoBoxAdmin = video.get(size - 1);

        Iterable<PublicationPostAdmin> publications = publicationPostAdminRepository.findAll();
        List<PublicationPostAdmin> publicationPostAdminList = new ArrayList<>();
        publications.forEach(publicationPostAdminList::add);
        publicationPostAdminList.sort(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed());

        model.addAttribute("videoBoxAdmin", videoBoxAdmin);
        model.addAttribute("publications", publicationPostAdminList);
        model.addAttribute("title", "MYHOME");
        return "mine-page";
    }

//    @Transactional
//    @GetMapping("/image/display/publications/{id}")
//    @ResponseBody
//    void minePagePublications(@PathVariable("id") Long id,
//                              HttpServletResponse response,
//                              Optional<PublicationPostAdmin> adminPhoto) throws ServletException, IOException {
//
//        Optional<PublicationPostAdmin> byId = publicationPostAdminRepository.findByIdPublication(id);
//        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
//        response.getOutputStream().write(byId.get().getImage());
//        response.getOutputStream().close();
//    }


    @GetMapping("/admin-page")
    public String userPage() {
        return "redirect:/admin-mine/users";
    }

//    @Transactional
//    @GetMapping("/user-page")
//    public String userFirstLoad(Authentication authentication, Model model, MultipartFile file) throws IOException {
//
//        String userAddress = findUserAddress(authentication);
//        Optional<UserPhoto> userPhotoFind = userPhotoRepository.findOneByAddress(userAddress);
//        final byte[] inputBytes = Files.readAllBytes(Paths.get("src/main/resources/static/img/mine-photo.jpg"));
//
//        if (!userPhotoFind.isPresent()) {
//            UserPhoto userFirst = new UserPhoto();
//            userFirst.setFullText("Tell us about yourself and your family");
//            userFirst.setImage(inputBytes);
//            userFirst.setAddress(userAddress);
//            userFirst.setType("image/jpg");
//            userFirst.setName("mine-photo.jpg");
//            userPhotoRepository.save(userFirst);
//        }
//        return "redirect:/load";
//
//    }

    @Transactional
    @GetMapping("/user-page")
    public String userSecondLoad(Authentication authentication, Model model, MultipartFile file) throws IOException {
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
                                      Model model,
                                      MultipartFile file,
                                      @RequestParam("fullText") String fullText) throws IOException {
        String userAddress = findUserAddress(authentication);
//        Optional<UserPhoto> oneByAddress = userPhotoRepository.findOneByAddress(userAddress);

        UserPhoto userPhoto = new UserPhoto();
        userPhoto.setAddress(userAddress);
        userPhoto.setImage(file.getBytes());
        userPhoto.setName(file.getOriginalFilename());
        userPhoto.setType(file.getContentType());
        userPhoto.setFullText(fullText);
        System.out.println(userPhoto.toString());
        userPhotoRepository.deleteByAddress(userAddress);
        userPhotoRepository.save(userPhoto); //Dell

//        if (userPhotoRepository.getClass().getName().equals("mine-photo.jpg") && !(file.getBytes() == null)) {
//            userPhotoRepository.deleteAll();
//            userPhotoRepository.save(userPhoto);
//        }

//        if (!(file.getBytes() == null) && !Objects.equals(file.getContentType(), "application/octet-stream")) {
//            userPhotoRepository.deleteAll();
//            userPhotoRepository.save(userPhoto);
//        }
        return "redirect:/user-page";
    }

    @GetMapping("/user/page/photo/display/{id}")
    @ResponseBody
    void showUserPageCPhoto(@PathVariable("id") Long id,
                            HttpServletResponse response,
                            Optional<UserPhoto> userPhoto) throws ServletException, IOException {

        userPhoto = userPhotoRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(userPhoto.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/study")
    public String studyWritePublication() {
        return "redirect:/study/read-publications";
    }

    @GetMapping("/kitchen")
    public String kitchenReadCookbook() {
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/living")
    public String livingReadPublications(Authentication authentication, Model model) {
//        Iterable<PublicationUser> publications = publicationRepository.findAll();
//        String userAddress = findUserAddress(authentication);
//       Optional<MyFriends> allByAddressUser = myFriendsRepository.findAllByAddressUser(userAddress);
//        List<MyFriends> myFriendsList = new ArrayList<>();
//        allByAddressUser.ifPresent(myFriendsList::add);

//        //TODO make class iterable MyFriendsRepository . ERROR
//        Iterable<PublicationUser> allByAddress = publicationRepository.findAllByAddress(allByAddressUser);
//        List<PublicationUser> publicationUserList = new ArrayList<>();
//        allByAddress.forEach(publicationUserList::add);
//        model.addAttribute("publications", publications);
        model.addAttribute("title", "LIVING ROOM");
        return "redirect:/living/publications";
    }

    @GetMapping("/safe")
    public String safeReadCookbook() {
        return "redirect:/safe/read-save-diary";
    }


    @GetMapping("/registration-error")
    public String registrationError(Model model) {
        model.addAttribute("title", "Registration-error");
        return "registration-error";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
