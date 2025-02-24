//package back.service;
//
//import back.controller.dto.CashflowRecordDTO;
//import back.controller.dto.MonthBudgetDTO;
//import back.model.*;
//import back.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CashflowRecordService {
//
//    private final CashflowRecordRepository cashflowRecordRepository;
//    private final CategoryRepository categoryRepository;
//    private final UserRepository userRepository;
//    private final MonthBudgetRepository monthBudgetRepository;
//    private final PlannedBudgetRepository plannedBudgetRepository;
//    private final MonthBudgetService monthBudgetService;
//    private final PlannedBudgetService plannedBudgetService;
//
//    public List<CashflowRecordDTO> getCashflowRecordsByTypeAndMonth(Long userId, boolean recordType, LocalDate monthStartDate) {
//        User user = userRepository.findById(Math.toIntExact(userId))
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        // Oblicz zakres dat dla miesiąca
//        LocalDate monthEndDate = monthStartDate.withDayOfMonth(monthStartDate.lengthOfMonth());
//
//        // Pobranie rekordów
//        List<CashflowRecord> records = cashflowRecordRepository.findByUserIdAndRecordTypeAndDateRange(
//                userId, recordType, monthStartDate, monthEndDate);
//
//        // Mapowanie na DTO
//        return records.stream().map(record -> new CashflowRecordDTO(
//                record.getCashflowRecordId(),
//                record.getAmount(),
//                record.getStartDate(),
//                record.getRecordType(),
//                record.getDesc(),
//                record.getCategory() != null ? record.getCategory().getCategoryId() : null,
//                userId
//        )).collect(Collectors.toList());
//    }
//
//    public List<CashflowRecordDTO> addIncomes(List<CashflowRecordDTO> cashflowRecordDTOs, Long userId) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        List<CashflowRecordDTO> savedRecords = new ArrayList<>();
//
//        for (CashflowRecordDTO cashflowRecordDTO : cashflowRecordDTOs) {
//            if (cashflowRecordDTO.getCategoryId() != null) {
//                throw new IllegalArgumentException("Category ID must be null for incomes");
//            }
//
//            CashflowRecord cashflowRecord = new CashflowRecord();
//            cashflowRecord.setAmount(cashflowRecordDTO.getAmount());
//            cashflowRecord.setStartDate(cashflowRecordDTO.getStartDate());
//            cashflowRecord.setRecordType(false);
//            cashflowRecord.setDesc(cashflowRecordDTO.getDesc());
//            cashflowRecord.setUser(user);
//
//            CashflowRecord savedCashflowRecord = cashflowRecordRepository.save(cashflowRecord);
//
//            // Sprawdzenie lub utworzenie MonthBudget
//            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                    userId,
//                    cashflowRecordDTO.getStartDate().getMonthValue(),
//                    cashflowRecordDTO.getStartDate().getYear()
//            ).orElseGet(() -> {
//                MonthBudget newMonthBudget = new MonthBudget();
//                newMonthBudget.setUser(user);
//                newMonthBudget.setFirstOfMonth(LocalDate.of(
//                        cashflowRecordDTO.getStartDate().getYear(),
//                        cashflowRecordDTO.getStartDate().getMonthValue(),
//                        1
//                ));
//                newMonthBudget.setTotalIncome(0.0);
//                newMonthBudget.setRemainingBalance(0.0);
//                return monthBudgetRepository.save(newMonthBudget);
//            });
//
//// Pobranie poprzedniego remainingBalance
//            double previousRemainingBalance = monthBudget.getRemainingBalance();
//
//// Aktualizacja totalIncome i remainingBalance
//            monthBudget.setTotalIncome(monthBudget.getTotalIncome() + cashflowRecord.getAmount());
//            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + cashflowRecord.getAmount());
//            monthBudgetRepository.save(monthBudget);
//
//// Obliczenie delta
//            double delta = monthBudget.getRemainingBalance() - previousRemainingBalance;
//
//// Aktualizacja totalIncome w następnym miesiącu z delta
//            if (delta != 0) {
//                monthBudgetService.updateNextMonthTotalIncome(userId, cashflowRecordDTO.getStartDate(), delta);
//            }
//
//        }
//        return savedRecords;
//    }
//
//    @Transactional
//    // zapewnia, że wszystkie zmiany w bazie danych są traktowane jako jedna transakcja i są commitowane po zakończeniu metody
//    public List<CashflowRecordDTO> addExpense(List<CashflowRecordDTO> cashflowRecordDTOs, Long userId) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
//
//        List<CashflowRecordDTO> savedRecords = new ArrayList<>();
//
//        for (CashflowRecordDTO cashflowRecordDTO : cashflowRecordDTOs) {
//            if (cashflowRecordDTO.getCategoryId() == null) {
//                throw new IllegalArgumentException("Category ID must not be null for expenses");
//            }
//
//            Category category = categoryRepository.findByCategoryIdAndUserId(cashflowRecordDTO.getCategoryId(), userId)
//                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + cashflowRecordDTO.getCategoryId()));
//
//            CashflowRecord cashflowRecord = new CashflowRecord();
//            cashflowRecord.setAmount(cashflowRecordDTO.getAmount());
//            cashflowRecord.setStartDate(cashflowRecordDTO.getStartDate());
//            cashflowRecord.setRecordType(true);
//            cashflowRecord.setDesc(cashflowRecordDTO.getDesc());
//            cashflowRecord.setCategory(category);
//            cashflowRecord.setUser(user);
//
//            CashflowRecord savedCashflowRecord = cashflowRecordRepository.save(cashflowRecord);
//
//            // Sprawdzenie lub utworzenie MonthBudget
//            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                    userId,
//                    cashflowRecordDTO.getStartDate().getMonthValue(),
//                    cashflowRecordDTO.getStartDate().getYear()
//            ).orElseGet(() -> {
//                MonthBudget newMonthBudget = new MonthBudget();
//                newMonthBudget.setUser(user);
//                newMonthBudget.setFirstOfMonth(LocalDate.of(
//                        cashflowRecordDTO.getStartDate().getYear(),
//                        cashflowRecordDTO.getStartDate().getMonthValue(),
//                        1
//                ));
//                newMonthBudget.setTotalIncome(0.0f);
//                newMonthBudget.setRemainingBalance(0.0f);
//                return monthBudgetRepository.save(newMonthBudget);
//            });
//
//            // Log przed zmianą
////    System.out.println("[DEBUG] Przed dodaniem wydatku:");
////    System.out.println("Total Income (Before): " + monthBudget.getTotalIncome());
////    System.out.println("Remaining Balance (Before): " + monthBudget.getRemainingBalance());
////    System.out.println("Dodawany wydatek: " + cashflowRecord.getAmount());
//
////            // Aktualizacja remainingBalance
////            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
////            monthBudgetRepository.save(monthBudget);
//
//            // Sprawdzenie i ewentualne utworzenie PlannedBudget
//            PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
//                    monthBudget.getMonthBudgetId(),
//                    category.getCategoryId()
//            ).orElse(null);
//
//            if (plannedBudget == null) {
//                PlannedBudget newPlannedBudget = new PlannedBudget();
//                newPlannedBudget.setMonthBudget(monthBudget);
//                newPlannedBudget.setCategory(category);
//                newPlannedBudget.setPlannedAmount(0.0f);
//                newPlannedBudget.setSpentAmount(0.0f);
//                plannedBudget = plannedBudgetRepository.save(newPlannedBudget);
//            } else {
//                // DEBUG: Sprawdzenie hashCode - Hibernate powinno śledzić zmiany
//                System.out.println("[DEBUG] plannedBudget hashCode: " + System.identityHashCode(plannedBudget));
//            }
//
//
//            // Aktualizacja spentAmount w PlannedBudget
//            plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + cashflowRecord.getAmount());
//            plannedBudgetRepository.save(plannedBudget);
//
//            savedRecords.add(new CashflowRecordDTO(
//                    savedCashflowRecord.getCashflowRecordId(),
//                    savedCashflowRecord.getAmount(),
//                    savedCashflowRecord.getStartDate(),
//                    savedCashflowRecord.getRecordType(),
//                    savedCashflowRecord.getDesc(),
//                    category.getCategoryId(),
//                    userId
//            ));
//
//// Pobranie poprzedniego remainingBalance
//            double previousRemainingBalance = monthBudget.getRemainingBalance();
//
//// Aktualizacja remainingBalance po dodaniu wydatku
//            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
//            monthBudgetRepository.save(monthBudget);
//
//// Obliczenie delta jako różnicy
//            double delta = monthBudget.getRemainingBalance() - previousRemainingBalance;
//            System.out.println("[DEBUG] Delta dla expense: " + delta);
//
//// Aktualizacja totalIncome w następnym miesiącu z delta
//            if (delta != 0) {
//                monthBudgetService.updateNextMonthTotalIncome(userId, cashflowRecordDTO.getStartDate(), delta);
//            }
//        }
//        return savedRecords;
//    }
//
//    @Transactional
//    public List<CashflowRecordDTO> updateMultipleCashflowRecords(Long userId, List<CashflowRecordDTO> cashflowRecordDTOs) {
//        List<CashflowRecordDTO> updatedRecords = new ArrayList<>();
//
//        for (CashflowRecordDTO newRecordDTO : cashflowRecordDTOs) {
//            Long recordId = newRecordDTO.getCashflowRecordId();
//
//            // Pobranie istniejącego rekordu
//            CashflowRecord existingRecord = cashflowRecordRepository.findById(recordId)
//                    .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + recordId));
//
//            // Sprawdzenie, czy rekord należy do użytkownika
//            if (!existingRecord.getUser().getUserId().equals(userId)) {
//                throw new IllegalArgumentException("Record does not belong to user with id: " + userId);
//            }
//
//            // Obliczenie różnicy w kwocie
//            float oldAmount = existingRecord.getAmount();
//            float newAmount = newRecordDTO.getAmount();
//            float difference = newAmount - oldAmount;
//
//            // Check if category has changed
//            boolean categoryChanged = !existingRecord.getCategory().getCategoryId().equals(newRecordDTO.getCategoryId());
//
//            // Fetch MonthBudget
//            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                    userId,
//                    existingRecord.getStartDate().getMonthValue(),
//                    existingRecord.getStartDate().getYear()
//            ).orElseThrow(() -> new IllegalStateException("MonthBudget not found for userId: " + userId
//                    + " and month: " + existingRecord.getStartDate().getMonthValue()
//                    + " and year: " + existingRecord.getStartDate().getYear()));
//
//            // Przeliczenie totalIncome po zmianie income
//            MonthBudgetDTO monthBudgetDTO = monthBudgetService.mapToDTO(monthBudget);
//            monthBudgetService.editBudgetByDate(userId, existingRecord.getStartDate(), monthBudgetDTO);
//
//
//            //TUTAJ
//            // Jeśli zmienia się kategoria, zmień spentAmount dla obu kategorii
//            if (categoryChanged) {
//                // 1. Zmniejszenie spentAmount w starej kategorii o starą kwotę
//                PlannedBudget oldPlannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
//                        monthBudget.getMonthBudgetId(),
//                        existingRecord.getCategory().getCategoryId()
//                ).orElseThrow(() -> new IllegalStateException("PlannedBudget not found for old category."));
//
//                oldPlannedBudget.setSpentAmount(oldPlannedBudget.getSpentAmount() - oldAmount);
//                plannedBudgetRepository.save(oldPlannedBudget);
//
//                // 2. Zwiększenie spentAmount w nowej kategorii o nową kwotę
//                Category newCategory = categoryRepository.findByCategoryIdAndUserId(newRecordDTO.getCategoryId(), userId)
//                        .orElseThrow(() -> new IllegalArgumentException("New category not found with id: " + newRecordDTO.getCategoryId()));
//
//                existingRecord.setCategory(newCategory);
//
//                PlannedBudget newPlannedBudget = plannedBudgetService.addOrUpdatePlannedBudget(
//                        monthBudget,
//                        newCategory,
//                        newAmount
//                );
//
//            } else {
//                // Jeśli nie zmienia się kategoria, zmień spentAmount o różnicę kwot
//                PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
//                        monthBudget.getMonthBudgetId(),
//                        existingRecord.getCategory().getCategoryId()
//                ).orElseThrow(() -> new IllegalStateException("PlannedBudget not found for current category."));
//
//                plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + difference);
//                plannedBudgetRepository.save(plannedBudget);
//            }
//            ///
//
//            // Aktualizacja rekordu
//            existingRecord.setAmount(newAmount);
//            existingRecord.setStartDate(newRecordDTO.getStartDate());
//            existingRecord.setDesc(newRecordDTO.getDesc());
//
//            cashflowRecordRepository.save(existingRecord);
//            updatedRecords.add(new CashflowRecordDTO(
//                    existingRecord.getCashflowRecordId(),
//                    existingRecord.getAmount(),
//                    existingRecord.getStartDate(),
//                    existingRecord.getRecordType(),
//                    existingRecord.getDesc(),
//                    existingRecord.getCategory() != null ? existingRecord.getCategory().getCategoryId() : null,
//                    userId
//            ));
//        }
//        return updatedRecords;
//    }
//
//
//    @Transactional
//    public void deleteMultipleCashflowRecords(Long userId, List<Long> recordIds) {
//        List<CashflowRecord> records = cashflowRecordRepository.findAllByUserIdAndRecordIds(userId, recordIds);
//
//        for (CashflowRecord record : records) {
//            updateBudgetAfterRecordDeletion(record, userId);
//        }
//
//        cashflowRecordRepository.deleteAll(records);
//    }
//
//    private void updateBudgetAfterRecordDeletion(CashflowRecord cashflowRecord, Long userId) {
//        MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
//                userId,
//                cashflowRecord.getStartDate().getMonthValue(),
//                cashflowRecord.getStartDate().getYear()
//        ).orElseThrow(() -> new IllegalArgumentException("No budget found for the record's date"));
//
//        if (cashflowRecord.getRecordType()) { // Wydatki
//            PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
//                    monthBudget.getMonthBudgetId(),
//                    cashflowRecord.getCategory().getCategoryId()
//            ).orElse(null);
//
//            if (plannedBudget != null) {
//                plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() - cashflowRecord.getAmount());
//                plannedBudgetRepository.save(plannedBudget);
//            }
//
//            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + cashflowRecord.getAmount());
//        } else { // Przychody
//            monthBudget.setTotalIncome(monthBudget.getTotalIncome() - cashflowRecord.getAmount());
//            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
//        }
//
//        monthBudgetRepository.save(monthBudget);
//    }
//}

