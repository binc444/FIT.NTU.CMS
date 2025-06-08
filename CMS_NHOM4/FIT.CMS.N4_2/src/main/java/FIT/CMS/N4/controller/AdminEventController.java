package FIT.CMS.N4.controller;

import FIT.CMS.N4.entity.Event;
import FIT.CMS.N4.repository.EventRepository;
import FIT.CMS.N4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import jakarta.validation.Valid;

@Controller
// Đánh dấu lớp này là một Controller.
@RequestMapping("/admin/events")
// Tất cả URL bắt đầu bằng "/admin/events" sẽ vào controller này.
public class AdminEventController
{

    @Autowired
    // Spring tiêm EventRepository tự động.
    private EventRepository eventRepo;

    @Autowired
    // Spring tiêm UserRepository tự động.
    private UserRepository userRepo;

    @GetMapping
    // Xử lý GET request tới "/admin/events".
    public String list(Model model, @RequestParam(defaultValue = "0") int page)
    {
        // Lấy một trang (page) gồm 10 sự kiện, sắp xếp theo createdAt giảm dần.
        model.addAttribute("events", eventRepo.findAll(
            PageRequest.of(page, 10, Sort.by("createdAt").descending())
        ));
        // Trả về view "admin/events/ad-events-list" để hiển thị danh sách.
        return "admin/events/ad-events-list";
    }

    @GetMapping("/create")
    // Xử lý GET request tới "/admin/events/create".
    public String createForm(Model model)
    {
        Event e = new Event();
        // Đặt mặc định ngày giờ hiện tại, để form hiển thị ngày giờ mặc định.
        e.setEventDate(LocalDateTime.now());
        // Đưa đối tượng Event rỗng (mới) vào model, phục vụ cho form.
        model.addAttribute("event", e);
        // Trả về view "admin/events/ad-events-form" để hiển thị form tạo mới.
        return "admin/events/ad-events-form";
    }

    @GetMapping("/edit/{id}")
    // Xử lý GET request tới "/admin/events/edit/{id}", {id} là biến path.
    public String edit(@PathVariable Long id, Model model)
    {
        // Tìm Event theo id, nếu không tìm thấy thì ném lỗi IllegalArgumentException.
        Event e = eventRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID: " + id));
        // Đưa event tìm được vào model, để form hiển thị dữ liệu để sửa.
        model.addAttribute("event", e);
        // Trả về view "admin/events/ad-events-form" (chung form cho create và edit).
        return "admin/events/ad-events-form";
    }

    @PostMapping("/save")
    // Xử lý POST request tới "/admin/events/save" khi nhấn nút Save trong form.
    public String save(@Valid @ModelAttribute("event") Event eventForm,
                       BindingResult binding,
                       @RequestParam("file") MultipartFile file)
            throws IOException
    {
        // Nếu có lỗi validate (ví dụ ngày sai định dạng), thì hiển thị lại form.
        if (binding.hasErrors())
        {
            return "admin/events/ad-events-form";
        }

        Event ev;
        if (eventForm.getId() != null)
        {
            // Nếu eventForm có id, tức là đang sửa, tìm lại event gốc từ DB.
            ev = eventRepo.findById(eventForm.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
        } else
        {
            // Nếu không có id (tạo mới), khởi tạo một Event mới và set tác giả là user id=1 (admin).
            ev = new Event();
            ev.setAuthor(userRepo.findById(1L).orElse(null));
        }

        // Sao chép các trường cần cập nhật: title, description, eventDate.
        ev.setTitle(eventForm.getTitle());
        ev.setDescription(eventForm.getDescription());
        ev.setEventDate(eventForm.getEventDate());

        // Nếu có file upload (ảnh), chuyển đổi thành mảng byte và gán vào ảnh thumbnail.
        if (!file.isEmpty())
        {
            ev.setImageThumbnail(file.getBytes());
        }

        // Lưu event (cả tạo mới hoặc cập nhật) vào CSDL.
        eventRepo.save(ev);
        // Chuyển hướng (redirect) về danh sách sự kiện sau khi lưu.
        return "redirect:/admin/events";
    }

    @GetMapping("/delete/{id}")
    // Xử lý GET request tới "/admin/events/delete/{id}" khi nhấn nút Delete.
    public String delete(@PathVariable Long id)
    {
        // Xóa event theo id.
        eventRepo.deleteById(id);
        // Chuyển hướng về lại trang danh sách.
        return "redirect:/admin/events";
    }
}
