package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.*;
import com.myhome.models.Diary;
import com.myhome.models.Reference;
import com.myhome.models.User;
import com.myhome.repository.DiaryRepository;
import com.myhome.repository.ReferenceRepository;
import com.myhome.repository.UserRepository;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

@Controller
public class _3_SafeRoomControllers {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ReferenceRepository referenceRepository;
    private final MetricsService metricsService;
    private final CompressorImgToJpg compressorImgToJpg;
    private final String SAFE_ROOM = "Робоча кімната";
    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_url = 2000; //chars
    private int limit_titleText_reference = 50; //chars
    private int limit_titleText = 1000; //chars
    private int limit_fullText = 3000; //chars

    public _3_SafeRoomControllers(UserRepository userRepository, DiaryRepository diaryRepository, ReferenceRepository referenceRepository, MetricsService metricsService, CompressorImgToJpg compressorImgToJpg) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.referenceRepository = referenceRepository;
        this.metricsService = metricsService;
        this.compressorImgToJpg = compressorImgToJpg;
    }

    @Transactional
    @GetMapping("/safe/read-save-diary")
    public String safeReadDiary(Authentication authentication,
                                Model model,
                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<DiaryDTO> dtos = findDiaryDTOByUserId(user.getId());
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<DiaryDTO> basePage = dtos.stream()
                .limit(10)
                .collect(toList());
        model.addAttribute("diaryList", basePage);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-diary";
    }

    @Transactional
    @GetMapping("/safe/read-save-diary/{offset}")
    public String safeReadDiaryByPages(@PathVariable(value = "offset") int offset,
                                       Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<DiaryDTO> dtos = findDiaryDTOByUserId(user.getId());
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<DiaryDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("diaryList", offsetList);
            model.addAttribute("title", SAFE_ROOM);
            return "safe-read-diary";
        }
        List<DiaryDTO> offsetList = getOffsetList(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("diaryList", offsetList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-diary";
    }

    private List<DiaryDTO> findDiaryDTOByUserId(int userId) {
        return diaryRepository.findAllByIdUser(userId).stream()
                .map(el -> new DiaryDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getFullText(),
                        converterImage(el.getImage())))
                .sorted(Comparator.comparing(DiaryDTO::getId).reversed())
                .collect(toList());
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

    private List<DiaryDTO> getOffsetList(List<DiaryDTO> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    @PostMapping("/safe/read-save-diary")
    public String safeSaveDiary(Authentication authentication,
                                MultipartFile file,
                                @RequestParam("titleText") String titleText,
                                @RequestParam("fullText") String fullText,
                                Model model) throws IOException {
        if (checkSaveDiary(file, titleText, fullText)) {
            return getErrorPageSaveDiary(file, titleText, fullText, model);
        } else {
            User user = getUser(authentication);
            Diary diary = new Diary();
            diary.setDate(new Date());
            diary.setTitleText(titleText);
            diary.setFullText(convertTextToSave(fullText));
            diary.setIdUser(user.getId());
            ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
            diary.setImage(convert.img);
            diaryRepository.save(diary);
            compressorImgToJpg.deleteImage(convert.nameStart);
            compressorImgToJpg.deleteImage(convert.nameEnd);
            return "redirect:/safe/read-save-diary";
        }
    }

    private boolean checkSaveDiary(MultipartFile file,
                                   String titleText,
                                   String fullText) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_titleText
                || fullText.toCharArray().length > limit_fullText;
    }

    private String getErrorPageSaveDiary(MultipartFile file,
                                         String titleText,
                                         String fullText,
                                         Model model) throws IOException {
        String stile = "crimson";
        String originalFilename = file.getOriginalFilename();
        int fileSize = file.getBytes().length / constant;
        int titleTextSize = titleText.toCharArray().length;
        int fullTextSize = fullText.toCharArray().length;
        if (file.getBytes().length / constant > limit_photo) {
            model.addAttribute("stile", stile);
        }

        model.addAttribute("originalFilename", originalFilename);
        model.addAttribute("fileSize", fileSize);

        model.addAttribute("titleText", titleText);
        model.addAttribute("titleTextSize", titleTextSize);

        model.addAttribute("full", fullText);
        model.addAttribute("fullTextSize", fullTextSize);

        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-diary-error";
    }

    @Transactional
    @GetMapping("/safe/edit-diary/{id}/remove")
    public String diaryRemove(@PathVariable(value = "id") int id,
                              Authentication authentication) {
        User user = getUser(authentication);
        Optional<Diary> diaryOptional = diaryRepository.findByIdUserAndId(user.getId(), id);
        diaryOptional.ifPresent(diaryRepository::delete);
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary")
    public String safeEditDiary(Authentication authentication,
                                Model model) {
        User user = getUser(authentication);
        List<DiaryDTO> diaryList = findDiaryDTOByUserId(user.getId());
        model.addAttribute("buttons", new Buttons(getCountPage(diaryList.size()), "10"));
        List<DiaryDTO> basePage = diaryList.stream()
                .limit(10)
                .collect(toList());
        model.addAttribute("diaryList", basePage);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary/{offset}")
    public String safeEditDiaryByPages(@PathVariable(value = "offset") int offset,
                                       Authentication authentication,
                                       Model model) {
        User user = getUser(authentication);
        List<DiaryDTO> diaryList = findDiaryDTOByUserId(user.getId());
        String countPage = getCountPage(diaryList.size());
        if (offset == 60) {
            List<DiaryDTO> offsetList = diaryList.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("diaryList", offsetList);
            model.addAttribute("title", SAFE_ROOM);
            return "safe-edit-diary";
        }
        List<DiaryDTO> offsetList = getOffsetList(diaryList, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("diaryList", offsetList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-edit-diary";
    }


    private String converterImage(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @Transactional
    @GetMapping("/safe/edit-diary/{id}/edit")
    public String diaryEdit(@PathVariable(value = "id") int id,
                            Authentication authentication,
                            Model model) {
        User user = getUser(authentication);
        Optional<Diary> diaryOptional = diaryRepository.findByIdUserAndId(user.getId(), id);
        if (diaryOptional.isPresent()) {
            Diary diary = diaryOptional.get();
            DiaryDTO diaryDTO = new DiaryDTO(
                    diary.getId(),
                    diary.getTitleText(),
                    convertTextToEdit(diary.getFullText()),
                    converterImage(diary.getImage()));
            model.addAttribute("title", SAFE_ROOM);
            model.addAttribute("diaryList", diaryDTO);
            return "safe-edit-diary-one-element";
        }
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @PostMapping("/safe/edit-diary/{id}/edit")
    public String diaryUpdate(@PathVariable(value = "id") int id,
                              MultipartFile file,
                              Authentication authentication,
                              @RequestParam String titleText,
                              @RequestParam String fullText,
                              Model model) throws IOException {
        if (checkSaveDiary(file, titleText, fullText)) {
            return getErrorPageSaveDiary(file, titleText, fullText, model);
        } else {
            User user = getUser(authentication);
            Optional<Diary> diaryOptional = diaryRepository.findByIdUserAndId(user.getId(), id);
            if (diaryOptional.isPresent()) {
                Diary diary = diaryOptional.get();
                diary.setTitleText(titleText);
                diary.setFullText(convertTextToSave(fullText));
                diary.setDate(diary.getDate());
                if (!Objects.equals(file.getContentType(), "application/octet-stream")) {     //new Photo
                    ConvertFile convert = compressorImgToJpg.convert(file, String.valueOf(diary.getIdUser()));
                    diary.setImage(convert.img);
                    compressorImgToJpg.deleteImage(convert.nameStart);
                    compressorImgToJpg.deleteImage(convert.nameEnd);
                }
                diaryRepository.save(diary);
            }
        }
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @GetMapping("/safe/read-save-edit-reference")
    public String safeReadAddRemoveReference(Authentication authentication,
                                             Model model,
                                             HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<ReferenceDTO> referenceList = referenceRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new ReferenceDTO(
                        el.getId(),
                        el.getUrl(),
                        el.getIdUser(),
                        el.getTitleText(),
                        converter(el.getImage())))
                .sorted(Comparator.comparing(ReferenceDTO::getId).reversed())
                .collect(toList());

        model.addAttribute("referenceList", referenceList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-save-edit-reference";
    }

    private String converter(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @PostMapping("/safe/read-save-edit-reference")
    public String safeSaveReference(Authentication authentication,
                                    MultipartFile file,
                                    Model model,
                                    @RequestParam("titleText") String titleText,
                                    @RequestParam("url") String url) throws IOException {
        if (validatorReference(titleText, url, file)) {
            return saveReferenceWithError(file, titleText, url, model);
        }
        User user = getUser(authentication);
        Reference reference = new Reference();
        reference.setIdUser(user.getId());
        reference.setUrl(url);
        reference.setTitleText(titleText);
        ConvertFile convert = compressorImgToJpg.convert(file, String.valueOf(user.getId()));
        reference.setImage(convert.img);
        compressorImgToJpg.deleteImage(convert.nameStart);
        compressorImgToJpg.deleteImage(convert.nameEnd);
        referenceRepository.save(reference);
        return "redirect:/safe/read-save-edit-reference";
    }

    private boolean validatorReference(String titleText, String url, MultipartFile file) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_titleText_reference
                || url.toCharArray().length > limit_url;
    }

    private String saveReferenceWithError(MultipartFile file,
                                          String titleText,
                                          String url,
                                          Model model) throws IOException {
        String stile = "crimson";
        String originalFilename = file.getOriginalFilename();
        int fileSize = file.getBytes().length / constant;
        int titleTextSize = titleText.toCharArray().length;
        int fullTextSize = url.toCharArray().length;
        if (file.getBytes().length / constant > limit_photo) {
            model.addAttribute("stile", stile);
        }

        model.addAttribute("originalFilename", originalFilename);
        model.addAttribute("fileSize", fileSize);

        model.addAttribute("titleText", titleText);
        model.addAttribute("titleTextSize", titleTextSize);

        model.addAttribute("url", url);
        model.addAttribute("urlTextSize", fullTextSize);

        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-save-edit-reference-with-error";
    }

    @Transactional
    @GetMapping("/safe/read-save-edit-reference/{id}/remove")
    public String referenceRemove(@PathVariable(value = "id") int id,
                                  Authentication authentication) {
        User user = getUser(authentication);
        Optional<Reference> referenceOptional = referenceRepository.findByIdUserAndId(user.getId(), id);
        referenceOptional.ifPresent(referenceRepository::delete);
        return "redirect:/safe/read-save-edit-reference";
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
