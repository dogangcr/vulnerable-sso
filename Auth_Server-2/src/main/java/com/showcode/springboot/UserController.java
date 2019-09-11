package com.showcode.springboot;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public class UserController {

	  @RequestMapping("/user/me")
	    public Principal user(Principal principal) {
	        System.out.println(principal);
	        return principal;
	    }
	}