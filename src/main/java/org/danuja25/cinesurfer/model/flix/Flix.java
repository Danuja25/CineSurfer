package org.danuja25.cinesurfer.model.flix;

import lombok.Getter;
import lombok.Setter;
import org.danuja25.cinesurfer.model.castMember.CastMember;
import org.danuja25.cinesurfer.model.tag.Tag;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Flix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String type;
    private int year;
    private String imdbId;
    private String title;
    @ManyToMany
    private Set<CastMember> cast;
    @ManyToMany
    private Set<Tag> tags;
    @Digits(integer=5, fraction=2)
    private BigDecimal size;
    private String disk;
    private String path;
    private boolean isDeleted;

    public Flix() {
        cast = new HashSet<>();
        tags = new HashSet<>();
    }

    public Flix(String name, String disk, String path, BigDecimal size) {
        this();
        this.name = name;
        this.disk = disk;
        this.path = path;
        this.size = size;
    }

    public Flix(String name, int year, String imdbId, String title, Set<CastMember> cast, Set<Tag> tags, BigDecimal size, String disk, String path) {
        this();
        this.name = name;
        this.year = year;
        this.imdbId = imdbId;
        this.title = title;
        this.cast = cast;
        this.tags = tags;
        this.size = size;
        this.disk = disk;
        this.path = path;
    }
}
