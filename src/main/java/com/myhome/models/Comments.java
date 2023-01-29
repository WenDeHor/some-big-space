package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Table(name = "comments")
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comments_id")
    private Long idComments;

    @Column(name = "email")
    private String email;

    @Column(name = "id_composition")
    private long idComposition;

    @Column(name = "comments")
    private String comments;

    public Comments() {
    }

    public Comments(String email, long idComposition, String comments) {
        this.email = email;
        this.idComposition = idComposition;
        this.comments = comments;
    }

    public Long getIdComments() {
        return idComments;
    }

    public void setIdComments(Long idComments) {
        this.idComments = idComments;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return idComposition == comments1.idComposition &&
                Objects.equals(idComments, comments1.idComments) &&
                Objects.equals(email, comments1.email) &&
                Objects.equals(comments, comments1.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idComments, email, idComposition, comments);
    }
}
