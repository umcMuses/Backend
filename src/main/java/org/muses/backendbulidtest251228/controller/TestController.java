package org.muses.backendbulidtest251228.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
//    /**
//     * 테스트 페이지 - Thymeleaf 뷰 반환
//     */
//    @GetMapping("/test")
//    public String testPage(Model model) {
//        model.addAttribute("message", "테스트 페이지입니다!");
//        model.addAttribute("timestamp", System.currentTimeMillis());
//        return "test";
//    }
//
//    /**
//     * API 테스트 - JSON 응답
//     */
//    @GetMapping("/api/test")
//    @ResponseBody
//    public TestResponse apiTest(@RequestParam(defaultValue = "World") String name) {
//        return new TestResponse("Hello, " + name + "!", System.currentTimeMillis());
//    }
//
//    /**
//     * 헬스체크 엔드포인트
//     */
//    @GetMapping("/health")
//    @ResponseBody
//    public String health() {
//        return "OK";
//    }
//
//    record TestResponse(String message, long timestamp) {}
}
