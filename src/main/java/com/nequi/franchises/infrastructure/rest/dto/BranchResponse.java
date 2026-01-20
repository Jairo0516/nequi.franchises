package com.nequi.franchises.infrastructure.rest.dto;

import java.util.UUID;

public record BranchResponse(UUID id, UUID franchiseId, String name) {}
