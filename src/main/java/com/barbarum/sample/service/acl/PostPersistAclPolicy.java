package com.barbarum.sample.service.acl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * Annotation for persisting a ACL entity on the specified object after a method has been invoked. 
 * When &#64;PostPersistAclPolicy is added into a method, the method return object must match the following rules: 
 * <ol>
 *      <li>Its class is either an <a link="https://github.com/javaee/jpa-spec/blob/master/javax.persistence-api/src/main/java/javax/persistence/Entity.java">JPA entity</a> or Collection&lt;? extends Entity&gt; </li>
 *      <li>The JPA entity must has a <a link="https://github.com/javaee/jpa-spec/blob/master/javax.persistence-api/src/main/java/javax/persistence/Id.java">identifier</a> field</li>
 * </ol>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostPersistAclPolicy {
    
}
