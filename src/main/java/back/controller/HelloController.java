package back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/admin")
    public String greetingAdmin(){
        return "Hello ADMIN";
    }

    @GetMapping("/user")
    public String greetingUser(){
        return "Hello USER";
    }

//    @GetMapping("/secured")
//    public String secured(@AuthenticationPrincipal UserPrincipal principal){
//        return "Zalogowano jako: " + principal.getEmail() + " User ID: " + principal.getUserId();}
//
//    @GetMapping("/admin")
//    public String admin(@AuthenticationPrincipal UserPrincipal principal){
//        return "Hello Admin :)\nUser ID: " + principal.getUserId();
//    }
}
