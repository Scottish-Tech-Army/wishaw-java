package org.scottishtecharmy.wishaw_java.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wishaw")
@Tag(name = "Welcome", description = "Public welcome endpoint")
public class WelcomeController {

    @GetMapping("/welcome")
    @Operation(summary = "Welcome message", description = "Returns a simple welcome message (no auth required)")
    public String patch() {
        return "Welcome to Wishaw Java!";
    }
}