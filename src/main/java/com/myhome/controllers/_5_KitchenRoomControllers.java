package com.myhome.controllers;

import com.myhome.controllers.compresor.CompressorImgToJpg;
import com.myhome.forms.*;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Controller
public class _5_KitchenRoomControllers {
    private final ShopMealsRepository shopMealsRepository;
    private final UserRepository userRepository;
    private final CookBookRepository cookBookRepository;
    private final MenuRepository menuRepository;
    private final MetricsService metricsService;
    private final CompressorImgToJpg compressorImgToJpg;
    private final String KITCHEN_ROOM = "Кухня";
    private int constant = 1049335;
    private int limit_photo = 6; //MB
    private int limit_cook_book_titleText = 100; //chars
    private int limit_cook_book_fullText = 3000; //chars
    private int limit_menu_text = 50; //chars
    private int limit_shop_meal_text = 100; //chars

    private final static int LIMIT_LIST = 10;

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
        List<CookBookDTO> dtos = getAllCookBookDTO(user);
        model.addAttribute("buttons", new Buttons(getCountPage(dtos.size()), "10"));
        List<CookBookDTO> basePage = dtos.stream()
                .limit(LIMIT_LIST)
                .collect(toList());
        model.addAttribute("cookBooks", basePage);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-read-cookbook";
    }

    @Transactional
    @GetMapping("/kitchen/read-cookbook/{offset}")
    public String kitchenReadCookBooksByPages(@PathVariable(value = "offset") int offset,
                                              Authentication authentication,
                                              Model model,
                                              HttpServletRequest request) {
        metricsService.startMetricsCheck(request, request.getRequestURI());
        User user = getUser(authentication);
        List<CookBookDTO> dtos = getAllCookBookDTO(user);
        String countPage = getCountPage(dtos.size());
        if (offset == 60) {
            List<CookBookDTO> offsetList = dtos.stream()
                    .skip(50)
                    .collect(toList());
            model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
            model.addAttribute("cookBooks", offsetList);
            model.addAttribute("title", KITCHEN_ROOM);
            return "kitchen-read-cookbook";
        }
        List<CookBookDTO> offsetList = getOffsetListCookBookDTO(dtos, offset);
        model.addAttribute("buttons", new Buttons(countPage, String.valueOf(offset)));
        model.addAttribute("cookBooks", offsetList);
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-read-cookbook";
    }

    private List<CookBookDTO> getOffsetListCookBookDTO(List<CookBookDTO> dtos, int offset) {
        return dtos.stream()
                .skip(offset - 10)
                .limit(10)
                .collect(toList());
    }

    private String getCountPage(int size) {
        if (size < 60) {
            char[] chars = String.valueOf(size).toCharArray();
            if (chars.length < 2) {
                return "10";
            }
            return Integer.parseInt(String.valueOf(chars[0])) + 1 + "0";
        }
        return "60";
    }

    private List<CookBookDTO> getAllCookBookDTO(User user) {
        return cookBookRepository.findAllByIdUser(user.getId()).stream()
                .map(el -> new CookBookDTO(
                        el.getId(),
                        el.getDate(),
                        el.getTitleText(),
                        el.getFullText(),
                        converterImage(el.getImage())
                ))
                .sorted(Comparator.comparing(CookBookDTO::getId).reversed())
                .collect(Collectors.toList());
    }

    private String converterImage(byte[] img) {
        return Base64.getEncoder().encodeToString(img);
    }

    @Transactional
    @GetMapping("/kitchen/read-cookbook/{id}/remove")
    public String cookBookRemove(@PathVariable(value = "id") int id,
                                 Authentication authentication) {
        User user = getUser(authentication);
        Optional<CookBook> cookBookOptional = cookBookRepository.findByIdAndIdUser(id, user.getId());
        cookBookOptional.ifPresent(cookBookRepository::delete);
        return "redirect:/kitchen/read-cookbook";
    }

    @Transactional
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
                    cookBook.getTitleText(),
                    convertTextToEdit(cookBook.getFullText()),
                    converterImage(cookBook.getImage()));
            model.addAttribute("cookBook", cookBookDTO);
            model.addAttribute("title", KITCHEN_ROOM);
            return "kitchen-edit-cookbook";
        }
        return "redirect:/kitchen/read-cookbook";
    }

    @Transactional
    @PostMapping("/kitchen/read-cookbook/{id}/edit")
    public String cookBookUpdate(@PathVariable(value = "id") int id,
                                 MultipartFile file,
                                 Authentication authentication,
                                 @RequestParam String titleText,
                                 @RequestParam String fullText,
                                 Model model) throws IOException {
        if (validatorCookBook(titleText, fullText, file)) {
            return saveCookBookWithError(file, titleText, fullText, model);
        }
        User user = getUser(authentication);
        Optional<CookBook> cookBookOptional = cookBookRepository.findByIdAndIdUser(id, user.getId());
        if (cookBookOptional.isPresent()) {
            CookBook cookBook = cookBookOptional.get();
            cookBook.setTitleText(titleText);
            cookBook.setFullText(convertTextToSave(fullText));
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

    private boolean validatorCookBook(String titleText, String fullText, MultipartFile file) throws IOException {
        return file.getBytes().length / constant > limit_photo
                || titleText.toCharArray().length > limit_cook_book_titleText
                || fullText.toCharArray().length > limit_cook_book_fullText;
    }

    private String saveCookBookWithError(MultipartFile file,
                                         String titleText,
                                         String fullText,
                                         Model model) throws IOException {
        String stile = "crimson";
        String originalFilename = file.getOriginalFilename();
        int fileSize = file.getBytes().length / constant;
        int titleTextSize = titleText.toCharArray().length;
        int fullTextSize = fullText.toCharArray().length;
        if (file.getBytes().length / constant > limit_photo) {
            model.addAttribute("stile", stile);
        }

        model.addAttribute("originalFilename", originalFilename);
        model.addAttribute("fileSize", fileSize);

        model.addAttribute("titleText", titleText);
        model.addAttribute("titleTextSize", titleTextSize);

        model.addAttribute("fullText", fullText);
        model.addAttribute("fullTextSize", fullTextSize);

        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-edit-cookbook-with-error";
    }

    @PostMapping("/kitchen/write-prescription")
    public String kitchenWritePrescriptionAdd(Authentication authentication,
                                              MultipartFile file,
                                              @RequestParam("titleText") String titleText,
                                              @RequestParam("fullText") String fullText,
                                              Model model) throws IOException {
        if (validatorCookBook(titleText, fullText, file)) {
            return saveCookBookWithError(file, titleText, fullText, model);
        }
        User user = getUser(authentication);
        CookBook cookBook = new CookBook();
        cookBook.setDate(new Date());
        cookBook.setTitleText(titleText);
        cookBook.setFullText(convertTextToSave(fullText));
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
        model.addAttribute("error", new ErrorMessage("", "", ""));
        model.addAttribute("menuWithError", new Menu());
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-menu";
    }

    @PostMapping("/kitchen/write-menu")
    public String kitchenWriteMenu(Authentication authentication,
                                   @RequestParam("date") String date,
                                   @RequestParam("breakfast") String breakfast,
                                   @RequestParam("dinner") String dinner,
                                   @RequestParam("supper") String supper,
                                   Model model) {
        ErrorMessage validator = validatorMenu(breakfast, dinner, supper);
        if (validator.getOne().length() > 0 || validator.getTwo().length() > 0 || validator.getThree().length() > 0) {
            return menuWithError(authentication, date, breakfast, dinner, supper, model, validator);
        }
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

    private ErrorMessage validatorMenu(String breakfast,
                                       String dinner,
                                       String supper) {
        ErrorMessage errorMessage = new ErrorMessage("", "", "");

        if (breakfast.length() > limit_menu_text) {
            errorMessage.setOne("1");
        }
        if (dinner.length() > limit_menu_text) {
            errorMessage.setTwo("2");
        }
        if (supper.length() > limit_menu_text) {
            errorMessage.setThree("3");
        }
        return errorMessage;
    }

    private String menuWithError(Authentication authentication,
                                 String date,
                                 String breakfast,
                                 String dinner,
                                 String supper,
                                 Model model,
                                 ErrorMessage errors) {
        Menu menu = new Menu();
        if (date.isEmpty()) {
            menu.setDate(new Date());
        } else {
            menu.setDate(Date.from(Instant.parse(date + "T10:15:30.00Z")));
        }
        menu.setBreakfast(breakfast);
        menu.setDinner(dinner);
        menu.setSupper(supper);

        User user = getUser(authentication);
        List<Menu> allMenuByAddress = menuRepository.findAllByIdUser(user.getId());
        allMenuByAddress.sort(Comparator.comparing(Menu::getId));
        model.addAttribute("allMenuByAddress", allMenuByAddress);

        model.addAttribute("title", KITCHEN_ROOM);
        model.addAttribute("error", errors);
        model.addAttribute("menuWithError", menu);
        return "kitchen-write-menu";
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

        model.addAttribute("error", new ErrorMessage(""));
        model.addAttribute("shopMealsWithError", new ShopMeals());
        model.addAttribute("title", KITCHEN_ROOM);
        return "kitchen-write-shop-meals";
    }

    @PostMapping("/kitchen/write-shop-meal")
    public String kitchenWriteShopMeal(Authentication authentication,
                                       Model model,
                                       @RequestParam("fullText") String fullText) {
        ErrorMessage validator = validatorShopMeal(fullText);
        if (validator.getOne().length() > 0) {
            return shopMealsWithError(authentication, fullText, model, validator);
        }
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

    private ErrorMessage validatorShopMeal(String fullText) {
        ErrorMessage errorMessage = new ErrorMessage("");
        if (fullText.length() > limit_shop_meal_text) {
            errorMessage.setOne("1");
        }
        return errorMessage;
    }

    private String shopMealsWithError(Authentication authentication,
                                      String fullText,
                                      Model model,
                                      ErrorMessage errors) {
        ShopMeals shopMeals = new ShopMeals();
        shopMeals.setFullText(fullText);

        User user = getUser(authentication);
        List<ShopMeals> shopMealsList = shopMealsRepository.findAllByIdUser(user.getId());
        shopMealsList.sort(Comparator.comparing(ShopMeals::getId));
        model.addAttribute("shopMealsList", shopMealsList);


        model.addAttribute("title", KITCHEN_ROOM);
        model.addAttribute("error", errors);
        model.addAttribute("shopMealsWithError", shopMeals);
        return "kitchen-write-shop-meals";
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


    private String convertTextToSave(String fullText) {
        String text = REGEX("(\\n\\r*)", "<br>&#160&#160 ", fullText);
        return REGEX("(\\A)", "&#160&#160 ", text);
    }

    private String REGEX(String patternRegex, String replace, String text) {
        Pattern pattern = Pattern.compile(patternRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.replaceAll(replace);
    }

    private String convertTextToEdit(String text) {
        return text
                .replace("&#160&#160 ", "")
                .replace("<br>", "");
    }
}
