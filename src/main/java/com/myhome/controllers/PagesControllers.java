package com.myhome.controllers;


import com.myhome.models.PublicationUser;
import com.myhome.repository.PublicationRepository;
import com.myhome.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesControllers {

    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;

    public PagesControllers(PublicationRepository publicationRepository, UserRepository userRepository) {
        this.publicationRepository = publicationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        Iterable<PublicationUser> publications = publicationRepository.findAll();
        model.addAttribute("publications", publications);
        model.addAttribute("title", "MYHOME");
        return "mine-page";
    }


    @GetMapping("/user-page")
    public String userPage() {
        return "userPage";
    }

    @GetMapping("/study")
    public String studyWritePublication() {
        return "study-write-publication";
    }

    @GetMapping("/kitchen")
    public String kitchenReadCookbook() {
        return "kitchen-read-cookbook";
    }

    @GetMapping("/living")
    public String livingReadPublications(Model model) {
        Iterable<PublicationUser> publications = publicationRepository.findAll();

        model.addAttribute("publications", publications);
        model.addAttribute("title", "LIVING ROOM");
        return "living-read-publications";
    }

    @GetMapping("/registration-error")
    public String registrationError(Model model) {
        model.addAttribute("title", "Registration-error");
        return "registration-error";
    }
}
