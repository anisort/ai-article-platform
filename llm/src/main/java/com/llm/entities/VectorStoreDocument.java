package com.llm.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vector_store")
public class VectorStoreDocument {

    @Id
    private String id;

    @Column(name = "metadata")
    private String metadata;

    public String getId() {
        return id;
    }

    public String getMetadata() {
        return metadata;
    }
}
