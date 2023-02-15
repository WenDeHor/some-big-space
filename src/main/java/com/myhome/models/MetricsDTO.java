package com.myhome.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "metrics_dto")
public class MetricsDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "count_users")
    private long countUsers;

    @Column(name = "count_of_votes")
    private long countOfVotes;

    public MetricsDTO() {
    }

    public MetricsDTO(LocalDate date, long countUsers, long countOfVotes) {
        this.date = date;
        this.countUsers = countUsers;
        this.countOfVotes = countOfVotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getCountUsers() {
        return countUsers;
    }

    public void setCountUsers(long countUsers) {
        this.countUsers = countUsers;
    }

    public long getCountOfVotes() {
        return countOfVotes;
    }

    public void setCountOfVotes(long countOfVotes) {
        this.countOfVotes = countOfVotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDTO that = (MetricsDTO) o;
        return id == that.id &&
                countUsers == that.countUsers &&
                countOfVotes == that.countOfVotes &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, countUsers, countOfVotes);
    }
}
