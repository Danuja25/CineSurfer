package org.danuja25.cinesurfer.model.castMember;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.danuja25.cinesurfer.model.flix.Flix;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CastMember {
    private String name;
    @Id
    @Column(unique = true)
    private String imdbId;
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER,mappedBy = "cast")
    Set<Flix> flixSet;

    public CastMember(String name, String imdbId) {
        this.name = name;
        this.imdbId = imdbId;
    }
}
