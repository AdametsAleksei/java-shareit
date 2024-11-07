CREATE TABLE IF NOT EXISTS USERS (
    USER_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL,
    EMAIL VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS ITEM_REQUESTS (
    REQUEST_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    DESCRIPTION VARCHAR(255) NOT NULL,
    CREATED TIMESTAMP,
    REQUESTER BIGINT NOT NULL REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ITEMS (
    ITEM_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL,
    DESCRIPTION VARCHAR(100),
    AVAILABLE BOOLEAN NOT NULL,
    OWNER_ID BIGINT REFERENCES USERS(USER_ID) ON DELETE CASCADE,
    REQUEST_ID BIGINT REFERENCES ITEM_REQUESTS (REQUEST_ID)
);

CREATE TABLE IF NOT EXISTS BOOKINGS (
    BOOKING_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    START_DATE TIMESTAMP NOT NULL,
    END_DATE TIMESTAMP NOT NULL,
    STATUS VARCHAR(20) NOT NULL,
    ITEM_ID BIGINT NOT NULL REFERENCES ITEMS(ITEM_ID) ON DELETE CASCADE,
    BOOKER_ID BIGINT NOT NULL REFERENCES USERS(USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    COMMENT_ID BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    TEXT VARCHAR(500) NOT NULL,
    ITEM_ID BIGINT NOT NULL REFERENCES ITEMS(ITEM_ID),
    AUTHOR_ID BIGINT NOT NULL REFERENCES USERS(USER_ID),
    CREATED_DATE TIMESTAMP
);