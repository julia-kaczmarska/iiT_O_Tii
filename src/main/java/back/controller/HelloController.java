package back.controller;

import back.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/")
    public String greeting(){
        return "Hello World";
    }
    @GetMapping("/secured")
    public String secured(@AuthenticationPrincipal UserPrincipal principal){
        return "Zalogowano jako: " + principal.getEmail() + " User ID: " + principal.getUserId();}

    @GetMapping("/admin")
    public String admin(@AuthenticationPrincipal UserPrincipal principal){
        return "Hello Admin :)\nUser ID: " + principal.getUserId();
    }
}
