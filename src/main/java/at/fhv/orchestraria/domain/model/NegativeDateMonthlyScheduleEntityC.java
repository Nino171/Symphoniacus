package at.fhv.orchestraria.domain.model;

import at.fhv.orchestraria.domain.Imodel.INegativeDateMonthlySchedule;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author generated by Intellij -  edited by Team C
 */

/*
 * Generated by IntelliJ
 */

@Entity
@Table(name = "negativeDate_monthlySchedule", schema = "ni128610_1sql8")
public class NegativeDateMonthlyScheduleEntityC implements INegativeDateMonthlySchedule, Serializable {
    private int negativeDateMonthlyScheduleId;
    private MonthlyScheduleEntityC monthlySchedule;
    private NegativeDateWishEntityC negativeDateWish;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "negativeDate_monthlyScheduleId")
    @Override
    public int getNegativeDateMonthlyScheduleId() {
        return negativeDateMonthlyScheduleId;
    }

    public void setNegativeDateMonthlyScheduleId(int negativeDateMonthlyScheduleId) {
        this.negativeDateMonthlyScheduleId = negativeDateMonthlyScheduleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NegativeDateMonthlyScheduleEntityC that = (NegativeDateMonthlyScheduleEntityC) o;
        return negativeDateMonthlyScheduleId == that.negativeDateMonthlyScheduleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeDateMonthlyScheduleId);
    }

    @ManyToOne
    @JoinColumn(name = "monthlyScheduleId", referencedColumnName = "monthlyScheduleId", nullable = false)
    @Override
    public MonthlyScheduleEntityC getMonthlySchedule() {
        return monthlySchedule;
    }

    public void setMonthlySchedule(MonthlyScheduleEntityC monthlySchedule) {
        this.monthlySchedule = monthlySchedule;
    }

    @ManyToOne
    @JoinColumn(name = "negativeDateId", referencedColumnName = "negativeDateId", nullable = false)
    @Override
    public NegativeDateWishEntityC getNegativeDateWish() {
        return negativeDateWish;
    }

    public void setNegativeDateWish(NegativeDateWishEntityC negativeDateWish) {
        this.negativeDateWish = negativeDateWish;
    }
}