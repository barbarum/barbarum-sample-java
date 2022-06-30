package com.barbarum.sample.persistence.repositories;

import com.barbarum.sample.persistence.entities.UserPost;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserPostRepository extends PagingAndSortingRepository<UserPost, Long> {

    public List<UserPost> findAllByAuthor(String author);

    @Override
    @PreAuthorize("#entity.author == principal.username")
    public void delete(UserPost entity);
}
