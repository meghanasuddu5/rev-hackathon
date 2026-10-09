package com.reverse.Hack.dto;

import jakarta.validation.constraints.*;

public class CircleDTO {
    @NotBlank @Size(min = 1, max = 100) private String name;
    @Size(max = 300) private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
