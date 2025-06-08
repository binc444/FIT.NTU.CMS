package FIT.CMS.N4.repository;

import FIT.CMS.N4.entity.Page;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PageRepository extends JpaRepository<Page, Long>
{
	boolean existsBySlug(String slug);

	Optional<Page> findBySlug(String slug);
}
