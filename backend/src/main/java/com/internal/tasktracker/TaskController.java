package com.internal.tasktracker;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping("/api/tasks")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int pageSize) {

        // Normalize query input
        String query = q == null ? "" : q.trim();
        String searchTerm = "%" + query.toLowerCase() + "%";

        // Parse status filter
        //throwing an appropriate error for selction failing
        String normalizedStatus = null;
        if (status != null && !status.isBlank())
        {
            try
            {
                normalizedStatus = TaskStatus.valueOf(status.trim().toUpperCase()).name();
            }
            catch (IllegalArgumentException e)
            {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status",
                               "message", "Status must be OPEN, IN_PROGRESS, or DONE")
                );
            }

        }


        // Query complexity estimation for logging


        System.out.println("[TaskController] q=\"" + query + "\" status=" + normalizedStatus
                + " page=" + page + " pageSize=" + pageSize);

        List<Task> allResults = taskRepository.searchTasks(searchTerm, normalizedStatus);

        if(page<1)
        {
            return ResponseEntity.badRequest()
                .body(Map.of("error","page must be Greater than 0"));
        }

        if(pageSize<1||pageSize>100)
        { return ResponseEntity.badRequest()
            .body(Map.of("error","page must be between 1 and 100"));
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, allResults.size());
        List<Task> pageResults = (start < allResults.size())
                ? allResults.subList(start, end)
                : Collections.emptyList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("items", pageResults);
        response.put("total", allResults.size());
        response.put("page", page);
        response.put("pageSize", pageSize);

        return ResponseEntity.ok(response);
    }
}
