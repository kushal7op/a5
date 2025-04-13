package com.a5.repository;

import com.a5.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAllWithCourses();

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(Long id);

    /**
     * Search students by name or email (case-insensitive).
     */
    List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    /**
     * Filter students by name and email (supports partial text and case-insensitive).
     */
    List<Student> findByNameContainingIgnoreCaseAndEmailContainingIgnoreCase(String name, String email);
}
