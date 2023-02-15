package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "comments")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "id_composition")
    private long idComposition;

    @Column(name = "comments")
    private String comments;

    public Comments() {
    }

    public Comments(int idUser, long idComposition, String comments) {
        this.idUser = idUser;
        this.idComposition = idComposition;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public long getIdComposition() {
        return idComposition;
    }

    public void setIdComposition(long idComposition) {
        this.idComposition = idComposition;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comments comments1 = (Comments) o;
        return id == comments1.id &&
                idUser == comments1.idUser &&
                idComposition == comments1.idComposition &&
                Objects.equals(comments, comments1.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idComposition, comments);
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idComposition=" + idComposition +
                ", comments='" + comments + '\'' +
                '}';
    }
}
