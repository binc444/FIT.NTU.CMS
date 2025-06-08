package FIT.CMS.N4.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import FIT.CMS.N4.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>
{
}