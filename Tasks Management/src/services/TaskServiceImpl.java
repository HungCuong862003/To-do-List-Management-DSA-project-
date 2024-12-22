package services;

import models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private final TaskArray tasks;

    private final TaskQueue taskQueue;
    private final CategoryService categoryService;

    public TaskServiceImpl(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.tasks = new TaskArray();
        this.taskQueue = new TaskQueue();
        initializeDefaultTasks();
    }

    private void initializeDefaultTasks() {
        try {
            Calendar cal = Calendar.getInstance();

            // Today's tasks
            cal.set(2024, Calendar.DECEMBER, 18, 14, 0);
            Task task1 = new Task(1, "Complete DSA Project",
                    categoryService.getAllCategories().get(2),
                    cal.getTime(), Task.ImportanceLevel.IMPORTANT_URGENT);
            tasks.add(task1);
            taskQueue.enqueue(task1);

            cal.set(2024, Calendar.DECEMBER, 18, 17, 30);
            Task task2 = new Task(2, "Gym Workout",
                    categoryService.getAllCategories().get(3),
                    cal.getTime(), Task.ImportanceLevel.NOT_IMPORTANT_URGENT);
            tasks.add(task2);

            cal.set(2024, Calendar.DECEMBER, 19, 10, 0);
            Task task3 = new Task(3, "Team Meeting",
                    categoryService.getAllCategories().get(0),
                    cal.getTime(), Task.ImportanceLevel.IMPORTANT_NOT_URGENT);
            tasks.add(task3);

            cal.set(2024, Calendar.DECEMBER, 19, 15, 0);
            Task task4 = new Task(4, "Buy Groceries",
                    categoryService.getAllCategories().get(4),
                    cal.getTime(), Task.ImportanceLevel.NOT_IMPORTANT_NOT_URGENT);
            tasks.add(task4);

            System.out.println("Available tasks for search:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println("- " + tasks.get(i).getDescription());
            }
        } catch (Exception e) {
            System.err.println("Error initializing tasks: " + e.getMessage());
        }
    }


    @Override
    public Category getCategoryById(int id) {
        return categoryService.getAllCategories().stream()
                .filter(category -> category.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }


    @Override
    public List<Task> getAllTasks() {
        return Arrays.asList(tasks.toArray());
    }

    @Override
    public List<Task> getTasksByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        List<Task> tasksByCategory = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getCategory().getId() == category.getId()) {
                tasksByCategory.add(task);
            }
        }
        return tasksByCategory;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        tasks.add(task);
        taskQueue.enqueue(task);
    }


    @Override
    public void updateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.remove(i);
                tasks.add(task);
                break;
            }
        }
    }

    @Override
    public void deleteTask(int taskId) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == taskId) {
                tasks.remove(i);
                break;
            }
        }
    }

    @Override
    public List<Task> getTasksByCategorySorted(Category category, Task.SortCriteria criteria) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        // Filter tasks by category first
        TaskArray categoryTasks = new TaskArray();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getCategory().getId() == category.getId()) {
                categoryTasks.add(task);
            }
        }

        // Sort the filtered tasks
        categoryTasks.sort(criteria);
        return Arrays.asList(categoryTasks.toArray());
    }

    @Override
    public int getTotalTaskCount() {
        return tasks.size();
    }


    @Override
    public List<Task> getAllTasksSorted(Task.SortCriteria criteria) {
        TaskArray sortedTasks = new TaskArray();
        for (int i = 0; i < tasks.size(); i++) {
            sortedTasks.add(tasks.get(i));
        }
        sortedTasks.sort(criteria);
        return Arrays.asList(sortedTasks.toArray());
    }


    private void sortTasks(List<Task> taskList, Task.SortCriteria criteria) {
        if (taskList == null || taskList.isEmpty()) return;
        quickSort(taskList, 0, taskList.size() - 1, criteria);
    }

    private void quickSort(List<Task> arr, int low, int high, Task.SortCriteria criteria) {
        if (low < high) {
            int pi = partition(arr, low, high, criteria);
            quickSort(arr, low, pi - 1, criteria);
            quickSort(arr, pi + 1, high, criteria);
        }
    }

    private int partition(List<Task> arr, int low, int high, Task.SortCriteria criteria) {
        Task pivot = arr.get(high);
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            if (shouldSwap(arr.get(j), pivot, criteria)) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private void swap(List<Task> arr, int i, int j) {
        Task temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }

    private boolean shouldSwap(Task a, Task b, Task.SortCriteria criteria) {
        switch (criteria) {
            case NAME_ASC:
                return a.getDescription().compareTo(b.getDescription()) <= 0;
            case NAME_DESC:
                return a.getDescription().compareTo(b.getDescription()) >= 0;
            case DEADLINE_ASC:
                return a.getDeadline().compareTo(b.getDeadline()) <= 0;
            case DEADLINE_DESC:
                return a.getDeadline().compareTo(b.getDeadline()) >= 0;
            case IMPORTANCE_ASC:
                // First compare completion status
                if (a.isCompleted() != b.isCompleted()) {
                    return !a.isCompleted(); // Incomplete tasks come first
                }
                // If completion status is the same, compare importance
                if (a.getImportance() != b.getImportance()) {
                    return a.getImportance().ordinal() <= b.getImportance().ordinal();
                }
                // If importance is the same, compare by deadline
                return a.getDeadline().compareTo(b.getDeadline()) <= 0;
            case IMPORTANCE_DESC:
                // First compare completion status
                if (a.isCompleted() != b.isCompleted()) {
                    return !a.isCompleted(); // Incomplete tasks come first
                }
                // If completion status is the same, compare importance
                if (a.getImportance() != b.getImportance()) {
                    return a.getImportance().ordinal() >= b.getImportance().ordinal();
                }
                // If importance is the same, compare by deadline
                return a.getDeadline().compareTo(b.getDeadline()) >= 0;
            default:
                return false;
        }
    }

    @Override
    public List<Task> advancedSearch(SearchCriteria criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("Search criteria cannot be null");
        }

        List<Task> results = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (matchesCriteria(task, criteria)) {
                results.add(task);
            }
        }
        return results;
    }


    public Task getNextScheduledTask() {
        if (!taskQueue.isEmpty()) {
            return taskQueue.peek();
        }
        return null;
    }

    private boolean matchesCriteria(Task task, SearchCriteria criteria) {
        // Start assuming it matches
        boolean matches = true;

        // Check keyword
        if (criteria.getKeyword() != null && !criteria.getKeyword().isEmpty()) {
            String taskDesc = task.getDescription().toLowerCase();
            String keyword = criteria.getKeyword().toLowerCase();
            boolean keywordMatch = taskDesc.contains(keyword);
            System.out.println("Keyword match: '" + taskDesc + "' contains '" + keyword + "': " + keywordMatch);
            matches &= keywordMatch;
        }

        // Check category
        if (criteria.getCategory() != null) {
            boolean categoryMatch = task.getCategory().getId() == criteria.getCategory().getId();
            System.out.println("Category match: " + categoryMatch);
            matches &= categoryMatch;
        }

        // Check importance
        if (criteria.getImportance() != null) {
            boolean importanceMatch = task.getImportance() == criteria.getImportance();
            System.out.println("Importance match: " + importanceMatch);
            matches &= importanceMatch;
        }

        // Check completion status
        if (criteria.getIsCompleted() != null) {
            boolean statusMatch = task.isCompleted() == criteria.getIsCompleted();
            System.out.println("Status match: " + statusMatch);
            matches &= statusMatch;
        }

        // Only check date range if both dates are provided
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            boolean dateMatch = task.getDeadline().after(criteria.getStartDate()) &&
                    task.getDeadline().before(criteria.getEndDate());
            System.out.println("Date range match: " + dateMatch);
            matches &= dateMatch;
        }

        return matches;
    }

    private boolean basicSearch(String text, String pattern) {
        if (text == null || pattern == null) {
            return false;
        }

        // Convert both strings to lowercase for case-insensitive search
        text = text.toLowerCase();
        pattern = pattern.toLowerCase();

        int n = text.length();
        int m = pattern.length();

        // Pattern can't be longer than text
        if (m > n) {
            return false;
        }

        // Try all potential starting positions
        for (int i = 0; i <= n - m; i++) {
            boolean found = true;

            // Check if pattern matches at current position
            for (int j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return true;
            }
        }

        return false;
    }

    private void swapTasks(Task[] arr, int i, int j) {
        Task temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
