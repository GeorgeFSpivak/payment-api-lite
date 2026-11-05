package com.paymentapilite.domain.user;


import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UserRequestDTO(
        @NotBlank
        @Size(max = 150)
        String fullName,

        @NotBlank
        @Size(max = 20)
        String document,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        String password,

        @NotNull
        UserType userType,

        @NotNull
        @PositiveOrZero
        @DecimalMax("99999999999999999.99")
        BigDecimal balance
) {
}
