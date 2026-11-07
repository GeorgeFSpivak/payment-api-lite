package com.paymentapilite.repositories;


import com.paymentapilite.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    boolean existsByEmail(String email);
    boolean existsByDocument(String document);
}
