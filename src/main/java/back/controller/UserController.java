package back.controller;

import back.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @GetMapping("/all")
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<UserDto> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }


}
