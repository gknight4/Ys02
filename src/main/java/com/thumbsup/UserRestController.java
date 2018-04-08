package com.thumbsup;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thumbsup.model.YsUser;
import com.thumbsup.model.YsUserRepository;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private YsUserRepository applicationUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserRestController(YsUserRepository applicationUserRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.applicationUserRepository = applicationUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody YsUser user) {
    	System.out.println("sign up");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setParentname("none");
        applicationUserRepository.save(user);
    }
}
