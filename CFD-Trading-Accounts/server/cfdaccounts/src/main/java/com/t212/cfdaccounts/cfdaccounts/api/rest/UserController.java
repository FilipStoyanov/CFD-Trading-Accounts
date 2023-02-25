//package com.t212.cfdaccounts.cfdaccounts.api.rest;
//
//import com.t212.cfdaccounts.cfdaccounts.api.rest.models.ApiResponse;
//import com.t212.cfdaccounts.cfdaccounts.core.UserService;
//import com.t212.cfdaccounts.cfdaccounts.core.models.User;
//import org.springframework.dao.DataAccessException;
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@CrossOrigin(value = "*")
//@RequestMapping(value = "/api/v1/users")
//public class UserController {
//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @GetMapping
//    public ResponseEntity<ApiResponse> listUsers(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize) {
//        if (page < 0 || pageSize <= 0) {
//            return ResponseEntity.status(400).body(new ApiResponse(200, "Invalid parameters"));
//        }
//        List<User> users = userService.listUsers(page, pageSize);
//        return ResponseEntity.status(200).body(new ApiResponse(200, "", users));
//    }
//
//    @GetMapping(value = "{id}")
//    public ResponseEntity<ApiResponse> getUser(@PathVariable("id") int id) {
//        if (id <= 0) {
//            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
//        }
//        try {
//            User user = userService.getById(id);
//            return ResponseEntity.status(200).body(new ApiResponse(200, "", user));
//        } catch (EmptyResultDataAccessException e) {
//            return ResponseEntity.status(404).body(new ApiResponse(404, "Not found user"));
//        }
//    }
//
//    @DeleteMapping(value = "{id}")
//    public ResponseEntity<ApiResponse> removeUser(@PathVariable("id") int id) {
//        if (id <= 0) {
//            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid path variable"));
//        }
//
//        try {
//            boolean successfullyDeleted = userService.removeUser(id);
//            if (successfullyDeleted) {
//                return ResponseEntity.status(200).body(new ApiResponse(200, "Successfully deleted"));
//            }
//        } catch (DataAccessException e) {
//            return ResponseEntity.status(404).body(new ApiResponse(400, "Not found user with this id"));
//        }
//        return ResponseEntity.status(404).body(new ApiResponse(404, "Not found user with this id"));
//    }
//
//
//}
