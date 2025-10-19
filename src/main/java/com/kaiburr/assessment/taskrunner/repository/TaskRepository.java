package com.kaiburr.assessment.taskrunner.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.kaiburr.assessment.taskrunner.model.Task;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByNameContainingIgnoreCase(String namePart);
}
