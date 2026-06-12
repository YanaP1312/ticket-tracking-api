CREATE TABLE users
(
    user_id    SERIAL PRIMARY KEY,
    user_name  VARCHAR(100) NOT NULL,
    user_email VARCHAR(255) NOT NULL
);

CREATE TABLE projects
(
    project_id   SERIAL PRIMARY KEY,
    project_name VARCHAR(100) NOT NULL
);


CREATE TABLE tickets
(
    ticket_id          SERIAL PRIMARY KEY,
    ticket_title       VARCHAR(255) NOT NULL,
    ticket_description TEXT,
    project_id         INT          NOT NULL,
    ticket_status      VARCHAR(20)  NOT NULL DEFAULT 'open',
    ticket_created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    ticket_updated_at  TIMESTAMP,

    FOREIGN KEY (project_id)
        REFERENCES projects (project_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_ticket_status
        CHECK (ticket_status IN ('open', 'in progress', 'closed'))
);


CREATE TABLE tickets_users
(
    ticket_id INT NOT NULL,
    user_id   INT NOT NULL,

    PRIMARY KEY (ticket_id, user_id),

    FOREIGN KEY (ticket_id)
        REFERENCES tickets (ticket_id)
        ON DELETE CASCADE,

    FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_tickets_users_user_id
    ON tickets_users (user_id);


CREATE INDEX ind_tickets_project_id
    ON tickets (project_id);