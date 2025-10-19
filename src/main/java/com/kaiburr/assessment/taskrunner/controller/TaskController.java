package com.kaiburr.assessment.taskrunner.controller;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kaiburr.assessment.taskrunner.model.Task;
import com.kaiburr.assessment.taskrunner.model.TaskExecution;
import com.kaiburr.assessment.taskrunner.service.TaskService;
import com.kaiburr.assessment.taskrunner.service.CommandValidator;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService svc;

    public TaskController(TaskService svc) {
        this.svc = svc;
    }

    @GetMapping
    public List<Task> allTasks() {
        return svc.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getOne(@PathVariable String id) {
        return svc.findById(id)
                  .map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<?> putTask(@Valid @RequestBody Task t) {
        if (!CommandValidator.isSafe(t.getCommand())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Unsafe command detected!");
        }
        return ResponseEntity.ok(svc.save(t));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> search(@RequestParam String name) {
        List<Task> res = svc.findByName(name);
        return res.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/executions")
    public ResponseEntity<?> run(@PathVariable String id) {
        return svc.findById(id).map(task -> {
            try {
                TaskExecution ex = svc.runCommandAndRecord(task);
                return ResponseEntity.ok(ex);
            } catch (IllegalArgumentException ie) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ie.getMessage());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Command execution failed");
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
