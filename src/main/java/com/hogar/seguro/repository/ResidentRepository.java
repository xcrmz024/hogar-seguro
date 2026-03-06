package com.hogar.seguro.repository;

import com.hogar.seguro.model.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    List<Resident> findByActiveTrue();

    Optional<Resident> findByIdAndActiveTrue(Long id);

}
