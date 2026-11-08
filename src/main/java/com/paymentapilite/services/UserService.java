package com.paymentapilite.services;


import com.paymentapilite.domain.user.User;
import com.paymentapilite.domain.user.UserRequestDTO;
import com.paymentapilite.domain.user.UserResponseDTO;
import com.paymentapilite.exceptions.UserAlreadyExistsException;
import com.paymentapilite.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        this.repository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO request) {
        if (repository.existsByDocument(request.document())) {
            throw new UserAlreadyExistsException("Document is already registered.");
        }
        if (repository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email is already registered.");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(
                request.fullName(),
                request.document(),
                request.email(),
                passwordHash,
                request.userType(),
                request.balance()
        );

        User saved = repository.save(user);

        return new UserResponseDTO(
                saved.getId(),
                saved.getFullName(),
                saved.getUserType(),
                saved.getBalance()
        );
    }
}
