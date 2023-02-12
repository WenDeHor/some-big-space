package com.myhome.controllers;

import com.myhome.models.CookBook;
import com.myhome.models.Menu;
import com.myhome.models.ShopMeals;
import com.myhome.models.User;
import com.myhome.repository.CookBookRepository;
import com.myhome.repository.MenuRepository;
import com.myhome.repository.ShopMealsRepository;
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
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class _5_KitchenRoomControllers {
    private final ShopMealsRepository shopMealsRepository;
    private final UserRepository userRepository;
    private final CookBookRepository cookBookRepository;
    private final MenuRepository menuRepository;
    private final MetricsService metricsService;
    private final String KITCHEN_ROOM = "Кухня";

    public _5_KitchenRoomControllers(ShopMealsRepository shopMealsRepository, UserRepository userRepository, CookBookRepository cookBookRepository, MenuRepository menuRepository, MetricsService metricsService) {
        this.shopMealsRepository = shopMealsRepository;
        this.userRepository = userRepository;
        this.cookBookRepository = cookBookRepository;
        this.menuRepository = menuRepository;
        this.metricsService = metricsService;
    }

    @GetMapping("/kitchen/write-prescription")
    public String kitchenWritePrescription(Model model,
                                           HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        model.addAttribute("title", "Prescription");
        return "kitchen-write-prescription";
    }

    @Transactional
    @GetMapping("/kitchen/read-cookbook")
    public String kitchenReadCookBooks(Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String userAddress = findUserAddress(authentication);
        List<CookBook> cookBooks = cookBookRepository.findAllByAddress(userAddress);
        cookBooks.sort(Comparator.comparing(CookBook::getId).reversed());
        model.addAttribute("cookBooks", cookBooks);
        model.addAttribute("title", KITCHEN_ROOM);
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
    public String cookBookRemove(@PathVariable(value = "id") Long id) {
        CookBook cookBook = cookBookRepository.findById(id).orElseThrow(null);
        cookBookRepository.delete(cookBook);
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookEdit(@PathVariable(value = "id") Long id,
                               Model model) {
        if (!cookBookRepository.existsById(id)) {
            return "redirect:/kitchen/read-cookbook";
        }
        Optional<CookBook> post = cookBookRepository.findById(id);
        List<CookBook> res = new ArrayList<>();
        post.ifPresent(res::add);
        model.addAttribute("cookBook", convertTextWithFormatCookBookEdit(res));
        model.addAttribute("cookBooks", convertTextWithFormatCookBookEdit(res));
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-edit-cookbook";
    }

    private List<CookBook> convertTextWithFormatCookBookEdit(List<CookBook> publicationPostAdminList) {
        List<CookBook> list = new ArrayList<>();
        for (CookBook publicationPostAdmin : publicationPostAdminList) {
            String fullText = publicationPostAdmin.getFullText();
            String trim1 = fullText.replace("&#160&#160 ", "");
            String trim2 = trim1.replace("<br>", "");
            publicationPostAdmin.setFullText(trim2);
            list.add(publicationPostAdmin);
        }
        return list;
    }

    @PostMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookUpdate(@PathVariable(value = "id") Long id,
                                 MultipartFile file,
                                 @RequestParam String titleText,
                                 @RequestParam String fullText) throws IOException {
        CookBook cookBook = cookBookRepository.findById(id).orElseThrow(null);
        cookBook.setTitleText(titleText);
        cookBook.setFullText(convertTextWithFormatToCookBookUpdateAndSave(fullText));
        Date date = new Date();
        cookBook.setDate(date);
        cookBook.setName(file.getOriginalFilename());
        cookBook.setType(file.getContentType());
        if (file.getContentType().equals("application/octet-stream")) {
            Optional<CookBook> byId = cookBookRepository.findById(id);
            byte[] image = byId.get().getImage();
            cookBook.setImage(image);
        } else {
            cookBook.setImage(file.getBytes());
        }
        cookBookRepository.save(cookBook);
        return "redirect:/kitchen/read-cookbook";
    }

    private String convertTextWithFormatToCookBookUpdateAndSave(String fullText) {
        String text1 = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text1);
    }


    @PostMapping("/kitchen/write-prescription")
    public String kitchenWritePrescriptionAdd(Authentication authentication,
                                              MultipartFile file,
                                              @RequestParam("titleText") String titleText,
                                              @RequestParam("fullText") String fullText) throws IOException {
        String userAddress = findUserAddress(authentication);
        Date createDate = new Date();
        CookBook cookBook = new CookBook();
        cookBook.setDate(createDate);
        cookBook.setTitleText(titleText);
        cookBook.setFullText(convertTextWithFormatToCookBookUpdateAndSave(fullText));
        cookBook.setAddress(userAddress);
        cookBook.setName(file.getOriginalFilename());
        cookBook.setType(file.getContentType());
        cookBook.setImage(file.getBytes());

        cookBookRepository.save(cookBook);
        return "redirect:/kitchen/read-cookbook";
    }

    //TODO MENU
    @GetMapping("/kitchen/write-menu")
    public String kitchenMenu(Authentication authentication,
                              Model model,
                              HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String userAddress = findUserAddress(authentication);
        List<Menu> allMenuByAddress = menuRepository.findAllByAddress(userAddress);
        allMenuByAddress.sort(Comparator.comparing(Menu::getId));
        model.addAttribute("allMenuByAddress", allMenuByAddress);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-menu";
    }

    @PostMapping("/kitchen/write-menu")
    public String kitchenWriteMenu(Authentication authentication,
                                   @RequestParam("date") String date,
                                   @RequestParam("breakfast") String breakfast,
                                   @RequestParam("dinner") String dinner,
                                   @RequestParam("supper") String supper) {
        String userAddress = findUserAddress(authentication);
        Menu menu = new Menu();
        menu.setSupper(supper);
        menu.setDinner(dinner);
        menu.setBreakfast(breakfast);
        menu.setAddress(userAddress);
        if (date.isEmpty()) {
            LocalDate localDate = LocalDate.now();
            java.sql.Date dateParse = java.sql.Date.valueOf(localDate);
            menu.setDate(dateParse);
        } else {
            LocalDate localDate = LocalDate.parse(date);
            java.sql.Date dateParse = java.sql.Date.valueOf(localDate);
            menu.setDate(dateParse);
        }
        menuRepository.save(menu);
        return "redirect:/kitchen/write-menu";
    }

    @GetMapping("/kitchen/write-menu/{id}/remove")
    public String menuRemove(@PathVariable(value = "id") Long id) {
        Menu menu = menuRepository.findById(id).orElseThrow(null);
        menuRepository.delete(menu);
        return "redirect:/kitchen/write-menu";
    }

    @GetMapping("/kitchen/write-menu/{id}/edit")
    public String menuEdit(@PathVariable(value = "id") Long id, Model model) {
        if (!menuRepository.existsById(id)) {
            return "redirect:/kitchen/write-menu";
        }
        Optional<Menu> post = menuRepository.findById(id);
        List<Menu> res = new ArrayList<>();
        post.ifPresent(res::add);
        String s = post.get().getDate().toString();
        model.addAttribute("menus", res);
        model.addAttribute("dateString", s);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-edit-menu";
    }


    @PostMapping("/kitchen/write-menu/{id}/edit")
    public String menuUpdate(@PathVariable(value = "id") Long id,
                             @RequestParam("date") String date,
                             @RequestParam("breakfast") String breakfast,
                             @RequestParam("dinner") String dinner,
                             @RequestParam("supper") String supper) {
        Menu menu = menuRepository.findById(id).orElseThrow(null);
        menu.setSupper(supper);
        menu.setDinner(dinner);
        menu.setBreakfast(breakfast);
        if (date.isEmpty()) {
            menu.setDate(menu.getDate());
        } else {
            LocalDate localDate = LocalDate.parse(date);
            java.sql.Date dateParse = java.sql.Date.valueOf(localDate);
            menu.setDate(dateParse);
        }
        menuRepository.save(menu);
        return "redirect:/kitchen/write-menu";
    }

    @Transactional
    @GetMapping("/kitchen/write-shop-meals")
    public String kitchenWriteShopMenu(Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        String userAddress = findUserAddress(authentication);
        List<ShopMeals> shopMealsList = shopMealsRepository.findAllByAddress(userAddress);

        shopMealsList.sort(Comparator.comparing(ShopMeals::getId));
        model.addAttribute("shopMealsList", shopMealsList);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-shop-meals";
    }

    @PostMapping("/kitchen/write-shop-meal")
    public String kitchenWriteShopMeal(Authentication authentication,
                                       @RequestParam("fullText") String fullText) {
        String userAddress = findUserAddress(authentication);
        LocalDate localDate = LocalDate.now();
        java.sql.Date dateParse = java.sql.Date.valueOf(localDate);
        ShopMeals meal = new ShopMeals();
        meal.setAddress(userAddress);
        meal.setDate(dateParse);
        if (fullText.isEmpty()) {
            meal.setFullText("No Meal");
        } else {
            meal.setFullText(fullText);
        }
        shopMealsRepository.save(meal);
        return "redirect:/kitchen/write-shop-meals";
    }

    @GetMapping("/kitchen/write-shop-meals/{id}/remove")
    public String mealRemove(@PathVariable(value = "id") Long id) {
        ShopMeals meal = shopMealsRepository.findById(id).orElseThrow(null);
        shopMealsRepository.delete(meal);
        return "redirect:/kitchen/write-shop-meals";
    }

    //TODO REGEX
    private String REGEX(String patternRegex,
                         String replace,
                         String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private String findUserAddress(Authentication authentication) {
        return getUser(authentication).getAddress();
    }

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        return oneByEmail.get();
    }
}
