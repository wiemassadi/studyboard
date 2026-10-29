package com.wiem.studyboard.service;

import com.wiem.studyboard.model.Task;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Logique métier : gère la liste des tâches en mémoire.
 * Les données sont perdues quand le conteneur redémarre (pas de base de données,
 * pour garder le déploiement simple).
 */
public class TaskService {

    public static final List<String> CATEGORIES = List.of("Cours", "TP", "Projet", "Examen");
    public static final int MAX_TITLE_LENGTH = 80;

    /** Tâches non terminées d'abord, puis par date d'échéance, puis par numéro. */
    private static final Comparator<Task> ORDER = Comparator
            .comparing(Task::done)
            .thenComparing(Task::dueDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(Task::id);

    private static final TaskService SHARED = withSampleData();

    private final List<Task> tasks = new ArrayList<>();
    private int nextId = 1;

    /** Instance partagée par toutes les pages de l'application. */
    public static TaskService shared() {
        return SHARED;
    }

    public static TaskService withSampleData() {
        TaskService service = new TaskService();
        LocalDate today = LocalDate.now();
        service.add("Réviser le chapitre 3 de Réseaux", "Cours", today.plusDays(2));
        service.add("Terminer le TP Docker", "TP", today.plusDays(4));
        service.add("Préparer la démo du projet CI/CD", "Projet", today.plusDays(7));
        return service;
    }

    public synchronized Task add(String title, String category, LocalDate dueDate) {
        String cleanTitle = title == null ? "" : title.trim();
        if (cleanTitle.isEmpty()) {
            throw new IllegalArgumentException("Le titre est obligatoire.");
        }
        if (cleanTitle.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Le titre ne doit pas dépasser " + MAX_TITLE_LENGTH + " caractères.");
        }
        if (!CATEGORIES.contains(category)) {
            throw new IllegalArgumentException("Catégorie inconnue.");
        }
        Task task = new Task(nextId++, cleanTitle, category, dueDate, false);
        tasks.add(task);
        return task;
    }

    /** Inverse l'état d'une tâche. Retourne false si l'identifiant n'existe pas. */
    public synchronized boolean toggle(int id) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id() == id) {
                tasks.set(i, tasks.get(i).toggled());
                return true;
            }
        }
        return false;
    }

    /** Supprime une tâche. Retourne false si l'identifiant n'existe pas. */
    public synchronized boolean delete(int id) {
        return tasks.removeIf(task -> task.id() == id);
    }

    public synchronized List<Task> all() {
        return tasks.stream().sorted(ORDER).toList();
    }

    /** Filtre par catégorie ; une catégorie nulle ou inconnue retourne toutes les tâches. */
    public synchronized List<Task> byCategory(String category) {
        if (category == null || !CATEGORIES.contains(category)) {
            return all();
        }
        return tasks.stream().filter(task -> task.category().equals(category)).sorted(ORDER).toList();
    }

    public synchronized Stats stats() {
        int done = (int) tasks.stream().filter(Task::done).count();
        return new Stats(tasks.size(), done);
    }

    public record Stats(int total, int done) {
        public int remaining() {
            return total - done;
        }

        public int percent() {
            return total == 0 ? 0 : Math.round(100f * done / total);
        }
    }
}
