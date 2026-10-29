package com.wiem.studyboard.model;

import java.time.LocalDate;

/**
 * Une tâche de révision.
 * Le type est immuable : pour la modifier, on crée une nouvelle instance.
 */
public record Task(int id, String title, String category, LocalDate dueDate, boolean done) {

    /** Retourne une copie de la tâche avec l'état « terminé » inversé. */
    public Task toggled() {
        return new Task(id, title, category, dueDate, !done);
    }
}
