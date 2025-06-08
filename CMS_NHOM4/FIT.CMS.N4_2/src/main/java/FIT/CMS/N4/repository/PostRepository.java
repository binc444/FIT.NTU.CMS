package FIT.CMS.N4.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import FIT.CMS.N4.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>
{
	boolean existsBySlug(String slug);

	Optional<Post> findBySlug(String slug);
}