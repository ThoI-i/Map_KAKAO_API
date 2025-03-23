CREATE TABLE users (
    nickname CHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

INSERT INTO users (nickname, email, password)
VALUES ('Admin', 'ojw1912@gmail.com', 'adminadmin');

DELETE FROM users
WHERE nickname = 'memoryEat_Master';

SELECT * FROM users;