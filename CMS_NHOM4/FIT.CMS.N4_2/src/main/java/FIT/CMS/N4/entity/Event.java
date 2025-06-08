package FIT.CMS.N4.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "events")
public class Event
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Lob
	@Column(nullable = false)
	private String description;

	@Lob
	@Column(name = "image_thumbnail")
	private byte[] imageThumbnail;

	@Column(name = "event_date", nullable = false)
	// ← tell Spring how to parse the String
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime eventDate;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

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

	@Transient
	public String getImageThumbnailBase64()
	{
		return (imageThumbnail == null) ? null : Base64.getEncoder().encodeToString(imageThumbnail);
	}

	// Getters and Setters

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

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public byte[] getImageThumbnail()
	{
		return imageThumbnail;
	}

	public void setImageThumbnail(byte[] imageThumbnail)
	{
		this.imageThumbnail = imageThumbnail;
	}

	public LocalDateTime getEventDate()
	{
		return eventDate;
	}

	public void setEventDate(LocalDateTime eventDate)
	{
		this.eventDate = eventDate;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt)
	{
		this.updatedAt = updatedAt;
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
