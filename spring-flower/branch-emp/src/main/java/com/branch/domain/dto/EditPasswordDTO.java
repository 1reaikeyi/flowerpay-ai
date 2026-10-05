package com.branch.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPasswordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
