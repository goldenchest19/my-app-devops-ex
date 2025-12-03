package com.vacancy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacancy.dto.StudentDto;
import com.vacancy.model.Student;
import com.vacancy.repository.StudentRepository;
import com.vacancy.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private JsonUtils jsonUtils; // не используется сервисом, но требуется для конструктора
    @Mock
    private ObjectMapper objectMapper; // аналогично
    @InjectMocks
    private StudentService studentService;

    private StudentDto sampleDto;
    private Student sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDto = dto(null, "Ivan", "Ivanov", "Ivanovich", "ivan@example.com");
        sampleEntity = entity(1, "Ivan", "Ivanov", "Ivanovich", "ivan@example.com");
    }

    @Test
    @DisplayName("createStudent — маппинг DTO→Entity, save, DTO с присвоенным id")
    void createStudent_ok() {
        // arrange: репозиторий вернёт сущность с присвоенным id
        when(studentRepository.save(any(Student.class)))
                .thenAnswer(inv -> {
                    Student s = inv.getArgument(0);
                    s.setId(42);
                    return s;
                });
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);

        // act
        StudentDto created = studentService.createStudent(sampleDto);

        // assert: захватываем отправленную сущность и проверяем копирование полей
        verify(studentRepository).save(captor.capture());
        Student saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(sampleDto.getName());
        assertThat(saved.getLastName()).isEqualTo(sampleDto.getLastName());
        assertThat(saved.getMiddleName()).isEqualTo(sampleDto.getMiddleName());
        assertThat(saved.getEmail()).isEqualTo(sampleDto.getEmail());

        // возвращённый DTO содержит присвоенный id
        assertThat(created.getId()).isEqualTo(42);
        assertThat(created.getEmail()).isEqualTo(sampleDto.getEmail());
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("getStudent — найден → корректный DTO")
    void getStudent_ok() {
        when(studentRepository.findById(1)).thenReturn(Optional.of(sampleEntity));

        StudentDto dto = studentService.getStudent(1);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Ivan");
        assertThat(dto.getLastName()).isEqualTo("Ivanov");
        assertThat(dto.getMiddleName()).isEqualTo("Ivanovich");
        assertThat(dto.getEmail()).isEqualTo("ivan@example.com");
        verify(studentRepository).findById(1);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("getStudent — не найден → RuntimeException('Student not found')")
    void getStudent_notFound() {
        when(studentRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudent(999))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository).findById(999);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("getAllStudents — список маппится корректно")
    void getAllStudents_ok() {
        when(studentRepository.findAll()).thenReturn(List.of(
                entity(1, "Ivan", "Ivanov", "Ivanovich", "ivan@example.com"),
                entity(2, "Petr", "Petrov", null, "petr@example.com")
        ));

        List<StudentDto> all = studentService.getAllStudents();

        assertThat(all).hasSize(2);
        assertThat(all.get(0).getId()).isEqualTo(1);
        assertThat(all.get(1).getId()).isEqualTo(2);
        assertThat(all.get(1).getMiddleName()).isNull();
        verify(studentRepository).findAll();
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("getAllStudents — пусто → пустой список")
    void getAllStudents_empty() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<StudentDto> all = studentService.getAllStudents();

        assertThat(all).isEmpty();
        verify(studentRepository).findAll();
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("updateStudent — найден, поля обновляются и сохраняются")
    void updateStudent_ok() {
        Student existing = entity(10, "Old", "Last", "Mid", "old@mail");
        when(studentRepository.findById(10)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentDto patch = dto(null, "NewName", "NewLast", null, "new@mail");
        StudentDto updated = studentService.updateStudent(10, patch);

        // проверяем обновление сущности
        assertThat(existing.getName()).isEqualTo("NewName");
        assertThat(existing.getLastName()).isEqualTo("NewLast");
        assertThat(existing.getMiddleName()).isNull();
        assertThat(existing.getEmail()).isEqualTo("new@mail");

        // проверяем возвращаемый DTO
        assertThat(updated.getId()).isEqualTo(10);
        assertThat(updated.getName()).isEqualTo("NewName");

        verify(studentRepository).findById(10);
        verify(studentRepository).save(existing);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("updateStudent — не найден → RuntimeException('Student not found')")
    void updateStudent_notFound() {
        when(studentRepository.findById(777)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.updateStudent(777, sampleDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository).findById(777);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("deleteStudent — найден, репозиторий вызывает deleteById")
    void deleteStudent_ok() {
        when(studentRepository.findById(5)).thenReturn(Optional.of(entity(5, "A", "B", null, "a@b")));

        studentService.deleteStudent(5);

        verify(studentRepository).findById(5);
        verify(studentRepository).deleteById(5);
        verifyNoMoreInteractions(studentRepository);
    }

    @Test
    @DisplayName("deleteStudent — не найден → RuntimeException('Student not found')")
    void deleteStudent_notFound() {
        when(studentRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.deleteStudent(404))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Student not found");

        verify(studentRepository).findById(404);
        verifyNoMoreInteractions(studentRepository);
    }

    // -------- Helpers --------
    private static Student entity(Integer id, String name, String last, String middle, String email) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setLastName(last);
        s.setMiddleName(middle);
        s.setEmail(email);
        return s;
    }

    private static StudentDto dto(Integer id, String name, String last, String middle, String email) {
        StudentDto d = new StudentDto();
        d.setId(id);
        d.setName(name);
        d.setLastName(last);
        d.setMiddleName(middle);
        d.setEmail(email);
        return d;
    }
}