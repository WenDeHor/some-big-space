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
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "local_date", nullable = false)
    private Date localDate;

    @Column(name = "id_composition")
    private long idComposition;

    @Column(name = "email_appraiser")
    private String emailAppraiser;

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
    @Size(min = 0, max = 3000)
    private String comments;

    public Evaluate() {
    }

    public Evaluate(Date localDate, long idComposition, String emailAppraiser, Mark environment, Mark characters, Mark atmosphere, Mark plot, Mark impression, @Size(min = 0, max = 3000) String comments) {
        this.localDate = localDate;
        this.idComposition = idComposition;
        this.emailAppraiser = emailAppraiser;
        this.environment = environment;
        this.characters = characters;
        this.atmosphere = atmosphere;
        this.plot = plot;
        this.impression = impression;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
    }

    public long getIdComposition() {
        return idComposition;
    }

    public void setIdComposition(long idComposition) {
        this.idComposition = idComposition;
    }

    public String getEmailAppraiser() {
        return emailAppraiser;
    }

    public void setEmailAppraiser(String emailAppraiser) {
        this.emailAppraiser = emailAppraiser;
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
        return idComposition == evaluate.idComposition &&
                Objects.equals(id, evaluate.id) &&
                Objects.equals(localDate, evaluate.localDate) &&
                Objects.equals(emailAppraiser, evaluate.emailAppraiser) &&
                environment == evaluate.environment &&
                characters == evaluate.characters &&
                atmosphere == evaluate.atmosphere &&
                plot == evaluate.plot &&
                impression == evaluate.impression &&
                Objects.equals(comments, evaluate.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, localDate, idComposition, emailAppraiser, environment, characters, atmosphere, plot, impression, comments);
    }
}
