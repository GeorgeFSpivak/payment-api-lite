package com.paymentapilite.domain.user.controllers;


import com.paymentapilite.controllers.UserController;
import com.paymentapilite.domain.user.UserRequestDTO;
import com.paymentapilite.domain.user.UserResponseDTO;
import com.paymentapilite.domain.user.UserType;
import com.paymentapilite.exceptions.UserAlreadyExistsException;
import com.paymentapilite.exceptions.UserNotFoundException;
import com.paymentapilite.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private UserService service;
    private final UserResponseDTO user = new UserResponseDTO(
            7L,
            "Micheal Spivak",
            UserType.COMMON,
            new BigDecimal("10.50")
    );
    private static final String VALID_USER_JSON =
                        """
                        {
                            "fullName": "Micheal Spivak",
                            "document": "12345678901",
                            "email": "micheal@example.com",
                            "password": "test-password",
                            "userType": "COMMON",
                            "balance": 10.50
                        }
                        """;


    @Test
    void shouldCreateUserAndReturnLocation() throws Exception {
        when(service.createUser(any(UserRequestDTO.class))).thenReturn(user);

        mvc.perform(post("/users")
                .queryParam("origin", "test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_USER_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/users/7"))
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fullName").value("Micheal Spivak"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(service.getUserById(7L)).thenReturn(user);

        mvc.perform(get("/users/{id}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.fullName").value("Micheal Spivak"))
                .andExpect(jsonPath("$.userType").value("COMMON"))
                .andExpect(jsonPath("$.balance").value(10.50));
    }

    @Test
    void shouldRejectBlankPassword() throws Exception {
        String invalidJson = VALID_USER_JSON.replace("\"test-password\"", "\"   \"");

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void shouldReturnNotFoundForUnknownUserId() throws Exception {
        long unknownId = 999L;

        when(service.getUserById(unknownId)).thenThrow(new UserNotFoundException(unknownId));

        mvc.perform(get("/users/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        when(service.createUser(any(UserRequestDTO.class)))
                .thenThrow(new UserAlreadyExistsException("Email is already registered."));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Email is already registered."));
    }

    @Test
    void shouldReturnConflictForDatabaseUniqueViolation() throws Exception {
        SQLException cause = new SQLException("Unique constraint violated", "23505");

        when(service.createUser(any(UserRequestDTO.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key", cause));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_USER_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail")
                        .value("The registration conflicts with an existing user."));
    }
}
