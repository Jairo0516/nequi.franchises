package com.nequi.franchises.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "branch",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_branch_franchise_name", columnNames = {"franchiseId", "name"})
        },
        indexes = {
                @Index(name = "ix_branch_franchiseId", columnList = "franchiseId")
        }
)
public class BranchEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID franchiseId;

    @Column(nullable = false)
    private String name;

    protected BranchEntity() {
    }

    public BranchEntity(UUID id, UUID franchiseId, String name) {
        this.id = id;
        this.franchiseId = franchiseId;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(UUID franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
