package back.controller.dto;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class CashflowRecordDTO {
    @Positive(message = "Amount must be positive")
    private Float amount;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotNull(message = "Record type cannot be null")
    private Boolean recordType;
    @NotNull(message = "Record description cannot be null")
    private String desc;

    private Long categoryId;
    private Long userId;

    // Domyślny konstruktor potrzebny dla Jacksona
    public CashflowRecordDTO() {
    }

    // Konstruktor używany przy tworzeniu rekordu
    public CashflowRecordDTO(Float amount, LocalDate startDate, Boolean recordType, String desc, Long categoryId, Long userId) {
        this.amount = amount;
        this.startDate = startDate;


        this.recordType = recordType;
        this.desc = desc;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    //dla zapytań
//    public CashflowRecordDTO(Float amount, LocalDate startDate, LocalDate endDate, Boolean recordType, Long categoryId, Long userId) {
//        this.amount = amount;
//        this.startDate = startDate;
//        this.endDate = endDate;
//
//        this.recordType = recordType;
//        this.categoryId = categoryId;
//        this.userId = userId;
//    }
}
