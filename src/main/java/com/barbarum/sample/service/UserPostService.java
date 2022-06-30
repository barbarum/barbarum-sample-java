package com.barbarum.sample.service;

import com.barbarum.sample.persistence.entities.UserPost;
import com.barbarum.sample.persistence.repositories.UserPostRepository;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

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

    public Long create(UserPost post, Principal principal) {
        post.setAuthor(principal.getName());
        return this.repository.save(post).getId();
    }

    public Optional<UserPost> getUserPost(Long id) {
        return this.repository.findById(id);
    }

}
