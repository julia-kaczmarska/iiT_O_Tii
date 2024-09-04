package back.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "sharing")
@Getter
@Setter
public class Sharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sharing_id")
    private Long sharingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cashflow_record_id", nullable = false)
    private CashflowRecord cashflowRecord;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}