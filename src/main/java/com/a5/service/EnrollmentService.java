package com.a5.service;

import com.a5.model.Enrollment;
import com.a5.model.Student;
import com.a5.model.Course;
import com.a5.repository.EnrollmentRepository;
import com.a5.repository.StudentRepository;
import com.a5.repository.CourseRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing student enrollments in courses.
 * Provides methods for enrolling/unenrolling students and retrieving enrollment information.
 * Includes metrics collection for monitoring enrollment operations.
 */
@Service
public class EnrollmentService {

    private static final int MAX_ENROLLMENTS = 5;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /** Counter for tracking the number of enrollment operations */
    private final Counter enrollmentCounter;

    /** Counter for tracking the number of unenrollment operations */
    private final Counter unenrollmentCounter;

    /**
     * Constructor that initializes metric counters for enrollment operations.
     * 
     * @param registry The Micrometer registry for registering metrics
     */
    public EnrollmentService(MeterRegistry registry) {
        this.enrollmentCounter = Counter.builder("app.enrollments")
                .description("Number of student enrollments")
                .register(registry);
        this.unenrollmentCounter = Counter.builder("app.unenrollments")
                .description("Number of student unenrollments")
                .register(registry);
    }

    @Transactional(readOnly = true)
    @Timed(value = "enrollment.list.time", description = "Time taken to list all enrollments")
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAllWithDetails();
    }

    @Transactional(readOnly = true)
    @Timed(value = "enrollment.student.list.time", description = "Time taken to list student enrollments")
    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    @Timed(value = "enrollment.course.list.time", description = "Time taken to list course enrollments")
    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Transactional
    @Timed(value = "enrollment.create.time", description = "Time taken to enroll a student")
    public Enrollment enrollStudent(Long studentId, Long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        long currentEnrollments = enrollmentRepository.countByStudentId(studentId);
        if (currentEnrollments >= MAX_ENROLLMENTS) {
            throw new IllegalStateException("Student has already enrolled in the maximum allowed number of courses (" + MAX_ENROLLMENTS + ").");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Enrollment enrollment = new Enrollment(student, course);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        enrollmentCounter.increment();
        return savedEnrollment;
    }

    @Transactional
    @Timed(value = "enrollment.delete.time", description = "Time taken to unenroll a student")
    public void unenrollStudent(Long studentId, Long courseId) {
        enrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .ifPresent(enrollment -> {
                    enrollmentRepository.delete(enrollment);
                    unenrollmentCounter.increment();
                });
    }
}
