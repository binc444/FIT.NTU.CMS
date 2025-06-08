package FIT.CMS.N4.controller;

import FIT.CMS.N4.repository.MenuRepository;
import FIT.CMS.N4.repository.SettingRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
// Đánh dấu đây là một lớp Controller, nghĩa là nó sẽ nhận các yêu cầu HTTP và trả về view.
@RequestMapping("/admin")
// Tất cả URL bắt đầu bằng "/admin" sẽ được chuyển vào lớp này để xử lý.
public class AdminController
{

	@Autowired
	// @Autowired cho Spring tự động tạo và gán đối tượng SettingRepository vào
	// thuộc tính settingRepo.
	private SettingRepository settingRepo;

	@Autowired
	// Tương tự, Spring sẽ gán một đối tượng MenuRepository vào menuRepo.
	private MenuRepository menuRepo;

	@GetMapping({ "/index", "/", "" })
	// Khi có GET request tới "/admin/index" hoặc "/admin/" hoặc "/admin", phương
	// thức index() này sẽ được gọi.
	public String index(Model model, HttpServletRequest request)
	{
		
		
		// Thêm dữ liệu "settings" (cấu hình chung) vào model, lấy bản ghi có id = 1
		// hoặc trả null nếu không có.
		model.addAttribute("settings", settingRepo.findById(1).orElse(null));
		// Thêm dữ liệu "menus" (danh sách menu) vào model, lấy tất cả menu sắp xếp theo
		// thứ tự đã định.
		model.addAttribute("menus", menuRepo.findAllByOrderBySortOrder());

		// Add current request URI for active menu highlighting
		model.addAttribute("currentUri", request.getRequestURI());

		// Trả về tên view "admin/index", Thymeleaf sẽ tìm file template tại
		// "templates/admin/index.html".
		return "admin/ad-index";
	}
}
