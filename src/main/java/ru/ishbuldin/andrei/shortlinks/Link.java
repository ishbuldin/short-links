package ru.ishbuldin.andrei.shortlinks;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;


@Entity
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private int id;
    private String link;
    @Column(length = 1024)
    private String original;
    private int rank;
    private int count;

    public Link() {
    }

    public Link(String original, String link, int rank) {
        this.original = original;
        this.link = link;
        this.rank = rank;
    }

    //for testing
    public Link(int id, String link, String original, int rank, int count) {
        this.id = id;
        this.link = link;
        this.original = original;
        this.rank = rank;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public String getOriginal() {
        return original;
    }

    public String getLink() {
        return link;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link1 = (Link) o;
        return id == link1.id &&
                rank == link1.rank &&
                count == link1.count &&
                Objects.equals(original, link1.original) &&
                Objects.equals(link, link1.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, original, link, rank, count);
    }

    @Override
    public String toString() {
        return "Link{" +
                "id=" + id +
                ", original='" + original + '\'' +
                ", link='" + link + '\'' +
                ", rank=" + rank +
                ", count=" + count +
                '}';
    }
}