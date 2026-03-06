package com.hogar.seguro.repository;

import com.hogar.seguro.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    //ex. authentication process
    Optional<User> findByUsername(String username);

}
