package lt.gov.vpb.pdb.repository.jpa.patent;

import lt.gov.vpb.pdb.model.patent.Document;
import lt.gov.vpb.pdb.repository.jpa.patent.dao.DocumentDAO;
import lt.gov.vpb.pdb.repository.jpa.patent.domain.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(transactionManager = "pdbpatentsTransactionManager", readOnly = true)
public class DocumentJpaRepository {

    @Autowired
    @Qualifier("documentDAO")
    DocumentDAO documentDAO;

    public Document findById(Long id) {
        Optional<DocumentEntity> entity = documentDAO.findById(id);
        if (entity.isPresent()) {
            Document document = new Document();
            document.setContent(entity.get().getContent());
            document.setFileName(entity.get().getFileName());
            return document;
        } else {
            return null;
        }
    }

}
