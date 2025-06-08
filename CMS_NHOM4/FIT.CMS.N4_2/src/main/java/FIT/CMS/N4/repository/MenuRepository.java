package FIT.CMS.N4.repository;

import FIT.CMS.N4.entity.Menu;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long>
{
	List<Menu> findAllByOrderBySortOrder();
}