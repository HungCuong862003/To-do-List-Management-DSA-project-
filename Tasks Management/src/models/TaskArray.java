package models;

public class TaskArray {
    private Task[] array;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public TaskArray() {
        array = new Task[DEFAULT_CAPACITY];
        size = 0;
    }

    public void add(Task task) {
        if (size == array.length) {
            resize();
        }
        array[size++] = task;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        // Shift elements left
        for (int i = index; i < size - 1; i++) {
            array[i] = array[i + 1];
        }
        array[--size] = null;
    }

    public Task get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        return array[index];
    }

    public int size() {
        return size;
    }

    public Task[] toArray() {
        Task[] result = new Task[size];
        System.arraycopy(array, 0, result, 0, size);
        return result;
    }

    private void resize() {
        Task[] newArray = new Task[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        array = newArray;
    }

    public void sort(Task.SortCriteria criteria) {
        switch (criteria) {
            case NAME_ASC:
            case NAME_DESC:
                mergeSort(criteria);  // Stable sort for strings
                break;

            case DEADLINE_ASC:
            case DEADLINE_DESC:
                quickSort(0, size - 1, criteria);  // Quick sort for dates
                break;

            case IMPORTANCE_ASC:
            case IMPORTANCE_DESC:
                countingSortByImportance(criteria == Task.SortCriteria.IMPORTANCE_DESC);
                break;
        }
    }

    //MergeSort
    private void mergeSort(Task.SortCriteria criteria) {
        Task[] temp = new Task[size];
        mergeSort(array, temp, 0, size - 1, criteria);
    }

    private void mergeSort(Task[] arr, Task[] temp, int left, int right, Task.SortCriteria criteria) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, temp, left, mid, criteria);
            mergeSort(arr, temp, mid + 1, right, criteria);
            merge(arr, temp, left, mid, right, criteria);
        }
    }

    private void merge(Task[] arr, Task[] temp, int left, int mid, int right, Task.SortCriteria criteria) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (compareByName(temp[i], temp[j], criteria)) {
                arr[k] = temp[i];
                i++;
            } else {
                arr[k] = temp[j];
                j++;
            }
            k++;
        }

        while (i <= mid) {
            arr[k] = temp[i];
            k++;
            i++;
        }
    }

    private boolean compareByName(Task a, Task b, Task.SortCriteria criteria) {
        int comparison = a.getDescription().compareTo(b.getDescription());
        return criteria == Task.SortCriteria.NAME_ASC ? comparison <= 0 : comparison >= 0;
    }




    // QuickSort implementation
    // QuickSort for deadline-based sorting
    public void quickSort(int low, int high, Task.SortCriteria criteria) {
        if (low < high) {
            // Use median-of-three pivot selection for better performance
            int mid = low + (high - low) / 2;
            Task pivot = medianOfThree(array[low], array[mid], array[high], criteria);

            // Move pivot to end
            swap(array, high, getIndexOfValue(array, pivot));

            int pi = partition(low, high, criteria);
            quickSort(low, pi - 1, criteria);
            quickSort(pi + 1, high, criteria);
        }
    }

    private Task medianOfThree(Task a, Task b, Task c, Task.SortCriteria criteria) {
        boolean aLessB = compareByDeadline(a, b, criteria);
        boolean bLessC = compareByDeadline(b, c, criteria);
        boolean aLessC = compareByDeadline(a, c, criteria);

        if (aLessB == bLessC) return b;
        if (aLessC == (bLessC != aLessB)) return c;
        return a;
    }

    private boolean compareByDeadline(Task a, Task b, Task.SortCriteria criteria) {
        int comparison = a.getDeadline().compareTo(b.getDeadline());
        return criteria == Task.SortCriteria.DEADLINE_ASC ? comparison <= 0 : comparison >= 0;
    }

    // CountingSort for importance-based sorting
    private void countingSortByImportance(boolean descending) {
        Task.ImportanceLevel[] levels = Task.ImportanceLevel.values();
        int[] count = new int[levels.length];
        Task[] output = new Task[size];

        // Count tasks of each importance level
        for (int i = 0; i < size; i++) {
            count[array[i].getImportance().ordinal()]++;
        }


        // Calculate starting positions
        if (descending) {
            for (int i = levels.length - 2; i >= 0; i--) {
                count[i] += count[i + 1];
            }
        } else {
            for (int i = 1; i < levels.length; i++) {
                count[i] += count[i - 1];
            }
        }

        // Build output array
        for (int i = size - 1; i >= 0; i--) {
            int importance = array[i].getImportance().ordinal();
            output[count[importance] - 1] = array[i];
            count[importance]--;
        }

        for (Task.ImportanceLevel level : Task.ImportanceLevel.values()) {
            int startIndex = -1;
            int tasksInLevel = 0;  // Renamed from count to avoid conflict

            // Find range of tasks with same importance
            for (int i = 0; i < size; i++) {
                if (output[i].getImportance() == level) {
                    if (startIndex == -1) startIndex = i;
                    tasksInLevel++;
                }
            }

            if (tasksInLevel > 1) {
                secondarySort(output, startIndex, startIndex + tasksInLevel - 1);
            }
        }


        // Copy back to original array
        System.arraycopy(output, 0, array, 0, size);
    }

    // Secondary sort for tasks with same importance
    private void secondarySort(Task[] tasks, int start, int end) {
        // Use insertion sort for small ranges
        for (int i = start + 1; i <= end; i++) {
            Task key = tasks[i];
            int j = i - 1;
            while (j >= start && tasks[j].getDeadline().after(key.getDeadline())) {
                tasks[j + 1] = tasks[j];
                j--;
            }
            tasks[j + 1] = key;
        }
    }

    private void swap(Task[] arr, int i, int j) {
        Task temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    private int getIndexOfValue(Task[] arr, Task value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    private boolean compareImportance(Task a, Task b, boolean ascending) {
        // First compare completion status
        if (a.isCompleted() != b.isCompleted()) {
            return !a.isCompleted();
        }

        // Compare importance levels
        if (a.getImportance() != b.getImportance()) {
            return ascending ?
                    a.getImportance().ordinal() <= b.getImportance().ordinal() :
                    a.getImportance().ordinal() >= b.getImportance().ordinal();
        }

        // Compare deadlines if importance is equal
        return ascending ?
                a.getDeadline().compareTo(b.getDeadline()) <= 0 :
                a.getDeadline().compareTo(b.getDeadline()) >= 0;
    }

    private int partition(int low, int high, Task.SortCriteria criteria) {
        Task pivot = array[high];
        int i = (low - 1);

        for (int j = low; j < high; j++) {
            // Should use specific comparison based on criteria
            switch (criteria) {
                case DEADLINE_ASC:
                case DEADLINE_DESC:
                    if (compareByDeadline(array[j], pivot, criteria)) {
                        i++;
                        swap(array, i, j);
                    }
                    break;
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }
}