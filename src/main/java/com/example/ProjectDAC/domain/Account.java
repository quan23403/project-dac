package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.util.constant.EMedia;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String accountCode;
    private String name;
    private EMedia media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anken_id")
    private Anken anken;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    List<Campaign> campaignList;
}
