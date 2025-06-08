package FIT.CMS.N4.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "menus")
public class Menu
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Tiêu đề menu
	@Column(nullable = false, length = 100)
	private String title;

	// URL tĩnh (nếu contentType == "URL")
	@Column(length = 255)
	private String url;

	// Thứ tự hiển thị (bé → lớn)
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	// Menu cha (nếu muốn tạo menu con)
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Menu parent;

	// Nếu muốn có danh sách menu con (không bắt buộc – chỉ phục vụ nếu cần hiển thị
	// menu đệ quy)
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<Menu> children;

	// Loại nội dung liên kết: "PAGE", "POST", "EVENT", "NOTIF", hoặc "URL"
	@Column(name = "content_type", length = 10)
	private String contentType;

	// ID của nội dung tương ứng (nếu contentType == PAGE/POST/EVENT/NOTIF)
	@Column(name = "content_id")
	private Long contentId;

	// === Getters & Setters ===

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public Integer getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	public Menu getParent()
	{
		return parent;
	}

	public void setParent(Menu parent)
	{
		this.parent = parent;
	}

	public List<Menu> getChildren()
	{
		return children;
	}

	public void setChildren(List<Menu> children)
	{
		this.children = children;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public Long getContentId()
	{
		return contentId;
	}

	public void setContentId(Long contentId)
	{
		this.contentId = contentId;
	}
}
