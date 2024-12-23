package ui;

import models.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class SearchResultsDialog extends JDialog {
    private final SimpleDateFormat dateFormat;
    private final List<Task> searchResults;

    public SearchResultsDialog(JFrame parentFrame, List<Task> results) {
        super(parentFrame, "Search Results", true);
        this.searchResults = results;
        this.dateFormat = new SimpleDateFormat("MMM dd, HH:mm");

        setupDialog();
    }

    private void setupDialog() {
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(getOwner());

        // Create the header panel
        JPanel headerPanel = createHeaderPanel();

        // Create the results panel
        JPanel resultsPanel = createResultsPanel();
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(null);

        // Add close button at bottom
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Color.GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(245, 245, 245));

        // Add column headers
        JLabel taskLabel = new JLabel("Task");
        JLabel deadlineLabel = new JLabel("Deadline");
        JLabel importanceLabel = new JLabel("Importance");
        JLabel statusLabel = new JLabel("Status");

        Font headerFont = new Font("Inter", Font.BOLD, 14);
        taskLabel.setFont(headerFont);
        deadlineLabel.setFont(headerFont);
        importanceLabel.setFont(headerFont);
        statusLabel.setFont(headerFont);

        panel.add(taskLabel);
        panel.add(deadlineLabel);
        panel.add(importanceLabel);
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        if (searchResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("No results found");
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noResultsLabel);
        } else {
            for (Task task : searchResults) {
                panel.add(createTaskRow(task));
                panel.add(Box.createRigidArea(new Dimension(0, 1)));
            }
        }

        return panel;
    }

    private JPanel createTaskRow(Task task) {
        JPanel row = new JPanel(new GridLayout(1, 4));
        row.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(8, 10, 8, 10)
        ));
        row.setBackground(Color.WHITE);

        // Task name
        JLabel nameLabel = new JLabel(task.getDescription());
        nameLabel.setFont(new Font("Inter", Font.PLAIN, 13));

        // Deadline
        JLabel deadlineLabel = new JLabel(dateFormat.format(task.getDeadline()));
        deadlineLabel.setFont(new Font("Inter", Font.PLAIN, 13));

        // Importance
        JLabel importanceLabel = new JLabel(task.getImportance().getLabel());
        importanceLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        importanceLabel.setForeground(task.getImportance().getColor());

        // Status
        JLabel statusLabel = new JLabel(task.isCompleted() ? "Complete" : "Pending");
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        statusLabel.setForeground(task.isCompleted() ? new Color(46, 204, 113) : Color.GRAY);

        row.add(nameLabel);
        row.add(deadlineLabel);
        row.add(importanceLabel);
        row.add(statusLabel);

        return row;
    }
}