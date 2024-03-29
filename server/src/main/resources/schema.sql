CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    CONSTRAINT pk_user PRIMARY KEY (user_id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id       BIGINT                                  NOT NULL,
    description   VARCHAR(200)                           NOT NULL,
    created TIMESTAMP                               NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (request_id),
    CONSTRAINT fk_request_user FOREIGN KEY (user_id)
    REFERENCES users (user_id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    item_name VARCHAR(200) NOT NULL,
    description VARCHAR(200),
    available BOOLEAN NOT NULL,
    request_id BIGINT,
    owner_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (item_id),
    CONSTRAINT fk_request FOREIGN KEY (request_id)
    REFERENCES requests (request_id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_booking TIMESTAMP                                           NOT NULL,
    end_booking   TIMESTAMP                                           NOT NULL,
    booker_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    item_id BIGINT REFERENCES items (item_id) ON DELETE CASCADE NOT NULL,
    status VARCHAR(20),
    CONSTRAINT pk_booking PRIMARY KEY (booking_id)
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    comment VARCHAR(200) NOT NULL,
    item_id BIGINT REFERENCES items (item_id),
    author_id BIGINT REFERENCES users (user_id),
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (comment_id),
    CONSTRAINT fk_author FOREIGN KEY (author_id)
    REFERENCES users (user_id),
    CONSTRAINT fk_item FOREIGN KEY (item_id)
    REFERENCES items (item_id)
);

