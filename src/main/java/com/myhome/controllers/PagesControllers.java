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
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
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

        List<VideoBoxAdmin> firstByDate = videoBoxAdminRepository.findAll();
        List<VideoBoxAdmin> video = new ArrayList<>(firstByDate);
        int size = video.size();
        VideoBoxAdmin videoBoxAdmin = video.get(size - 1);

        List<PublicationPostAdmin> publications = publicationPostAdminRepository.findAll();
        List<PublicationPostAdmin> publicationPostAdminList = new ArrayList<>(publications);
        publicationPostAdminList.sort(Comparator.comparing(PublicationPostAdmin::getIdPublication).reversed());

        model.addAttribute("videoBoxAdmin", videoBoxAdmin);
        model.addAttribute("publications", publicationPostAdminList);
        model.addAttribute("title", "MYHOME");
        return "mine-page";
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
        return "redirect:/study/read-publications";
    }

    @GetMapping("/kitchen")
    public String kitchenReadCookbook() {
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/living")
    public String livingReadPublications(Authentication authentication, Model model) {
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

    private TargetDataLine microphone;
    private SourceDataLine speakers;

    @GetMapping("/call")
    public String startUserVolume() {
        for (int i = 0; i < 100; i++) {
            AudioFormat format = new AudioFormat(8000.0f, 8, 2, true, true);

            try {
                microphone = AudioSystem.getTargetDataLine(format);

                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                microphone = (TargetDataLine) AudioSystem.getLine(info);
                microphone.open(format);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int numBytesRead;
                int CHUNK_SIZE = 1024;
                byte[] data = new byte[microphone.getBufferSize() / 5];
                microphone.start();

                int bytesRead = 0;
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
                speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                speakers.open(format);
                speakers.start();
                while (bytesRead < 100000) {
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead += numBytesRead;
                    // write the mic data to a stream for use later
                    out.write(data, 0, numBytesRead);
                    // write mic data to stream for immediate playback
                    speakers.write(data, 0, numBytesRead);
                }
                speakers.drain();
                speakers.close();
                microphone.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/user-page";
    }

    @GetMapping("/stop")
    public String stopUserVolume() {
        speakers.close();
        microphone.close();
        return "redirect:/user-page";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }
}
