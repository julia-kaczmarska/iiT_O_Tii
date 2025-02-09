package back.service;

import back.controller.dto.CashflowRecordDTO;
import back.model.*;
import back.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CashflowRecordService {

    private final CashflowRecordRepository cashflowRecordRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SharingRepository sharingRepository;
    private final MonthBudgetRepository monthBudgetRepository;
    private final PlannedBudgetRepository plannedBudgetRepository;

    public List<CashflowRecordDTO> getCashflowRecordsByTypeAndMonth(Long userId, boolean recordType, LocalDate monthStartDate) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Oblicz zakres dat dla miesiąca
        LocalDate monthEndDate = monthStartDate.withDayOfMonth(monthStartDate.lengthOfMonth());

        // Pobranie rekordów
        List<CashflowRecord> records = cashflowRecordRepository.findByUserIdAndRecordTypeAndDateRange(
                userId, recordType, monthStartDate, monthEndDate);

        // Mapowanie na DTO
        return records.stream().map(record -> new CashflowRecordDTO(
                record.getCashflowRecordId(),
                record.getAmount(),
                record.getStartDate(),
                record.getRecordType(),
                record.getDesc(),
                record.getCategory() != null ? record.getCategory().getCategoryId() : null,
                userId
        )).collect(Collectors.toList());
    }

    public List<CashflowRecordDTO> addIncomes(List<CashflowRecordDTO> cashflowRecordDTOs, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<CashflowRecordDTO> savedRecords = new ArrayList<>();

        for (CashflowRecordDTO cashflowRecordDTO : cashflowRecordDTOs) {
            if (cashflowRecordDTO.getCategoryId() != null) {
                throw new IllegalArgumentException("Category ID must be null for incomes");
            }

            CashflowRecord cashflowRecord = new CashflowRecord();
            cashflowRecord.setAmount(cashflowRecordDTO.getAmount());
            cashflowRecord.setStartDate(cashflowRecordDTO.getStartDate());
            cashflowRecord.setRecordType(false);
            cashflowRecord.setDesc(cashflowRecordDTO.getDesc());
            cashflowRecord.setUser(user);

            CashflowRecord savedCashflowRecord = cashflowRecordRepository.save(cashflowRecord);

            // Sprawdzenie lub utworzenie MonthBudget
            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
                    userId,
                    cashflowRecordDTO.getStartDate().getMonthValue(),
                    cashflowRecordDTO.getStartDate().getYear()
            ).orElseGet(() -> {
                MonthBudget newMonthBudget = new MonthBudget();
                newMonthBudget.setUser(user);
                newMonthBudget.setFirstOfMonth(LocalDate.of(
                        cashflowRecordDTO.getStartDate().getYear(),
                        cashflowRecordDTO.getStartDate().getMonthValue(),
                        1
                ));
                newMonthBudget.setTotalIncome(0.0);
                newMonthBudget.setRemainingBalance(0.0);
                return monthBudgetRepository.save(newMonthBudget);
            });

            // Aktualizacja totalIncome i remainingBalance
            monthBudget.setTotalIncome(monthBudget.getTotalIncome() + cashflowRecord.getAmount());
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + cashflowRecord.getAmount());
            monthBudgetRepository.save(monthBudget);

            savedRecords.add(new CashflowRecordDTO(
                    savedCashflowRecord.getCashflowRecordId(),
                    savedCashflowRecord.getAmount(),
                    savedCashflowRecord.getStartDate(),
                    savedCashflowRecord.getRecordType(),
                    savedCashflowRecord.getDesc(),
                    null,
                    userId
            ));
        }

        return savedRecords;
    }


    public List<CashflowRecordDTO> addExpense(List<CashflowRecordDTO> cashflowRecordDTOs, Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<CashflowRecordDTO> savedRecords = new ArrayList<>();

        for (CashflowRecordDTO cashflowRecordDTO : cashflowRecordDTOs) {
            if (cashflowRecordDTO.getCategoryId() == null) {
                throw new IllegalArgumentException("Category ID must not be null for expenses");
            }

            Category category = categoryRepository.findByCategoryIdAndUserId(cashflowRecordDTO.getCategoryId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + cashflowRecordDTO.getCategoryId()));

            CashflowRecord cashflowRecord = new CashflowRecord();
            cashflowRecord.setAmount(cashflowRecordDTO.getAmount());
            cashflowRecord.setStartDate(cashflowRecordDTO.getStartDate());
            cashflowRecord.setRecordType(true);
            cashflowRecord.setDesc(cashflowRecordDTO.getDesc());
            cashflowRecord.setCategory(category);
            cashflowRecord.setUser(user);

            CashflowRecord savedCashflowRecord = cashflowRecordRepository.save(cashflowRecord);

            // Sprawdzenie lub utworzenie MonthBudget
            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
                    userId,
                    cashflowRecordDTO.getStartDate().getMonthValue(),
                    cashflowRecordDTO.getStartDate().getYear()
            ).orElseGet(() -> {
                MonthBudget newMonthBudget = new MonthBudget();
                newMonthBudget.setUser(user);
                newMonthBudget.setFirstOfMonth(LocalDate.of(
                        cashflowRecordDTO.getStartDate().getYear(),
                        cashflowRecordDTO.getStartDate().getMonthValue(),
                        1
                ));
                newMonthBudget.setTotalIncome(0.0f);
                newMonthBudget.setRemainingBalance(0.0f);
                return monthBudgetRepository.save(newMonthBudget);
            });

            // Aktualizacja remainingBalance
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
            monthBudgetRepository.save(monthBudget);

            // Sprawdzenie i ewentualne utworzenie PlannedBudget
            PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(monthBudget.getMonthBudgetId(), category.getCategoryId())
                    .orElseGet(() -> {
                        PlannedBudget newPlannedBudget = new PlannedBudget();
                        newPlannedBudget.setMonthBudget(monthBudget);
                        newPlannedBudget.setCategory(category);
                        newPlannedBudget.setPlannedAmount(0.0f);
                        newPlannedBudget.setSpentAmount(0.0f);
                        return plannedBudgetRepository.save(newPlannedBudget);
                    });

            // Aktualizacja spentAmount w PlannedBudget
            plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + cashflowRecord.getAmount());
            plannedBudgetRepository.save(plannedBudget);

            savedRecords.add(new CashflowRecordDTO(
                    savedCashflowRecord.getCashflowRecordId(),
                    savedCashflowRecord.getAmount(),
                    savedCashflowRecord.getStartDate(),
                    savedCashflowRecord.getRecordType(),
                    savedCashflowRecord.getDesc(),
                    category.getCategoryId(),
                    userId
            ));
        }
        return savedRecords;
    }


    @Transactional
    public List<CashflowRecordDTO> updateMultipleCashflowRecords(Long userId, List<CashflowRecordDTO> cashflowRecordDTOs) {
        List<CashflowRecordDTO> updatedRecords = new ArrayList<>();

        for (CashflowRecordDTO newRecordDTO : cashflowRecordDTOs) {
            Long recordId = newRecordDTO.getCashflowRecordId();

            // Pobranie istniejącego rekordu
            CashflowRecord existingRecord = cashflowRecordRepository.findById(recordId)
                    .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + recordId));

            // Sprawdzenie, czy rekord należy do użytkownika
            if (!existingRecord.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("Record does not belong to user with id: " + userId);
            }

            // Obliczenie różnicy w kwocie
            float oldAmount = existingRecord.getAmount();
            float newAmount = newRecordDTO.getAmount();
            float difference = newAmount - oldAmount;

            // Aktualizacja MonthBudget
            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
                    userId,
                    existingRecord.getStartDate().getMonthValue(),
                    existingRecord.getStartDate().getYear()
            ).orElseThrow(() -> new IllegalArgumentException("Month budget not found"));

            if (!existingRecord.getRecordType()) { // Jeśli to dochód (recordType = false)
                monthBudget.setTotalIncome(monthBudget.getTotalIncome() + difference);
                monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + difference);
            } else { // Jeśli to wydatek (recordType = true)
                monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - difference);

                // Obsługa zmiany kategorii
                if (!existingRecord.getCategory().getCategoryId().equals(newRecordDTO.getCategoryId())) {
                    // Aktualizacja spentAmount dla poprzedniej kategorii
                    PlannedBudget oldPlannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                            monthBudget.getMonthBudgetId(),
                            existingRecord.getCategory().getCategoryId()
                    ).orElse(null);

                    if (oldPlannedBudget != null) {
                        oldPlannedBudget.setSpentAmount(oldPlannedBudget.getSpentAmount() - oldAmount);
                        plannedBudgetRepository.save(oldPlannedBudget);
                    }

                    // Aktualizacja spentAmount dla nowej kategorii
                    PlannedBudget newPlannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                            monthBudget.getMonthBudgetId(),
                            newRecordDTO.getCategoryId()
                    ).orElse(null);

                    if (newPlannedBudget != null) {
                        newPlannedBudget.setSpentAmount(newPlannedBudget.getSpentAmount() + newAmount);
                        plannedBudgetRepository.save(newPlannedBudget);
                    }
                } else { // Jeśli kategoria się nie zmienia
                    PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                            monthBudget.getMonthBudgetId(),
                            existingRecord.getCategory().getCategoryId()
                    ).orElse(null);

                    if (plannedBudget != null) {
                        plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + difference);
                        plannedBudgetRepository.save(plannedBudget);
                    }
                }
            }

            // Zapisanie MonthBudget
            monthBudgetRepository.save(monthBudget);

            // Aktualizacja rekordu
            existingRecord.setAmount(newAmount);
            existingRecord.setStartDate(newRecordDTO.getStartDate());
            existingRecord.setDesc(newRecordDTO.getDesc());

