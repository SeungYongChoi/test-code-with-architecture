package com.example.demo.mock;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.port.AuthenticationService;
import com.example.demo.user.controller.port.UserCreateService;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.port.UserUpdateService;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {

    public final MailSender mailSender;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final UserReadService userReadService;
    public final UserUpdateService userUpdateService;
    public final UserCreateService userCreateService;
    public final AuthenticationService authenticationService;
    public final PostService postService;
    public final CertificationService certificationService;
    public final UserController userController;
    public final UserCreateController userCreateController;

    @Builder
    public TestContainer(ClockHolder clockHolder, UuidHolder uuidHolder) {
        this.userRepository = new FakeUserRepository();
        this.postRepository = new FakePostRepository();
        this.mailSender = new FakeMailSender();
        this.certificationService = new CertificationService(mailSender);
        this.postService = PostServiceImpl.builder()
                .postRepository(this.postRepository)
                .userRepository(this.userRepository)
                .clockHolder(clockHolder)
                .build();

        UserServiceImpl userService = UserServiceImpl.builder()
                .userRepository(this.userRepository)
                .clockHolder(clockHolder)
                .uuidHolder(uuidHolder)
                .certificationService(this.certificationService)
                .build();
        this.userCreateService = userService;
        this.userUpdateService = userService;
        this.userReadService = userService;
        this.authenticationService = userService;

        this.userController = UserController.builder()
                .userCreateService(userCreateService)
                .userUpdateService(userUpdateService)
                .userReadService(userReadService)
                .authenticationService(authenticationService)
                .build();
        this.userCreateController = UserCreateController.builder()
                .userCreateService(userCreateService).build();

    }


}
