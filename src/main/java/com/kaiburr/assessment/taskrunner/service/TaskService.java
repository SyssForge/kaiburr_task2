package com.kaiburr.assessment.taskrunner.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.kaiburr.assessment.taskrunner.model.Task;
import com.kaiburr.assessment.taskrunner.model.TaskExecution;
import com.kaiburr.assessment.taskrunner.repository.TaskRepository;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> findAll() {
        return repo.findAll();
    }

    public Optional<Task> findById(String id) {
        return repo.findById(id);
    }

    public Task save(Task t) {
        return repo.save(t);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }

    public List<Task> findByName(String name) {
        return repo.findByNameContainingIgnoreCase(name);
    }

    public TaskExecution runCommandAndRecord(Task task) throws Exception {
    String cmd = task.getCommand();
    if (!CommandValidator.isSafe(cmd)) {
        throw new IllegalArgumentException("Unsafe command detected!");
    }

    io.kubernetes.client.openapi.Configuration.setDefaultApiClient(
        io.kubernetes.client.util.Config.defaultClient()
    );
    io.kubernetes.client.openapi.apis.CoreV1Api api = new io.kubernetes.client.openapi.apis.CoreV1Api();

    var pod = new io.kubernetes.client.openapi.models.V1Pod()
        .metadata(new io.kubernetes.client.openapi.models.V1ObjectMeta()
            .name("taskrun-" + task.getId() + "-" + System.currentTimeMillis())
            .namespace("default"))
        .spec(new io.kubernetes.client.openapi.models.V1PodSpec()
            .addContainersItem(new io.kubernetes.client.openapi.models.V1Container()
                .name("runner")
                .image("busybox")
                .command(List.of("sh", "-c", cmd)))
            .restartPolicy("Never"));

    api.createNamespacedPod("default", pod, null, null, null, null);


    // record execution metadata
    TaskExecution exec = new TaskExecution();
    exec.setStartTime(Instant.now());
    exec.setOutput("Command scheduled in Kubernetes pod: " + pod.getMetadata().getName());
    exec.setEndTime(Instant.now());

    task.getTaskExecutions().add(exec);
    repo.save(task);
    return exec;
}

}
