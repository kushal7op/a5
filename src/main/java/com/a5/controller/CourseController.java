package com.a5.controller;

import com.a5.model.Course;
import com.a5.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public String listCourses(@RequestParam(required = false) String name,
                              @RequestParam(required = false) String description,
                              @RequestParam(required = false, name = "search") String search,
                              Model model) {

        List<Course> courses;

        if (search != null && !search.isBlank()) {
            courses = courseService.searchCourses(search.trim());
        } else if (name != null && description != null &&
                   !name.isBlank() && !description.isBlank()) {
            courses = courseService.filterCourses(name.trim(), description.trim());
        } else {
            courses = courseService.getAllCourses();
        }

        model.addAttribute("courses", courses);
        model.addAttribute("name", name);
        model.addAttribute("description", description);
        model.addAttribute("search", search);

        return "courses/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/form";
    }

    @PostMapping
    public String createCourse(@Valid @ModelAttribute("course") Course course, BindingResult result) {
        if (result.hasErrors()) {
            return "courses/form";
        }
        if (courseService.existsByName(course.getName())) {
            result.rejectValue("name", "error.course", "Course name already exists");
            return "courses/form";
        }
        courseService.saveCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid course Id:" + id));
        model.addAttribute("course", course);
        return "courses/form";
    }

    @PostMapping("/{id}")
    public String updateCourse(@PathVariable Long id,
                               @Valid @ModelAttribute("course") Course course,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "courses/form";
        }
        course.setId(id);
        courseService.saveCourse(course);
        return "redirect:/courses";
    }

    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}
