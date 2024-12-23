package models;

public class TaskQueue {
    private static class Node {
        Task task;
        Node next;

        Node(Task task) {
            this.task = task;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;
    private int size;

    public TaskQueue() {
        front = null;
        rear = null;
        size = 0;
    }

    // Add task to queue
    public void enqueue(Task task) {
        Node newNode = new Node(task);
        if (isEmpty()) {
            front = newNode;
        } else {
            rear.next = newNode;
        }
        rear = newNode;
        size++;
    }

    // Remove and return front task
    public Task dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        Task task = front.task;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return task;
    }

    // View front task without removing
    public Task peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return front.task;
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Get current size of queue
    public int size() {
        return size;
    }

    // Clear all tasks from queue
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    // Get all tasks as array
    public Task[] toArray() {
        Task[] result = new Task[size];
        Node current = front;
        int index = 0;
        while (current != null) {
            result[index++] = current.task;
            current = current.next;
        }
        return result;
    }
}
