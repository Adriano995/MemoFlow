-- data.sql
INSERT INTO users (id, nome, cognome) VALUES (1, 'Test', 'User');

INSERT INTO credenziali (utente_id, email, password) VALUES (1, 'test@example.com', '$2a$10$W88fqn0gdKNrXNxlZtJ.C.Tf1SMOitFFNCsVp7QM3ulqlb45zSJdC');

INSERT INTO authority (authority_enum) VALUES ('ROLE_USER');
INSERT INTO authority (authority_enum) VALUES ('ROLE_DEVELOPER');

INSERT INTO user_authority (utente_id, authority_enum) VALUES (1, 'ROLE_DEVELOPER');