package ui;

import models.Category;
import services.CategoryService;
import services.TaskService;

import javax.swing.*;
import java.awt.*;

public class UIManager {
    private final JFrame parentFrame;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    private Category selectedCategory;
    private HomeScreen homeScreen;
    private TaskScreen taskScreen;

    public static final String HOME_SCREEN = "HOME";
    public static final String TASK_SCREEN = "TASKS";

    public UIManager(JFrame parentFrame, TaskService taskService, CategoryService categoryService) {
        if (parentFrame == null || taskService == null || categoryService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }

        this.parentFrame = parentFrame;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.cardLayout = new CardLayout();
        this.mainPanel = new JPanel(cardLayout);

        initializeScreens();
    }

    private void initializeScreens() {
        homeScreen = new HomeScreen(this, taskService, categoryService);
        taskScreen = new TaskScreen(this, taskService, categoryService);  // Modified line
        mainPanel.add(homeScreen, HOME_SCREEN);
        mainPanel.add(taskScreen, TASK_SCREEN);
        // Set default category
        if (!categoryService.getAllCategories().isEmpty()) {
            selectedCategory = categoryService.getAllCategories().get(0);
        }
    }

    // Screen management
    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    public void refreshScreens() {
        homeScreen.refresh();
        taskScreen.refresh();
    }

    // Category management
    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.selectedCategory = category;
        taskScreen.refresh();
    }

    // Getters for dependencies
    public JFrame getParentFrame() {
        return parentFrame;
    }

    public TaskService getTaskService() {
        return taskService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // Error handling
    public void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                        parentFrame,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }

    // Confirmation dialogs
    public boolean confirmAction(String message) {
        return JOptionPane.showConfirmDialog(
                parentFrame,
                message,
                "Confirm Action",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }

    // Screen factory methods
    public void createAndShowAddTaskDialog() {
        SwingUtilities.invokeLater(() ->
                new AddTaskDialog(this, taskService, categoryService)
        );
    }

    public void navigateHome() {
        showScreen(HOME_SCREEN);
        refreshScreens();
    }

    public void navigateToTasks() {
        showScreen(TASK_SCREEN);
        refreshScreens();
    }
}