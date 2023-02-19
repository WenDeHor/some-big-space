package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.ConvertFile;
import com.myhome.forms.DiaryDTO;
import com.myhome.forms.ReferenceDTO;
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
import org.springframework.web.bind.annotation.*;
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
        List<DiaryDTO> diaryList = diaryRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new DiaryDTO(
                        el.getTitleText(),
                        el.getFullText(),
                        converterImage(el.getImage())))
                .sorted(Comparator.comparing(DiaryDTO::getId).reversed())
                .collect(toList());
        model.addAttribute("diaryList", diaryList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-diary";
    }

    @PostMapping("/safe/read-save-diary")
    public String safeSaveDiary(Authentication authentication,
                                MultipartFile file,
                                @RequestParam("titleText") String titleText,
                                @RequestParam("fullText") String fullText) throws IOException {
        User user = getUser(authentication);
        Diary diary = new Diary();
        diary.setDate(new Date());
        diary.setTitleText(titleText);
        diary.setFullText(convertTextWithFormatToDiarySaveAndEdit(fullText));
        diary.setIdUser(user.getId());
        ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
        diary.setImage(convert.img);
        diaryRepository.save(diary);
        compressorImgToJpg.deleteImage(convert.nameStart);
        compressorImgToJpg.deleteImage(convert.nameEnd);
        return "redirect:/safe/read-save-diary";
    }

    private String convertTextWithFormatToDiarySaveAndEdit(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @DeleteMapping("/safe/edit-diary/{id}/remove")
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
        List<DiaryDTO> diaryList = diaryRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new DiaryDTO(
                        el.getId(),
                        el.getTitleText(),
                        el.getFullText(),
                        converterImage(el.getImage())))
                .sorted(Comparator.comparing(DiaryDTO::getId).reversed())
                .collect(toList());
        model.addAttribute("diaryList", diaryList);
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
                    convertText(diary.getTitleText()),
                    convertText(diary.getFullText()),
                    converterImage(diary.getImage()));
            model.addAttribute("title", SAFE_ROOM);
            model.addAttribute("diaryList", diaryDTO);
            return "safe-edit-diary-one-element";
        }
        return "redirect:/safe/edit-diary";
    }

    private String convertText(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }

    @Transactional
    @PostMapping("/safe/edit-diary/{id}/edit")
    public String diaryUpdate(@PathVariable(value = "id") int id,
                              MultipartFile file,
                              Authentication authentication,
                              @RequestParam String titleText,
                              @RequestParam String fullText) throws IOException {
        User user = getUser(authentication);
        Optional<Diary> diaryOptional = diaryRepository.findByIdUserAndId(user.getId(), id);
        if (diaryOptional.isPresent()) {
            Diary diary = diaryOptional.get();
            diary.setTitleText(titleText);
            diary.setFullText(convertTextWithFormatToDiarySaveAndEdit(fullText));
            diary.setDate(diary.getDate());
            if (!Objects.equals(file.getContentType(), "application/octet-stream")) {     //new Photo
                ConvertFile convert = compressorImgToJpg.convert(file, String.valueOf(diary.getIdUser()));
                diary.setImage(convert.img);
                compressorImgToJpg.deleteImage(convert.nameStart);
                compressorImgToJpg.deleteImage(convert.nameEnd);
            }
            diaryRepository.save(diary);
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
                                    @RequestParam("titleText") String titleText,
                                    @RequestParam("url") String url) throws IOException {
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

    @GetMapping("/safe/read-save-edit-reference/{id}/remove")
    public String referenceRemove(@PathVariable(value = "id") int id,
                                  Authentication authentication) {
        User user = getUser(authentication);
        Optional<Reference> referenceOptional = referenceRepository.findByIdUserAndId(user.getId(), id);
        referenceOptional.ifPresent(referenceRepository::delete);
        return "redirect:/safe/read-save-edit-reference";
    }

    //TODO REGEX
    private String REGEX(String patternRegex,
                         String replace,
                         String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        return userRepository.findOneByEmail(userName).get();
    }
}
