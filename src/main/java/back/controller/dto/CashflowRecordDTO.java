package back.controller.dto;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Getter
@Setter
public class CashflowRecordDTO {
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private float amount;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotNull(message = "Record type cannot be null")
    private boolean recordType;

    private Long categoryId;
    private String categoryTitle;

    private Long userId;

    // Domyślny konstruktor potrzebny dla Jacksona
    public CashflowRecordDTO() {
    }

    // Konstruktor używany przy tworzeniu rekordu
    public CashflowRecordDTO(float amount, LocalDate date, boolean recordType, Long categoryId, Long userId) {
        this.amount = amount;
        this.date = date;
        this.recordType = recordType;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    //dla zapytań
    public CashflowRecordDTO(float amount, LocalDate date, boolean recordType, Long categoryId, String categoryTitle, Long userId) {
        this.amount = amount;
        this.date = date;
        this.recordType = recordType;
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.userId = userId;
    }
}
