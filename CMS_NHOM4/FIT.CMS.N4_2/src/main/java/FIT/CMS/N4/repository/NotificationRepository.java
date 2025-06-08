package FIT.CMS.N4.repository;

import FIT.CMS.N4.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>
{
}
