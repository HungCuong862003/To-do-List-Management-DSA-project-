package ui;

import models.Category;
import models.Task;
import services.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class TaskListPanel extends JPanel {
    private final TaskService taskService;
    private final UIManager uiManager;
    private final SimpleDateFormat dateFormat;

    public TaskListPanel(TaskService taskService, UIManager uiManager) {
        this.taskService = taskService;
        this.uiManager = uiManager;
        this.dateFormat = new SimpleDateFormat("MMM dd, HH:mm");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
    }

    public void updateTasks(List<Task> tasks) {
        removeAll();
        add(createHeaderPanel());

        for (Task task : tasks) {
            add(createTaskRow(task));
            add(Box.createRigidArea(new Dimension(0, 2)));
        }

        revalidate();
        repaint();
    }

    public void refresh(Category selectedCategory) {
        removeAll();

        // Add header
        add(createHeaderPanel());

        // Add tasks
        List<Task> tasks = taskService.getTasksByCategory(selectedCategory);
        for (Task task : tasks) {
            add(createTaskRow(task));
            add(Box.createRigidArea(new Dimension(0, 2)));
        }

        revalidate();
        repaint();
    }

    private void updateRowBackground(JPanel row, boolean isCompleted) {
        if (isCompleted) {
            row.setBackground(new Color(220, 255, 220)); // Light green for completed
        } else {
            row.setBackground(new Color(255, 220, 220)); // Light red for incomplete
        }
    }

    private JPanel createDeleteButton(JPanel parentRow, Task task) {
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deletePanel.setOpaque(false);

        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 20));
        deleteButton.setForeground(Color.RED);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    SwingUtilities.getWindowAncestor(parentRow),
                    "Are you sure you want to delete this task?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                taskService.deleteTask(task.getId());
                uiManager.refreshScreens();
            }
        });

        deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                deleteButton.setForeground(new Color(200, 0, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                deleteButton.setForeground(Color.RED);
            }
        });

        deletePanel.add(deleteButton);
        return deletePanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, Color.DARK_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        headerPanel.setBackground(new Color(245, 245, 245));

        Font headerFont = new Font("Inter", Font.BOLD, 14);

        // Column headers
        JLabel[] headers = {
                new JLabel("TASK"),
                new JLabel("DEADLINE"),
                new JLabel("IMPORTANCE"),
                new JLabel("STATUS"),
                new JLabel("") // Empty header for delete button column
        };

        for (JLabel header : headers) {
            header.setFont(headerFont);
            headerPanel.add(header);
        }

        return headerPanel;
    }

    private JPanel createTaskRow(Task task) {
        JPanel taskRow = new JPanel(new GridLayout(1, 5, 10, 0));
        taskRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        taskRow.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Set initial background color based on completion status
        updateRowBackground(taskRow, task.isCompleted());

        // Task description
        JLabel taskLabel = new JLabel(task.getDescription());
        taskLabel.setFont(new Font("Inter", Font.BOLD, 14));

        // Deadline
        JLabel deadlineLabel = new JLabel(dateFormat.format(task.getDeadline()));
        deadlineLabel.setFont(new Font("Inter", Font.PLAIN, 14));

        // Importance
        JPanel importancePanel = createImportancePanel(task);

        // Status
        JPanel statusPanel = createStatusPanel(task, taskLabel, taskRow);

        // Delete button
        JPanel deletePanel = createDeleteButton(taskRow, task);

        // Add components
        taskRow.add(taskLabel);
        taskRow.add(deadlineLabel);
        taskRow.add(importancePanel);
        taskRow.add(statusPanel);
        taskRow.add(deletePanel);

        return taskRow;
    }

    private JPanel createImportancePanel(Task task) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);

        Task.ImportanceLevel importance = task.getImportance();
        JLabel importanceLabel = new JLabel(importance.getLabel());
        importanceLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        importanceLabel.setForeground(importance.getColor());

        panel.add(importanceLabel);
        return panel;
    }

    private JPanel createStatusPanel(Task task, JLabel taskLabel, JPanel taskRow) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);

        JCheckBox statusCheckbox = new JCheckBox();
        statusCheckbox.setSelected(task.isCompleted());

        JLabel statusLabel = new JLabel(task.isCompleted() ? "Complete" : "Pending");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 14));

        statusCheckbox.addActionListener(e -> {
            task.setCompleted(statusCheckbox.isSelected());
            statusLabel.setText(task.isCompleted() ? "Complete" : "Pending");
            taskLabel.setForeground(task.isCompleted() ? Color.GRAY : Color.BLACK);
            updateRowBackground(taskRow, task.isCompleted());
            taskService.updateTask(task);
        });

        panel.add(statusCheckbox);
        panel.add(statusLabel);
        return panel;
    }
}

