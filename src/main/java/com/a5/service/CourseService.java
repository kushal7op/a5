package com.a5.service;

import com.a5.model.Course;
import com.a5.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<Course> getAllCourses() {
        return courseRepository.findAllWithStudents();
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    @Transactional
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return courseRepository.existsByName(name);
    }

    /**
     * Search for courses by name or description.
     *
     * @param keyword The keyword to search
     * @return List of matching courses
     */
    public List<Course> searchCourses(String keyword) {
        return courseRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    /**
     * Filter courses by name and description (both must match if provided).
     *
     * @param name        Course name
     * @param description Course description
     * @return Filtered list of courses
     */
    public List<Course> filterCourses(String name, String description) {
        return courseRepository.findByNameIgnoreCaseAndDescriptionIgnoreCase(name, description);
    }
}
