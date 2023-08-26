package org.danuja25.cinesurfer.model.tag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.danuja25.cinesurfer.model.flix.Flix;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tag {
    @Id
    @Column(unique = true)
    private String name;
    private String imdbCode;
    @ManyToMany(mappedBy = "tags")
    private Set<Flix> flixSet;
}