package back.service;

import back.controller.dto.CashflowRecordDTO;
import back.model.*;
import back.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MonthBudgetRepository monthBudgetRepository;
    private final PlannedBudgetRepository plannedBudgetRepository;
    private final MonthBudgetService monthBudgetService;
    private final PlannedBudgetService plannedBudgetService;

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

// Pobranie poprzedniego remainingBalance
            double previousRemainingBalance = monthBudget.getRemainingBalance();

// Aktualizacja totalIncome i remainingBalance
            monthBudget.setTotalIncome(monthBudget.getTotalIncome() + cashflowRecord.getAmount());
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() + cashflowRecord.getAmount());
            monthBudgetRepository.save(monthBudget);

// Obliczenie delta
            double delta = monthBudget.getRemainingBalance() - previousRemainingBalance;

// Aktualizacja totalIncome w następnym miesiącu z delta
            if (delta != 0) {
                monthBudgetService.updateNextMonthTotalIncome(userId, cashflowRecordDTO.getStartDate(), delta);
            }

        }
        return savedRecords;
    }

    @Transactional
    // zapewnia, że wszystkie zmiany w bazie danych są traktowane jako jedna transakcja i są commitowane po zakończeniu metody
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

//            // Sprawdzenie i ewentualne utworzenie PlannedBudget
//            PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
//                    monthBudget.getMonthBudgetId(),
//                    category.getCategoryId()
//            ).orElse(null);
//
//            if (plannedBudget == null) {
//                PlannedBudget newPlannedBudget = new PlannedBudget();
//                newPlannedBudget.setMonthBudget(monthBudget);
//                newPlannedBudget.setCategory(category);
//                newPlannedBudget.setPlannedAmount(0.0f);
//                newPlannedBudget.setSpentAmount(0.0f);
//                plannedBudget = plannedBudgetRepository.save(newPlannedBudget);
//            } else {
//                // DEBUG: Sprawdzenie hashCode - Hibernate powinno śledzić zmiany
//                System.out.println("[DEBUG] plannedBudget hashCode: " + System.identityHashCode(plannedBudget));
//            }
//
//            // Aktualizacja spentAmount w PlannedBudget
//            plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + cashflowRecord.getAmount());
//            plannedBudgetRepository.save(plannedBudget);

            //TUTAJ
            PlannedBudget plannedBudget = plannedBudgetService.addOrUpdatePlannedBudget(
                    monthBudget,
                    category,
                    cashflowRecord.getAmount()
            );
            //

            savedRecords.add(new CashflowRecordDTO(
                    savedCashflowRecord.getCashflowRecordId(),
                    savedCashflowRecord.getAmount(),
                    savedCashflowRecord.getStartDate(),
                    savedCashflowRecord.getRecordType(),
                    savedCashflowRecord.getDesc(),
                    category.getCategoryId(),
                    userId
            ));

