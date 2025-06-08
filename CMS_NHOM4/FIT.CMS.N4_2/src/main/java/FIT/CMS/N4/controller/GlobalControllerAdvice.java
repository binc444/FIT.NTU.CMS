package FIT.CMS.N4.controller;

import FIT.CMS.N4.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
// @ControllerAdvice đánh dấu đây là lớp giúp thêm dữ liệu chung (common) vào mọi view.
public class GlobalControllerAdvice
{

    @Autowired
    // Tiêm SettingRepository.
    private SettingRepository settingRepo;

    @Autowired
    // Tiêm MenuRepository.
    private MenuRepository menuRepo;

    @Autowired
    // Tiêm PostRepository.
    private PostRepository postRepo;

    @Autowired
    // Tiêm PageRepository.
    private PageRepository pageRepo;

    @Autowired
    // Tiêm EventRepository.
    private EventRepository eventRepo;

    @Autowired
    // Tiêm NotificationRepository.
    private NotificationRepository notifRepo;

    @ModelAttribute
    // Phương thức này sẽ chạy trước mọi controller, tự động thêm các thuộc tính vào model.
    public void addGlobalAttributes(Model model)
    {
        // Thêm cấu hình chung (settings) vào model, lấy bản ghi id=1 hoặc null.
        model.addAttribute("settings", settingRepo.findById(1).orElse(null));
        // Thêm danh sách menu sắp xếp vào model.
        model.addAttribute("menus", menuRepo.findAllByOrderBySortOrder());

        // Đưa tất cả posts (sắp xếp theo createdAt giảm dần) vào model (dùng cho dropdown, sidebar, v.v.).
        model.addAttribute("allPosts", postRepo.findAll(Sort.by("createdAt").descending()));
        // Đưa tất cả pages (sắp xếp theo createdAt giảm dần) vào model.
        model.addAttribute("allPages", pageRepo.findAll(Sort.by("createdAt").descending()));
        // Đưa tất cả events (sắp xếp theo eventDate giảm dần) vào model.
        model.addAttribute("allEvents", eventRepo.findAll(Sort.by("eventDate").descending()));
        // Đưa tất cả notifications (sắp xếp theo createdAt giảm dần) vào model.
        model.addAttribute("allNotifs", notifRepo.findAll(Sort.by("createdAt").descending()));
    }
}
