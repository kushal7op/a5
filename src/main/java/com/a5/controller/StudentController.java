package com.a5.controller;

import com.a5.DTO.StudentDTO;
import com.a5.model.Student;
import com.a5.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public String listStudents(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            Model model
    ) {
        List<Student> students;

        if ((name != null && !name.isEmpty()) || (email != null && !email.isEmpty())) {
            students = studentService.searchAndFilterStudents(name, email);
        } else {
            students = studentService.getAllStudents();
        }

        model.addAttribute("students", students);
        model.addAttribute("name", name);   // To retain search input in the view
        model.addAttribute("email", email); // To retain search input in the view
        return "students/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("studentDTO", new StudentDTO());
        return "students/form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("studentDTO") StudentDTO studentDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "students/form";
        }
        if (studentService.existsByEmail(studentDTO.getEmail())) {
            result.rejectValue("email", "error.studentDTO", "Email already exists");
            return "students/form";
        }

        Student student = new Student();
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        studentService.saveStudent(student);

        return "redirect:/students";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        StudentDTO dto = new StudentDTO(student.getId(), student.getName(), student.getEmail());
        model.addAttribute("studentDTO", dto);
        return "students/form";
    }

    @PostMapping("/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("studentDTO") StudentDTO studentDTO,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "students/form";
        }

        Student student = new Student();
        student.setId(id);
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        studentService.saveStudent(student);

        return "redirect:/students";
    }

    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}
