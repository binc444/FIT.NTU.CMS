package FIT.CMS.N4.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@Table(name = "settings")
public class Setting
{

	@Id
	@Column(columnDefinition = "INT CHECK (id=1)")
	private Integer id = 1;

	@Column(name = "site_title", nullable = false, length = 150)
	private String siteTitle;

	@Column(length = 255)
	private String slogan;

	@Lob
	@Column(name = "logo")
	private byte[] logo;

	@Column(length = 100)
	private String email;

	@Column(length = 50)
	private String phone;

	@Column(name = "facebook_url", length = 255)
	private String facebookUrl;

	@Column(name = "youtube_url", length = 255)
	private String youtubeUrl;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	@PreUpdate
	public void updateTimestamp()
	{
		this.updatedAt = LocalDateTime.now();
	}

	// ---- Transient helper to get logo as Base64 string ----
	@Transient
	public String getLogoBase64()
	{
		return (logo == null) ? null : Base64.getEncoder().encodeToString(logo);
	}

	// ---- Getters & Setters ----

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getSiteTitle()
	{
		return siteTitle;
	}

	public void setSiteTitle(String siteTitle)
	{
		this.siteTitle = siteTitle;
	}

	public String getSlogan()
	{
		return slogan;
	}

	public void setSlogan(String slogan)
	{
		this.slogan = slogan;
	}

	public byte[] getLogo()
	{
		return logo;
	}

	public void setLogo(byte[] logo)
	{
		this.logo = logo;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getFacebookUrl()
	{
		return facebookUrl;
	}

	public void setFacebookUrl(String facebookUrl)
	{
		this.facebookUrl = facebookUrl;
	}

	public String getYoutubeUrl()
	{
		return youtubeUrl;
	}

	public void setYoutubeUrl(String youtubeUrl)
	{
		this.youtubeUrl = youtubeUrl;
	}

	public LocalDateTime getUpdatedAt()
	{
		return updatedAt;
	}
}
