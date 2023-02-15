package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "evaluate")
public class Evaluate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "id_composition")
    private int idComposition;

    @Column(name = "id_appraiser")
    private int idAppraiser;

    //MARK
    @Column(name = "environment")
    private Mark environment;

    @Column(name = "characters")
    private Mark characters;

    @Column(name = "atmosphere")
    private Mark atmosphere;

    @Column(name = "plot")
    private Mark plot;

    @Column(name = "impression")
    private Mark impression;

    @Column(name = "comments")
    @Size(max = 3000)
    private String comments;

    public Evaluate() {
    }

    public Evaluate(Date date, int idComposition, int idAppraiser, Mark environment, Mark characters, Mark atmosphere, Mark plot, Mark impression, @Size(max = 3000) String comments) {
        this.date = date;
        this.idComposition = idComposition;
        this.idAppraiser = idAppraiser;
        this.environment = environment;
        this.characters = characters;
        this.atmosphere = atmosphere;
        this.plot = plot;
        this.impression = impression;
        this.comments = comments;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIdComposition() {
        return idComposition;
    }

    public void setIdComposition(int idComposition) {
        this.idComposition = idComposition;
    }

    public int getIdAppraiser() {
        return idAppraiser;
    }

    public void setIdAppraiser(int idAppraiser) {
        this.idAppraiser = idAppraiser;
    }

    public Mark getEnvironment() {
        return environment;
    }

    public void setEnvironment(Mark environment) {
        this.environment = environment;
    }

    public Mark getCharacters() {
        return characters;
    }

    public void setCharacters(Mark characters) {
        this.characters = characters;
    }

    public Mark getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(Mark atmosphere) {
        this.atmosphere = atmosphere;
    }

    public Mark getPlot() {
        return plot;
    }

    public void setPlot(Mark plot) {
        this.plot = plot;
    }

    public Mark getImpression() {
        return impression;
    }

    public void setImpression(Mark impression) {
        this.impression = impression;
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
        Evaluate evaluate = (Evaluate) o;
        return id == evaluate.id &&
                idComposition == evaluate.idComposition &&
                idAppraiser == evaluate.idAppraiser &&
                Objects.equals(date, evaluate.date) &&
                environment == evaluate.environment &&
                characters == evaluate.characters &&
                atmosphere == evaluate.atmosphere &&
                plot == evaluate.plot &&
                impression == evaluate.impression &&
                Objects.equals(comments, evaluate.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, idComposition, idAppraiser, environment, characters, atmosphere, plot, impression, comments);
    }

    @Override
    public String toString() {
        return "Evaluate{" +
                "id=" + id +
                ", date=" + date +
                ", idComposition=" + idComposition +
                ", idAppraiser=" + idAppraiser +
                ", environment=" + environment +
                ", characters=" + characters +
                ", atmosphere=" + atmosphere +
                ", plot=" + plot +
                ", impression=" + impression +
                ", comments='" + comments + '\'' +
                '}';
    }
}
