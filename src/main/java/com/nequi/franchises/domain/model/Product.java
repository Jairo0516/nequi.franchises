package com.nequi.franchises.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Product(UUID id, UUID branchId, String name, int stock, Long version, boolean deleted,
                      Instant deletedAt) {
}
