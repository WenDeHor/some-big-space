package com.myhome.controllers;

import com.myhome.models.*;
import com.myhome.repository.LetterRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
public class StudyRoomControllers {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final LetterRepository letterRepository;

    public StudyRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, LetterRepository letterRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.letterRepository = letterRepository;
    }

    //TODO LETTER
    @GetMapping("/study/write-letter")
    public String studyWriteLetter(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();

        model.addAttribute("address", address);
        model.addAttribute("title", "Letter");
        return "study-write-letter";
    }

    @PostMapping("/study/write-letter/send")
    public String LetterSend(Letter letter,
                            @RequestParam String titleText,
                            @RequestParam String fullText,
                            @RequestParam String senderAddress,
                            @RequestParam String recipientAddress,
                            Model model, Authentication authentication) {

//        private LocalDate localDate;
//        private Integer numberOfLetter;

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();
        String Login = oneByEmail.get().getLogin();
        System.out.println("mailingAddress  ".toUpperCase() + address + " Login:" + Login);

        LocalDate localDate = LocalDate.now();// получаем текущую дату
        String date = localDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")); // патерн формату дати

        Letter userSendLetter = Letter.builder()
                .localDate(localDate)
                .titleText(titleText)
                .fullText(fullText)
                .numberOfLetter(numberOfLetter())
                .senderAddress(senderAddress)
                .recipientAddress(recipientAddress)
                .build();
        System.out.println(userSendLetter.toString());
        letterRepository.save(userSendLetter);
        return "redirect:/user-page";
    }
    private Integer numberOfLetter() {
        List<Letter> allByNumber =  letterRepository.findAll();
        int size = allByNumber.size();
        return size + 1;
    }

    @GetMapping("/study/read-letters")
    public String studyReadLettersOfUser(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String address = oneByEmail.get().getAddress();
        Iterable<Letter> lettersRecipientUser = letterRepository.findAllByRecipientAddress(address);
        Iterable<Letter> letterSendersUser = letterRepository.findAllBySenderAddress(address);
        List<Letter>letters=new ArrayList<>();
        lettersRecipientUser.forEach(letters::add);
        letterSendersUser.forEach(letters::add);

//        letters.sort(Comparator.comparing(Letter::getNumberOfLetter));
        letters.sort(Comparator.comparing(Letter::getNumberOfLetter).reversed());

        model.addAttribute("letters", letters);
        model.addAttribute("title", "letters");
        return "study-read-letters";
    }

    //TODO Publications
    @GetMapping("/study/read-publications")
    public String studyReadPublicationsOfUser(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Iterable<PublicationUser> publicationUser = publicationRepository.findAllByEmail(userName);

        model.addAttribute("publicationUser", publicationUser);
        model.addAttribute("title", "Publication of User");
        return "study-read-publications";
    }

    @GetMapping("/study/publication/{idPublication}/remove")
    public String publicationRemove(@PathVariable(value = "idPublication") Long idPublication, Model model) {
        PublicationUser publicationUser = publicationRepository.findById(idPublication).orElseThrow(null);
        publicationRepository.delete(publicationUser);
        return "redirect:/study/read-publications";
    }

    @GetMapping("/study/publication/{idPublication}/edit")
    public String publicationEdit(@PathVariable(value = "idPublication") Long idPublication, Model model) {
        if (!publicationRepository.existsById(idPublication)) {
            return "redirect:/study/read-publications";
        }
        Optional<PublicationUser> post = publicationRepository.findById(idPublication);
//        Optional<PublicationUser> post = publicationRepository.findAllByEmailId(idPublication);
        List<PublicationUser> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("publication", res);
        return "study-edit-publications";
    }

    @PostMapping("/study/publication/{idPublication}/edit")
    public String publicationUpdate(@PathVariable(value = "idPublication") Long idPublication,
                                    @RequestParam String titleText,
                                    @RequestParam String fullText,
                                    Model model) {
        PublicationUser publicationUser = publicationRepository.findById(idPublication).orElseThrow(null);
        publicationUser.setTitleText(titleText);
//        String url= "https://www.youtube.com/embed/"+video+"?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
//        https://www.youtube.com/embed/vguSoDvurss?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent
        publicationUser.setFullText(fullText);
        LocalDate localDate = LocalDate.now();
        publicationUser.setLocalDate(localDate);
        publicationRepository.save(publicationUser);
        return "redirect:/study/read-publications";
    }

    @GetMapping("/study/write-publication")
    public String studyWritePublication(Model model) {
        return "study-write-publication";
    }

    @PostMapping("/study/publication/add")
    public String blogPublicationAdd(PublicationUser pub,
                                     @RequestParam String titleText,
                                     @RequestParam String fullText,
                                     Model model, Authentication authentication) {

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();

//        publicationRepository.findOneByEmail(userEmail);
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();
        String Login = oneByEmail.get().getLogin();
        System.out.println("mailingAddress  ".toUpperCase() + address + "Login:" + Login);

        LocalDate localDate = LocalDate.now();// получаем текущую дату
        String date = localDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")); // патерн формату дати

        PublicationUser publicationUser = PublicationUser.builder()
                .address(address)
                .email(userEmail)
                .localDate(localDate)
                .titleText(titleText)
                .fullText(fullText)
                .build();
        System.out.println(publicationUser.toString());
        publicationRepository.save(publicationUser);
        return "redirect:/user-page";
    }

    //TODO ЗРАЗОК з іншого проекту

    //    @GetMapping("/publication/add")
//    public String publication(Model model) {
//
//        return "publication-add";
//    }
//    @GetMapping("/blog/user/{id}")
//    public String blogUserDetails(@PathVariable(value = "id") Long id, Model model) {
//        Optional<PublicationUser> post = publicationRepository.findById(id);
//        List<PublicationUser> userpost = new ArrayList<>();
//        post.ifPresent(userpost::add);
//        model.addAttribute("title", "user page");
//        model.addAttribute("post", userpost);
//        return "blogUserDetails";
//    }

//    @PostMapping("/blog/{id}/edit")
//    public String blogPostUpdate(@PathVariable(value = "id") Long id,
//                                 @RequestParam String title,
//                                 @RequestParam String video,
//                                 @RequestParam String anons,
//                                 @RequestParam String full_text,
//                                 Model model) {
//        PublicationUser publicationUser = publicationRepository.findById(id).orElseThrow(null);
//        publicationUser.setTitle(title);
////        String url= "https://www.youtube.com/embed/"+video+"?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
////        https://www.youtube.com/embed/vguSoDvurss?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent
//        publicationUser.setVideo(video);
//        publicationUser.setAnons(anons);
//        publicationUser.setFull_text(full_text);
//        publicationRepository.save(publicationUser);
//        return "redirect:/blog";
//    }

//    @GetMapping("/blog/{id}")
//    public String blogDetails(@PathVariable(value = "id") Long id, Model model) {
//
//        if (!publicationRepository.existsById(id)) {
//            return "redirect:/blog";
//        }
//
//        Optional<PublicationUser> post = publicationRepository.findById(id);
//        List<PublicationUser> res = new ArrayList<>();
//        post.ifPresent(res::add);
//        model.addAttribute("post", res);
//        return "blog-details";
//    }
}
