package com.example.ProjectDAC.request;

import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteCategoryBindingRequest {
    @NotNull(message = "Entity ID can not be blank")
    private long entityId;
    @NotNull(message = "Type Category can not be blank")
    private ETypeCategory typeCategory;
}
