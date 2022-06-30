package com.barbarum.sample.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathConstants {
    
    public static final String ROOT = "/";

    public static final String WELCOME = "/welcome";

    public static final String HOME = "/home";

    public static final String LOGIN_FORM = "/login";

    public static final String LOGIN = "/auth/login";

    public static final String ADMIN = "/admin";

    public static final String SYS_MANAGER = "/management";

    public static final String USER_POSTS = "/posts";

    public static final String USER_POST_BY_ID = "/posts/{id}";
}