// Pobranie poprzedniego remainingBalance
            double previousRemainingBalance = monthBudget.getRemainingBalance();

// Aktualizacja remainingBalance po dodaniu wydatku
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - cashflowRecord.getAmount());
            monthBudgetRepository.save(monthBudget);

// Obliczenie delta jako różnicy
            double delta = monthBudget.getRemainingBalance() - previousRemainingBalance;

// DEBUG: Sprawdzenie delta
            System.out.println("[DEBUG] Delta dla expense: " + delta);

// Aktualizacja totalIncome w następnym miesiącu z delta
            if (delta != 0) {
                monthBudgetService.updateNextMonthTotalIncome(userId, cashflowRecordDTO.getStartDate(), delta);
            }
        }
        return savedRecords;
    }


    @Transactional
    public List<CashflowRecordDTO> updateMultipleCashflowRecords(Long userId, List<CashflowRecordDTO> cashflowRecordDTOs) {
        List<CashflowRecordDTO> updatedRecords = new ArrayList<>();

        for (CashflowRecordDTO newRecordDTO : cashflowRecordDTOs) {
            Long recordId = newRecordDTO.getCashflowRecordId();

            // Retrieve existing record
            CashflowRecord existingRecord = cashflowRecordRepository.findById(recordId)
                    .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + recordId));

            // Check ownership
            if (!existingRecord.getUser().getUserId().equals(userId)) {
                throw new IllegalArgumentException("Record does not belong to user with id: " + userId);
            }

            // Calculate amount difference
            double oldAmount = existingRecord.getAmount();
            float newAmount = newRecordDTO.getAmount();
            double amountDifference = newAmount - oldAmount;

            // Fetch MonthBudget
            MonthBudget monthBudget = monthBudgetRepository.findByUserIdAndMonth(
                    userId,
                    existingRecord.getStartDate().getMonthValue(),
                    existingRecord.getStartDate().getYear()
            ).orElseThrow(() -> new IllegalStateException("MonthBudget not found for userId: " + userId
                    + " and month: " + existingRecord.getStartDate().getMonthValue()
                    + " and year: " + existingRecord.getStartDate().getYear()));

            // Sprawdzenie dla Przychodów (recordType == 0)
            if (!existingRecord.getRecordType()) { // recordType == 0 (Przychody)
                // categoryId musi być null dla przychodów
                if (newRecordDTO.getCategoryId() != null) {
                    throw new IllegalArgumentException("Category ID must be null for incomes (recordType 0)");
                }
            } else { // recordType == 1 (Wydatki)
                // categoryId nie może być null dla wydatków
                if (newRecordDTO.getCategoryId() == null) {
                    throw new IllegalArgumentException("Category ID cannot be null for expenses (recordType 1)");
                }

                // Sprawdzenie czy zmieniła się kategoria
                boolean categoryChanged = !existingRecord.getCategory().getCategoryId().equals(newRecordDTO.getCategoryId());

                if (categoryChanged) {
                    // 1. Zmniejszenie spentAmount w starej kategorii o starą kwotę
                    PlannedBudget oldPlannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                            monthBudget.getMonthBudgetId(),
                            existingRecord.getCategory().getCategoryId()
                    ).orElseThrow(() -> new IllegalStateException("PlannedBudget not found for old category."));

                    oldPlannedBudget.setSpentAmount(oldPlannedBudget.getSpentAmount() - oldAmount);
                    plannedBudgetRepository.save(oldPlannedBudget);

                    // 2. Zwiększenie spentAmount w nowej kategorii o nową kwotę
                    Category newCategory = categoryRepository.findByCategoryIdAndUserId(newRecordDTO.getCategoryId(), userId)
                            .orElseThrow(() -> new IllegalArgumentException("New category not found with id: " + newRecordDTO.getCategoryId()));

                    existingRecord.setCategory(newCategory);

                    PlannedBudget newPlannedBudget = plannedBudgetService.addOrUpdatePlannedBudget(
                            monthBudget,
                            newCategory,
                            newAmount
                    );
                } else {
                    // Jeśli nie zmienia się kategoria, zmień spentAmount o różnicę kwot
                    PlannedBudget plannedBudget = plannedBudgetRepository.findByMonthBudget_MonthBudgetIdAndCategory_CategoryId(
                            monthBudget.getMonthBudgetId(),
                            existingRecord.getCategory().getCategoryId()
                    ).orElseThrow(() -> new IllegalStateException("PlannedBudget not found for current category."));

                    plannedBudget.setSpentAmount(plannedBudget.getSpentAmount() + amountDifference);
                    plannedBudgetRepository.save(plannedBudget);
                }
            }

            // Aktualizacja danych w CashflowRecord
            existingRecord.setAmount(newAmount);
            existingRecord.setStartDate(newRecordDTO.getStartDate());
            existingRecord.setDesc(newRecordDTO.getDesc());

            cashflowRecordRepository.save(existingRecord);

            updatedRecords.add(new CashflowRecordDTO(
                    existingRecord.getCashflowRecordId(),
                    existingRecord.getAmount(),
                    existingRecord.getStartDate(),
                    existingRecord.getRecordType(),
                    existingRecord.getDesc(),
                    existingRecord.getCategory() != null ? existingRecord.getCategory().getCategoryId() : null,
                    userId
            ));

            // Aktualizacja remainingBalance (nie totalIncome!)
            monthBudget.setRemainingBalance(monthBudget.getRemainingBalance() - amountDifference);

            //TUTAJJ
            // Obliczenie delta jako różnicy
            double delta = -amountDifference;

// DEBUG: Sprawdzenie delta
            System.out.println("[DEBUG] Delta dla update: " + delta);

// Aktualizacja totalIncome w następnym miesiącu z delta
            if (delta != 0) {
                monthBudgetService.updateNextMonthTotalIncome(userId, existingRecord.getStartDate(), delta);
            }
///

            monthBudgetRepository.save(monthBudget);
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