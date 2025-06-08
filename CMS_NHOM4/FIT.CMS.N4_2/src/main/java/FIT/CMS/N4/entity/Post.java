package FIT.CMS.N4.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "posts")
public class Post
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, unique = true, length = 200)
	private String slug;

	@Lob
	@Column(nullable = false)
	private String content;

	@Lob
	@Column(name = "image_thumbnail")
	private byte[] imageThumbnail;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "status_id", nullable = false)
	private Status status;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User author;

	@PrePersist
	public void prePersist()
	{
		this.createdAt = this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate()
	{
		this.updatedAt = LocalDateTime.now();
	}

	// ---- Transient helper to get image as Base64 string ----
	@Transient
	public String getImageThumbnailBase64()
	{
		return (imageThumbnail == null) ? null : Base64.getEncoder().encodeToString(imageThumbnail);
	}

	// ---- Getters & Setters ----

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

	public String getSlug()
	{
		return slug;
	}

	public void setSlug(String slug)
	{
		this.slug = slug;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public byte[] getImageThumbnail()
	{
		return imageThumbnail;
	}

	public void setImageThumbnail(byte[] imageThumbnail)
	{
		this.imageThumbnail = imageThumbnail;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public LocalDateTime getUpdatedAt()
	{
		return updatedAt;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	public User getAuthor()
	{
		return author;
	}

	public void setAuthor(User author)
	{
		this.author = author;
	}
}
