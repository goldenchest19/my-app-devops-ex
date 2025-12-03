package com.vacancy.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * DTO для передачи данных резюме.
 */
@Data
@Schema(description = "DTO для резюме")
public class StudentDto {
    private Integer id;

    private String name;

    private String lastName;

    private String middleName;

    private String email;
} 