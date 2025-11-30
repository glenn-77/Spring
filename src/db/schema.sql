CREATE DATABASE IF NOT EXISTS rh_spring;
USE rh_spring;


CREATE TABLE IF NOT EXISTS departement(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom     VARCHAR(100) NOT NULL,
    chef_id BIGINT
    );


CREATE TABLE IF NOT EXISTS employe (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricule VARCHAR(50) UNIQUE NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    telephone VARCHAR(30),
    adresse VARCHAR(255) NOT NULL,
    salaireBase DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    grade ENUM('JUNIOR', 'SENIOR', 'STAGIAIRE', 'INTERMEDIAIRE', 'DIRECTEUR', 'MANAGER') DEFAULT 'JUNIOR',
    poste VARCHAR(100) NOT NULL,
    dateEmbauche DATE NOT NULL,
    departement_id BIGINT,
    CONSTRAINT fk_employee_department FOREIGN KEY (departement_id)
    REFERENCES departement(id)
    ON DELETE SET NULL
    );

CREATE INDEX idx_employee_email ON employe(email);
CREATE INDEX idx_employee_department ON employe(departement_id);

ALTER TABLE departement
    ADD CONSTRAINT fk_department_manager FOREIGN KEY (chef_id)
        REFERENCES employe(id)
        ON DELETE SET NULL;


