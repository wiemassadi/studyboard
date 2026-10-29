package com.wiem.studyboard.web;

import com.wiem.studyboard.model.Task;
import com.wiem.studyboard.service.TaskService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Page d'accueil : progression globale et prochaines tâches. */
@WebServlet(urlPatterns = "/home")
public class HomeServlet extends HttpServlet {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        TaskService service = TaskService.shared();
        TaskService.Stats stats = service.stats();
        String ctx = req.getContextPath();

        StringBuilder body = new StringBuilder();
        body.append("<section class=\"overview\">\n")
                .append("<div class=\"ring\" style=\"--pct:").append(stats.percent())
                .append("\" role=\"img\" aria-label=\"").append(stats.percent())
                .append(" % des tâches terminées\"><span class=\"ring-value\">")
                .append(stats.percent()).append(" %</span></div>\n")
                .append("<div class=\"overview-text\">")
                .append("<p class=\"lead\">").append(stats.done()).append(" tâche(s) terminée(s) sur ")
                .append(stats.total()).append(".</p>")
                .append("<p>").append(stats.remaining()).append(" restante(s) à traiter.</p>")
                .append("<a class=\"button\" href=\"").append(Layout.escape(ctx))
                .append("/tasks\">Voir mes tâches</a></div>\n</section>\n");

        body.append("<h2>À faire en priorité</h2>\n");
        List<Task> pending = service.all().stream().filter(task -> !task.done()).limit(3).toList();
        if (pending.isEmpty()) {
            body.append("<p class=\"empty\">Tout est terminé. Ajoute une tâche dans « Mes tâches ».</p>\n");
        } else {
            body.append("<ul class=\"task-list\">\n");
            for (Task task : pending) {
                body.append("<li class=\"task\"><div class=\"task-main\"><span class=\"task-title\">")
                        .append(Layout.escape(task.title())).append("</span><span class=\"task-meta\">")
                        .append(Layout.escape(task.category()));
                if (task.dueDate() != null) {
                    body.append(", pour le ").append(DATE.format(task.dueDate()));
                }
                body.append("</span></div></li>\n");
            }
            body.append("</ul>\n");
        }

        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().write(Layout.page(ctx, "home", "Tableau de bord", body.toString()));
    }
}
