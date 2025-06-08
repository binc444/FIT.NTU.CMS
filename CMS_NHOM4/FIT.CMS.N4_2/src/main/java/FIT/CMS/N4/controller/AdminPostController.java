package FIT.CMS.N4.controller;

import FIT.CMS.N4.entity.Post;
import FIT.CMS.N4.repository.CategoryRepository;
import FIT.CMS.N4.repository.PostRepository;
import FIT.CMS.N4.repository.StatusRepository;
import FIT.CMS.N4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Controller
// Đánh dấu đây là Controller.
@RequestMapping("/admin/posts")
// Tất cả URL bắt đầu bằng "/admin/posts" vào controller này.
public class AdminPostController
{

    @Autowired
    // Spring gán PostRepository vào postRepo.
    private PostRepository postRepo;

    @Autowired
    // Spring gán CategoryRepository vào catRepo.
    private CategoryRepository catRepo;

    @Autowired
    // Spring gán StatusRepository vào statusRepo.
    private StatusRepository statusRepo;

    @Autowired
    // Spring gán UserRepository vào userRepo.
    private UserRepository userRepo;

    // LIST với phân trang
    @GetMapping
    // Xử lý GET request tới "/admin/posts".
    public String list(Model model, @RequestParam(defaultValue = "0") int page)
    {
        // Lấy trang gồm 10 post, sắp xếp theo createdAt giảm dần.
        model.addAttribute("posts", postRepo.findAll(
            PageRequest.of(page, 10, Sort.by("createdAt").descending())
        ));
        // Trả về view "admin/posts/ad-posts-list".
        return "admin/posts/ad-posts-list";
    }

    // CREATE form
    @GetMapping("/create")
    // Xử lý GET request tới "/admin/posts/create".
    public String createForm(Model model)
    {
        // Đưa một đối tượng Post trống vào model (dùng cho form).
        model.addAttribute("post", new Post());
        // Đưa danh sách categories để chọn trong form.
        model.addAttribute("categories", catRepo.findAll());
        // Đưa danh sách statuses để chọn trong form.
        model.addAttribute("statuses", statusRepo.findAll());
        // Trả về view "admin/posts/ad-posts-form".
        return "admin/posts/ad-posts-form";
    }

    // EDIT form
    @GetMapping("/edit/{id}")
    // Xử lý GET request tới "/admin/posts/edit/{id}".
    public String edit(@PathVariable Long id, Model model)
    {
        // Tìm post theo id, nếu không tìm thấy thì ném lỗi.
        Post existing = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + id));
        // Đưa post tìm được vào model để hiển thị form edit.
        model.addAttribute("post", existing);
        // Đưa danh sách categories vào model.
        model.addAttribute("categories", catRepo.findAll());
        // Đưa danh sách statuses vào model.
        model.addAttribute("statuses", statusRepo.findAll());
        // Trả về view "admin/posts/ad-posts-form".
        return "admin/posts/ad-posts-form";
    }

    // SAVE (tạo mới hoặc cập nhật)
    @PostMapping("/save")
    // Xử lý POST request tới "/admin/posts/save".
    public String save(@ModelAttribute Post postForm,
                       @RequestParam("file") MultipartFile file)
            throws IOException
    {
        Post post;
        if (postForm.getId() != null)
        {
            // Nếu postForm có id, tức là đang sửa, tìm lại post gốc.
            post = postRepo.findById(postForm.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
        } else
        {
            // Nếu không có id, tạo mới và set tác giả là user id=1 (admin).
            post = new Post();
            post.setAuthor(userRepo.findById(1L).orElse(null));
        }

        // Copy các trường: title, slug, content.
        post.setTitle(postForm.getTitle());
        post.setSlug(postForm.getSlug());
        post.setContent(postForm.getContent());

        // Re-fetch associations by ID (lấy category từ id ở postForm).
        post.setCategory(catRepo.findById(postForm.getCategory().getId()).orElse(null));
        // Lấy status từ id ở postForm.
        post.setStatus(statusRepo.findById(postForm.getStatus().getId()).orElse(null));

        // Nếu có file upload (ảnh), gán ảnh mới.
        if (!file.isEmpty())
        {
            post.setImageThumbnail(file.getBytes());
        }

        // Lưu post (tạo mới hoặc cập nhật).
        postRepo.save(post);
        // Redirect về trang danh sách posts.
        return "redirect:/admin/posts";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    // Xử lý GET request tới "/admin/posts/delete/{id}".
    public String delete(@PathVariable Long id)
    {
        // Xóa post theo id.
        postRepo.deleteById(id);
        // Redirect về lại danh sách posts.
        return "redirect:/admin/posts";
    }
}
