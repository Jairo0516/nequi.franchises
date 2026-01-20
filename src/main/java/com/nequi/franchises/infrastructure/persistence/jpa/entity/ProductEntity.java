package com.nequi.franchises.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "product",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_branch_name", columnNames = {"branchId", "name"})
        },
        indexes = {
                @Index(name = "ix_product_branchId", columnList = "branchId"),
                @Index(name = "ix_product_stock", columnList = "stock")
        }
)
public class ProductEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID branchId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int stock;

    @Version
    private Long version;

    @Column(nullable = false)
    boolean deleted;

    @Column
    Instant deletedAt;

    protected ProductEntity() {
    }

    public ProductEntity(UUID id, UUID branchId, String name, int stock) {
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

}
