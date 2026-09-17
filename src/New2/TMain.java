package New2;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

enum TaskStatus {
    PENDING,RUNNING,COMPLETED,FAILED;
}

record Task(String id, String description, int priority, TaskStatus status, LocalDateTime createdAt){}

class DuplicateTaskException extends Exception {
    public DuplicateTaskException(String message) { super(message); }
}

class TaskScheduler{
    private final PriorityQueue<Task> taskQueue;
    public TaskScheduler(){
        taskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::priority).reversed().thenComparing(Task::createdAt));
    }
    public void addTask(Task task) throws DuplicateTaskException {
        boolean exists = taskQueue.stream().anyMatch(t -> t.id().equals(task.id()));
        if(exists){
            throw new DuplicateTaskException("Task already exists");
        }
        taskQueue.add(task);
    }
    public Optional<Task> getNextTask(){
        if (taskQueue.isEmpty()){
            return Optional.empty();
        }
        return Optional.ofNullable(taskQueue.poll());
    }
    public Optional<Task> peekNextTask(){
        if (taskQueue.isEmpty()){
            return Optional.empty();
        }
        return Optional.ofNullable(taskQueue.peek());
    }
    public List<Task> getTasks(TaskStatus status){
        return taskQueue.stream().filter(t->t.status()==status).toList();
    }
    public double getAveragePriority(){
        return taskQueue.stream().mapToInt(Task::priority).average().orElse(0.0);
    }
    public boolean cancelTask(String id){
        return taskQueue.removeIf(task->task.id().equals(id));
    }
}

public class TMain {
    public static void main(String[] args) throws DuplicateTaskException{
        TaskScheduler scheduler = new TaskScheduler();

        scheduler.addTask(new Task("T1", "Fix bug", 5, TaskStatus.PENDING, LocalDateTime.now()));
        scheduler.addTask(new Task("T2", "Write docs", 3, TaskStatus.PENDING, LocalDateTime.now().plusMinutes(1)));
        scheduler.addTask(new Task("T3", "Deploy", 5, TaskStatus.PENDING, LocalDateTime.now().plusMinutes(2)));

        try {
            scheduler.addTask(new Task("T1", "Run", 2, TaskStatus.RUNNING, LocalDateTime.now()));
        }catch (DuplicateTaskException e){
            System.out.println("Caught: "+e.getMessage());
        }

        scheduler.getNextTask().ifPresent(t->System.out.println(t.id()));

        scheduler.peekNextTask().ifPresent(t->System.out.println(t.id()));

        System.out.println("Cancel T2:"+scheduler.cancelTask("T2"));
        System.out.println("Cancel T99: " + scheduler.cancelTask("T99"));
    }
}
