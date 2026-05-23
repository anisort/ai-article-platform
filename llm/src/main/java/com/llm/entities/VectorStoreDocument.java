package com.llm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "vector_store")
@Getter
public class VectorStoreDocument {

    @Id
    private String id;

    @Column(name = "metadata")
    private String metadata;
}
