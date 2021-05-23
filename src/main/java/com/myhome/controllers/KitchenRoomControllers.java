package com.myhome.controllers;

import com.myhome.forms.ResponseFile;
import com.myhome.forms.ResponseMessage;
import com.myhome.models.FileDB;
import com.myhome.models.User;
import com.myhome.repository.FileDBRepository;
import com.myhome.repository.UserRepository;
import com.myhome.security.UserDetailsImpl;
import com.myhome.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class KitchenRoomControllers {
    @Autowired
    private FileStorageService storageService;
    private final UserRepository userRepository;
    private final FileDBRepository fileDBRepository;

    public KitchenRoomControllers(UserRepository userRepository, FileDBRepository fileDBRepository) {
        this.userRepository = userRepository;
        this.fileDBRepository = fileDBRepository;
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

    @PostMapping("/kitchen/write-prescription")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.store(file);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/kitchen/read-cookbook")
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> files = storageService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getData().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }
//    public String kitchenWritePrescriptionAdd(Authentication authentication, Model model,
//                                              MultipartFile file,
//                                              String titleText,
//                                              String fullText) throws IOException {
//
//        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
//        String userEmail = details.getUsername();
//        Optional<User> oneByEmail = userRepository.findOneByEmail(userEmail);
//        String address = oneByEmail.get().getAddress();

//        private String titleText;
//        private String fullText;
//        private String address;
//        private String name;
//        private String type;
//        private byte[] image;

//        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

//        FileDB fileDB = new FileDB();
//        fileDB.setTitleText(titleText);
//        fileDB.setFullText(fullText);
//        fileDB.setAddress(address);
//        fileDB.setName(fileName);
//        fileDB.setType(file.getContentType());
//        fileDB.setImage(file.getBytes());

//        fileDBRepository.save(fileDB);
//        System.out.println("good save");
//        return "redirect:/kitchen/read-cookbook";
//    }

//    @GetMapping("/kitchen/read-cookbook")
//    public String kitchenReadPrescription(Authentication authentication, Model model) {
//        UserDetailsImpl details = (UserDetailsImpl) authentication.getPrincipal();
//        String userName = details.getUsername();
//        Optional<User> oneByEmail = userRepository.findOneByEmail(userName);
//        String address = oneByEmail.get().getAddress();
//        Iterable<FileDB> cookBooks = fileDBRepository.findAllByAddress(address);
//
//        List<FileDB> fileDBArrayList = new ArrayList<>();
//        cookBooks.forEach(fileDBArrayList::add);
//
//
////        letters.sort(Comparator.comparing(Letter::getNumberOfLetter));
//        fileDBArrayList.sort(Comparator.comparing(FileDB::getIdCookBook).reversed());
//
//
//        model.addAttribute("cookbooks", fileDBArrayList);
//        model.addAttribute("title", "Cook Book");
//        return "kitchen-read-cookbook";
//    }
}
