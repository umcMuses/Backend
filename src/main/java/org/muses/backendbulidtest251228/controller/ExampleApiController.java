package org.muses.backendbulidtest251228.controller;

import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * API 응답 포맷 예시 컨트롤러
 */
@RestController
@RequestMapping("/api")
public class ExampleApiController {

    /**
     * GET 요청 예시
     */
    @GetMapping("/example/simple")
    public ApiResponse<Map<String, Object>> simple() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "안녕하세요");
        data.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(data);
    }

    /**
     * POST 요청 예시
     */
    @PostMapping("/example/echo")
    public ApiResponse<Map<String, Object>> echo(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", body);
        response.put("timestamp", System.currentTimeMillis());
        return ApiResponse.success(response);
    }

    /**
     * 에러 응답 예시
     */
    @GetMapping("/example/error/notfound")
    public ApiResponse<Void> errorNotFound() {
        return ApiResponse.fail(ErrorCode.NOT_FOUND);
    }

    // =============== DTO 사용하는 경우 예시 ===============

//    // DTO 클래스
//    public class UserDto {
//        private Long id;
//        private String name;
//        // getter, setter...
//    }
//
//    // 컨트롤러
//    @GetMapping("/users")
//    public ApiResponse<List<UserDto>> getUsers() {
//        List<UserDto> users = userService.findAll();
//        return ApiResponse.success(users);
//    }
}
