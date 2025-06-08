package FIT.CMS.N4.controller;
import FIT.CMS.N4.entity.Page;
import FIT.CMS.N4.repository.PageRepository;
import FIT.CMS.N4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
// Đánh dấu đây là Controller.
@RequestMapping("/admin/pages")
// Tất cả URL bắt đầu bằng "/admin/pages" sẽ vào lớp này.
public class AdminPageController
{

    @Autowired
    // Spring sẽ gán PageRepository cho thuộc tính pageRepo.
    private PageRepository pageRepo;

    @Autowired
    // Spring sẽ gán UserRepository cho thuộc tính userRepo.
    private UserRepository userRepo;

    @GetMapping
    // Xử lý GET request tới "/admin/pages".
    public String list(Model model, @RequestParam(defaultValue = "0") int page)
    {
        // Lấy trang (page) gồm 10 trang, sắp xếp theo createdAt giảm dần.
        model.addAttribute("pages", pageRepo.findAll(
            PageRequest.of(page, 10, Sort.by("createdAt").descending())
        ));
        // Trả về view "admin/pages/ad-pages-list".
        return "admin/pages/ad-pages-list";
    }

    @GetMapping("/create")
    // Xử lý GET request tới "/admin/pages/create".
    public String createForm(Model model)
    {
        // Đưa một đối tượng Page trống vào model (dùng cho form).
        model.addAttribute("page", new Page());
        // Trả về view "admin/pages/ad-pages-form".
        return "admin/pages/ad-pages-form";
    }

    @GetMapping("/edit/{id}")
    // Xử lý GET request tới "/admin/pages/edit/{id}".
    public String edit(@PathVariable Long id, Model model)
    {
        // Tìm Page theo id, nếu không có thì ném lỗi.
        Page p = pageRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid page ID: " + id));
        // Đưa page tìm được vào model để hiển thị form edit.
        model.addAttribute("page", p);
        // Trả về view "admin/pages/ad-pages-form".
        return "admin/pages/ad-pages-form";
    }

    @PostMapping("/save")
    // Xử lý POST request tới "/admin/pages/save".
    public String save(@ModelAttribute Page pageForm,
                       @RequestParam("file") MultipartFile file)
            throws IOException
    {
        Page page;
        if (pageForm.getId() != null)
        {
            // Nếu pageForm có id thì đang sửa, tìm page gốc.
            page = pageRepo.findById(pageForm.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid page ID"));
        } else
        {
            // Nếu không có id thì tạo mới, set tác giả là user id=1 (admin).
            page = new Page();
            page.setAuthor(userRepo.findById(1L).orElse(null));
        }

        // Sao chép các trường: title, slug, content.
        page.setTitle(pageForm.getTitle());
        page.setSlug(pageForm.getSlug());
        page.setContent(pageForm.getContent());

        // Nếu có file upload (ảnh), gán vào imageThumbnail.
        if (!file.isEmpty())
        {
            page.setImageThumbnail(file.getBytes());
        }

        // Lưu page (tạo mới hoặc cập nhật).
        pageRepo.save(page);
        // Redirect về danh sách pages.
        return "redirect:/admin/pages";
    }

    @GetMapping("/delete/{id}")
    // Xử lý GET request tới "/admin/pages/delete/{id}".
    public String delete(@PathVariable Long id)
    {
        // Xóa page theo id.
        pageRepo.deleteById(id);
        // Redirect về lại danh sách pages.
        return "redirect:/admin/pages";
    }
}
