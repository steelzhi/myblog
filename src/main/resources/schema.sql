DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS comments;

CREATE TABLE posts (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) NOT NULL,
    base_64_image varchar(500),
    text varchar(1000) NOT NULL,
    number_of_likes int,
    tags varchar(50) NOT NULL
);

CREATE TABLE comments (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text varchar(1000) NOT NULL,
    post_id int NOT NULL,
    CONSTRAINT post_for_comment FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO posts (name, text, tags) VALUES ('1-й пост', 'Это 1-й пост', '1-й, 1-му');
INSERT INTO posts (name, text, tags) VALUES ('2-й пост', 'Это 2-й пост', '2-й, 2-му, о 2-м');
INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!', 1);
INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!. И - 2-й комментарий к нему', 1);

