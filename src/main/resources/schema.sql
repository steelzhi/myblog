DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS comments;

CREATE TABLE posts (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) NOT NULL,
    base_64_image varchar(500000),
    text varchar(1000) NOT NULL,
    number_of_likes int DEFAULT 0
);

CREATE TABLE comments (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text varchar(1000) NOT NULL,
    post_id int NOT NULL,
    CONSTRAINT post_for_comment FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tags (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text varchar(50) NOT NULL
);

CREATE TABLE posts_tags (
    id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    post_id int NOT NULL,
    tag_id int NOT NULL,
    CONSTRAINT posts_tags_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT posts_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO posts (name, text) VALUES ('1-й пост', 'Это 1-й пост');
INSERT INTO posts (name, text) VALUES ('2-й пост', 'Это 2-й пост');
INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!', 1);
INSERT INTO comments (text, post_id) VALUES ('Это самый 1-й пост!. И - 2-й комментарий к нему', 1);
INSERT INTO tags (id, text) VALUES (1, 'Общий тег');
INSERT INTO tags (id, text) VALUES (2, 'Тег для поста 2');
INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 1);
INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 1);
INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 2);

