package ui;

import services.CategoryService;
import services.CategoryServiceImpl;
import services.TaskService;
import services.TaskServiceImpl;

import javax.swing.*;

public class TodoApp extends JFrame {
    private final CategoryService categoryService;
    private final TaskService taskService;
    private final UIManager uiManager;

    public TodoApp() {
        // Create services in the correct order
        this.categoryService = new CategoryServiceImpl();
        this.taskService = new TaskServiceImpl(categoryService);  // Pass categoryService
        this.uiManager = new UIManager(this, taskService, categoryService);

        initializeFrame();
    }

    private void initializeFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 800);
        setLocationRelativeTo(null);
        setContentPane(uiManager.getMainPanel());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Use fully qualified name for Swing's UIManager
                javax.swing.UIManager.setLookAndFeel(
                        javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            new TodoApp().setVisible(true);
        });
    }
}