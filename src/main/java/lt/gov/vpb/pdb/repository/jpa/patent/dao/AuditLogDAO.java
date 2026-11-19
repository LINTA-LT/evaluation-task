package lt.gov.vpb.pdb.repository.jpa.patent.dao;

import lt.gov.vpb.pdb.repository.jpa.patent.domain.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("auditLogDAO")
public interface AuditLogDAO extends JpaRepository<AuditLogEntity, Long> {

    List<AuditLogEntity> findAllByTypeOrderByCreatedAtDesc(@Param("type") String type);

}
