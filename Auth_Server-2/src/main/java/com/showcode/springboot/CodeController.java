package com.showcode.springboot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CodeController {
    @RequestMapping("/showcode")
    public String showcode(Model model, @RequestParam(value="code", required=false, defaultValue="World") String code) {
        model.addAttribute("code", code);
        return "showcode";
    }
}
