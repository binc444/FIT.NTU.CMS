package FIT.CMS.N4.controller;

import FIT.CMS.N4.entity.Setting;
import FIT.CMS.N4.repository.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
// Đánh dấu đây là Controller.
@RequestMapping("/admin/settings")
// Tất cả URL bắt đầu bằng "/admin/settings" vào controller này.
public class AdminSettingController
{

    @Autowired
    // Spring gán SettingRepository vào thuộc tính settingRepo.
    private SettingRepository settingRepo;

    @GetMapping
    // Xử lý GET request tới "/admin/settings".
    public String editForm(Model model)
    {
        // Lấy setting có id=1, nếu không có (lần đầu), tạo mới một Setting.
        Setting s = settingRepo.findById(1).orElse(new Setting());
        // Đưa setting vào model để hiển thị form chỉnh sửa.
        model.addAttribute("settings", s);
        // Trả về view "admin/settings/ad-settings-form".
        return "admin/settings/ad-settings-form";
    }

    @PostMapping("/save")
    // Xử lý POST request tới "/admin/settings/save".
    public String save(@ModelAttribute Setting form,
                       @RequestParam("logoFile") MultipartFile logo)
            throws IOException
    {
        // Lấy setting gốc (id=1) nếu đã có, nếu chưa có thì sẽ dùng form làm bản gốc.
        Setting s = settingRepo.findById(1).orElse(form);

        // Cập nhật các trường siteTitle, slogan, email, phone, facebookUrl, youtubeUrl.
        s.setSiteTitle(form.getSiteTitle());
        s.setSlogan(form.getSlogan());
        s.setEmail(form.getEmail());
        s.setPhone(form.getPhone());
        s.setFacebookUrl(form.getFacebookUrl());
        s.setYoutubeUrl(form.getYoutubeUrl());

        // Nếu có file logo upload, chuyển thành byte[] và gán vào thuộc tính logo.
        if (!logo.isEmpty())
        {
            s.setLogo(logo.getBytes());
        }

        // Lưu setting vào DB (tạo mới hoặc cập nhật).
        settingRepo.save(s);
        // Redirect về trang admin chính (/admin).
        return "redirect:/admin";
    }
}
