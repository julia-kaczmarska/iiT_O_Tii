package back.service;

import back.controller.dto.CashflowRecordDTO;
import back.model.CashflowRecord;
import back.model.Category;
import back.model.User;
import back.repository.CashflowRecordRepository;
import back.repository.CategoryRepository;
import back.repository.SharingRepository;
import back.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CashflowRecordService {

    private final CashflowRecordRepository cashflowRecordRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SharingRepository sharingRepository;

    public List<CashflowRecordDTO> getCashflowRecordsByUserId(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return cashflowRecordRepository.findRecordsByUserId(userId);
    }

    public CashflowRecordDTO addCashflowRecord(CashflowRecordDTO cashflowRecordDTO, Long userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        //walidacja kombinacji recordType i categoryId
        if (cashflowRecordDTO.getRecordType() && cashflowRecordDTO.getCategoryId() == null) {
            throw new IllegalArgumentException("Category ID must not be null when recordType is true");
        }
        if (!cashflowRecordDTO.getRecordType() && cashflowRecordDTO.getCategoryId() != null) {
            throw new IllegalArgumentException("Category ID must be null when recordType is false");
        }

        // Pobranie kategorii, jeśli categoryId jest podane
        Category category = null;
        if (cashflowRecordDTO.getCategoryId() != null) {
            category = categoryRepository.findByCategoryIdAndUserId(cashflowRecordDTO.getCategoryId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + cashflowRecordDTO.getCategoryId()));
        }

//        System.out.println(cashflowRecordDTO);

        CashflowRecord cashflowRecord = new CashflowRecord();
        cashflowRecord.setAmount(cashflowRecordDTO.getAmount());
        cashflowRecord.setStartDate(cashflowRecordDTO.getStartDate());
        cashflowRecord.setRecordType(cashflowRecordDTO.getRecordType());
        cashflowRecord.setDesc(cashflowRecordDTO.getDesc());
        cashflowRecord.setCategory(category);
        cashflowRecord.setUser(user);
        CashflowRecord savedCashflowRecord = cashflowRecordRepository.save(cashflowRecord);


        return new CashflowRecordDTO(savedCashflowRecord.getAmount(), savedCashflowRecord.getStartDate(), savedCashflowRecord.getRecordType(), savedCashflowRecord.getDesc(),
                savedCashflowRecord.getCategory() != null ? savedCashflowRecord.getCategory().getCategoryId() : null, userId);
    }

    public CashflowRecordDTO updateCashflowRecord(Long userId, Long recordId, CashflowRecordDTO newRecordData) {
        // Pobierz rekord CashflowRecord
        CashflowRecord existingRecord = cashflowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with id: " + recordId));
        Category category = null;

        //walidacja kombinacji recordType i categoryId
        if (newRecordData.getRecordType()) {
            if (newRecordData.getCategoryId() == null) {
                throw new IllegalArgumentException("Category ID must not be null when recordType is true");
            }
            // pobiera kategorię, jeśli recordType jest true
            category = categoryRepository.findByCategoryIdAndUserId(newRecordData.getCategoryId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No category with id: " + newRecordData.getCategoryId() + ", for user with id: " + userId));
        } else {
            if (newRecordData.getCategoryId() != null) {
                throw new IllegalArgumentException("Category ID must be null when recordType is false");
            }
        }

        existingRecord.setAmount(newRecordData.getAmount());
        existingRecord.setStartDate(newRecordData.getStartDate());
        existingRecord.setRecordType(newRecordData.getRecordType());
        existingRecord.setCategory(category);
        CashflowRecord updatedRecord = cashflowRecordRepository.save(existingRecord);

//        Sharing sharing = sharingRepository.findByCashflowRecord_CashflowRecordId(updatedRecord.getCashflowRecordId())
//                .orElseThrow(() -> new IllegalArgumentException("No sharing record found for cashflow record id: " + updatedRecord.getCashflowRecordId()));

        return new CashflowRecordDTO(
                updatedRecord.getAmount(),
                updatedRecord.getStartDate(),
                updatedRecord.getRecordType(),
                updatedRecord.getDesc(),
                updatedRecord.getCategory() != null ? updatedRecord.getCategory().getCategoryId() : null,
                userId
        );
    }

    public void deleteCashflowRecord(Long userId, Long recordId) {
        CashflowRecord cashflowRecord = cashflowRecordRepository.findByCashflowRecordIdAndUserId(userId, recordId)
                .orElseThrow(() -> new IllegalArgumentException("No record with id: " + recordId + ", for user with id: " + userId));
        cashflowRecordRepository.delete(cashflowRecord);
    }
}