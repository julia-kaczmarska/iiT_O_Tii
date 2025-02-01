package back.controller.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CashflowRecordDTO {
    @NotNull(message = "RecordID cannot be null")
    private Long cashflowRecordId;

    @Positive(message = "Amount must be positive")
    private Float amount;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    private Boolean recordType;
    private String desc;

    private Long categoryId;
    private Long userId;


    @Override
    public String toString() {
        return "CashflowRecordDTO{" +
                "cashflowRecordId=" + cashflowRecordId +
                "recordType=" + recordType +
                ", startDate=" + startDate +
                ", amount=" + amount +
                ", description='" + desc + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }


    // Domyślny konstruktor potrzebny dla Jacksona
    public CashflowRecordDTO() {
    }

    // Konstruktor używany przy tworzeniu rekordu
    public CashflowRecordDTO(Long cashflowRecordId, Float amount, LocalDate startDate, Boolean recordType, String desc, Long categoryId, Long userId) {
        this.cashflowRecordId = cashflowRecordId;
        this.amount = amount;
        this.startDate = startDate;

        this.recordType = recordType;
        this.desc = desc;
        this.categoryId = categoryId;
        this.userId = userId;
    }
    public CashflowRecordDTO(Float amount, LocalDate startDate, Boolean recordType, String desc, Long categoryId, Long userId) {
        this.amount = amount;
        this.startDate = startDate;

        this.recordType = recordType;
        this.desc = desc;
        this.categoryId = categoryId;
        this.userId = userId;
    }

    //dla zapytań
    public CashflowRecordDTO(Float amount, LocalDate startDate, Boolean recordType, Long categoryId, Long userId) {
        this.amount = amount;
        this.startDate = startDate;

        this.recordType = recordType;
        this.categoryId = categoryId;
        this.userId = userId;
    }
}
