package com.studypod.studypod_backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserId(String userId);
}