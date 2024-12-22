package models;

import java.awt.*;
import java.util.Date;

public class Task {
    private final int id;
    private String description;
    private Category category;
    private boolean completed;
    private Date deadline;
    private ImportanceLevel importance;

    public enum SortCriteria {
        NAME_ASC("Name ↑"),
        NAME_DESC("Name ↓"),
        DEADLINE_ASC("Deadline ↑"),
        DEADLINE_DESC("Deadline ↓"),
        IMPORTANCE_ASC("Importance ↑"),
        IMPORTANCE_DESC("Importance ↓");

        private final String label;

        SortCriteria(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }


    public enum ImportanceLevel {
        IMPORTANT_URGENT("Important & Urgent", "🔴", new Color(255, 59, 48)),
        IMPORTANT_NOT_URGENT("Important & Not Urgent", "🟡", new Color(255, 204, 0)),
        NOT_IMPORTANT_URGENT("Not Important & Urgent", "🟠", new Color(255, 149, 0)),
        NOT_IMPORTANT_NOT_URGENT("Not Important & Not Urgent", "🔵", new Color(0, 122, 255));

        private final String label;
        private final String emoji;
        private final Color color;

        ImportanceLevel(String label, String emoji, Color color) {
            this.label = label;
            this.emoji = emoji;
            this.color = color;
        }

        public String getLabel() { return label; }
        public String getEmoji() { return emoji; }
        public Color getColor() { return color; }
    }


    public Task(int id, String description, Category category, Date deadline, ImportanceLevel importance) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.importance = importance;
        this.completed = false;
    }


    // Getters and setters
    public int getId() { return id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public ImportanceLevel getImportance() { return importance; }
    public void setImportance(ImportanceLevel importance) { this.importance = importance; }
}
