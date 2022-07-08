package com.yas.product.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String slug;

    private String metaKeyword;

    private String metaDescription;

    private Short displayOrder;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private List<Category> categories = new ArrayList<>();

    private ZonedDateTime createdOn = ZonedDateTime.now();

    private String createdBy;

    private ZonedDateTime lastModifiedOn = ZonedDateTime.now();

    private String lastModifiedBy;
}
