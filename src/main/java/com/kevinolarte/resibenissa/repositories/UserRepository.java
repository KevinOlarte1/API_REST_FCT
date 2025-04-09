package com.kevinolarte.resibenissa.repositories;

import com.kevinolarte.resibenissa.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findByResidenciaId(Long id);
    List<User> findAll();
}
