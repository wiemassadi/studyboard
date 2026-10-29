package com.wiem.studyboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wiem.studyboard.model.Task;
import com.wiem.studyboard.service.TaskService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaskServiceTest {

    @Test
    void addTrimsTitleAndAssignsIncreasingIds() {
        TaskService service = new TaskService();
        Task first = service.add("  Réviser Docker  ", "TP", null);
        Task second = service.add("Lire le cours", "Cours", LocalDate.of(2027, 1, 10));

        assertEquals("Réviser Docker", first.title());
        assertEquals(1, first.id());
        assertEquals(2, second.id());
        assertFalse(first.done());
    }

    @Test
    void addRejectsBlankTitle() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.add("   ", "Cours", null));
        assertThrows(IllegalArgumentException.class, () -> service.add(null, "Cours", null));
    }

    @Test
    void addRejectsTooLongTitle() {
        TaskService service = new TaskService();
        String tooLong = "x".repeat(TaskService.MAX_TITLE_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> service.add(tooLong, "Cours", null));
    }

    @Test
    void addRejectsUnknownCategory() {
        TaskService service = new TaskService();
        assertThrows(IllegalArgumentException.class, () -> service.add("Tâche", "Inconnue", null));
    }

    @Test
    void toggleFlipsStateAndReportsUnknownId() {
        TaskService service = new TaskService();
        Task task = service.add("Tâche", "Cours", null);

        assertTrue(service.toggle(task.id()));
        assertTrue(service.all().get(0).done());
        assertTrue(service.toggle(task.id()));
        assertFalse(service.all().get(0).done());
        assertFalse(service.toggle(999));
    }

    @Test
    void deleteRemovesTask() {
        TaskService service = new TaskService();
        Task task = service.add("Tâche", "Cours", null);

        assertTrue(service.delete(task.id()));
        assertTrue(service.all().isEmpty());
        assertFalse(service.delete(task.id()));
    }

    @Test
    void statsComputesPercentage() {
        TaskService service = new TaskService();
        assertEquals(0, service.stats().percent());

        service.add("A", "Cours", null);
        service.add("B", "Cours", null);
        service.add("C", "TP", null);
        Task last = service.add("D", "Projet", null);
        service.toggle(last.id());

        TaskService.Stats stats = service.stats();
        assertEquals(4, stats.total());
        assertEquals(1, stats.done());
        assertEquals(3, stats.remaining());
        assertEquals(25, stats.percent());
    }

    @Test
    void allListsPendingTasksBeforeDoneOnesAndSortsByDueDate() {
        TaskService service = new TaskService();
        Task late = service.add("Tard", "Cours", LocalDate.of(2027, 3, 1));
        Task soon = service.add("Bientôt", "Cours", LocalDate.of(2027, 1, 1));
        Task finished = service.add("Fini", "Cours", LocalDate.of(2026, 12, 1));
        service.toggle(finished.id());

        List<Task> ordered = service.all();
        assertEquals(soon.id(), ordered.get(0).id());
        assertEquals(late.id(), ordered.get(1).id());
        assertEquals(finished.id(), ordered.get(2).id());
    }

    @Test
    void byCategoryFiltersAndFallsBackToAll() {
        TaskService service = new TaskService();
        service.add("Cours 1", "Cours", null);
        service.add("TP 1", "TP", null);

        assertEquals(1, service.byCategory("TP").size());
        assertEquals(2, service.byCategory(null).size());
        assertEquals(2, service.byCategory("Inconnue").size());
    }
}
