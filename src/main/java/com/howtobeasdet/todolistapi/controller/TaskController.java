package com.howtobeasdet.todolistapi.controller;

import com.howtobeasdet.todolistapi.model.Task;
import com.howtobeasdet.todolistapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping(value = "/")
    public String holaMundo(){
        return "HOLA MUNDO!!!";
    }

    @GetMapping(value= "/tasks")
    public List<Task> getTasks(){
        return taskRepository.findAll();
    }

    @PostMapping(value="/savetask")
    public String saveTask(@RequestBody Task task){
        taskRepository.save(task);
        return "Saved task";
    }

    @PutMapping(value="/update/{id}")
    public String updateTask(@PathVariable long id, @RequestBody Task task){
        Task updatedTask = taskRepository.findById(id).get();
        updatedTask.setTitle(task.getTitle());
        updatedTask.setDescription(task.getDescription());
        taskRepository.save(updatedTask);
        return "Updated Task";
    }

    @DeleteMapping(value="delete/{id}")
    public String deleteTask(@PathVariable long id){
        Task deletedTask = taskRepository.findById(id).get();
        taskRepository.delete(deletedTask);
        return "Deleted Task";
    }

}
