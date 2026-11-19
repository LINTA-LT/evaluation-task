package lt.gov.vpb.pdb.repository.jpa.patent.dao;

import lt.gov.vpb.pdb.repository.jpa.patent.domain.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("countryDAO")
public interface CountryDAO extends JpaRepository<CountryEntity, Long> {

    List<CountryEntity> findAllByOrderByNameLtAsc();

    List<CountryEntity> findAllByOrderByNameEnAsc();

}
