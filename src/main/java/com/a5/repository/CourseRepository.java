package com.a5.repository;

import com.a5.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByName(String name);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.students")
    List<Course> findAllWithStudents();

    /**
     * Search courses by name or description (case-insensitive).
     *
     * @param keyword Keyword to search in name or description
     * @return List of matching courses
     */
    List<Course> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * Filter courses strictly by name and description (case-insensitive).
     *
     * @param name        Course name filter
     * @param description Course description filter
     * @return Filtered list of courses
     */
    List<Course> findByNameIgnoreCaseAndDescriptionIgnoreCase(String name, String description);
}
