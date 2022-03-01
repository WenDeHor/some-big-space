package com.myhome.controllers;


import com.myhome.forms.UserForm;
import com.myhome.models.User;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.RegistrationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class SecurityController {
    private final RegistrationService service;
    private final UserRepository userRepository;

    public SecurityController(UserRepository userRepository, RegistrationService service) {
        this.userRepository = userRepository;
        this.service = service;
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("title", "registration page");
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(UserForm userForm, Model model, HttpServletRequest request) {
        Optional<User> userPresent = userRepository.findOneByEmail(userForm.getEmail());
        if (request.getParameterMap().containsKey("error") || userPresent.isPresent()) {
            model.addAttribute("error", true);
            model.addAttribute("title", "registration page");
            return "registration-error";
        }
        service.signUp(userForm);
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String getLoginPage(Authentication authentication, ModelMap model, HttpServletRequest request) {
        if (authentication != null) {
            return "redirect:/success";
        } else if (request.getParameterMap().containsKey("error")) {
            model.addAttribute("error", true);
        }
        model.addAttribute("title", "LOGIN");
        return "login";
    }


    @GetMapping("/success")
    public String getSuccessPage(Authentication authentication, Model model) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userRole = details.getAuthorities().toString();
        if (userRole.equals("[ADMIN]") || userRole.equals("ADMIN")) {
            return "redirect:/admin-page";
        }
        if (userRole.equals("[USER]") || userRole.equals("USER")) {
            return "redirect:/user-page";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/logout")
    public String postLogout(Model model) {
        return "redirect:/";
    }
}
