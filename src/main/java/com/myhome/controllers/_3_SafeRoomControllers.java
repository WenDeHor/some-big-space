package com.myhome.controllers;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class _3_SafeRoomControllers {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ReferenceRepository referenceRepository;
    private final MetricsService metricsService;
    private final String SAFE_ROOM = "Робоча кімната";

    public _3_SafeRoomControllers(UserRepository userRepository, DiaryRepository diaryRepository, ReferenceRepository referenceRepository, MetricsService metricsService) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.referenceRepository = referenceRepository;
        this.metricsService = metricsService;
    }

    @Transactional
    @GetMapping("/safe/read-save-diary")
    public String safeReadDiary(Authentication authentication,
                                Model model,
                                HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<Diary> diaryList = diaryRepository.findAllByIdUser(user.getId());
        diaryList.sort(Comparator.comparing(Diary::getId).reversed());
        model.addAttribute("diaryList", diaryList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-diary";
    }

    @GetMapping("/image/display/diary/{id}")
    @ResponseBody
    void showImageDiary(@PathVariable("id") int id,
                        HttpServletResponse response,
                        Optional<Diary> diary) throws ServletException, IOException {

        diary = diaryRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(diary.get().getImage());
        response.getOutputStream().close();
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
        diary.setImage(file.getBytes());
        diaryRepository.save(diary);
        return "redirect:/safe/read-save-diary";
    }

    private String convertTextWithFormatToDiarySaveAndEdit(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @DeleteMapping("/safe/edit-diary/{id}/remove")
    public String diaryRemove(@PathVariable(value = "id") int id) {
        Diary diary = diaryRepository.findById(id).orElseThrow(null);
        diaryRepository.delete(diary);
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary")
    public String safeEditDiary(Authentication authentication,
                                Model model) {
        User user = getUser(authentication);
        List<Diary> diaryList = diaryRepository.findAllByIdUser(user.getId());
        diaryList.sort(Comparator.comparing(Diary::getId).reversed());
        model.addAttribute("diaryList", diaryList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary/{id}/edit")
    public String diaryEdit(@PathVariable(value = "id") int id,
                            Model model) {
        if (!diaryRepository.existsById(id)) {
            return "redirect:/safe/edit-diary";
        }
        Optional<Diary> diary = diaryRepository.findById(id);
        List<Diary> diaryList = new ArrayList<>();
        diary.ifPresent(diaryList::add);
        model.addAttribute("title", SAFE_ROOM);
        model.addAttribute("diaryList", convertTextWithFormatEditDiary(diaryList));
        return "safe-edit-diary-one-element";
    }

    private List<Diary> convertTextWithFormatEditDiary(List<Diary> publicationPostAdminList) {
        List<Diary> list = new ArrayList<>();
        for (Diary publicationPostAdmin : publicationPostAdminList) {
            String fullText = publicationPostAdmin.getFullText();
            String trim1 = fullText.replace("&#160&#160 ", "");
            String trim2 = trim1.replace("<br>", "");
            publicationPostAdmin.setFullText(trim2);
            list.add(publicationPostAdmin);
        }
        return list;
    }

    @PostMapping("/safe/edit-diary/{id}/edit")
    public String diaryUpdate(@PathVariable(value = "id") int id,
                              MultipartFile file,
                              @RequestParam String titleText,
                              @RequestParam String fullText) throws IOException {
        Diary diary = diaryRepository.findById(id).orElseThrow(null);
        diary.setTitleText(titleText);
        diary.setFullText(convertTextWithFormatToDiarySaveAndEdit(fullText));
        diary.setDate(diary.getDate());
        if (Objects.equals(file.getContentType(), "application/octet-stream")) {
            Optional<Diary> byId = diaryRepository.findById(id);
            byte[] image = byId.get().getImage();
            diary.setImage(image);
        } else {
            diary.setImage(file.getBytes());
        }
        diaryRepository.save(diary);
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @GetMapping("/safe/read-save-edit-reference")
    public String safeReadAddRemoveReference(Authentication authentication,
                                             Model model,
                                             HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<Reference> referenceList = referenceRepository.findAllByIdUser(user.getId());
        referenceList.sort(Comparator.comparing(Reference::getId).reversed());
        model.addAttribute("referenceList", referenceList);
        model.addAttribute("title", SAFE_ROOM);
        return "safe-read-save-edit-reference";
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
        reference.setImage(file.getBytes());
        referenceRepository.save(reference);
        return "redirect:/safe/read-save-edit-reference";
    }

    @GetMapping("/image/display/reference/{id}")
    @ResponseBody
    void showImageReference(@PathVariable("id") int id,
                            HttpServletResponse response,
                            Optional<Reference> reference) throws ServletException, IOException {
        reference = referenceRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(reference.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/safe/read-save-edit-reference/{id}/remove")
    public String referenceRemove(@PathVariable(value = "id") int id) {
        Reference reference = referenceRepository.findById(id).orElseThrow(null);
        referenceRepository.delete(reference);
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
