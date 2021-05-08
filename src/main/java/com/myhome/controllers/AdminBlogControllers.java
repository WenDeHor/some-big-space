package com.myhome.controllers;


import com.myhome.models.GenreOfLiterature;
import com.myhome.models.PublicationUser;
import com.myhome.repository.PublicationRepository;
import com.myhome.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
//@RequestMapping
public class AdminBlogControllers {


//    @GetMapping("/publication")
//    public String blogMain(Model model) {
//        Iterable<PublicationUser> publication = publicationRepository.findAll();
//        model.addAttribute("title", "blog page");
//        model.addAttribute("publication", publication);
//        return "adminPage";
//    }


}
