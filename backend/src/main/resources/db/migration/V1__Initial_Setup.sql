CREATE TABLE customer
(
    id    BIGSERIAL PRIMARY KEY,
    password  TEXT NOT NULL,
    name  TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE ,
    age   INT  NOT NULL,
    gender  TEXT NOT NULL
);