package com.wiem.studyboard.web;

import com.wiem.studyboard.AppInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Page « Déploiement » : prouve quelle version tourne et où.
 * Après un git push, le numéro de build et le nom du conteneur changent.
 */
@WebServlet(urlPatterns = "/about")
public class AboutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String build = AppInfo.build();
        String buildLabel = "local".equals(build) ? "local (compilé hors Jenkins)" : "n° " + build + " (Jenkins)";

        StringBuilder body = new StringBuilder();
        body.append("<p class=\"lead\">Cette page indique quelle version de l'application répond. ")
                .append("Après chaque <code>git push</code>, le pipeline en construit une nouvelle : ")
                .append("le numéro de build et le conteneur changent.</p>\n")
                .append("<table class=\"info\">\n")
                .append(row("Version", AppInfo.version()))
                .append(row("Build", buildLabel))
                .append(row("Compilé le", AppInfo.builtAt()))
                .append(row("Conteneur (hôte)", AppInfo.hostname()))
                .append(row("Java", System.getProperty("java.version")))
                .append(row("Heure du serveur",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))))
                .append("</table>\n");

        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(Layout.page(req.getContextPath(), "about", "Déploiement", body.toString()));
    }

    private static String row(String label, String value) {
        return "<tr><th scope=\"row\">" + Layout.escape(label) + "</th><td>"
                + Layout.escape(value) + "</td></tr>\n";
    }
}
