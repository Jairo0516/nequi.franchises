package com.nequi.franchises.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
    @NotBlank(message = "name is required")
    String name,

    @PositiveOrZero(message = "stock must be >= 0")
    int stock
) {}
