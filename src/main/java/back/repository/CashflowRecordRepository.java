package back.repository;

import back.controller.dto.CashflowRecordDTO;
import back.model.CashflowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query("SELECT cr FROM CashflowRecord cr WHERE cr.user.userId = :userId AND cr.recordType = :recordType AND FUNCTION('MONTH', cr.startDate) = :month AND FUNCTION('YEAR', cr.startDate) = :year")
    List<CashflowRecord> findAllByUserIdAndRecordTypeAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("recordType") boolean recordType,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT cr FROM CashflowRecord cr WHERE cr.user.userId = :userId AND cr.recordType = :recordType AND cr.category.categoryId = :categoryId AND FUNCTION('MONTH', cr.startDate) = :month AND FUNCTION('YEAR', cr.startDate) = :year")
    List<CashflowRecord> findAllByUserIdAndRecordTypeAndCategoryAndMonthAndYear(
            @Param("userId") Long userId,
            @Param("recordType") boolean recordType,
            @Param("categoryId") Long categoryId,
            @Param("month") int month,
            @Param("year") int year
    );


    @Query("SELECT cr FROM CashflowRecord cr WHERE cr.user.userId = :userId " +
            "AND cr.recordType = :recordType " +
            "AND cr.startDate BETWEEN :startDate AND :endDate")
    List<CashflowRecord> findByUserIdAndRecordTypeAndDateRange(
            @Param("userId") Long userId,
            @Param("recordType") boolean recordType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);


    @Query("SELECT cr FROM CashflowRecord cr WHERE cr.user.userId = :userId AND cr.cashflowRecordId IN :recordIds")
    List<CashflowRecord> findAllByUserIdAndRecordIds(@Param("userId") Long userId, @Param("recordIds") List<Long> recordIds);



}