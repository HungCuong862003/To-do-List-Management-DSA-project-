package services;

import models.Category;
import models.SearchCriteria;
import models.Task;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();
    List<Task> getTasksByCategory(Category category);
    void addTask(Task task);
    void updateTask(Task task);
    void deleteTask(int taskId);
    int getTotalTaskCount();
    List<Task> getAllTasksSorted(Task.SortCriteria criteria);
    List<Task> getTasksByCategorySorted(Category category, Task.SortCriteria criteria);

    Category getCategoryById(int id);

    List<Task> advancedSearch(SearchCriteria criteria);

}
