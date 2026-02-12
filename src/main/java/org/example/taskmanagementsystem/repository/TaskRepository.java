package org.example.taskmanagementsystem.repository;

import org.example.taskmanagementsystem.model.Task;
import org.example.taskmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByTitle(String title);
    List<Task> findByOwner(User owner);
}
