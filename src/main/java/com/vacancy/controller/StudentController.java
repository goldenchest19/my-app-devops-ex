package com.vacancy.controller;

import com.vacancy.dto.StudentDto;
import com.vacancy.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы со студентами.
 * Предоставляет API для CRUD операций со студентами.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
@Tag(name = "Student Controller", description = "API для управления студентами")
public class StudentController {
    private final StudentService studentService;

    /**
     * Создает нового студента.
     *
     * @param studentDto DTO с данными студента
     * @return созданный студент
     */
    @PostMapping
    @Operation(summary = "Создать нового студента", description = "Создает нового студента")
    public ResponseEntity<StudentDto> createStudent(@RequestBody StudentDto studentDto) {
        log.info("Получен запрос на создание студента: {}", studentDto.getName());
        StudentDto createdStudent = studentService.createStudent(studentDto);
        log.info("Студент успешно создан с ID: {}", createdStudent.getId());
        return ResponseEntity.ok(createdStudent);
    }

    /**
     * Получает студента по ID.
     *
     * @param id ID студента
     * @return студент
     */
    @GetMapping("/{id}")
    @Operation(summary = "Получить студента по ID", description = "Возвращает студента по указанному ID")
    public ResponseEntity<StudentDto> getStudent(@PathVariable Integer id) {
        log.info("Получен запрос на получение студента с ID: {}", id);
        StudentDto student = studentService.getStudent(id);
        log.info("Студент успешно получен для ID: {}", student.getId());
        return ResponseEntity.ok(student);
    }

    /**
     * Получает список всех студентов.
     *
     * @return список студентов
     */
    @GetMapping
    @Operation(summary = "Получить всех студентов", description = "Возвращает список всех студентов")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        log.info("Получен запрос на получение всех студентов");
        List<StudentDto> students = studentService.getAllStudents();
        log.info("Успешно получено {} студентов", students.size());
        return ResponseEntity.ok(students);
    }

    /**
     * Обновляет студента.
     *
     * @param id ID студента
     * @param studentDto DTO с новыми данными студента
     * @return обновленный студент
     */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить студента", description = "Обновляет данные студента по указанному ID")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        log.info("Получен запрос на обновление студента с ID: {}", id);
        StudentDto updatedStudent = studentService.updateStudent(id, studentDto);
        log.info("Студент успешно обновлен для ID: {}", updatedStudent.getId());
        return ResponseEntity.ok(updatedStudent);
    }

    /**
     * Удаляет студента.
     *
     * @param id ID студента
     * @return статус операции
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить студента", description = "Удаляет студента по указанному ID")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        log.info("Получен запрос на удаление студента с ID: {}", id);
        studentService.deleteStudent(id);
        log.info("Студент успешно удален для ID: {}", id);
        return ResponseEntity.ok().build();
    }
}