package com.myhome.forms;

import java.util.Arrays;
import java.util.Objects;

public class ConvertFile {
    public String path;
    public byte[] img;
    public String nameStart;
    public String nameEnd;
    public String userEmail;

    public ConvertFile() {
    }

    public ConvertFile(String path, byte[] img, String nameStart, String nameEnd, String userEmail) {
        this.path = path;
        this.img = img;
        this.nameStart = nameStart;
        this.nameEnd = nameEnd;
        this.userEmail = userEmail;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getNameStart() {
        return nameStart;
    }

    public void setNameStart(String nameStart) {
        this.nameStart = nameStart;
    }

    public String getNameEnd() {
        return nameEnd;
    }

    public void setNameEnd(String nameEnd) {
        this.nameEnd = nameEnd;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConvertFile that = (ConvertFile) o;
        return Objects.equals(path, that.path) &&
                Arrays.equals(img, that.img) &&
                Objects.equals(nameStart, that.nameStart) &&
                Objects.equals(nameEnd, that.nameEnd) &&
                Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(path, nameStart, nameEnd, userEmail);
        result = 31 * result + Arrays.hashCode(img);
        return result;
    }
}
