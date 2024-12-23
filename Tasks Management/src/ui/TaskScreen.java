package ui;

import models.Category;
import models.Task;
import models.Task.SortCriteria;
import services.CategoryService;
import services.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class TaskScreen extends JPanel {
    private final UIManager uiManager;
    private final CategoryService categoryService;
    private final TaskService taskService;
    private final TaskListPanel taskListPanel;
    private SortCriteria currentSortCriteria = SortCriteria.DEADLINE_ASC;

    public TaskScreen(UIManager uiManager, TaskService taskService, CategoryService categoryService) {
        this.uiManager = uiManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        // Header
        JPanel header = createHeader();
        // Task List
        taskListPanel = new TaskListPanel(taskService, uiManager);
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);

        // Bottom Panel with Add Task button
        JPanel bottomPanel = createBottomPanel();

        // Add components
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Add Task button in a right-aligned panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.setFont(new Font("Inter", Font.BOLD, 14));
        addTaskButton.setBackground(new Color(25, 118, 210));
        addTaskButton.setForeground(Color.BLUE);
        addTaskButton.setPreferredSize(new Dimension(120, 40));
        addTaskButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(25, 118, 210), 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));
        Category selectedCategory = uiManager.getSelectedCategory();
        addTaskButton.addActionListener(e -> new AddTaskDialog(uiManager, taskService, categoryService, selectedCategory));

        buttonPanel.add(addTaskButton);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);

        return bottomPanel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.BLUE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Left panel with back button and category title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Search button
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Inter", Font.BOLD, 14));
        searchButton.setBackground(new Color(25, 118, 210));
        searchButton.setForeground(Color.BLUE);
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(25, 118, 210), 1, true),
                new EmptyBorder(5, 15, 5, 15)
        ));

        // Add action listener for search
        searchButton.addActionListener(e -> new SearchDialog(uiManager, taskService, categoryService));

        // Back button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 40));
        backButton.setForeground(Color.WHITE);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> uiManager.showScreen("HOME"));
        leftPanel.add(backButton);

        // Category title
        Category selectedCategory = uiManager.getSelectedCategory();
        String categoryTitle = selectedCategory != null ? selectedCategory.getTitle() : " ";
        JLabel categoryLabel = new JLabel(categoryTitle);
        categoryLabel.setForeground(Color.WHITE);
        categoryLabel.setFont(new Font("Inter", Font.BOLD, 28));
        leftPanel.add(categoryLabel);

        // Right panel for controls
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Sort controls
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setForeground(Color.WHITE);
        sortLabel.setFont(new Font("Inter", Font.PLAIN, 14));

        JComboBox<SortCriteria> sortCombo = new JComboBox<>(SortCriteria.values());
        sortCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        sortCombo.setPreferredSize(new Dimension(120, 30));
        sortCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SortCriteria) {
                    setText(((SortCriteria) value).getLabel());
                }
                return this;
            }
        });
        sortCombo.setSelectedItem(currentSortCriteria);
        sortCombo.addActionListener(e -> {
            currentSortCriteria = (SortCriteria) sortCombo.getSelectedItem();
            refresh();
        });

        rightPanel.add(sortLabel);
        rightPanel.add(sortCombo);
        rightPanel.add(Box.createHorizontalStrut(15));
        rightPanel.add(searchButton);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    public void refresh() {
        Category selectedCategory = uiManager.getSelectedCategory();
        List<Task> sortedTasks = taskService.getTasksByCategorySorted(
                selectedCategory,
                currentSortCriteria
        );
        taskListPanel.updateTasks(sortedTasks);  // Remove the cast
    }
    public TaskListPanel getTaskListPanel() {
        return taskListPanel;
    }


}
