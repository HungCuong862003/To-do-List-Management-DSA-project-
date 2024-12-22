package ui;

import models.Category;
import models.SearchCriteria;
import models.Task;
import services.CategoryService;
import services.TaskService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Date;
import java.util.List;


public class SearchDialog extends JDialog {
    private final UIManager uiManager;
    private final TaskService taskService;
    private final CategoryService categoryService;
    private JTextField keywordField;
    private JComboBox<Category> categoryCombo;
    private JComboBox<Task.ImportanceLevel> importanceCombo;
    private JComboBox<String> completionCombo;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    public SearchDialog(UIManager uiManager, TaskService taskService, CategoryService categoryService) {
        super(uiManager.getParentFrame(), "Advanced Search", true);
        this.uiManager = uiManager;
        this.taskService = taskService;
        this.categoryService = categoryService;
        setupDialog();
        setVisible(true);  // Make dialog visible when created
    }

    private void setupDialog() {
        setLayout(new BorderLayout());
        setSize(400, 450);
        setLocationRelativeTo(getOwner());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Keyword field
        addFormField(form, gbc, 0, "Keyword:",
                keywordField = new JTextField(20));

        // Category dropdown
        categoryCombo = new JComboBox<>(categoryService.getAllCategories().toArray(new Category[0]));
        categoryCombo.insertItemAt(null, 0);
        categoryCombo.setSelectedIndex(0);
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getTitle());
                } else {
                    setText("Any Category");
                }
                return this;
            }
        });
        addFormField(form, gbc, 1, "Category:", categoryCombo);

        // Importance level dropdown
        importanceCombo = new JComboBox<>(Task.ImportanceLevel.values());
        importanceCombo.insertItemAt(null, 0);
        importanceCombo.setSelectedIndex(0);
        addFormField(form, gbc, 2, "Importance:", importanceCombo);

        // Status dropdown
        completionCombo = new JComboBox<>(new String[]{"Any", "Completed", "Pending"});
        addFormField(form, gbc, 3, "Status:", completionCombo);

        // Date spinners
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd HH:mm");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd HH:mm");
        startDateSpinner.setEditor(startEditor);
        endDateSpinner.setEditor(endEditor);

        addFormField(form, gbc, 4, "Start Date:", startDateSpinner);
        addFormField(form, gbc, 5, "End Date:", endDateSpinner);

        add(form, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(searchButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private void performSearch() {
        SearchCriteria.Builder builder = new SearchCriteria.Builder();

        // Get keyword
        String keyword = keywordField.getText().trim();
        if (!keyword.isEmpty()) {
            builder.withKeyword(keyword);
        }

        // Get category
        if (categoryCombo.getSelectedIndex() > 0) {
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            builder.withCategory(selectedCategory);
        }

        // Get importance level
        if (importanceCombo.getSelectedIndex() > 0) {
            Task.ImportanceLevel importance = (Task.ImportanceLevel) importanceCombo.getSelectedItem();
            builder.withImportance(importance);
        }

        // Get completion status
        String completionStatus = (String) completionCombo.getSelectedItem();
        if (!"Any".equals(completionStatus)) {
            builder.withCompletionStatus("Completed".equals(completionStatus));
        }

        // Get date range if both dates are set
        if (startDateSpinner.getValue() != null && endDateSpinner.getValue() != null) {
            builder.withDateRange(
                    (Date) startDateSpinner.getValue(),
                    (Date) endDateSpinner.getValue()
            );
        }

        SearchCriteria criteria = builder.build();
        List<Task> results = taskService.advancedSearch(criteria);

        // Show results dialog
        SearchResultsDialog resultsDialog = new SearchResultsDialog(uiManager.getParentFrame(), results);
        resultsDialog.setVisible(true);

        uiManager.refreshScreens();
        dispose();
    }
}