//            if (!existingRecord.getCategory().getCategoryId().equals(newRecordDTO.getCategoryId())) {
//                Category newCategory = categoryRepository.findById(newRecordDTO.getCategoryId())
//                        .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + newRecordDTO.getCategoryId()));
//                existingRecord.setCategory(newCategory);
//            }

            if (existingRecord.getCategory() == null ||
                    !existingRecord.getCategory().getCategoryId().equals(newRecordDTO.getCategoryId())) {

                // Jeśli nowa kategoria jest nieprawidłowa (null), usuń kategorię z rekordu
                if (newRecordDTO.getCategoryId() == null) {
                    existingRecord.setCategory(null);
                } else {
                    // Znajdź nową kategorię i ustaw ją
                    Category newCategory = categoryRepository.findById(newRecordDTO.getCategoryId())
                            .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + newRecordDTO.getCategoryId()));
                    existingRecord.setCategory(newCategory);
                }
            }


            cashflowRecordRepository.save(existingRecord);

            // Dodanie do listy zaktualizowanych rekordów
            updatedRecords.add(new CashflowRecordDTO(
                    existingRecord.getCashflowRecordId(),
                    existingRecord.getAmount(),
                    existingRecord.getStartDate(),
                    existingRecord.getRecordType(),
                    existingRecord.getDesc(),
                    existingRecord.getCategory() != null ? existingRecord.getCategory().getCategoryId() : null,
                    userId
            ));
        }

        return updatedRecords;
    }


    @Transactional
    public void deleteMultipleCashflowRecords(Long userId, List<Long> recordIds) {
        List<CashflowRecord> records = cashflowRecordRepository.findAllByUserIdAndRecordIds(userId, recordIds);

        for (CashflowRecord record : records) {
            updateBudgetAfterRecordDeletion(record, userId);
        }

        cashflowRecordRepository.deleteAll(records);
    }

    private void updateBudgetAfterRecordDeletion(CashflowRecord cashflowRecord, Long userId) {
        MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
                userId,
                cashflowRecord.getStartDate().getMonthValue(),
                cashflowRecord.getStartDate().getYear()
        ).orElseThrow(() -> new IllegalArgumentException("No budget found for the record's date"));

        if (cashflowRecord.getRecordType()) { // Wydatki
            PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                    monthBudget.getMonthBudgetId(),
                    cashflowRecord.getCategory().getCategoryId()
            ).orElse(null);

            if (plannedBudget != null) {
                plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() - cashflowRecord.getAmount());
                plannedBudgetRepository.save(plannedBudget);
            }

            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + cashflowRecord.getAmount());
        } else { // Przychody
            monthBudget.setTotalIncome(monthBudget.getTotalIncome() - cashflowRecord.getAmount());
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
        }

        monthBudgetRepository.save(monthBudget);
    }
}