package org.scottishtecharmy.wishaw_java.repository;

import org.scottishtecharmy.wishaw_java.entity.NotificationRecord;
import org.scottishtecharmy.wishaw_java.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, String> {
    List<NotificationRecord> findByUserAccount_IdOrderByCreatedAtDesc(String userId);
    List<NotificationRecord> findByTypeOrderByCreatedAtDesc(NotificationType type);
}
