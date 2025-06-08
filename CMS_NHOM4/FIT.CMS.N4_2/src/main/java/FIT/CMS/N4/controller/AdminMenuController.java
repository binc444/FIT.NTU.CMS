package FIT.CMS.N4.controller;
// Khai báo gói (package) để Java biết lớp này nằm ở đâu.

import FIT.CMS.N4.entity.Menu;
// Nhập lớp Menu (thực ra là entity tương ứng với bảng menus).

import FIT.CMS.N4.repository.MenuRepository;
// Nhập Interface MenuRepository, để gọi các method truy xuất CSDL (CRUD).

import FIT.CMS.N4.repository.PageRepository;
import FIT.CMS.N4.repository.PostRepository;
import FIT.CMS.N4.repository.EventRepository;
import FIT.CMS.N4.repository.NotificationRepository;
// Nhập các repository khác (Post, Page, Event, Notification) để Admin chọn liên kết nội dung.

import org.springframework.beans.factory.annotation.Autowired;
// Nhập @Autowired để Spring tự động tiêm (inject) đối tượng.

import org.springframework.stereotype.Controller;
// Nhập @Controller để đánh dấu lớp này là một controller.

import org.springframework.ui.Model;
// Nhập Model để truyền dữ liệu từ Controller xuống View.

import org.springframework.web.bind.annotation.*;
// Nhập @GetMapping, @PostMapping, @PathVariable, @RequestParam, @ModelAttribute.

import java.util.List;
// Nhập danh sách (List) để chứa kết quả query ra.
import java.util.Optional;

@Controller
// Đánh dấu đây là Controller (các phương thức sẽ xử lý yêu cầu HTTP).
@RequestMapping("/admin/menus")
// Tất cả URL bắt đầu bằng "/admin/menus" sẽ vào controller này.
public class AdminMenuController
{

	@Autowired
	// Spring tự động gán đối tượng MenuRepository vào menuRepo.
	private MenuRepository menuRepo;

	@Autowired
	// Nhằm lấy danh sách Page (nếu Admin muốn gán menu trỏ tới 1 page cụ thể).
	private PageRepository pageRepo;

	@Autowired
	// Lấy danh sách Post (nếu Admin chọn menu.content_type = POST).
	private PostRepository postRepo;

	@Autowired
	// Lấy danh sách Event.
	private EventRepository eventRepo;

	@Autowired
	// Lấy danh sách Notification.
	private NotificationRepository notifRepo;

	// ─────────────────────────────────────────────────────────────────────
	// 1) HIỂN THỊ DANH SÁCH MENU
	// URL: GET /admin/menus
	@GetMapping
	public String list(Model model)
	{
		// Lấy toàn bộ menu (đã được sắp xếp theo sortOrder bởi repository).
		List<Menu> menus = menuRepo.findAllByOrderBySortOrder();
		// Đưa "menus" vào model để Thymeleaf hiển thị
		model.addAttribute("menus", menus);
		// Trả về view "admin/menus/ad-menus-list.html"
		return "admin/menus/ad-menus-list";
	}

	// ─────────────────────────────────────────────────────────────────────
	// 2) HIỂN THỊ FORM TẠO MỚI MENU
	// URL: GET /admin/menus/create
	@GetMapping("/create")
	public String createForm(Model model)
	{
		// Tạo 1 đối tượng Menu rỗng để gán vào form
		Menu m = new Menu();
		// Đặt giá trị mặc định contentType = "CUSTOM" (hoặc "PAGE")
		m.setContentType("CUSTOM");
		// Đưa menu rỗng vào model, để form bindi
		model.addAttribute("menu", m);

		// Đưa vào model các lựa chọn contentType (Page, Post, Event, Notif, Custom)
		// để Thymeleaf hiển thị dropdown. Mình có thể đưa trực tiếp danh sách chuỗi.
		model.addAttribute("contentTypes", List.of("PAGE", "POST", "EVENT", "NOTIF", "CUSTOM"));

		// Nếu Admin muốn chọn Page làm nội dung, ta cũng cần list Page nào có sẵn
		model.addAttribute("allPages", pageRepo.findAll());
		// Tương tự với Post
		model.addAttribute("allPosts", postRepo.findAll());
		// Tương tự với Event
		model.addAttribute("allEvents", eventRepo.findAll());
		// Tương tự với Notification
		model.addAttribute("allNotifs", notifRepo.findAll());

		// Trả về form new: "admin/menus/ad-menus-form.html"
		return "admin/menus/ad-menus-form";
	}

