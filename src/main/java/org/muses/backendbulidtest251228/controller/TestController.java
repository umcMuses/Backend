package org.muses.backendbulidtest251228.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/test")
    public String testPage(Model model) {
        model.addAttribute("message", "테스트 페이지입니다");
        model.addAttribute("timestamp", System.currentTimeMillis());
        return "test";
    }
    

    record TestResponse(String message, long timestamp) {}
}
