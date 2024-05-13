CREATE TABLE sizes
(
    size_name text PRIMARY KEY
);

INSERT INTO sizes (size_name)
VALUES ('small'),
       ('medium'),
       ('large');

CREATE TYPE entry_state as enum ('PENDING', 'ERROR', 'READY');

CREATE TABLE entries
(
    entry_id           text primary key,
    state              entry_state default 'PENDING',
    author             text not null,
    message            text not null,
    source_picture_url text not null,
    error_message      text,
    created_at         timestamp with time zone default now()
);

CREATE TABLE resized_pictures
(
    entry_id    text references entries (entry_id),
    size        text references sizes (size_name),
    picture_url text,
    primary key (entry_id, size)
);

CREATE VIEW last_ten_entries AS
    WITH last_ten as (
        SELECT
            entry_id, author, message, created_at, source_picture_url, state, error_message
        FROM entries
        WHERE state='READY'
        ORDER BY created_at DESC
        LIMIT 10 )

    SELECT
        t.*,
        rp.size,
        rp.picture_url
    FROM last_ten t
             LEFT JOIN resized_pictures rp ON t.entry_id=rp.entry_id
    ORDER BY created_at desc, entry_id;
