
CREATE TABLE Auto
(
    spz CHAR(8),
    barva INT UNSIGNED NOT NULL,
    vyrobce VARCHAR(30) NOT NULL,
    typ VARCHAR(30) NOT NULL,
    
    PRIMARY KEY (spz)
);

CREATE TABLE Ridic
(
	crp CHAR(8),
    jmeno VARCHAR(100) NOT NULL,
    
    PRIMARY KEY (crp)
);

CREATE TABLE Brana
(
	id CHAR(10),
	longtitude FLOAT NOT NULL,
    latitude FLOAT NOT NULL,
    cena FLOAT NOT NULL,
    typ VARCHAR(10) NOT NULL,
    
    PRIMARY KEY (id)
);

CREATE TABLE Projeti
(
	id_projeti INT AUTO_INCREMENT,
	cas TIMESTAMP NOT NULL,
    najeto INT UNSIGNED NOT NULL,
    benzin INT UNSIGNED NOT NULL,
    napeti FLOAT NOT NULL,
    crp_ridic CHAR(8),
    id_brana CHAR(10),
    spz_auto CHAR(8),
    
    PRIMARY KEY (id_projeti,crp_ridic)
);


ALTER TABLE Projeti ADD CONSTRAINT fk_crp_ridic FOREIGN KEY (crp_ridic) REFERENCES Ridic (crp);
ALTER TABLE Projeti ADD CONSTRAINT fk_spz_auto FOREIGN KEY (spz_auto) REFERENCES Auto (spz);
ALTER TABLE Projeti ADD CONSTRAINT fk_id_brana FOREIGN KEY (id_brana) REFERENCES Brana (id);

CREATE INDEX najeto_index ON Projeti (najeto);
CREATE INDEX cas_index ON Projeti (cas);