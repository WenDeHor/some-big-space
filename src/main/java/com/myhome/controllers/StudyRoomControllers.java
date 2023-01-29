package com.myhome.controllers;

import com.myhome.models.LetterToUSER;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.repository.LetterToUSERRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class StudyRoomControllers {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final LetterToUSERRepository letterToUSERRepository;

    public StudyRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, LetterToUSERRepository letterToUSERRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.letterToUSERRepository = letterToUSERRepository;
    }

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
    public String LetterSend(LetterToUSER letterToUSER,
                             @RequestParam String titleText,
                             @RequestParam String fullText,
                             @RequestParam String senderAddress,
                             @RequestParam String recipientAddress,
                             Model model, Authentication authentication) {

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();
        String Login = oneByEmail.get().getLogin();
        System.out.println("mailingAddress  ".toUpperCase() + address + " Login:" + Login);

        LocalDate localDate = LocalDate.now();// получаем текущую дату
        String localdate = localDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")); // патерн формату дати

        Date date = new Date();

        LetterToUSER userSendLetterToUSER = new LetterToUSER();
        userSendLetterToUSER.setDate(date);
        userSendLetterToUSER.setTitleText(titleText);
        userSendLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        userSendLetterToUSER.setSenderAddress(senderAddress);
        userSendLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(userSendLetterToUSER);
        return "redirect:/user-page";
    }

    private String convertTextWithFormatToSave(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @GetMapping("/study/read-letters")
    public String studyReadLettersOfUser(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String address = oneByEmail.get().getAddress();
        Iterable<LetterToUSER> lettersRecipientUser = letterToUSERRepository.findAllByRecipientAddress(address);
        Iterable<LetterToUSER> letterSendersUser = letterToUSERRepository.findAllBySenderAddress(address);
        List<LetterToUSER> letterToUSERS = new ArrayList<>();
        lettersRecipientUser.forEach(letterToUSERS::add);
        letterSendersUser.forEach(letterToUSERS::add);

        letterToUSERS.sort(Comparator.comparing(LetterToUSER::getDate).reversed());

        model.addAttribute("letters", letterToUSERS);
        model.addAttribute("title", "letters");
        return "study-read-letters";
    }

    //TODO Publications
    @GetMapping("/study/read-publications")
    public String studyReadPublicationsOfUser(Authentication authentication, Model model) {

        String userAddress = findUserAddress(authentication);
        List<PublicationUser> publicationUser = publicationRepository.findAllByAddress(userAddress);

        List<PublicationUser> publicationList = new ArrayList<>(publicationUser);
        publicationList.sort(Comparator.comparing(PublicationUser::getIdPublication).reversed());

        model.addAttribute("publicationUser", publicationList);
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
        model.addAttribute("publication", convertTextWithFormatPublicationEdit(res));
        return "study-edit-publications";
    }

    private List<PublicationUser> convertTextWithFormatPublicationEdit(List<PublicationUser> publicationPostAdminList) {
        List<PublicationUser> list = new ArrayList<>();
        for (PublicationUser publicationPostAdmin : publicationPostAdminList) {
            String fullText = publicationPostAdmin.getFullText();
            String trim1 = fullText.replace("&#160&#160 ", "");
            String trim2 = trim1.replace("<br>", "");
            publicationPostAdmin.setFullText(trim2);
            list.add(publicationPostAdmin);
        }
        return list;
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
        publicationUser.setFullText(convertTextWithFormatToSave(fullText));
        Date date = new Date();
        publicationUser.setDate(date);
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

        LocalDate localDateNow = LocalDate.now();// получаем текущую дату
        String localDate = localDateNow.format(DateTimeFormatter.ofPattern("yyyy:MM:dd")); // патерн формату дати

        Date date = new Date();
        PublicationUser publicationUser = new PublicationUser();
        publicationUser.setAddress(address);
        publicationUser.setEmail(userEmail);
        publicationUser.setDate(date);
        publicationUser.setTitleText(titleText);
        publicationUser.setTitleText(convertTextWithFormatToSave(fullText));
        publicationRepository.save(publicationUser);
        return "redirect:/study/read-publications";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }
}
