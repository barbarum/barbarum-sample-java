package com.barbarum.sample.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class UserPost {
    
    @Id
    @GeneratedValue
    private Long id; 

    @Column(name = "content")
    private String content;

    @Column(name = "author")
    private String author;
}
