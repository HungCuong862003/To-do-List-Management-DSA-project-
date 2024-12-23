        package ui;

        import models.Category;
        import services.CategoryService;
        import services.TaskService;

        import javax.swing.*;
        import javax.swing.border.EmptyBorder;
        import javax.swing.border.LineBorder;
        import java.awt.*;

        public class HomeScreen extends JPanel {
            private final UIManager uiManager;
            private final TaskService taskService;
            private final CategoryService categoryService;
            private final JLabel totalTasksLabel;
            private final JLabel incompleteTasksLabel;
            private final CategoryListPanel categoryListPanel;


            public HomeScreen(UIManager uiManager, TaskService taskService, CategoryService categoryService) {
                this.uiManager = uiManager;
                this.taskService = taskService;
                this.categoryService = categoryService;

                // Initialize labels
                this.totalTasksLabel = new JLabel();
                this.incompleteTasksLabel = new JLabel();
                this.categoryListPanel = new CategoryListPanel(uiManager, taskService, categoryService);

                // Setup panel
                setLayout(new BorderLayout());
                setBackground(Color.BLUE);

                // Add components
                add(createHeader(), BorderLayout.NORTH);

                // Create and add scrollable category list
                JScrollPane scrollPane = new JScrollPane(categoryListPanel);
                scrollPane.setBorder(null);
                add(scrollPane, BorderLayout.CENTER);

                // Add Task Button
                add(createBottomPanel(), BorderLayout.SOUTH);

                // Initial update
                updateTaskCounts();
            }


            private JPanel createHeader() {
                JPanel header = new JPanel(new BorderLayout());
                header.setOpaque(false);
                header.setBorder(new EmptyBorder(20, 20, 20, 20));

                // Left side with text content
                JPanel leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                leftPanel.setOpaque(false);

                // Welcome label
                JLabel welcomeLabel = new JLabel("Hello");
                welcomeLabel.setForeground(Color.WHITE);
                welcomeLabel.setFont(new Font("Inter", Font.BOLD, 24));
                welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Style the counter labels
                totalTasksLabel.setForeground(Color.WHITE);
                totalTasksLabel.setFont(new Font("Inter", Font.PLAIN, 16));
                totalTasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                incompleteTasksLabel.setForeground(new Color(255, 255, 255, 220));
                incompleteTasksLabel.setFont(new Font("Inter", Font.PLAIN, 14));
                incompleteTasksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Add components with spacing
                leftPanel.add(welcomeLabel);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                leftPanel.add(totalTasksLabel);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                leftPanel.add(incompleteTasksLabel);

                // Right side with search button only
                JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                rightPanel.setOpaque(false);

                JButton searchButton = new JButton("Search");
                searchButton.setPreferredSize(new Dimension(100, 50));
                searchButton.setBackground(new Color(25, 118, 210));
                searchButton.setForeground(Color.BLUE);
                searchButton.setFont(new Font("Inter", Font.BOLD, 17));
                searchButton.addActionListener(e -> new SearchDialog(uiManager, taskService, categoryService));

                // Add hover effect to match your style
                searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        searchButton.setBackground(new Color(21, 101, 192));
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        searchButton.setBackground(new Color(25, 118, 210));
                    }
                });

                rightPanel.add(searchButton);

                header.add(leftPanel, BorderLayout.WEST);
                header.add(rightPanel, BorderLayout.EAST);
                return header;
            }


            private JPanel createBottomPanel() {
                JPanel bottomPanel = new JPanel(new BorderLayout());
                bottomPanel.setBackground(Color.BLUE);
                bottomPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

                // Left side - Add Category button
                JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                leftButtonPanel.setOpaque(false);

                JButton addCategoryButton = new JButton("Add Category");
                addCategoryButton.setFont(new Font("Inter", Font.BOLD, 17));
                addCategoryButton.setBackground(new Color(25, 118, 210));
                addCategoryButton.setForeground(Color.BLUE);
                addCategoryButton.addActionListener(e -> showAddCategoryDialog());

                leftButtonPanel.add(addCategoryButton);

                // Right side - Add Task button
                JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                rightButtonPanel.setOpaque(false);
                rightButtonPanel.add(createAddButton());

                bottomPanel.add(leftButtonPanel, BorderLayout.WEST);
                bottomPanel.add(rightButtonPanel, BorderLayout.EAST);

                return bottomPanel;
            }

            private void showAddCategoryDialog() {
                JDialog dialog = new JDialog(uiManager.getParentFrame(), "Add Category", true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(350, 150);
                dialog.setLocationRelativeTo(uiManager.getParentFrame());

                JPanel form = new JPanel(new GridBagLayout());
                form.setBorder(new EmptyBorder(15, 15, 15, 15));

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.insets = new Insets(5, 5, 5, 5);

                form.add(new JLabel("Category Name:"), gbc);

                JTextField nameField = new JTextField(20);
                gbc.gridx = 1;
                form.add(nameField, gbc);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Save");
                JButton cancelButton = new JButton("Cancel");

                saveButton.addActionListener(e -> {
                    String name = nameField.getText().trim();
                    if (!name.isEmpty()) {
                        int newId = categoryService.getAllCategories().size() + 1;
                        Category newCategory = new Category(newId, name);
                        categoryService.addCategory(newCategory);
                        refresh();
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Please enter a category name");
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                dialog.add(form, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }

            private JButton createAddButton() {
                JButton addButton = new JButton("Add Task");
                addButton.setFont(new Font("Inter", Font.BOLD, 16));
                addButton.setForeground(Color.BLUE);
                addButton.setBackground(new Color(25, 118, 210));
                addButton.setPreferredSize(new Dimension(120, 45));
                addButton.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(25, 118, 210), 2, true),
                        new EmptyBorder(5, 10, 5, 10)
                ));

                // Add hover effect
                addButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        addButton.setBackground(new Color(21, 101, 192));
                    }
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        addButton.setBackground(new Color(25, 118, 210));
                    }
                });

                // Add task dialog
                addButton.addActionListener(e -> new AddTaskDialog(uiManager, taskService, categoryService));

                return addButton;
            }

            private void updateTaskCounts() {
                int totalTasks = taskService.getTotalTaskCount();
                totalTasksLabel.setText("Total tasks: " + totalTasks);

                long incompleteTasks = taskService.getAllTasks().stream()
                        .filter(task -> !task.isCompleted())
                        .count();

                if (incompleteTasks > 0) {
                    incompleteTasksLabel.setText("You have " + incompleteTasks + " tasks to complete");
                } else {
                    incompleteTasksLabel.setText("All tasks are completed!");
                }
            }

            public void refresh() {
                updateTaskCounts();
                categoryListPanel.refresh();
                revalidate();
                repaint();
            }
        }
