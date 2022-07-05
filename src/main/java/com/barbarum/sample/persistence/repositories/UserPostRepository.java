package com.barbarum.sample.persistence.repositories;

import com.barbarum.sample.persistence.entities.UserPost;
import com.barbarum.sample.service.acl.PostPersistAclPolicy;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserPostRepository extends PagingAndSortingRepository<UserPost, Long> {

    @Override
    @PostPersistAclPolicy
    public <S extends UserPost> S save(S entity);

    @Override
    @PostPersistAclPolicy
    public <S extends UserPost> Iterable<S> saveAll(Iterable<S> entities);

    @Override
    @PreAuthorize("#entity.author == principal.username")
    public void delete(UserPost entity);
    
    @Override
    @PostAuthorize("returnObject.isEmpty() or hasPermission(returnObject.get(), 'READ')")
    public Optional<UserPost> findById(Long id);


    public List<UserPost> findAllByAuthor(String author);
}
