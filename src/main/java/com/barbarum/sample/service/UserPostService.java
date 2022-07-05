package com.barbarum.sample.service;

import com.barbarum.sample.persistence.entities.UserPost;
import com.barbarum.sample.persistence.repositories.UserPostRepository;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPostService {
    
    @Autowired
    private UserPostRepository repository; 

    public List<UserPost> getAll(Principal principal) {
        return this.repository.findAllByAuthor(principal.getName());
    }

    public void delete(long id) {
       this.repository.findById(id).ifPresent(this.repository::delete);
    }

    @Transactional
    public Long create(UserPost post, Principal principal) {
        post.setAuthor(principal.getName());
        Iterable<UserPost> result = this.repository.saveAll(Arrays.asList(post));
        return StreamSupport.stream(result.spliterator(), false)
            .findFirst()
            .map(UserPost::getId)
            .orElse(0L);
    }

    public Optional<UserPost> getUserPost(Long id) {
        return this.repository.findById(id);
    }
}
