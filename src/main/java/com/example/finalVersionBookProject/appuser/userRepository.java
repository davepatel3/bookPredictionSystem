package com.example.finalVersionBookProject.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
@Transactional(readOnly = true)
public interface userRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
}