	// ─────────────────────────────────────────────────────────────────────
	// 3) HIỂN THỊ FORM SỬA MENU
	// URL: GET /admin/menus/edit/{id}
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model)
	{
		// Tìm Menu theo id (nếu không có, ném Exception)
		Menu m = menuRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu ID: " + id));
		// Đưa menu hiện tại vào model (để điền giá trị vào form)
		model.addAttribute("menu", m);

		// Cũng giống createForm, đưa những danh sách lựa chọn vào model
		model.addAttribute("contentTypes", List.of("PAGE", "POST", "EVENT", "NOTIF", "CUSTOM"));
		model.addAttribute("allPages", pageRepo.findAll());
		model.addAttribute("allPosts", postRepo.findAll());
		model.addAttribute("allEvents", eventRepo.findAll());
		model.addAttribute("allNotifs", notifRepo.findAll());

		return "admin/menus/ad-menus-form";
	}

	// ─────────────────────────────────────────────────────────────────────
	// 4) XỬ LÝ LƯU MENU (TẠO MỚI HOẶC CẬP NHẬT)
	// URL: POST /admin/menus/save
	@PostMapping("/save")
	public String save(@ModelAttribute("menu") Menu menuForm)
	{
		// menuForm là dữ liệu được binding từ form (title, url, sortOrder, contentType,
		// contentId,...)

		// Nếu Admin chọn contentType ≠ "CUSTOM", chúng ta sẽ tự động thiết lập
		// cột url = chuỗi tương ứng (ví dụ contentType = "PAGE" & contentId=5 → url =
		// "/pages/{slugOf5}"
		// hoặc contentType = "POST" & contentId = null → url = "/posts", v.v.
		String ct = menuForm.getContentType();

		// Nếu contentType là PAGE và có contentId, ta tìm Page đó để lấy slug
		if ("PAGE".equals(ct))
		{
			if (menuForm.getContentId() != null)
			{
				// Lấy page theo id
				Optional<FIT.CMS.N4.entity.Page> op = pageRepo.findById(menuForm.getContentId());
				if (op.isPresent())
				{
					// url = "/pages/{slug}"
					menuForm.setUrl("/pages/" + op.get().getSlug());
				}
			} else
			{
				// Nếu Admin không chọn contentId mà vẫn chọn contentType = PAGE,
				// ta đặt url = "/pages" (dẫn tới trang danh sách)
				menuForm.setUrl("/pages");
			}

		} else if ("POST".equals(ct))
		{
			// Nếu Post, contentId có thể null (dẫn tới "/posts"), hoặc nếu Admin
			// chọn 1 post cụ thể (getId), thì dẫn tới "/posts/{slug}".
			if (menuForm.getContentId() != null)
			{
				Optional<FIT.CMS.N4.entity.Post> op = postRepo.findById(menuForm.getContentId());
				if (op.isPresent())
				{
					menuForm.setUrl("/posts/" + op.get().getSlug());
				}
			} else
			{
				menuForm.setUrl("/posts");
			}

		} else if ("EVENT".equals(ct))
		{
			// Tương tự: nếu có contentId → "/events/{id}", ngược lại "/events"
			if (menuForm.getContentId() != null)
			{
				menuForm.setUrl("/events/" + menuForm.getContentId());
			} else
			{
				menuForm.setUrl("/events");
			}

		} else if ("NOTIF".equals(ct))
		{
			// Tương tự: contentId → "/notifications/{id}", else "/notifications"
			if (menuForm.getContentId() != null)
			{
				menuForm.setUrl("/notifications/" + menuForm.getContentId());
			} else
			{
				menuForm.setUrl("/notifications");
			}

		} else if ("CUSTOM".equals(ct))
		{
			// Nếu CUSTOM, ta để nguyên menuForm.getUrl() (Admin đã nhập gì thì lưu nấy).
			// Ví dụ: "https://facebook.com" hoặc "/admissions".
			// Không làm gì thêm.
		}

		// Cuối cùng, lưu menuForm (tạo mới hoặc cập nhật) vào database
		menuRepo.save(menuForm);
		// Redirect về danh sách menu
		return "redirect:/admin/menus";
	}

	// ─────────────────────────────────────────────────────────────────────
	// 5) XÓA MENU
	// URL: GET /admin/menus/delete/{id}
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id)
	{
		menuRepo.deleteById(id);
		return "redirect:/admin/menus";
	}
}
