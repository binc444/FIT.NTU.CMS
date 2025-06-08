package FIT.CMS.N4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
// Đánh dấu lớp này là Controller.
public class LoginController
{

    @GetMapping("/login")
    // Khi có GET request tới "/login", gọi phương thức này.
    public String login()
    {
        // Trả về view "login" (File template: src/main/resources/templates/login.html).
        return "login";
    }
}
