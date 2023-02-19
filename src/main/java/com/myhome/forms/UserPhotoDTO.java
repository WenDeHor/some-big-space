package com.myhome.forms;

import javax.persistence.Lob;
import java.util.Objects;

public class UserPhotoDTO {

    private String fullText;
    @Lob
    private String image;

    public UserPhotoDTO() {
    }

    public UserPhotoDTO(String fullText, String image) {
        this.fullText = fullText;
        this.image = image;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPhotoDTO that = (UserPhotoDTO) o;
        return Objects.equals(fullText, that.fullText) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullText, image);
    }
}
