package FIT.CMS.N4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
// Đánh dấu khi exception này được ném ra, Spring sẽ tự động trả HTTP 404.
public class ResourceNotFoundException extends RuntimeException
{
    // Lớp này kế thừa RuntimeException: chỉ để báo lỗi "Không tìm thấy tài nguyên".
}
