package com.nequi.franchises.infrastructure.rest.dto;

import java.util.UUID;

public record ProductResponse(UUID id, UUID branchId, String name, int stock) {}
