package FIT.CMS.N4.controller;

import FIT.CMS.N4.entity.Page;
import FIT.CMS.N4.entity.Post;
import FIT.CMS.N4.entity.Event;
import FIT.CMS.N4.entity.Notification;
import FIT.CMS.N4.controller.ResourceNotFoundException;
import FIT.CMS.N4.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
// Đánh dấu lớp này là Controller (phục vụ trang công khai).
public class PublicController
{

	@Autowired
	// Spring tự động tiêm PostRepository.
	private PostRepository postRepo;

	@Autowired
	// Spring tự động tiêm PageRepository.
	private PageRepository pageRepo;

	@Autowired
	// Spring tự động tiêm EventRepository.
	private EventRepository eventRepo;

	@Autowired
	// Spring tự động tiêm NotificationRepository.
	private NotificationRepository notifRepo;

	@Autowired
	// Spring tự động tiêm SettingRepository.
	private SettingRepository settingRepo;

	@Autowired
	// Spring tự động tiêm MenuRepository.
	private MenuRepository menuRepo;

	@ModelAttribute
	// Phương thức này chạy trước mọi request trong lớp PublicController,
	// thêm các thuộc tính "settings" và "menus" vào model (đã có từ
	// GlobalControllerAdvice,
	// nhưng nếu muốn riêng PublicController cũng có thể lặp lại).
	public void addCommon(Model model)
	{
		model.addAttribute("settings", settingRepo.findById(1).orElse(null));
		model.addAttribute("menus", menuRepo.findAllByOrderBySortOrder());
	}

	@GetMapping("/")
	// Xử lý GET request tới "/" (trang chủ).
	public String home(Model model, HttpServletRequest request)
	{
		// Tạo PageRequest để lấy 5 bài viết mới nhất.
		var top5 = PageRequest.of(0, 5, Sort.by("createdAt").descending());
		// Tạo PageRequest để lấy 8 trang mới nhất.
		var top8 = PageRequest.of(0, 8, Sort.by("createdAt").descending());

		// Lấy 5 bài viết mới nhất, cho vào model với key "latestPosts".
		model.addAttribute("latestPosts", postRepo.findAll(top5).getContent());
		// Lấy 8 trang mới nhất, cho vào model với key "latestPages".
		model.addAttribute("latestPages", pageRepo.findAll(top8).getContent());
		// Lấy 8 sự kiện mới nhất, cho vào model với key "latestEvents".
		model.addAttribute("latestEvents", eventRepo.findAll(top8).getContent());
		// Lấy 8 thông báo mới nhất, cho vào model với key "latestNotifs".
		model.addAttribute("latestNotifs", notifRepo.findAll(top8).getContent());

		// Add current request URI for active menu highlighting
		model.addAttribute("currentUri", request.getRequestURI());

		// Trả về view "public/index" (src/main/resources/templates/public/index.html).
		return "public/index";
	}

	// ─── POSTS ─────────────────────────────────────────────────────────────────

	@GetMapping("/posts")
	// Xử lý GET request tới "/posts" (danh sách bài viết).
	public String listPosts(Model model)
	{
		// Lấy toàn bộ bài viết, sắp xếp theo createdAt giảm dần.
		List<Post> all = postRepo.findAll(Sort.by("createdAt").descending());
		// Đưa danh sách posts vào model.
		model.addAttribute("posts", all);
		// Trả về view "public/posts/posts-list".
		return "public/posts/posts-list";
	}

	@GetMapping("/posts/{slug}")
	// Xử lý GET request tới "/posts/{slug}" (hiển thị chi tiết bài viết theo slug).
	public String showPost(@PathVariable String slug, Model model)
	{
		// Tìm bài viết theo slug, nếu tìm được thì đưa vào model với key "post",
		// nếu không tìm được thì ném ResourceNotFoundException (trả 404).
		postRepo.findBySlug(slug).ifPresentOrElse(p -> model.addAttribute("post", p), () ->
		{
			throw new ResourceNotFoundException();
		});
		// Trả về view "public/posts/posts-detail".
		return "public/posts/posts-detail";
	}

	// ─── PAGES ─────────────────────────────────────────────────────────────────

	@GetMapping("/pages")
	// Xử lý GET request tới "/pages" (danh sách trang).
	public String listPages(Model model)
	{
		// Lấy toàn bộ trang, sắp xếp theo createdAt giảm dần.
		List<Page> all = pageRepo.findAll(Sort.by("createdAt").descending());
		// Đưa danh sách pages vào model.
		model.addAttribute("pages", all);
		// Trả về view "public/pages/pages-list".
		return "public/pages/pages-list";
	}

	@GetMapping("/pages/{slug}")
	// Xử lý GET request tới "/pages/{slug}" (chi tiết trang theo slug).
	public String showPage(@PathVariable String slug, Model model)
	{
		// Tìm page theo slug, nếu tìm được đưa vào model với key "page",
		// nếu không tìm được, ném lỗi 404.
		pageRepo.findBySlug(slug).ifPresentOrElse(p -> model.addAttribute("page", p), () ->
		{
			throw new ResourceNotFoundException();
		});
		// Trả về view "public/pages/pages-detail".
		return "public/pages/pages-detail";
	}

	// ─── EVENTS ────────────────────────────────────────────────────────────────

	@GetMapping("/events")
	// Xử lý GET request tới "/events" (danh sách sự kiện).
	public String listEvents(Model model)
	{
		// Lấy toàn bộ sự kiện, sắp xếp theo eventDate giảm dần.
		List<Event> all = eventRepo.findAll(Sort.by("eventDate").descending());
		// Đưa danh sách events vào model.
		model.addAttribute("events", all);
		// Trả về view "public/events/events-list".
		return "public/events/events-list";
	}

	@GetMapping("/events/{id}")
	// Xử lý GET request tới "/events/{id}" (chi tiết sự kiện theo id).
	public String showEvent(@PathVariable Long id, Model model)
	{
		// Tìm event theo id, nếu tìm được đưa vào model với key "event",
		// nếu không tìm được, ném lỗi 404.
		eventRepo.findById(id).ifPresentOrElse(e -> model.addAttribute("event", e), () ->
		{
			throw new ResourceNotFoundException();
		});
		// Trả về view "public/events/events-detail".
		return "public/events/events-detail";
	}

	// ─── NOTIFICATIONS ────────────────────────────────────────────────────────

	@GetMapping("/notifications")
	// Xử lý GET request tới "/notifications" (danh sách thông báo).
	public String listNotifs(Model model)
	{
		// Lấy toàn bộ notifications, sắp xếp theo createdAt giảm dần.
		List<Notification> all = notifRepo.findAll(Sort.by("createdAt").descending());
		// Đưa danh sách notifications vào model.
		model.addAttribute("notifications", all);
		// Trả về view "public/notifications/notifications-list".
		return "public/notifications/notifications-list";
	}

	@GetMapping("/notifications/{id}")
	// Xử lý GET request tới "/notifications/{id}" (chi tiết thông báo theo id).
	public String showNotif(@PathVariable Long id, Model model)
	{
		// Tìm notif theo id, nếu tìm được đưa vào model với key "notification",
		// nếu không tìm được, ném lỗi 404.
		notifRepo.findById(id).ifPresentOrElse(n -> model.addAttribute("notification", n), () ->
		{
			throw new ResourceNotFoundException();
		});
		// Trả về view "public/notifications/notifications-detail".
		return "public/notifications/notifications-detail";
	}
}
