package com.myhome.controllers.compresor;

import com.myhome.forms.ConvertFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;

@Component
public class CompressorImgToJpg {
    private final String DOT = ".";
    private final String JPG = "jpg";
    private final float ZIP_3 = 0.3f;
    private final String TMP_PATH = "src\\main\\java\\com\\myhome\\controllers\\compresor\\tmp_image\\";

    public ConvertFile convert(MultipartFile multipartFile, String userEmail, int count) throws IOException {
        String nameEnd = generateTMPImageName(userEmail, count);
        File compressedImageFile = new File(TMP_PATH + nameEnd);
        File imageFile = convertMultipartFileToFile(multipartFile);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(JPG);
        try (InputStream is = new FileInputStream(imageFile);
             OutputStream os = new FileOutputStream(compressedImageFile);
             ImageOutputStream ios = ImageIO.createImageOutputStream(os)) {
            BufferedImage image = ImageIO.read(is);
            if (!writers.hasNext())
                throw new IllegalStateException("No found image name:" + multipartFile.getOriginalFilename() +
                        ".Of User Email:" + userEmail);
            ImageWriter writer = (ImageWriter) writers.next();
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(ZIP_3);
            writer.write(null, new IIOImage(image, null, null), param);

            writer.dispose();
        } catch (IOException e) {
            throw new IOException("Not converted IMG:" + multipartFile.getOriginalFilename() +
                    ".Of User Email:" + userEmail, e);
        }
        return new ConvertFile(TMP_PATH, Files.readAllBytes(compressedImageFile.toPath()), multipartFile.getOriginalFilename(), nameEnd, userEmail);

    }

    public boolean deleteImage(String name) {
        File file = new File(TMP_PATH + name);
        return file.delete();
    }

    private String generateTMPImageName(String userEmail, int count) {
        return userEmail + "_tmp_" + count + DOT + JPG;
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(TMP_PATH + multipartFile.getOriginalFilename());
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        }
        return file;
    }

}
