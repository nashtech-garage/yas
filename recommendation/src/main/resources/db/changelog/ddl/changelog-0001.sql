--liquibase formatted sql
CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS vector_store (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    item_id BIGINT NOT NULL,
    type TEXT,
    content TEXT,
    metadata JSON,
    content_embedding VECTOR(1536)
);