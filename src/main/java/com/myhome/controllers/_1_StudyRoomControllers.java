package com.myhome.controllers;

import com.myhome.forms.LettersDTO;
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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class _1_StudyRoomControllers {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final LetterToUSERRepository letterToUSERRepository;

    private final int LIMIT_LIST = 10;
    private final String MY_ROOM = "Моя кімната";

    public _1_StudyRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, LetterToUSERRepository letterToUSERRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.letterToUSERRepository = letterToUSERRepository;
    }

    @GetMapping("/study/write-letter")
    public String studyWriteLetter(Authentication authentication,
                                   Model model) {
        String address = findUserAddress(authentication);

        model.addAttribute("address", address);
        model.addAttribute("title", MY_ROOM);
        return "study-write-letter";
    }

    @PostMapping("/study/write-letter/send")
    public String LetterSend(@RequestParam String titleText,
                             @RequestParam String fullText,
                             @RequestParam String recipientAddress,
                             Authentication authentication) {
        String address = findUserAddress(authentication);

        LetterToUSER userSendLetterToUSER = new LetterToUSER();
        userSendLetterToUSER.setDate(new Date());
        userSendLetterToUSER.setTitleText(titleText);
        userSendLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        userSendLetterToUSER.setSenderAddress(address);
        userSendLetterToUSER.setRecipientAddress(recipientAddress);
        letterToUSERRepository.save(userSendLetterToUSER);
        return "redirect:/study/outer-letters";
    }

    private String convertTextWithFormatToSave(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @GetMapping("/study/outer-letters")
    public String studyReadAllOutLetters(Authentication authentication,
                                         Model model) {
        String address = findUserAddress(authentication);
        List<LettersDTO> lettersDTOS = letterToUSERRepository.findAllBySenderAddress(address).stream()
                .map(el -> new LettersDTO(el.getIdLetter(), el.getTitleText(), el.getFullText(), buildInfoBySender(el)))
                .sorted(Comparator.comparing(LettersDTO::getIdLetter).reversed())
                .collect(Collectors.toList());
        model.addAttribute("letters", lettersDTOS);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-outer";
    }

    private String buildInfoBySender(LetterToUSER letter) {
        return "Отримувач: " + letter.getRecipientAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/outer-letters/{idLetter}/remove")
    public String letterRemoveByOuter(@PathVariable(value = "idLetter") Long idLetter) {
        LetterToUSER letter = letterToUSERRepository.findById(idLetter).orElseThrow(null);
        letterToUSERRepository.delete(letter);
        return "redirect:/study/outer-letters";
    }

    @GetMapping("/study/enter-letters")
    public String studyReadAlEnterLetters(Authentication authentication,
                                          Model model) {
        String address = findUserAddress(authentication);
        List<LettersDTO> lettersDTOS = letterToUSERRepository.findAllByRecipientAddress(address).stream()
                .map(el -> new LettersDTO(el.getIdLetter(), el.getTitleText(), el.getFullText(), buildInfoByRecipient(el)))
                .sorted(Comparator.comparing(LettersDTO::getIdLetter).reversed())
                .collect(Collectors.toList());
        model.addAttribute("letters", lettersDTOS);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-enter"; //TODO UI
    }

    private String buildInfoByRecipient(LetterToUSER letter) {
        return "Відправник : " + letter.getSenderAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/enter-letters/{idLetter}/remove")
    public String letterRemoveByEnter(@PathVariable(value = "idLetter") Long idLetter) {
        LetterToUSER letter = letterToUSERRepository.findById(idLetter).orElseThrow(null);
        letterToUSERRepository.delete(letter);
        return "redirect:/study/enter-letters";
    }

    //TODO Publications
    @GetMapping("/study/read-all-publications")
    public String studyReadAllPublicationsOfUser(Authentication authentication,
                                                 Model model) {
        String userAddress = findUserAddress(authentication);
        List<PublicationUser> publicationAllUsers = publicationRepository.findAll().stream()
                .filter(el -> !el.getAddress().equals(userAddress))
                .limit(LIMIT_LIST)
                .sorted(Comparator.comparing(PublicationUser::getIdPublication).reversed())
                .collect(Collectors.toList());

        model.addAttribute("publicationUser", publicationAllUsers);
        model.addAttribute("title", MY_ROOM);
        return "study-read-all-publications";
    }

    @GetMapping("/study/read-my-publications")
    public String studyReadMyPublicationsOfUser(Authentication authentication,
                                                Model model) {
        String userAddress = findUserAddress(authentication);
        List<PublicationUser> publicationUser = publicationRepository.findAllByAddress(userAddress);
        List<PublicationUser> publicationList = new ArrayList<>(publicationUser);
        publicationList.sort(Comparator.comparing(PublicationUser::getIdPublication).reversed());

        model.addAttribute("publicationUser", publicationList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-my-publications";
    }

    @GetMapping("/study/publication/{idPublication}/remove")
    public String publicationRemove(@PathVariable(value = "idPublication") Long idPublication) {
        PublicationUser publicationUser = publicationRepository.findById(idPublication).orElseThrow(null);
        publicationRepository.delete(publicationUser);
        return "redirect:/study/read-my-publications";
    }

    @GetMapping("/study/publication/{idPublication}/edit")
    public String publicationEdit(@PathVariable(value = "idPublication") Long idPublication,
                                  Model model) {
        if (!publicationRepository.existsById(idPublication)) {
            return "redirect:/study/read-my-publications";
        }
        Optional<PublicationUser> post = publicationRepository.findById(idPublication);
        List<PublicationUser> res = new ArrayList<>();
        post.ifPresent(res::add);

        model.addAttribute("title", MY_ROOM);
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
                                    @RequestParam String fullText) {
        PublicationUser publicationUser = publicationRepository.findById(idPublication).orElseThrow(null);
        publicationUser.setTitleText(titleText);
        publicationUser.setFullText(convertTextWithFormatToSave(fullText));
        publicationUser.setDate(new Date());
        publicationRepository.save(publicationUser);
        return "redirect:/study/read-my-publications";
    }

    @GetMapping("/study/write-publication")
    public String studyWritePublication(Model model) {
        model.addAttribute("title", MY_ROOM);
        return "study-write-publication";
    }

    @PostMapping("/study/publication/add")
    public String blogPublicationAdd(@RequestParam String titleText,
                                     @RequestParam String fullText,
                                     Authentication authentication) {
        String userEmail = getUserEmail(authentication);
        String address = findUserAddress(authentication);

        PublicationUser publicationUser = new PublicationUser();
        publicationUser.setAddress(address);
        publicationUser.setEmail(userEmail);
        publicationUser.setDate(new Date());
        publicationUser.setTitleText(titleText);
        publicationUser.setFullText(convertTextWithFormatToSave(fullText));
        publicationRepository.save(publicationUser);
        return "redirect:/study/read-my-publications";
    }

    private String findUserAddress(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        return oneByEmail.get().getAddress();
    }

    private String getUserEmail(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        return details.getUsername();
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }
}
