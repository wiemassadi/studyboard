# StudyBoard

Application web de suivi de révisions (Java, Servlets, Tomcat), livrée par un pipeline CI/CD complet :
**GitHub → Jenkins → Maven → Docker → Docker Hub → Ansible → Tomcat**.

## Fonctionnalités

- **Accueil** : anneau de progression et tâches prioritaires.
- **Mes tâches** : ajouter, terminer, supprimer, filtrer par catégorie (Cours, TP, Projet, Examen).
- **Déploiement** : version, numéro de build Jenkins et conteneur qui répond (preuve du déploiement automatique).
- **/health** : point de contrôle utilisé par le test de fumée du pipeline.

Les données sont gardées en mémoire (pas de base de données) : elles sont réinitialisées à chaque déploiement.

## Structure

```
studyboard/
├── pom.xml                      Build Maven (WAR, tests JUnit)
├── Dockerfile                   Image Tomcat + application
├── Jenkinsfile                  Pipeline : test, package, docker, push, deploy, smoke test
├── ansible/                     Configuration et déploiement du serveur de production
│   ├── ansible.cfg
│   ├── inventory.ini
│   └── deploy.yml
└── src/
    ├── main/java/com/wiem/studyboard/
    │   ├── model/Task.java            Données
    │   ├── service/TaskService.java   Logique métier
    │   ├── AppInfo.java               Version et build
    │   └── web/                       Pages (servlets) et gabarit HTML
    ├── main/resources/app.properties  Version injectée par Maven
    ├── main/webapp/                   CSS, web.xml, page d'entrée
    └── test/java/                     Tests unitaires
```

## Lancer en local (dans la VM `ci`)

```bash
mvn -B clean package                    # compile, teste, crée target/studyboard.war
docker build -t studyboard:local .
docker run -d -p 8081:8080 --name studyboard-local studyboard:local
# puis ouvrir http://IP_DE_CI:8081
docker rm -f studyboard-local           # pour arrêter
```

## Avant de lancer le pipeline : 3 valeurs à remplacer

| Fichier | Valeur | Par |
|---|---|---|
| `Jenkinsfile` | `TON_USER_DOCKERHUB` | ton identifiant Docker Hub |
| `Jenkinsfile` | `IP_DE_APP` | l'IP de la VM `app` |
| `ansible/inventory.ini` | `IP_DE_APP` | l'IP de la VM `app` |

## Envoyer le projet sur GitHub

```bash
git init
git add .
git commit -m "feat: projet StudyBoard et pipeline CI/CD"
git branch -M main
git remote add origin https://github.com/TON_USER_GITHUB/studyboard.git
git push -u origin main
```

## Modifier l'application

1. Modifie le code (par exemple le titre de la page d'accueil dans `HomeServlet.java`).
2. `git add .`, `git commit -m "feat: ..."`, `git push`.
3. Jenkins détecte le commit (environ 2 minutes), exécute le pipeline et redéploie.
4. Recharge la page « Déploiement » : le numéro de build a changé.

## Tests

`mvn test` lance les tests de `TaskService`, du gabarit HTML et des informations de version.
