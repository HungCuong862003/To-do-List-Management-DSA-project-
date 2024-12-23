package services;

import models.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class CategoryServiceImpl implements CategoryService {
    private final List<Category> categories;

    public CategoryServiceImpl() {
        this.categories = new ArrayList<>();
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        // Clear existing categories
        categories.clear();

        // Add default categories
        categories.addAll(Arrays.asList(
                new Category(1, "Work"),
                new Category(2, "Personal"),
                new Category(3, "Study"),
                new Category(4, "Health"),
                new Category(5, "Shopping")
        ));
    }

    @Override
    public List<Category> getAllCategories() {
        return new ArrayList<>(categories);
    }

    @Override
    public void addCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (categoryExists(category.getId())) {
            throw new IllegalArgumentException("Category with ID " + category.getId() + " already exists");
        }
        categories.add(category);
    }

    @Override
    public void updateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        boolean updated = false;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId() == category.getId()) {
                categories.set(i, category);
                updated = true;
                break;
            }
        }

        if (!updated) {
            throw new NoSuchElementException("Category with ID " + category.getId() + " not found");
        }
    }

    @Override
    public void deleteCategory(int categoryId) {
        boolean removed = categories.removeIf(category -> category.getId() == categoryId);
        if (!removed) {
            throw new NoSuchElementException("Category with ID " + categoryId + " not found");
        }
    }

    private boolean categoryExists(int id) {
        return categories.stream().anyMatch(category -> category.getId() == id);
    }
}