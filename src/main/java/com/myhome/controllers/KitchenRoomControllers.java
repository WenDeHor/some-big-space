package com.myhome.controllers;

import com.myhome.models.CookBook;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import com.myhome.repository.CookBookRepository;
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
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

@Controller
public class KitchenRoomControllers {
    private final UserRepository userRepository;
    private final CookBookRepository cookBookRepository;

    public KitchenRoomControllers(UserRepository userRepository, CookBookRepository cookBookRepository) {
        this.userRepository = userRepository;
        this.cookBookRepository = cookBookRepository;
    }


    @GetMapping("/kitchen/write-prescription")
    public String kitchenWritePrescription(Authentication authentication, Model model) {
//        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
//        String userEmail = details.getUsername();
//        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
//        String address = oneByEmail.get().getAddress();

//        model.addAttribute("address", address);
        model.addAttribute("title", "Prescription");
        return "kitchen-write-prescription";
    }

    @Transactional
    @GetMapping("/kitchen/read-cookbook")
    public String kitchenReadCookBooks(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userName = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
        String email = oneByEmail.get().getEmail();
        List<CookBook> cookBooks = cookBookRepository.findAllByEmail(email);

        //TODO REVERS
        cookBooks.sort(Comparator.comparing(CookBook::getId).reversed());
        model.addAttribute("cookBooks", cookBooks);
        model.addAttribute("title", "Cook Book");
        return "kitchen-read-cookbook";
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id,
                   HttpServletResponse response,
                   Optional<CookBook> cookBook) throws ServletException, IOException {

        cookBook = cookBookRepository.findById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(cookBook.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/kitchen/read-cookbook/{id}/remove")
    public String cookBookRemove(@PathVariable(value = "id") Long id, Model model) {
        CookBook cookBook = cookBookRepository.findById(id).orElseThrow(null);
        cookBookRepository.delete(cookBook);
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!cookBookRepository.existsById(id)) {
            return "redirect:/kitchen/read-cookbook";
        }
        Optional<CookBook> post = cookBookRepository.findById(id);
        List<CookBook> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("cookBook", res);
        model.addAttribute("cookBooks", res);
        return "kitchen-edit-cookbook";
    }

    @PostMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookUpdate(@PathVariable(value = "id") Long id,
                                 MultipartFile file,
                                 @RequestParam String titleText,
                                 @RequestParam String fullText,
                                 Model model) throws IOException {
        CookBook cookBook = cookBookRepository.findById(id).orElseThrow(null);
        cookBook.setTitleText(titleText);
//        String url= "https://www.youtube.com/embed/"+video+"?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent";
//        https://www.youtube.com/embed/vguSoDvurss?version=3&rel=1&fs=1&autohide=2&showsearch=0&showinfo=1&iv_load_policy=1&wmode=transparent
        cookBook.setFullText(fullText);
        Date date = new Date();
        cookBook.setLocalDate(date);
        cookBook.setName(file.getOriginalFilename());
        cookBook.setType(file.getContentType());
        if(file.getContentType().equals("application/octet-stream")){
            Optional<CookBook> byId = cookBookRepository.findById(id);
            byte[] image = byId.get().getImage();
            cookBook.setImage(image);
        } else {
            cookBook.setImage(file.getBytes());
        }
        cookBookRepository.save(cookBook);
        return "redirect:/kitchen/read-cookbook";
    }


    @PostMapping("/kitchen/write-prescription")
    public String kitchenWritePrescriptionAdd(Authentication authentication,
                                              Model model,
                                              MultipartFile file,
                                              @RequestParam("titleText") String titleText,
                                              @RequestParam("fullText") String fullText) throws IOException {

        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        String email = oneByEmail.get().getEmail();
        Date createDate = new Date();
        CookBook cookBook = new CookBook();
        cookBook.setLocalDate(createDate);
        cookBook.setTitleText(titleText);
        cookBook.setFullText(fullText);
        cookBook.setEmail(email);
        cookBook.setName(file.getOriginalFilename());
        cookBook.setType(file.getContentType());
        cookBook.setImage(file.getBytes());

        cookBookRepository.save(cookBook);
        System.out.println("good save");
        return "redirect:/kitchen/read-cookbook";
    }

}
