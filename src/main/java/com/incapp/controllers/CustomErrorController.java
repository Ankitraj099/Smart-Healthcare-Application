package com.incapp.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object error = request.getAttribute("javax.servlet.error.error_message");
        Object message = request.getAttribute("javax.servlet.error.message");
        model.addAttribute("status", status);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "error";
    }

    // Deprecated in newer Spring Boot, but can be left for compatibility
    public String getErrorPath() {
        return "/error";
    }
} 