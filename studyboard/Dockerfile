# Image de base : Tomcat 10.1 avec Java 17 (Tomcat 10 utilise l'API « jakarta »)
FROM tomcat:10.1-jdk17

# On retire l'application d'exemple de Tomcat pour servir la nôtre à la racine (/)
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# On copie le WAR construit par Maven ; ROOT.war = application par défaut
COPY target/studyboard.war /usr/local/tomcat/webapps/ROOT.war

# Documentation : l'application écoute sur le port 8080
EXPOSE 8080