CREATE TABLE IF NOT EXISTS projet (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    manager_id BIGINT,
    departement_id BIGINT,
    description TEXT,
    budget DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    dateDebut DATE,
    dateFin DATE,
    etat ENUM('EN_COURS','TERMINE','ANNULE') DEFAULT 'EN_COURS',
    CONSTRAINT fk_project_manager FOREIGN KEY (manager_id)
    REFERENCES employe(id)
    ON DELETE SET NULL,
    CONSTRAINT fk_projet_departement FOREIGN KEY (departement_id) REFERENCES departement(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
    );

CREATE TABLE IF NOT EXISTS employe_projet (
    employe_id BIGINT,
    projet_id BIGINT,
    PRIMARY KEY (employe_id, projet_id),
    CONSTRAINT fk_emp_proj_employee FOREIGN KEY (employe_id)
    REFERENCES employe(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_emp_proj_project FOREIGN KEY (projet_id)
    REFERENCES projet(id)
    ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS fiche_de_paie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employe_id BIGINT,
    annee INT NOT NULL,
    mois INT NOT NULL,
    salaireBase DECIMAL(10,2),
    prime DECIMAL(10,2),
    deduction DECIMAL(10,2),
    netAPayer DECIMAL(10,2),
    date_generation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payslip_employee FOREIGN KEY (employe_id)
    REFERENCES employe(id)
    ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nomRole ENUM('ADMINISTRATEUR', 'CHEF_DE_DEPARTEMENT','CHEF_DE_PROJET','EMPLOYE') NOT NULL
    );

CREATE TABLE IF NOT EXISTS utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) UNIQUE NOT NULL,
    motDePasse VARCHAR(255) NOT NULL,
    employe_id BIGINT,
    role_id BIGINT,
    CONSTRAINT fk_user_employee FOREIGN KEY (employe_id)
    REFERENCES employe(id)
    ON DELETE SET NULL,
    FOREIGN KEY (role_id) REFERENCES role(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
    );

INSERT INTO role (nomRole)
VALUES ('ADMINISTRATEUR'),
       ('CHEF_DE_DEPARTEMENT'),
       ('CHEF_DE_PROJET'),
       ('EMPLOYE');

INSERT INTO departement (nom) VALUES ('Informatique'), ('Finance'), ('Journalisme'), ('Administration');

insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP3536', 'Agna', 'Rallinshaw', 'arallinshaw0@cam.ac.uk', '702-447-8112', '525 Jana Pass', 1645.06, 'Desktop Support Technician', '2025-04-19', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP9523', 'Vernice', 'Campbell', 'vcampbell1@un.org', '686-253-4396', '97246 Loftsgordon Avenue', 3093.38, 'Community Outreach Specialist', '2025-11-24', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP6385', 'Arney', 'Iddiens', 'aiddiens2@statcounter.com', '546-960-3192', '8183 Mandrake Point', 7350.49, 'Social Worker', '2025-05-12', 3);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP7150', 'Nisse', 'Bigland', 'nbigland3@unesco.org', '775-699-9752', '749 Claremont Trail', 7030.19, 'Mechanical Systems Engineer', '2025-09-09', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP1279', 'Pace', 'Matuszak', 'pmatuszak4@exblog.jp', '510-950-7298', '27 Cordelia Junction', 5485.85, 'Database Administrator III', '2025-08-26', 4);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP7035', 'Joan', 'Pickthorne', 'jpickthorne5@1und1.de', '972-202-3748', '6183 Morning Street', 1353.69, 'Account Executive', '2025-03-04', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP9796', 'Elaina', 'Trulocke', 'etrulocke6@netlog.com', '612-163-7205', '42971 Vernon Avenue', 1818.23, 'Compensation Analyst', '2025-05-31', 3);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP4916', 'Austin', 'Inkster', 'ainkster7@macromedia.com', '330-890-0193', '7708 American Terrace', 9065.72, 'Senior Developer', '2025-05-05', 4);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP0222', 'Minna', 'Clapison', 'mclapison8@utexas.edu', '296-315-2636', '08420 Oak Court', 9081.92, 'Human Resources Manager', '2025-01-22', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP9709', 'Moreen', 'Tice', 'mtice9@skype.com', '391-681-7146', '8 Roth Parkway', 3497.09, 'Cost Accountant', '2025-04-07', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP6899', 'Rodrigo', 'Garbutt', 'rgarbutt0@arizona.edu', '839-754-6471', '43402 Eagan Court', 3793.03, 'Software Consultant', '2025-05-19', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP2149', 'Dwain', 'Degoy', 'ddegoy1@123-reg.co.uk', '474-750-3184', '38 Pearson Junction', 3672.33, 'Engineer II', '2025-01-29', 3);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP5993', 'Raddie', 'Hawtin', 'rhawtin2@bizjournals.com', '223-416-4149', '72325 Commercial Circle', 6388.27, 'Programmer I', '2025-08-19', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP1916', 'Nickolas', 'Kelwaybamber', 'nkelwaybamber3@t-online.de', '397-537-2255', '44623 Tennyson Hill', 2514.88, 'Senior Editor', '2025-09-23', 4);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP8810', 'Rollins', 'Slaten', 'rslaten4@dyndns.org', '793-847-5008', '6 Rutledge Street', 2925.85, 'Compensation Analyst', '2025-08-26', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP5465', 'Gussie', 'Menhenitt', 'gmenhenitt5@ucsd.edu', '636-281-5072', '135 Nancy Junction', 2989.56, 'Staff Accountant I', '2025-09-16', 1);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP9961', 'Harlin', 'Edson', 'hedson6@cyberchimps.com', '687-769-5511', '405 Sloan Court', 9439.07, 'Legal Assistant', '2025-03-13', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP8721', 'Leupold', 'Ibberson', 'libberson7@twitpic.com', '316-607-7103', '7 Namekagon Court', 8997.5, 'Account Coordinator', '2025-06-21', 3);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP1772', 'Johann', 'Tadlow', 'jtadlow8@creativecommons.org', '651-403-5762', '70 Ramsey Place', 1499.74, 'Environmental Specialist', '2025-07-19', 2);
insert into employe (matricule, prenom, nom, email, telephone, adresse, salaireBase, poste, dateEmbauche, departement_id) values ('EMP3373', 'Max', 'Tallet', 'mtallet9@huffingtonpost.com', '529-867-4852', '9 Mallard Avenue', 5992.9, 'Technical Writer', '2025-02-18', 3);

insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Arallinshaw', 'gX8+@6XdLV7', 1, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Vcampbell', 'jT6''LKl1Qx', 2, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Addiens', 'bM1{B!/XgB', 3, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Nbigland', 'dA0|(+0DpNS96gE', 4, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('admin1', 'pI8<aBV?qQlPMza(', 5, 1);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Jpickthorne', 'kD6@my.IywW', 6, 3);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Etrulocke', 'wV4`{9R.B!(J', 7, 3);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('admin2', 'gF2>y34n|ZTb>', 8, 1);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Ainkster', 'xX7+|jp+', 9, 3);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Mclapison', 'gF6<zs7=Zf', 10, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Mtice', 'vN4!/ckj', 11, 3);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Rgarbutt', 'nV9%{''sL5px&''%b', 12, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Ddegoy', 'wO3+&<5/zgUdBAQH', 13, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('admin3', 'iL7+?GDR&', 14, 1);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Nkelwaybamber', 'gQ6%2"wKx,sc7.wu', 15, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Rslaten', 'wT8}ve~P', 16, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Gmenhenitt', 'yH1>z/5zs,A$', 17, 2);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Hedson', 'hH7&"1fCiZ''u', 18, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Jtadlow', 'uV5''7KKI~Mz', 19, 4);
insert into utilisateur (login, motDePasse, employe_id, role_id) values ('Mtallet', 'oO8"Y9Sc)', 20, 2);

update departement set chef_id = 1 where nom = 'Informatique';
update departement set chef_id = 2 where nom = 'Finance';
update departement set chef_id = 3 where nom = 'Journalisme';
update departement set chef_id = 5 where nom = 'Administration';

insert into projet (nom, manager_id, departement_id, description, budget, dateDebut, dateFin) values ('At Home Group Inc.', 7, 3, 'Proin eu mi. Nulla ac enim. In tempor, turpis nec euismod scelerisque, quam turpis adipiscing lorem, vitae mattis nibh ligula nec sem.

Duis aliquam convallis nunc. Proin at turpis a pede posuere nonummy. Integer non velit.

Donec diam neque, vestibulum eget, vulputate ut, ultrices vel, augue. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec pharetra, magna vestibulum aliquet ultrices, erat tortor sollicitudin mi, sit amet lobortis sapien sapien non mi. Integer ac neque.', 2101, '2025-09-23', '2025-11-29');
insert into projet (nom, manager_id, departement_id, description, budget, dateDebut, dateFin) values ('PCSB Financial Corporation', 6, 1, 'Cras non velit nec nisi vulputate nonummy. Maecenas tincidunt lacus at velit. Vivamus vel nulla eget eros elementum pellentesque.

Quisque porta volutpat erat. Quisque erat eros, viverra eget, congue eget, semper rutrum, nulla. Nunc purus.

Phasellus in felis. Donec semper sapien a libero. Nam dui.', 2885, '2025-04-05', '2025-11-29');
insert into projet (nom, manager_id, departement_id, description, budget, dateDebut, dateFin) values ('Digirad Corporation', 9, 2, 'Suspendisse potenti. In eleifend quam a odio. In hac habitasse platea dictumst.

Maecenas ut massa quis augue luctus tincidunt. Nulla mollis molestie lorem. Quisque ut erat.

Curabitur gravida nisi at nibh. In hac habitasse platea dictumst. Aliquam augue quam, sollicitudin vitae, consectetuer eget, rutrum at, lorem.', 3044, '2025-08-21', '2025-11-29');
insert into projet (nom, manager_id, departement_id, description, budget, dateDebut, dateFin) values ('ClearBridge Dividend Strategy ESG ETF', 9, 2, 'Maecenas tristique, est et tempus semper, est quam pharetra magna, ac consequat metus sapien ut nunc. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Mauris viverra diam vitae quam. Suspendisse potenti.', 4032, '2025-05-26', '2025-11-29');
insert into projet (nom, manager_id, departement_id, description, budget, dateDebut, dateFin) values ('Dynagas LNG Partners LP', 11, 1, 'Pellentesque at nulla. Suspendisse potenti. Cras in purus eu magna vulputate luctus.', 3660, '2025-01-24', '2025-11-29');
