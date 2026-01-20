package com.nequi.franchises.domain.model;

import java.util.UUID;

public record Branch(UUID id, UUID franchiseId, String name) {
}
