package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.ConvertFile;
import com.myhome.forms.CookBookDTO;
import com.myhome.forms.MenuDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class _5_KitchenRoomControllers {
    private final ShopMealsRepository shopMealsRepository;
    private final UserRepository userRepository;
    private final CookBookRepository cookBookRepository;
    private final MenuRepository menuRepository;
    private final MetricsService metricsService;
    private final CompressorImgToJpg compressorImgToJpg;
    private final String KITCHEN_ROOM = "Кухня";

    public _5_KitchenRoomControllers(ShopMealsRepository shopMealsRepository, UserRepository userRepository, CookBookRepository cookBookRepository, MenuRepository menuRepository, MetricsService metricsService, CompressorImgToJpg compressorImgToJpg) {
        this.shopMealsRepository = shopMealsRepository;
        this.userRepository = userRepository;
        this.cookBookRepository = cookBookRepository;
        this.menuRepository = menuRepository;
        this.metricsService = metricsService;
        this.compressorImgToJpg = compressorImgToJpg;
    }

    @GetMapping("/kitchen/write-prescription")
    public String kitchenWritePrescription(Model model,
                                           HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-prescription";
    }

    @Transactional
    @GetMapping("/kitchen/read-cookbook")
    public String kitchenReadCookBooks(Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<CookBookDTO> cookBooks = cookBookRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new CookBookDTO(
                        el.getId(),
                        el.getDate(),
                        convertText(el.getTitleText()),
                        convertText(el.getFullText()),
                        converterImage(el.getImage())
                ))
                .sorted(Comparator.comparing(CookBookDTO::getId).reversed())
                .collect(Collectors.toList());
        model.addAttribute("cookBooks", cookBooks);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-read-cookbook";
    }

    private String convertText(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }

    private String converterImage(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @GetMapping("/kitchen/read-cookbook/{id}/remove")
    public String cookBookRemove(@PathVariable(value = "id") int id,
                                 Authentication authentication) {
        User user = getUser(authentication);
        Optional<CookBook> cookBookOptional = cookBookRepository.findByIdAndIdUser(id, user.getId());
        cookBookOptional.ifPresent(cookBookRepository::delete);
        return "redirect:/kitchen/read-cookbook";
    }

    @GetMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookEdit(@PathVariable(value = "id") int id,
                               Model model,
                               Authentication authentication) {
        User user = getUser(authentication);
        Optional<CookBook> cookBookOptional = cookBookRepository.findByIdAndIdUser(id, user.getId());
        if (cookBookOptional.isPresent()) {
            CookBook cookBook = cookBookOptional.get();
            CookBookDTO cookBookDTO = new CookBookDTO(
                    cookBook.getId(),
                    convertText(cookBook.getTitleText()),
                    convertText(cookBook.getFullText()),
                    converterImage(cookBook.getImage()));
            model.addAttribute("cookBook", cookBookDTO);
            model.addAttribute("title", KITCHEN_ROOM);
            return "kitchen-edit-cookbook";
        }
        return "redirect:/kitchen/read-cookbook";
    }

    @PostMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookUpdate(@PathVariable(value = "id") int id,
                                 MultipartFile file,
                                 Authentication authentication,
                                 @RequestParam String titleText,
                                 @RequestParam String fullText) throws IOException {
        User user = getUser(authentication);
        Optional<CookBook> cookBookOptional = cookBookRepository.findByIdAndIdUser(id, user.getId());
        if (cookBookOptional.isPresent()) {
            CookBook cookBook = cookBookOptional.get();
            cookBook.setTitleText(titleText);
            cookBook.setFullText(convertText(fullText));
            cookBook.setDate(new Date());
            if (!Objects.equals(file.getContentType(), "application/octet-stream")) { //new Photo
                ConvertFile convert = compressorImgToJpg.convert(file, user.getEmail());
                cookBook.setImage(convert.img);
                compressorImgToJpg.deleteImage(convert.nameStart);
                compressorImgToJpg.deleteImage(convert.nameEnd);
            }
            cookBookRepository.save(cookBook);
        }
        return "redirect:/kitchen/read-cookbook";
    }

    @PostMapping("/kitchen/write-prescription")
    public String kitchenWritePrescriptionAdd(Authentication authentication,
                                              MultipartFile file,
                                              @RequestParam("titleText") String titleText,
                                              @RequestParam("fullText") String fullText) throws IOException {
        User user = getUser(authentication);
        CookBook cookBook = new CookBook();
        cookBook.setDate(new Date());
        cookBook.setTitleText(titleText);
        cookBook.setFullText(convertText(fullText));
        cookBook.setIdUser(user.getId());
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
        User user = getUser(authentication);
        List<Menu> allMenuByAddress = menuRepository.findAllByIdUser(user.getId());
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
        User user = getUser(authentication);
        Menu menu = new Menu();
        menu.setSupper(supper);
        menu.setDinner(dinner);
        menu.setBreakfast(breakfast);
        menu.setIdUser(user.getId());
        if (date.isEmpty()) {
            menu.setDate(new Date());
        } else {
            menu.setDate(Date.from(Instant.parse(date + "T10:15:30.00Z")));
        }
        menuRepository.save(menu);
        return "redirect:/kitchen/write-menu";
    }

    @GetMapping("/kitchen/write-menu/{id}/remove")
    public String menuRemove(@PathVariable(value = "id") int id,
                             Authentication authentication) {
        User user = getUser(authentication);
        Optional<Menu> menuOptional = menuRepository.findByIdAndIdUser(id, user.getId());
        menuOptional.ifPresent(menuRepository::delete);
        return "redirect:/kitchen/write-menu";
    }

    @GetMapping("/kitchen/write-menu/{id}/edit")
    public String menuEdit(@PathVariable(value = "id") int id,
                           Model model,
                           Authentication authentication) {
        User user = getUser(authentication);
        Optional<Menu> menuOptional = menuRepository.findByIdAndIdUser(id, user.getId());
        if (menuOptional.isPresent()) {
            Menu menu = menuOptional.get();
            MenuDTO menuDTO = new MenuDTO(
                    menu.getId(),
                    menu.getDate(),
                    menu.getBreakfast(),
                    menu.getDinner(),
                    menu.getSupper());
            model.addAttribute("menus", menuDTO);
            model.addAttribute("title", KITCHEN_ROOM);
            return "kitchen-edit-menu";
        }
        return "redirect:/kitchen/write-menu";
    }


    @PostMapping("/kitchen/write-menu/{id}/edit")
    public String menuUpdate(@PathVariable(value = "id") int id,
                             @RequestParam("date") String date,
                             @RequestParam("breakfast") String breakfast,
                             @RequestParam("dinner") String dinner,
                             @RequestParam("supper") String supper,
                             Authentication authentication) {
        User user = getUser(authentication);
        Optional<Menu> menuOptional = menuRepository.findByIdAndIdUser(id, user.getId());
        if (menuOptional.isPresent()) {
            Menu menu = menuOptional.get();
            menu.setSupper(supper);
            menu.setDinner(dinner);
            menu.setBreakfast(breakfast);
            if (date.isEmpty()) {
                menu.setDate(new Date());
            } else {
                menu.setDate(Date.from(Instant.parse(date + "T10:15:30.00Z")));
            }
            menuRepository.save(menu);
        }
        return "redirect:/kitchen/write-menu";
    }

    @Transactional
    @GetMapping("/kitchen/write-shop-meals")
    public String kitchenWriteShopMenu(Authentication authentication,
                                       Model model,
                                       HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<ShopMeals> shopMealsList = shopMealsRepository.findAllByIdUser(user.getId());
        shopMealsList.sort(Comparator.comparing(ShopMeals::getId));
        model.addAttribute("shopMealsList", shopMealsList);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-shop-meals";
    }

    @PostMapping("/kitchen/write-shop-meal")
    public String kitchenWriteShopMeal(Authentication authentication,
                                       @RequestParam("fullText") String fullText) {
        User user = getUser(authentication);
        ShopMeals shopMeals = new ShopMeals();
        shopMeals.setIdUser(user.getId());
        shopMeals.setDate(new Date());
        if (fullText.isEmpty()) {
            shopMeals.setFullText("No Meal");
        } else {
            shopMeals.setFullText(fullText);
        }
        shopMealsRepository.save(shopMeals);
        return "redirect:/kitchen/write-shop-meals";
    }

    @GetMapping("/kitchen/write-shop-meals/{id}/remove")
    public String mealRemove(@PathVariable(value = "id") int id,
                             Authentication authentication) {
        User user = getUser(authentication);
        Optional<ShopMeals> mealsOptional = shopMealsRepository.findByIdAndIdUser(id, user.getId());
        mealsOptional.ifPresent(shopMealsRepository::delete);
        return "redirect:/kitchen/write-shop-meals";
    }

    private User getUser(Authentication authentication) {
        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
        String userEmail = details.getUsername();
        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
        return oneByEmail.get();
    }
}
