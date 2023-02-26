package com.myhome.controllers;


import com.myhome.forms.ErrorMessage;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SecurityController {
    private final RegistrationService service;
    private final UserRepository userRepository;

    private final String MY_HOME = "Мій дім";

    public SecurityController(UserRepository userRepository, RegistrationService service) {
        this.userRepository = userRepository;
        this.service = service;
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model) {
        model.addAttribute("title", MY_HOME);
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(UserForm userForm, Model model, HttpServletRequest request) {
        Optional<User> userPresent = userRepository.findOneByEmail(userForm.getEmail());
        String login = userForm.getLogin().trim().replaceAll("\\s+", "00");

        ErrorMessage validator = validatorRegistration(userPresent, userForm, login);
        if (validator.getOne().length() > 0
                || validator.getTwo().length() > 0
                || validator.getThree().length() > 0
                || validator.getFour().length() > 0
                || validator.getFive().length() > 0) {
            return registrationWithError(userForm, validator, model);
        }
        model.addAttribute("title", MY_HOME);
        service.signUp(userForm);
        return "redirect:/login";
    }

    private String registrationWithError(UserForm userForm,
                                         ErrorMessage errors,
                                         Model model) {
        User userWithError = new User();
        userWithError.setLogin(userForm.getLogin());
        userWithError.setEmail(userForm.getEmail());
        userWithError.setSettlement(userForm.getSettlement());
        model.addAttribute("title", MY_HOME);
        model.addAttribute("error", errors);
        model.addAttribute("userWithError", userWithError);
        return "registration-with-error";
    }

    private ErrorMessage validatorRegistration(Optional<User> userPresent,
                                               UserForm userForm,
                                               String login) {
        ErrorMessage errorMessage = new ErrorMessage("", "", "", "", "");
        if (userPresent.isPresent()) {
            errorMessage.setOne("1");
        }
        if (!onlyLatinAlphabet(login)) {
            errorMessage.setTwo("2");
        }
        if (userForm.getLogin().length() < 3) {
            errorMessage.setThree("3");
        }
        if (userForm.getPassword().length() < 4) {
            errorMessage.setFour("4");
        }
        if (!emailValidator(userForm.getEmail())) {
            errorMessage.setFive("5");
        }

        return errorMessage;
    }

    private boolean onlyLatinAlphabet(String userLogin) {
        return userLogin.matches("^[a-zA-Z0-9]+$");
    }

    private boolean emailValidator(String email) {
        String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    @GetMapping("/login")
    public String getLoginPage(Authentication authentication, ModelMap model, HttpServletRequest request) {
        if (authentication != null) {
            return "redirect:/success";
        } else if (request.getParameterMap().containsKey("error")) {
            model.addAttribute("error", true);
        }
        model.addAttribute("title", MY_HOME);
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
