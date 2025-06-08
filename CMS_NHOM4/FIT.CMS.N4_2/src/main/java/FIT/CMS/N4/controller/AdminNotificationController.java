package FIT.CMS.N4.controller;

import FIT.CMS.N4.entity.Notification;
import FIT.CMS.N4.repository.NotificationRepository;
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
@RequestMapping("/admin/notifications")
// Tất cả URL bắt đầu bằng "/admin/notifications" vào controller này.
public class AdminNotificationController
{

	@Autowired
	// Spring tự gán NotificationRepository vào thuộc tính notifRepo.
	private NotificationRepository notifRepo;

	@Autowired
	// Spring tự gán UserRepository vào userRepo.
	private UserRepository userRepo;

	@GetMapping
	// Xử lý GET request tới "/admin/notifications".
	public String list(Model model, @RequestParam(defaultValue = "0") int page)
	{
		// Lấy trang (page) gồm 10 thông báo, sắp xếp theo createdAt giảm dần.
		model.addAttribute("notifs", notifRepo.findAll(PageRequest.of(page, 10, Sort.by("createdAt").descending())));
		// Trả về view "admin/notifications/ad-notifications-list".
		return "admin/notifications/ad-notifications-list";
	}

	@GetMapping("/create")
	// Xử lý GET request tới "/admin/notifications/create".
	public String createForm(Model model)
	{
		// Đưa một đối tượng Notification trống vào model (dùng cho form).
		model.addAttribute("notif", new Notification());
		// Trả về view "admin/notifications/ad-notifications-form".
		return "admin/notifications/ad-notifications-form";
	}

	@GetMapping("/edit/{id}")
	// Xử lý GET request tới "/admin/notifications/edit/{id}".
	public String edit(@PathVariable Long id, Model model)
	{
		// Tìm Notification theo id, ném lỗi nếu không tìm thấy.
		Notification n = notifRepo.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid notif ID: " + id));
		// Đưa đối tượng tìm được vào model (để form hiển thị dữ liệu).
		model.addAttribute("notif", n);
		// Trả về view "admin/notifications/ad-notifications-form".
		return "admin/notifications/ad-notifications-form";
	}

	@PostMapping("/save")
	// Xử lý POST request tới "/admin/notifications/save".
	public String save(@ModelAttribute Notification notifForm, @RequestParam("file") MultipartFile file)
			throws IOException
	{
		Notification n;
		if (notifForm.getId() != null)
		{
			// Nếu có id -> đang sửa, tìm Notification gốc.
			n = notifRepo.findById(notifForm.getId())
					.orElseThrow(() -> new IllegalArgumentException("Invalid notification ID"));
		} else
		{
			// Nếu không có id -> tạo mới, set tác giả là user id=1 (admin).
			n = new Notification();
			n.setAuthor(userRepo.findById(1L).orElse(null));
		}

		// Sao chép các trường: title, content.
		n.setTitle(notifForm.getTitle());
		n.setContent(notifForm.getContent());

		// Nếu có file upload (ảnh), chuyển thành byte[] và gán.
		if (!file.isEmpty())
		{
			n.setImageThumbnail(file.getBytes());
		}

		// Lưu notification (tạo mới hoặc cập nhật).
		notifRepo.save(n);
		// Redirect về trang danh sách thông báo.
		return "redirect:/admin/notifications";
	}

	@GetMapping("/delete/{id}")
	// Xử lý GET request tới "/admin/notifications/delete/{id}".
	public String delete(@PathVariable Long id)
	{
		// Xóa notification theo id.
		notifRepo.deleteById(id);
		// Redirect về trang danh sách thông báo.
		return "redirect:/admin/notifications";
	}
}
