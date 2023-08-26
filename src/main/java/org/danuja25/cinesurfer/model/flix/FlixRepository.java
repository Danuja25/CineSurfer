package org.danuja25.cinesurfer.model.flix;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FlixRepository extends JpaRepository<Flix, Long> {

    Collection<Flix> findByImdbIdIsNull();
}
