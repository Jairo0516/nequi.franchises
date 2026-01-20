package com.nequi.franchises.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest(
    @NotBlank(message = "name is required")
    String name
) {}
