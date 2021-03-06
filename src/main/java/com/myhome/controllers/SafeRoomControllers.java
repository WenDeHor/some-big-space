package com.myhome.controllers;

import com.myhome.models.Diary;
import com.myhome.models.Reference;
import com.myhome.models.User;
import com.myhome.repository.DiaryRepository;
import com.myhome.repository.ReferenceRepository;
import com.myhome.repository.UserRepository;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SafeRoomControllers {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final ReferenceRepository referenceRepository;

    public SafeRoomControllers(UserRepository userRepository, DiaryRepository diaryRepository, ReferenceRepository referenceRepository) {
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.referenceRepository = referenceRepository;
    }

    @Transactional
    @GetMapping("/safe/read-save-diary")
    public String safeReadDiary(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String address = oneByEmail.get().getAddress();
        List<Diary> diaryList = diaryRepository.findAllByAddress(address);

        //TODO REVERS
        diaryList.sort(Comparator.comparing(Diary::getId).reversed());
        model.addAttribute("diaryList", diaryList);
        model.addAttribute("title", "Diary");
        return "safe-read-diary";
    }

    @GetMapping("/image/display/diary/{id}")
    @ResponseBody
    void showImageDiary(@PathVariable("id") Long id,
                        HttpServletResponse response,
                        Optional<Diary> diary) throws ServletException, IOException {

        diary = diaryRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(diary.get().getImage());
        response.getOutputStream().close();
    }

    @PostMapping("/safe/read-save-diary")
    public String safeSaveDiary(Authentication authentication,
                                Model model,
                                MultipartFile file,
                                @RequestParam("titleText") String titleText,
                                @RequestParam("fullText") String fullText) throws IOException {

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();
        Date date = new Date();

        Diary diary = new Diary();
        diary.setLocalDate(date);
        diary.setTitleText(titleText);
        diary.setFullText(convertTextWithFormatToDiarySaveAndEdit(fullText));
        diary.setAddress(address);
        diary.setName(file.getOriginalFilename());
        diary.setType(file.getContentType());
        diary.setImage(file.getBytes());
        diaryRepository.save(diary);

        System.out.println(diary.toString());
        System.out.println("good save");
        return "redirect:/safe/read-save-diary";
    }

    private String convertTextWithFormatToDiarySaveAndEdit(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }

    @GetMapping("/safe/edit-diary/{id}/remove")
    public String diaryRemove(@PathVariable(value = "id") Long id, Model model) {
        Diary diary = diaryRepository.findById(id).orElseThrow(null);
        diaryRepository.delete(diary);
        return "redirect:/safe/edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary")
    public String safeEditDiary(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String address = oneByEmail.get().getAddress();
        List<Diary> diaryList = diaryRepository.findAllByAddress(address);

        //TODO REVERS
        diaryList.sort(Comparator.comparing(Diary::getId).reversed());
        model.addAttribute("diaryList", diaryList);
        model.addAttribute("title", "Diary");
        return "safe-edit-diary";
    }

    @Transactional
    @GetMapping("/safe/edit-diary/{id}/edit")
    public String diaryEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!diaryRepository.existsById(id)) {
            return "redirect:/safe/edit-diary";
        }
        Optional<Diary> diary = diaryRepository.findById(id);
        List<Diary> diaryList = new ArrayList<>();
        diary.ifPresent(diaryList::add);
//        model.addAttribute("diaryList", diaryList);
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
    public String diaryUpdate(@PathVariable(value = "id") Long id,
                              MultipartFile file,
                              @RequestParam String titleText,
                              @RequestParam String fullText,
                              Model model) throws IOException {
        Diary diary = diaryRepository.findById(id).orElseThrow(null);
        diary.setTitleText(titleText);
        diary.setFullText(convertTextWithFormatToDiarySaveAndEdit(fullText));
        diary.setLocalDate(diary.getLocalDate());
        diary.setName(file.getOriginalFilename());
        diary.setType(file.getContentType());
        if (file.getContentType().equals("application/octet-stream")) {
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
    public String safeReadAddRemoveReference(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String address = oneByEmail.get().getAddress();
        List<Reference> referenceList = referenceRepository.findAllByAddress(address);

        //TODO REVERS
        referenceList.sort(Comparator.comparing(Reference::getId).reversed());
        model.addAttribute("referenceList", referenceList);
        model.addAttribute("title", "referenceList");
        return "safe-read-save-edit-reference";
    }

    @PostMapping("/safe/read-save-edit-reference")
    public String safeSaveReference(Authentication authentication,
                                    Model model,
                                    MultipartFile file,
                                    @RequestParam("titleText") String titleText,
                                    @RequestParam("url") String url) throws IOException {

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String address = oneByEmail.get().getAddress();

        Reference reference = new Reference();
        reference.setAddress(address);
        reference.setUrl(url);
        reference.setTitleText(titleText);
//        if (file.getContentType().equals("application/octet-stream")) {
//            Optional<Diary> byId = diaryRepository.findById(id);
//            byte[] image = byId.get().getImage();
//            diary.setImage(image);
//        } else {
//            diary.setImage(file.getBytes());
//        }
        reference.setName(file.getOriginalFilename());
        reference.setType(file.getContentType());
        reference.setImage(file.getBytes());
        referenceRepository.save(reference);

        System.out.println(reference.toString());
        return "redirect:/safe/read-save-edit-reference";
    }

    @GetMapping("/image/display/reference/{id}")
    @ResponseBody
    void showImageReference(@PathVariable("id") Long id,
                            HttpServletResponse response,
                            Optional<Reference> reference) throws ServletException, IOException {

        reference = referenceRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(reference.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/safe/read-save-edit-reference/{id}/remove")
    public String referenceRemove(@PathVariable(value = "id") Long id, Model model) {
        Reference reference = referenceRepository.findById(id).orElseThrow(null);
        referenceRepository.delete(reference);
        return "redirect:/safe/read-save-edit-reference";
    }

    //TODO REGEX
    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

}
