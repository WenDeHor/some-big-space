package com.myhome.controllers;

import com.myhome.forms.*;
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

import static java.util.stream.Collectors.toList;

@Controller
public class _1_StudyRoomControllers {
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final LetterToUSERRepository letterToUSERRepository;
    private final LetterToADMINRepository letterToADMINRepository;
    private final MetricsService metricsService;

    private int limit_letter_titleText = 100; //chars
    private int limit_letter_fullText = 3000; //chars
    private int limit_recipientAddress = 50;
    private int limit_publication_titleText = 100; //chars
    private int limit_publication_fullText = 3000; //chars

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
                             Authentication authentication,
                             Model model) {
        User user = getUser(authentication);
        ErrorMessage validator = validatorLetter(titleText, fullText, recipientAddress);
        if (validator.getOne().length() > 0 || validator.getTwo().length() > 0 || validator.getThree().length() > 0) {
            return letterEdit(titleText, fullText, recipientAddress, user, model, validator);
        }
        if (recipientAddress.equals(ADMIN)) {
            LetterToADMIN userSendLetterToADMIN = new LetterToADMIN();
            userSendLetterToADMIN.setDate(new Date());
            userSendLetterToADMIN.setTitleText(titleText);
            userSendLetterToADMIN.setFullText(convertTextToSave(fullText));
            userSendLetterToADMIN.setAddressUser(user.getAddress());
            letterToADMINRepository.save(userSendLetterToADMIN);
        } else {
            LetterToUSER userSendLetterToUSER = createLetterToUSER(titleText, convertTextToSave(fullText), recipientAddress, user);
            letterToUSERRepository.save(userSendLetterToUSER);
            return "redirect:/study/outer-letters";
        }
        return "redirect:/study/outer-letters";
    }

    private ErrorMessage validatorLetter(String titleText,
                                         String fullText,
                                         String recipientAddress) {
        Optional<User> isUserPresent = userRepository.findOneByAddress(recipientAddress.trim());
        ErrorMessage errorMessage = new ErrorMessage("", "", "");

        if (recipientAddress.length() > limit_recipientAddress || !isUserPresent.isPresent()) {
            errorMessage.setOne("1");
        }
        if (titleText.length() > limit_letter_titleText) {
            errorMessage.setTwo("2");
        }
        if (fullText.length() > limit_letter_fullText) {
            errorMessage.setThree("3");
        }
        return errorMessage;
    }

    private LetterToUSER createLetterToUSER(String titleText, String fullText, String recipientAddress, User user) {
        LetterToUSER userSendLetterToUSER = new LetterToUSER();
        userSendLetterToUSER.setDate(new Date());
        userSendLetterToUSER.setTitleText(titleText);
        userSendLetterToUSER.setFullText(fullText);
        userSendLetterToUSER.setSenderAddress(user.getAddress());
        userSendLetterToUSER.setRecipientAddress(recipientAddress);
        return userSendLetterToUSER;
    }

    private String letterEdit(String titleText, String fullText, String recipientAddress, User user, Model model, ErrorMessage errors) {
        LetterToUSER editUserLetter = createLetterToUSER(titleText, fullText, recipientAddress, user);
        editUserLetter.setSenderAddress(user.getAddress());
        model.addAttribute("title", MY_ROOM);
        model.addAttribute("error", errors);
        model.addAttribute("editUserLetter", editUserLetter);
        return "study-edit-letter";
    }

    @GetMapping("/study/outer-letters")
    public String studyReadAllOutLetters(Authentication authentication,
                                         Model model,
                                         HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String address = findUserAddress(authentication);
        List<LettersDTO> dtos = getAllBySenderAddress(address);
        List<LettersDTO> basePage = dtos.stream()
                .limit(LIMIT_LIST)
                .collect(toList());
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        model.addAttribute("letters", basePage);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-outer";
    }

    @GetMapping("/study/outer-letters/{offset}")
    public String studyReadAllOutLettersByPages(@PathVariable(value = "offset") int offset,
                                                Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String address = findUserAddress(authentication);
        List<LettersDTO> dtos = getAllBySenderAddress(address);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<LettersDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("letters", offsetList);
            model.addAttribute("title", MY_ROOM);
            return "study-read-letters-outer";
        }
        List<LettersDTO> offsetList = getOffsetListLettersDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("letters", offsetList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-outer";
    }

    private List<LettersDTO> getOffsetListLettersDTO(List<LettersDTO> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    List<LettersDTO> getAllBySenderAddress(String address) {
        return Stream
                .concat(letterToUSERRepository.findAllBySenderAddress(address).stream()
                                .map(el -> new LettersDTO(el.getId(), el.getDate(), el.getTitleText(), el.getFullText(),
                                        buildInfoBySenderToLetterToUSER(el), el.getRecipientAddress())),
                        letterToADMINRepository.findAllByAddressUser(address).stream()
                                .map(el -> new LettersDTO(el.getId(), el.getDate(), el.getTitleText(), el.getFullText(),
                                        buildInfoBySenderToLetterToADMIN(el), ADMIN)))
                .sorted(Comparator.comparing(LettersDTO::getDate).reversed())
                .collect(Collectors.toList());
    }

    private String buildInfoBySenderToLetterToUSER(LetterToUSER letter) {
        return "Отримувач: " + letter.getRecipientAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    private String buildInfoBySenderToLetterToADMIN(LetterToADMIN letter) {
        return "Отримувач: " + ADMIN +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/outer-letters/{idLetter}/remove/by-user/{email}")
    public String letterRemoveByOuter(@PathVariable(value = "idLetter") int idLetter,
                                      @PathVariable(value = "email") String email) {
        removeLetter(idLetter, email);
        return "redirect:/study/outer-letters";
    }

    @GetMapping("/study/enter-letters")
    public String studyReadAlEnterLetters(Authentication authentication,
                                          Model model) {
        String address = findUserAddress(authentication);
        List<LettersDTO> basePage = letterToUSERRepository.findAllByRecipientAddress(address).stream()
                .map(el -> new LettersDTO(el.getId(), el.getTitleText(), el.getFullText(), buildInfoByRecipient(el)))
                .sorted(Comparator.comparing(LettersDTO::getId).reversed())
                .limit(LIMIT_LIST)
                .collect(Collectors.toList());
        model.addAttribute("buttons", new Buttons(getCountPage(basePage.size()), "10"));
        model.addAttribute("letters", basePage);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-enter";
    }

    @GetMapping("/study/enter-letters/{offset}")
    public String studyReadAlEnterLettersByPages(@PathVariable(value = "offset") int offset,
                                                 Authentication authentication,
                                                 Model model) {
        String address = findUserAddress(authentication);
        List<LettersDTO> dtos = letterToUSERRepository.findAllByRecipientAddress(address).stream()
                .map(el -> new LettersDTO(el.getId(), el.getTitleText(), el.getFullText(), buildInfoByRecipient(el)))
                .sorted(Comparator.comparing(LettersDTO::getId).reversed())
                .collect(Collectors.toList());
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<LettersDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("letters", offsetList);
            model.addAttribute("title", MY_ROOM);
            return "study-read-letters-enter";
        }
        List<LettersDTO> offsetList = getOffsetListLettersDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("letters", offsetList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-letters-enter";
    }

    private String buildInfoByRecipient(LetterToUSER letter) {
        return "Відправник : " + letter.getSenderAddress() +
                ", Дата: " + letter.getDate().toString().split("\\s")[0];
    }

    @GetMapping("/study/enter-letters/{idLetter}/remove/by-user/{email}")
    public String letterRemoveByEnter(@PathVariable(value = "idLetter") int idLetter,
                                      @PathVariable(value = "email") String email) {
        removeLetter(idLetter, email);
        return "redirect:/study/enter-letters";
    }

    private void removeLetter(int idLetter, String email) {
        if (email.equals(ADMIN)) {
            Optional<LetterToADMIN> letterToADMIN = letterToADMINRepository.findById(idLetter);
            letterToADMIN.ifPresent(letterToADMINRepository::delete);
        } else {
            Optional<LetterToUSER> letterOptional = letterToUSERRepository.findById(idLetter);
            letterOptional.ifPresent(letterToUSERRepository::delete);
        }
    }

    //TODO Publications
    @GetMapping("/study/read-all-publications")
    public String studyReadAllPublicationsOfUser(Authentication authentication,
                                                 Model model,
                                                 HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int userId = user.getId();
        List<PublicationDTO> publications = findPublicationsDTOByUserId(userId);

        model.addAttribute("buttons", new Buttons(getCountPage(publications.size()), "10"));
        List<PublicationDTO> basePage = publications.stream()
                .limit(LIMIT_LIST)
                .collect(toList());

        model.addAttribute("publications", basePage);
        model.addAttribute("title", MY_ROOM);
        return "study-read-all-publications";
    }

    private List<PublicationDTO> findPublicationsDTOByUserId(int userId) {
        return publicationRepository.findAllByIdUserNot(userId).stream()
                .map(el -> new PublicationDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getFullText(),
                        info(el)))
                .sorted(Comparator.comparing(PublicationDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    @GetMapping("/study/read-all-publications/{offset}")
    public String studyReadAllPublicationsOfUserByPages(@PathVariable(value = "offset") int offset,
                                                        Authentication authentication,
                                                        Model model,
                                                        HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int userId = user.getId();
        List<PublicationDTO> dtos = findPublicationsDTOByUserId(userId);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<PublicationDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("publications", offsetList);
            model.addAttribute("title", MY_ROOM);
            return "study-read-all-publications";
        }
        List<PublicationDTO> offsetList = getOffsetListDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("publications", offsetList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-all-publications";
    }

    private List<PublicationDTO> getOffsetListDTO(List<PublicationDTO> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    private String info(PublicationUser publicationUser) {
        Optional<User> userOptional = userRepository.findOneById(publicationUser.getIdUser());
        return userOptional.map(value -> "Автор : " + value.getAddress() +
                ", Дата: " + publicationUser.getDate().toString().split("\\s")[0])
                .orElseGet(() -> "Дата: " + publicationUser.getDate().toString().split("\\s")[0]);
    }

    private String getCountPage(int size) {
        if (size < 60) {
            char[] chars = String.valueOf(size).toCharArray();
            if (chars.length < 2) {
                return "10";
            }
            return Integer.parseInt(String.valueOf(chars[0])) + 1 + "0";
        }
        return "60";
    }

    @GetMapping("/study/read-my-publications")
    public String studyReadMyPublicationsOfUser(Authentication authentication,
                                                Model model,
                                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<PublicationUser> dtos = findPublicationsByUserId(idUser);
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<PublicationUser> basePage = dtos.stream()
                .limit(10)
                .collect(toList());
        model.addAttribute("publicationUser", basePage);
        model.addAttribute("title", MY_ROOM);
        return "study-read-my-publications";
    }

    @GetMapping("/study/read-my-publications/{offset}")
    public String studyReadMyPublicationsOfUserByPages(@PathVariable(value = "offset") int offset,
                                                       Authentication authentication,
                                                       Model model,
                                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        int idUser = user.getId();
        List<PublicationUser> dtos = findPublicationsByUserId(idUser);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<PublicationUser> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("publicationUser", offsetList);
            model.addAttribute("title", MY_ROOM);
            return "study-read-my-publications";
        }
        List<PublicationUser> offsetList = getOffsetListPublicationUser(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("publicationUser", offsetList);
        model.addAttribute("title", MY_ROOM);
        return "study-read-my-publications";
    }

    private List<PublicationUser> findPublicationsByUserId(int userId) {
        return publicationRepository.findAllByIdUser(userId).stream()
                .sorted(Comparator.comparing(PublicationUser::getId).reversed())
                .collect(Collectors.toList());
    }

    private List<PublicationUser> getOffsetListPublicationUser(List<PublicationUser> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    @GetMapping("/study/publication/{idPublication}/remove")
    public String publicationRemove(@PathVariable(value = "idPublication") int idPublication) {
        Optional<PublicationUser> publicationUserOptional = publicationRepository.findById(idPublication);
        publicationUserOptional.ifPresent(publicationRepository::delete);
        return "redirect:/study/read-my-publications";
    }

    @GetMapping("/study/publication/{idPublication}/edit")
    public String publicationEdit(@PathVariable(value = "idPublication") int idPublication,
                                  Model model) {
        Optional<PublicationUser> userPublicationOptional = publicationRepository.findById(idPublication);
        if (userPublicationOptional.isPresent()) {
            PublicationUser publicationUser = userPublicationOptional.get();
            publicationUser.setFullText(convertTextToEdit(publicationUser.getFullText()));
            model.addAttribute("title", MY_ROOM);
            model.addAttribute("error", new ErrorMessage("", ""));
            model.addAttribute("publication", publicationUser);
            return "study-edit-publications";
        }
        return "redirect:/study/read-my-publications";
    }

    @PostMapping("/study/publication/{idPublication}/edit")
    public String publicationUpdate(@PathVariable(value = "idPublication") int idPublication,
                                    @RequestParam String titleText,
                                    @RequestParam String fullText,
                                    Model model) {
        ErrorMessage validator = validatorPublication(titleText, fullText);
        if (validator.getOne().length() > 0 || validator.getTwo().length() > 0) {
            return publicationWithError(titleText, fullText, model, validator);
        }
        Optional<PublicationUser> publicationUserOptional = publicationRepository.findById(idPublication);
        if (publicationUserOptional.isPresent()) {
            PublicationUser publicationUser = publicationUserOptional.get();
            publicationUser.setTitleText(titleText);
            publicationUser.setFullText(convertTextToSave(fullText));
            publicationUser.setDate(new Date());
            publicationRepository.save(publicationUser);
        }
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
                                     Authentication authentication,
                                     Model model) {
        User user = getUser(authentication);
        ErrorMessage validator = validatorPublication(titleText, fullText);
        if (validator.getOne().length() > 0 || validator.getTwo().length() > 0) {
            return publicationWithError(titleText, fullText, model, validator);
        }

        PublicationUser publicationUser = new PublicationUser();
        publicationUser.setIdUser(user.getId());
        publicationUser.setDate(new Date());
        publicationUser.setTitleText(titleText);
        publicationUser.setFullText(convertTextToSave(fullText));
        publicationRepository.save(publicationUser);
        return "redirect:/study/read-my-publications";
    }

    private ErrorMessage validatorPublication(String titleText,
                                              String fullText) {
        ErrorMessage errorMessage = new ErrorMessage("", "");
        if (titleText.length() > limit_publication_titleText) {
            errorMessage.setOne("1");
        }
        if (fullText.length() > limit_publication_fullText) {
            errorMessage.setTwo("2");
        }
        return errorMessage;
    }

    private String publicationWithError(String titleText, String fullText, Model model, ErrorMessage errors) {
        PublicationUser publicationUser = new PublicationUser();
        publicationUser.setTitleText(titleText);
        publicationUser.setFullText(fullText);
        model.addAttribute("title", MY_ROOM);
        model.addAttribute("error", errors);
        model.addAttribute("publication", publicationUser);
        return "study-edit-publications";
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

    private String convertTextToSave(String fullText) {
        String text = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text);
    }

    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private String convertTextToEdit(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }
}
