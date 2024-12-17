package com.example.ProjectDAC.request;

import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryBindingRequest {
    @NotNull(message = "Category ID can not be blank")
    private long categoryId;

    @NotNull(message = "Entity ID can not be blank")
    private long entityId;

    @NotNull(message = "Entity Type can not be blank")
    private ETypeCategory entityType;
}
