# Projet de Gestion RH – JEE & Spring Boot

Auteurs : Owen Paimba-Sail, Glenn-Airton Diffo, Mohamed ElGhali Sadiqi, Christelle Millet
Année : 2025 – 2026
Encadrant : Mohamed Haddache
Matière : JEE

## Présentation du projet

Ce projet propose une application web complète de gestion des ressources humaines. Elle permet d’administrer les employés, les départements, les projets et les fiches de paie, tout en intégrant un système d'authentification basé sur les rôles (Admin, Chef de département, Chef de projet, Employé).
Le projet existe en deux versions :

* Version **JEE** : Servlets, JSP, JSTL, DAO Hibernate.
* Version **Spring Boot** : Controllers Spring, Thymeleaf, Spring Data JPA.

## Fonctionnalités principales

### Authentification & rôles

Connexion via identifiant unique et mot de passe.
Rôles disponibles : Admin, Chef de département, Chef de projet, Employé.

### Gestion des employés

Création, modification, suppression, recherche avancée.
Attribution automatique identifiant + mot de passe.
Consultation projets + fiches de paie.

### Gestion des départements

Ajout, modification, suppression.
Chef de département assigné.
Liste des employés.

### Gestion des projets

Création, modification, suppression.
Chef de projet assigné.
Ajout/retrait d’employés aux projets.

### Gestion des fiches de paie

Création, modification, suppression.
Consultation des fiches existantes.

### Profil utilisateur

Affichage des informations personnelles et changement de mot de passe.

## Arborescence du projet (JEE)

```
src/
 ├── main/java/com/example/
 │    ├── controller/
 │    ├── dao/
 │    ├── model/
 │    └── utils/
 └── main/webapp/
       ├── jsp/
       ├── css/
       └── index.jsp
```

## Arborescence du projet (Spring Boot)

```
src/
 ├── main/java/com/spring/
 │    ├── controller/
 │    ├── repository/
 │    ├── service/
 │    └── model/
 └── main/resources/
       ├── templates/
       └── application.properties
```

## Base de données

SGBD : MySQL
Mapping via JPA/Hibernate
DDL contrôlé : `spring.jpa.hibernate.ddl-auto=none`

## Lancer le projet

### Version JEE

1. Importer le projet.
2. Configurer Tomcat.
3. Configurer vos identifiants MySQL dans `hibernate.cfg.xml`.
4. Lancer le script MySQL dans `schema.sql` pour importer les données.
5. Accéder à :
   `http://localhost:8080/ProjetRH`

### Version Spring Boot

1. Importer le projet Maven.
2. Configurer vos identifiants MySQL dans `application.properties`.
3. Lancer le script MySQL dans `schema.sql` pour importer les données.
4. Lancer `Application.java`.
5. Accéder à :
   `http://localhost:8085/`

## Technologies utilisées

### JEE

Jakarta Servlet, JSP, JSTL, Hibernate, MySQL, Tomcat, Bootstrap

### Spring Boot

Spring Web, Spring Data JPA, Thymeleaf, Lombok, Spring Mail, MySQL, Bootstrap

## Répartition des tâches

Owen : Authentification, profil, gestion employés/projets/paie (JEE + Spring)
Glenn : Gestion projets et départements, intégration DAO et Spring
Ghali : Rapport, organisation MVC, recherche employé
Christelle : Interfaces JSP/Thymeleaf, navigation, tests, cohérence visuelle

## Conclusion

Ce projet nous a permis de maîtriser les concepts JEE et d’étendre nos compétences vers Spring Boot. L’intégration d’une architecture MVC claire, de rôles utilisateurs et de modules RH complets nous a permis de produire une application structurée, fonctionnelle et proche d’un outil professionnel.
