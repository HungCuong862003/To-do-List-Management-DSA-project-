package models;

public class Category {
    private final int id;
    private String title;

    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}