package com.wiem.studyboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.wiem.studyboard.web.Layout;
import org.junit.jupiter.api.Test;

class LayoutTest {

    @Test
    void escapeNeutralisesHtml() {
        assertEquals("&lt;script&gt;alert(&#39;x&#39;)&lt;/script&gt;",
                Layout.escape("<script>alert('x')</script>"));
        assertEquals("a &amp; b &quot;c&quot;", Layout.escape("a & b \"c\""));
        assertEquals("", Layout.escape(null));
    }

    @Test
    void pageMarksActiveNavigationItem() {
        String html = Layout.page("", "tasks", "Mes tâches", "<p>contenu</p>");

        assertTrue(html.contains("href=\"/tasks\" class=\"active\" aria-current=\"page\""));
        assertFalse(html.contains("href=\"/home\" class=\"active\""));
        assertTrue(html.contains("<p>contenu</p>"));
        assertTrue(html.contains("<h1>Mes tâches</h1>"));
    }

    @Test
    void pageEscapesTitle() {
        String html = Layout.page("", "home", "<b>Titre</b>", "");
        assertFalse(html.contains("<h1><b>"));
        assertTrue(html.contains("&lt;b&gt;Titre&lt;/b&gt;"));
    }
}
