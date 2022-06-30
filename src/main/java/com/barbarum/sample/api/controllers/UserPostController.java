package com.barbarum.sample.api.controllers;

import static com.barbarum.sample.api.PathConstants.USER_POSTS;
import static com.barbarum.sample.api.PathConstants.USER_POST_BY_ID;

import com.barbarum.sample.persistence.entities.UserPost;
import com.barbarum.sample.service.UserPostService;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserPostController {
    
    @Autowired
    private UserPostService service; 

    @GetMapping(USER_POSTS)
    public List<UserPost> getMyUserPosts(Principal principal) {
        return this.service.getAll(principal);
    }

    @PostMapping(USER_POSTS)
    public Long post(@RequestBody UserPost post, Principal principal) {
        return this.service.create(post, principal);
    }

    @DeleteMapping(USER_POST_BY_ID)
    public void delete(@PathVariable("id") Long id) {
        this.service.delete(id);
    }

}
