CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255),
    cognome VARCHAR(255)
);

CREATE TABLE credenziali (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utente_id BIGINT NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES users(id)
);

CREATE TABLE authority (
    authority_enum VARCHAR(255) PRIMARY KEY
);

CREATE TABLE user_authority (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    utente_id BIGINT NOT NULL,
    authority_enum VARCHAR(255) NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES users(id),
    FOREIGN KEY (authority_enum) REFERENCES authority(authority_enum)
);

CREATE TABLE note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255),
    tipo_nota VARCHAR(255),
    contenuto_testo TEXT,
    contenuto_svg TEXT,
    data_creazione TIMESTAMP,
    ultima_modifica TIMESTAMP,
    utente_id BIGINT NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES users(id)
);

CREATE TABLE eventi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titolo TEXT,
    descrizione TEXT,
    data_inizio TIMESTAMP,
    data_fine TIMESTAMP,
    ora_inizio TIMESTAMP,
    ora_fine TIMESTAMP,
    luogo TEXT,
    stato VARCHAR(255),
    utente_id BIGINT NOT NULL,
    FOREIGN KEY (utente_id) REFERENCES users(id)
);

CREATE TABLE password_reset_token (
    id UUID PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    data_creazione TIMESTAMP,
    data_scadenza TIMESTAMP,
    usato BOOLEAN,
    credenziali_id BIGINT,
    FOREIGN KEY (credenziali_id) REFERENCES credenziali(id)
);