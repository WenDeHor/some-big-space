package com.myhome.controllers;

import com.myhome.forms.LettersDTO;
import com.myhome.forms.PublicationDTO;
import com.myhome.models.LetterToADMIN;
import com.myhome.models.LetterToUSER;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.repository.LetterToADMINRepository;
import com.myhome.repository.LetterToUSERRepository;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.MetricsService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class _1_StudyRoomControllers {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final LetterToUSERRepository letterToUSERRepository;
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;

    private final static int LIMIT_LIST = 10;
    private final String MY_ROOM = "Моя кімната";
    private final String ADMIN = "ADMIN from New_Apple";

    public _1_StudyRoomControllers(PublicationRepository publicationRepository, UserRepository userRepository, LetterToUSERRepository letterToUSERRepository, LetterToADMINRepository letterToADMINRepository, MetricsService metricsService) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
        this.letterToUSERRepository = letterToUSERRepository;
        this.letterToADMINRepository = letterToADMINRepository;
        this.metricsService = metricsService;
    }

    @GetMapping("/study/write-letter")
    public String studyWriteLetter(Authentication authentication,
                                   Model model,
                                   HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String address = findUserAddress(authentication);

        model.addAttribute("address", address);
        model.addAttribute("title", MY_ROOM);
        return "study-write-letter";
    }

    @PostMapping("/study/write-letter/send")
    public String letterSend(@RequestParam String titleText,
                             @RequestParam String fullText,
                             @RequestParam String recipientAddress,
                             Authentication authentication) {
        User user = getUser(authentication);
        if (recipientAddress.equals(ADMIN)) {
            LetterToADMIN userSendLetterToADMIN = new LetterToADMIN();
            userSendLetterToADMIN.setDate(new Date());
            userSendLetterToADMIN.setTitleText(titleText);
            userSendLetterToADMIN.setFullText(convertTextWithFormatToSave(fullText));
            userSendLetterToADMIN.setAddressUser(user.getAddress());
            letterToADMINRepository.save(userSendLetterToADMIN);
        } else {
            LetterToUSER userSendLetterToUSER = createLetterToUSER(titleText, fullText, recipientAddress, user);
            letterToUSERRepository.save(userSendLetterToUSER);
        }
        return "redirect:/study/outer-letters";
    }

    private LetterToUSER createLetterToUSER(String titleText, String fullText, String recipientAddress, User user) {
        LetterToUSER userSendLetterToUSER = new LetterToUSER();
        userSendLetterToUSER.setDate(new Date());
        userSendLetterToUSER.setTitleText(titleText);
        userSendLetterToUSER.setFullText(convertTextWithFormatToSave(fullText));
        userSendLetterToUSER.setSenderAddress(user.getAddress());
        userSendLetterToUSER.setRecipientAddress(recipientAddress);
        return userSendLetterToUSER;
    }

    private String convertTextWithFormatToSave(String fullText) {
        String text = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text);
    }

    @GetMapping("/study/outer-letters")
    public String studyReadAllOutLetters(Authentication authentication,
                                         Model model,
                                         HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String address = findUserAddress(authentication);
        List<LettersDTO> lettersDTOS = Stream
                .concat(letterToUSERRepository.findAllBySenderAddress(address).stream()
                                .map(el -> new LettersDTO(el.getId(), el.getDate(), el.getTitleText(), el.getFullText(), buildInfoBySenderToLetterToUSER(el))),
                        letterToADMINRepository.findAllByAddressUser(address).stream()
                                .map(el -> new LettersDTO(el.getId(), el.getDate(), el.getTitleText(), el.getFullText(), buildInfoBySenderToLetterToADMIN(el))))
                .sorted(Comparator.comparing(LettersDTO::getDate).reversed())
                .collect(Collectors.toList());
        model.addAttribute("letters", lettersDTOS);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-outer";
    }

    private String buildInfoBySenderToLetterToUSER(LetterToUSER letter) {
        return "Отримувач: " + letter.getRecipientAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    private String buildInfoBySenderToLetterToADMIN(LetterToADMIN letter) {
        return "Отримувач: " + ADMIN +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/outer-letters/{idLetter}/remove")
    public String letterRemoveByOuter(@PathVariable(value = "idLetter") int idLetter) {
        LetterToUSER letter = letterToUSERRepository.findById(idLetter).orElseThrow(null);
        letterToUSERRepository.delete(letter);
        return "redirect:/study/outer-letters";
    }

    @GetMapping("/study/enter-letters")
    public String studyReadAlEnterLetters(Authentication authentication,
                                          Model model) {
        String address = findUserAddress(authentication);
        List<LettersDTO> lettersDTOS = letterToUSERRepository.findAllByRecipientAddress(address).stream()
                .map(el -> new LettersDTO(el.getId(), el.getTitleText(), el.getFullText(), buildInfoByRecipient(el)))
                .sorted(Comparator.comparing(LettersDTO::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("letters", lettersDTOS);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-enter";
    }

    private String buildInfoByRecipient(LetterToUSER letter) {
        return "Відправник : " + letter.getSenderAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/enter-letters/{idLetter}/remove")
    public String letterRemoveByEnter(@PathVariable(value = "idLetter") int idLetter) {
        LetterToUSER letter = letterToUSERRepository.findById(idLetter).orElseThrow(null);
        letterToUSERRepository.delete(letter);
        return "redirect:/study/enter-letters";
    }

    //TODO Publications
    @GetMapping("/study/read-all-publications")
    public String studyReadAllPublicationsOfUser(Authentication authentication,
                                                 Model model,
                                                 HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int userId = user.getId();
        List<PublicationDTO> publications = publicationRepository.findAllByIdUserNot(userId).stream()
                .limit(LIMIT_LIST)
                .map(el -> new PublicationDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getFullText(),
                        info(el)))
                .sorted(Comparator.comparing(PublicationDTO::getId).reversed())
                .collect(Collectors.toList());

        model.addAttribute("publications", publications);
        model.addAttribute("title", MY_ROOM);
        return "study-read-all-publications";
    }

    private String info(PublicationUser publicationUser) {
        Optional<User> userOptional = userRepository.findOneById(publicationUser.getIdUser());
        return userOptional.map(value -> "Автор : " + value.getAddress() +
                ", Дата: " + publicationUser.getDate().toString().split("\\s")[0])
                .orElseGet(() -> "Дата: " + publicationUser.getDate().toString().split("\\s")[0]);
    }

    @GetMapping("/study/read-my-publications")
    public String studyReadMyPublicationsOfUser(Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<PublicationUser> publicationList = publicationRepository.findAllByIdUser(idUser).stream()
                .sorted(Comparator.comparing(PublicationUser::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("publicationUser", publicationList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-my-publications";
    }

    @GetMapping("/study/publication/{idPublication}/remove")
    public String publicationRemove(@PathVariable(value = "idPublication") int idPublication) {
        PublicationUser publicationUser = publicationRepository.findById(idPublication).orElseThrow(null);
        publicationRepository.delete(publicationUser);
        return "redirect:/study/read-my-publications";
    }

    @GetMapping("/study/publication/{idPublication}/edit")
    public String publicationEdit(@PathVariable(value = "idPublication") int idPublication,
                                  Model model) {
        if (!publicationRepository.existsById(idPublication)) {
            return "redirect:/study/read-my-publications";
        }
        Optional<PublicationUser> userPublication = publicationRepository.findById(idPublication);
        PublicationUser publicationUser = userPublication.orElseGet(PublicationUser::new);
        model.addAttribute("title", MY_ROOM);
        model.addAttribute("publication", convertTextWithFormatPublicationEdit(publicationUser));
        return "study-edit-publications";
    }

    private PublicationUser convertTextWithFormatPublicationEdit(PublicationUser publication) {
        String fullText = publication.getFullText();
        String trim = fullText
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
        publication.setFullText(trim);
        return publication;
    }

    @PostMapping("/study/publication/{idPublication}/edit")
    public String publicationUpdate(@PathVariable(value = "idPublication") int idPublication,
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
        User user = getUser(authentication);
        PublicationUser publicationUser = new PublicationUser();
        publicationUser.setIdUser(user.getId());
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

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }
}
