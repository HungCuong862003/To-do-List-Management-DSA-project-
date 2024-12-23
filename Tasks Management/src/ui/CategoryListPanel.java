package ui;

import models.Category;
import services.CategoryService;
import services.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.List;

public class CategoryListPanel extends JPanel {
    private final UIManager uiManager;
    private final TaskService taskService;
    private final CategoryService categoryService;

    public CategoryListPanel(UIManager uiManager, TaskService taskService, CategoryService categoryService) {
        this.uiManager = uiManager;
        this.taskService = taskService;
        this.categoryService = categoryService;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 0, 10, 0));

        refresh();
    }

    public void refresh() {
        removeAll();
        List<Category> categories = categoryService.getAllCategories();

        for (Category category : categories) {
            add(createCategoryBox(category));
            add(Box.createRigidArea(new Dimension(0, 2)));
        }

        revalidate();
        repaint();
    }

    private JPanel createCategoryBox(Category category) {
        JPanel categoryBox = new JPanel(new BorderLayout());
        categoryBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        categoryBox.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)
        ));
        categoryBox.setBackground(Color.WHITE);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(category.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));

        long taskCount = taskService.getTasksByCategory(category).size();

        JLabel countLabel = new JLabel(taskCount + " Tasks");
        countLabel.setForeground(Color.GRAY);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(countLabel, BorderLayout.CENTER);

        categoryBox.add(textPanel, BorderLayout.CENTER);

        addCategoryBoxListeners(categoryBox, category);

        return categoryBox;
    }

    private void addCategoryBoxListeners(JPanel categoryBox, Category category) {
        categoryBox.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                uiManager.setSelectedCategory(category);
                uiManager.showScreen("TASKS");
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                categoryBox.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                categoryBox.setBackground(Color.WHITE);
            }
        });
    }
}
