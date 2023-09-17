create table Author
(
    ID              SERIAL PRIMARY KEY,
    FIO             VARCHAR(255),
    CREATE_DATETIME timestamp
);

ALTER TABLE Budget
    ADD COLUMN AUTHOR_ID INTEGER,
    ADD FOREIGN KEY (AUTHOR_ID) REFERENCES Author (ID);