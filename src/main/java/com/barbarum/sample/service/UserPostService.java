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
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Service;

@Service
public class UserPostService {
    
    @Autowired
    private UserPostRepository repository; 

    @Autowired
    private JdbcMutableAclService aclService;

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

        MutableAcl acl = createAclIfNotExists(post);
        Sid sid = new PrincipalSid(principal.getName());

        CumulativePermission permission = new CumulativePermission();
        permission.set(BasePermission.READ);
        permission.set(BasePermission.WRITE);
        permission.set(BasePermission.DELETE);
        permission.set(BasePermission.ADMINISTRATION);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        this.aclService.updateAcl(acl);

        return StreamSupport.stream(result.spliterator(), false)
            .findFirst()
            .map(UserPost::getId)
            .orElse(0L);
    }

    public Optional<UserPost> getUserPost(Long id) {
        return this.repository.findById(id);
    }

    private MutableAcl createAclIfNotExists(UserPost post) {
        ObjectIdentity oi = new ObjectIdentityImpl(UserPost.class, post.getId());
        
        MutableAcl acl = null; 
        try {
            acl = (MutableAcl) this.aclService.readAclById(oi);
        } catch (NotFoundException e) {
            acl = this.aclService.createAcl(oi);
        }
        return acl;
    }
}
