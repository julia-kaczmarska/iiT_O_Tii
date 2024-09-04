package back.repository;


import back.model.Sharing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface SharingRepository extends JpaRepository<Sharing, Long>{

//    Optional<Sharing> findByCashflowRecordId(Long cashflowRecordId);
    Optional<Sharing> findByCashflowRecord_CashflowRecordId(Long cashflowRecordId);


}
