package lt.gov.vpb.pdb.repository.jpa.patent.dao;


import lt.gov.vpb.pdb.repository.jpa.patent.domain.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("documentDAO")
public interface DocumentDAO extends JpaRepository<DocumentEntity, Long> {
}

