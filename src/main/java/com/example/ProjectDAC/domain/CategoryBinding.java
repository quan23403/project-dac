package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.util.constant.EMedia;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category_binding")
@Getter
@Setter
public class CategoryBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private long entityId;
    private ETypeCategory entityType;
}
