CREATE TABLE users (
                       id INT PRIMARY KEY,
                       first_name VARCHAR(50),
                       last_name VARCHAR(50),
                       email VARCHAR(100) UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(20)
);

CREATE TABLE states (
                        id INT PRIMARY KEY,
                        name VARCHAR(50)
);

CREATE TABLE todos (
                       id INT PRIMARY KEY,
                       title VARCHAR(255),
                       created_at DATETIME,
                       owner_id INT,
                       FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE tasks (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255),
                       priority ENUM('LOW', 'MEDIUM', 'HIGH'),
                       todo_id INT,
                       state_id INT,
                       FOREIGN KEY (todo_id) REFERENCES todos(id),
                       FOREIGN KEY (state_id) REFERENCES states(id)
);

CREATE TABLE todo_collaborator (
                                   todo_id INT,
                                   collaborator_id INT,
                                   PRIMARY KEY (todo_id, collaborator_id),
                                   FOREIGN KEY (todo_id) REFERENCES todos(id),
                                   FOREIGN KEY (collaborator_id) REFERENCES users(id)
);

INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (5, 'Nick', 'Green', 'nick@mail.com', '{noop}Qwerty1!', 'USER');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (6, 'Nora', 'White', 'nora@mail.com', '{noop}Qwerty2@', 'USER');
INSERT INTO users (id, first_name, last_name, email, password, role) VALUES (4, 'Mike', 'Brown', 'mike@mail.com', '{noop}Qwerty0#', 'ADMIN');


INSERT INTO states (id, name) VALUES (5, 'New');
INSERT INTO states (id, name) VALUES (6, 'Doing');
INSERT INTO states (id, name) VALUES (7, 'Verify');
INSERT INTO states (id, name) VALUES (8, 'Done');


INSERT INTO todos (id, title, created_at, owner_id) VALUES (7, 'Mike''s To-Do #1', '2023-09-16 14:00:04.810221', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (8, 'Mike''s To-Do #2', '2023-09-16 14:00:11.480271', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (9, 'Mike''s To-Do #3', '2023-09-16 14:00:16.351238', 4);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (10, 'Nick''s To-Do #1', '2023-09-16 14:14:54.532337', 5);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (11, 'Nick''s To-Do #2', '2023-09-16 14:15:04.707176', 5);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (12, 'Nora''s To-Do #1', '2023-09-16 14:15:32.464391', 6);
INSERT INTO todos (id, title, created_at, owner_id) VALUES (13, 'Nora''s To-Do #2', '2023-09-16 14:15:39.16246', 6);

INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (6, 'Task #2', 'LOW', 7, 5);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (5, 'Task #1', 'HIGH', 7, 8);
INSERT INTO tasks (id, name, priority, todo_id, state_id) VALUES (7, 'Task #3', 'MEDIUM', 7, 6);


INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (7, 5);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (7, 6);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (10, 6);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (10, 4);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (12, 5);
INSERT INTO todo_collaborator (todo_id, collaborator_id) VALUES (12, 4);

ALTER SEQUENCE state_sequence RESTART WITH 10;
