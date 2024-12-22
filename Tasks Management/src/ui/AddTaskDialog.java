package ui;

import models.Category;
import models.Task;
import services.CategoryService;
import services.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;

public class AddTaskDialog extends JDialog {
    private final UIManager uiManager;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private final Category preSelectedCategory;


    public AddTaskDialog(UIManager uiManager, TaskService taskService,
                         CategoryService categoryService, Category preSelectedCategory) {
        super(uiManager.getParentFrame(), "Add Task", true);
        this.uiManager = uiManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.preSelectedCategory = preSelectedCategory;
        setupDialog();
    }

    public AddTaskDialog(UIManager uiManager, TaskService taskService, CategoryService categoryService) {
        this(uiManager, taskService, categoryService, null);
    }

    private void setupDialog() {
        setLayout(new BorderLayout());
        setSize(500, 450);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel form = createForm();
        add(form, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createForm() {
        JPanel form = new JPanel(null);
        form.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Task Description Field
        JLabel taskLabel = new JLabel("Task:");
        taskLabel.setFont(new Font("Inter", Font.BOLD, 14));
        taskLabel.setBounds(40, 30, 100, 25);
        JTextField taskField = new JTextField();
        taskField.setBounds(40, 60, 400, 35);
        taskField.setFont(new Font("Inter", Font.PLAIN, 14));

        // Category ComboBox
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Inter", Font.BOLD, 14));
        categoryLabel.setBounds(40, 110, 100, 25);
        JComboBox<Category> categoryCombo = new JComboBox<>(
                categoryService.getAllCategories().toArray(new Category[0])
        );

        // Set pre-selected category if available
        if (preSelectedCategory != null) {
            categoryCombo.setSelectedItem(preSelectedCategory);
            categoryCombo.setEnabled(false); // Lock the selection
        }

        categoryCombo.setBounds(40, 140, 400, 35);
        categoryCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getTitle());
                }
                return this;
            }
        });


        // Deadline Spinner
        JLabel deadlineLabel = new JLabel("Deadline:");
        deadlineLabel.setFont(new Font("Inter", Font.BOLD, 14));
        deadlineLabel.setBounds(40, 190, 100, 25);

        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(deadlineSpinner, "yyyy-MM-dd HH:mm");
        deadlineSpinner.setEditor(dateEditor);
        deadlineSpinner.setBounds(40, 220, 400, 35);
        deadlineSpinner.setFont(new Font("Inter", Font.PLAIN, 14));

        // Importance ComboBox
        JLabel importanceLabel = new JLabel("Importance:");
        importanceLabel.setFont(new Font("Inter", Font.BOLD, 14));
        importanceLabel.setBounds(40, 270, 100, 25);

        JComboBox<Task.ImportanceLevel> importanceCombo = new JComboBox<>(Task.ImportanceLevel.values());
        importanceCombo.setBounds(40, 300, 400, 35);
        importanceCombo.setFont(new Font("Inter", Font.PLAIN, 14));
        importanceCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Task.ImportanceLevel) {
                    Task.ImportanceLevel level = (Task.ImportanceLevel) value;
                    setText(level.getEmoji() + " " + level.getLabel());
                }
                return this;
            }
        });

        // Add Button
        JButton addButton = new JButton("Add Task");
        addButton.setBounds(190, 360, 100, 30);
        addButton.setFont(new Font("Inter", Font.BOLD, 14));
        addButton.setBackground(new Color(25, 118, 210));
        addButton.setForeground(Color.BLUE);
        addButton.addActionListener(e -> {
            String description = taskField.getText().trim();
            if (!description.isEmpty()) {
                Task newTask = new Task(
                        generateTaskId(),
                        description,
                        (Category) categoryCombo.getSelectedItem(),
                        (Date) deadlineSpinner.getValue(),
                        (Task.ImportanceLevel) importanceCombo.getSelectedItem()
                );
                taskService.addTask(newTask);
                uiManager.refreshScreens();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a task description",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add components to form
        form.add(taskLabel);
        form.add(taskField);
        form.add(categoryLabel);
        form.add(categoryCombo);
        form.add(deadlineLabel);
        form.add(deadlineSpinner);
        form.add(importanceLabel);
        form.add(importanceCombo);
        form.add(addButton);

        return form;
    }

    private int generateTaskId() {
        return taskService.getAllTasks().stream()
                .mapToInt(Task::getId)
                .max()
                .orElse(0) + 1;
    }
}
