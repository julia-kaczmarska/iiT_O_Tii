package back.repository;

import back.controller.dto.CashflowRecordDTO;
import back.model.CashflowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashflowRecordRepository extends JpaRepository<CashflowRecord, Long> {

    @Query("SELECT new back.controller.dto.CashflowRecordDTO(r.amount, r.date, r.recordType, r.title, c.categoryId, u.userId) " +
            "FROM CashflowRecord r " +
            "JOIN r.category c " +
            "JOIN r.user u " +
            "WHERE u.userId = :userId")
    List<CashflowRecordDTO> findRecordsByUserId(@Param("userId") Long userId);


//    @Query("SELECT new back.controller.dto.CashflowRecordDTO(r.amount, r.date, r.recordType, c.categoryId, c.title, u.userId) " +
//            "FROM CashflowRecord r " +
//            "JOIN r.category c " +
//            "JOIN Sharing s ON s.cashflowRecord.cashflowRecordId = r.cashflowRecordId " +
//            "JOIN s.user u " +
//            "WHERE r.cashflowRecordId = :cashflowRecordId AND u.userId = :userId")
//    Optional<CashflowRecordDTO> findByCashflowRecordIdAndUserId(@Param("cashflowRecordId") Long cashflowRecordId, @Param("userId") Long userId);

}

