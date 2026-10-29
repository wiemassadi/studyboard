package com.wiem.studyboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Informations de version lues dans app.properties.
 * Maven remplace les ${...} à la compilation ; Jenkins fournit le numéro de build.
 */
public final class AppInfo {

    private static final Properties PROPS = load();

    private AppInfo() {
    }

    public static String version() {
        return PROPS.getProperty("app.version", "inconnue");
    }

    public static String build() {
        return PROPS.getProperty("app.build", "local");
    }

    public static String builtAt() {
        return PROPS.getProperty("app.built", "inconnue");
    }

    /** Nom de la machine ; dans Docker, c'est l'identifiant du conteneur. */
    public static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            return "inconnu";
        }
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = AppInfo.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            // On garde les valeurs par défaut : la page doit s'afficher quand même.
        }
        return props;
    }
}
