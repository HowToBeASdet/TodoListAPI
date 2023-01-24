package com.howtobeasdet.todolistapi.repository;

import com.howtobeasdet.todolistapi.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
