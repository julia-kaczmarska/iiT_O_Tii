package back.repository;

import back.controller.dto.CashflowRecordDTO;
import back.model.CashflowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CashflowRecordRepository extends JpaRepository<CashflowRecord, Long> {

    // GET records
    @Query("SELECT new back.controller.dto.CashflowRecordDTO(r.cashflowRecordId, r.amount, r.startDate, r.recordType, r.desc, c.categoryId, u.userId) " +
            "FROM CashflowRecord r " +
            "JOIN r.category c " +
            "JOIN r.user u " +
            "WHERE u.userId = :userId")
    List<CashflowRecordDTO> findRecordsByUserId(@Param("userId") Long userId);


    // DELETE record
    @Query("SELECT r " +
            "FROM CashflowRecord r " +
            "JOIN r.category c " +
            "WHERE r.cashflowRecordId = :cashflowRecordId AND r.user.userId = :userId")
    Optional<CashflowRecord> findByCashflowRecordIdAndUserId(@Param("userId") Long userId, @Param("cashflowRecordId") Long cashflowRecordId);

}

