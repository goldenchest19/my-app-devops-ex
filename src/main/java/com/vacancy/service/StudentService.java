package com.vacancy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacancy.dto.StudentDto;
import com.vacancy.model.Student;
import com.vacancy.repository.StudentRepository;
import com.vacancy.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы со студентами.
 * Реализует бизнес-логику CRUD операций.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final JsonUtils jsonUtils;
    private final ObjectMapper objectMapper;
    
    /**
     * Создает нового студента.
     *
     * @param studentDto DTO с данными студента
     * @return DTO созданного студента
     */
    @Transactional
    public StudentDto createStudent(StudentDto studentDto) {
        log.info("Создание нового студента: {}", studentDto.getName());
        Student student = convertToEntity(studentDto);
        Student savedStudent = studentRepository.save(student);
        log.info("Студент успешно создан с ID: {}", savedStudent.getId());
        return convertToDto(savedStudent);
    }

    /**
     * Получает студента по ID.
     *
     * @param id ID студента
     * @return DTO студента
     */
    @Transactional(readOnly = true)
    public StudentDto getStudent(Integer id) {
        log.info("Получение студента по ID: {}", id);
        return studentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    log.error("Студент с ID {} не найден", id);
                    return new RuntimeException("Student not found");
                });
    }

    /**
     * Получает список всех студентов.
     *
     * @return список DTO студентов
     */
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        log.info("Получение списка всех студентов");
        List<StudentDto> students = studentRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        log.info("Найдено {} студентов", students.size());
        return students;
    }

    /**
     * Обновляет данные студента.
     *
     * @param id ID студента
     * @param studentDto DTO с новыми данными студента
     * @return обновленный DTO студента
     */
    @Transactional
    public StudentDto updateStudent(Integer id, StudentDto studentDto) {
        log.info("Обновление студента с ID: {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Студент с ID {} не найден", id);
                    return new RuntimeException("Student not found");
                });
        
        updateEntityFromDto(existingStudent, studentDto);
        studentRepository.save(existingStudent);
        log.info("Студент с ID {} успешно обновлен", id);
        return convertToDto(existingStudent);
    }

    /**
     * Удаляет студента.
     *
     * @param id ID студента
     */
    @Transactional
    public void deleteStudent(Integer id) {
        log.info("Удаление студента с ID: {}", id);
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Студент с ID {} не найден", id);
                    return new RuntimeException("Student not found");
                });
        
        studentRepository.deleteById(id);
        log.info("Студент с ID {} успешно удален", id);
    }

    /**
     * Конвертирует DTO в сущность Student.
     *
     * @param dto DTO студента
     * @return сущность Student
     */
    private Student convertToEntity(StudentDto dto) {
        Student entity = new Student();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setEmail(dto.getEmail());
        return entity;
    }

    /**
     * Обновляет поля сущности Student из DTO.
     *
     * @param entity сущность Student
     * @param dto DTO студента
     */
    private void updateEntityFromDto(Student entity, StudentDto dto) {
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setEmail(dto.getEmail());
    }

    /**
     * Конвертирует сущность Student в DTO.
     *
     * @param entity сущность Student
     * @return DTO студента
     */
    private StudentDto convertToDto(Student entity) {
        StudentDto dto = new StudentDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setEmail(entity.getEmail());
        return dto;
    }



}