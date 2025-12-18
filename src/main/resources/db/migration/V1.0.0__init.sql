CREATE SCHEMA IF NOT EXISTS demo;

CREATE TABLE demo.item (
    id uuid NOT NULL,
    name varchar(4096) NOT NULL,
    colour varchar(512) NOT NULL,
    CONSTRAINT item_pkey PRIMARY KEY (id)
);
