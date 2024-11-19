package com.example.ProjectDAC.response;

import com.example.ProjectDAC.util.constant.ETypeCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCategoryBindingDTO {
    private long id;
    private long categoryId;
    private long entityId;
    private ETypeCategory typeCategory;

    public ResCategoryBindingDTO(long id, long categoryId, long entityId, ETypeCategory typeCategory) {
        this.id = id;
        this.categoryId = categoryId;
        this.entityId = entityId;
        this.typeCategory = typeCategory;
    }

    public ResCategoryBindingDTO() {

    }
}
