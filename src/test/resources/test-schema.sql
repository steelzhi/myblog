DROP TABLE IF EXISTS posts_tags;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS tags;

CREATE TABLE posts (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    image BYTEA,
    text VARCHAR(1000) NOT NULL,
    number_of_likes INT DEFAULT 0
);

CREATE TABLE comments (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR(1000) NOT NULL,
    post_id INT NOT NULL,
    CONSTRAINT post_for_comment FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tags (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    text VARCHAR(50) NOT NULL
);

CREATE TABLE posts_tags (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    post_id INT NOT NULL,
    tag_id INT NOT NULL,
    CONSTRAINT posts_tags_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT posts_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

--ALTER TABLE posts ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE tags ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE posts_tags ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;
--INSERT INTO posts (name, text) VALUES ('Post1', 'Text1');
--INSERT INTO posts (name, text) VALUES ('Post2', 'Text2');
--INSERT INTO posts (name, text) VALUES ('Post3', 'Text3');
--INSERT INTO tags (text) VALUES ('#Tag1');
--INSERT INTO tags (text) VALUES ('#Tag2');
--INSERT INTO tags (text) VALUES ('#CommonTag');
--INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 1);
--INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 2);
--INSERT INTO posts_tags (post_id, tag_id) VALUES (1, 3);
--INSERT INTO posts_tags (post_id, tag_id) VALUES (2, 3);
--INSERT INTO comments (post_id, text) VALUES (2, 'comment1');
--INSERT INTO comments (post_id, text) VALUES (2, 'comment2');




