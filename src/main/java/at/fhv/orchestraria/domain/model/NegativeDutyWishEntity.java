package at.fhv.orchestraria.domain.model;

import at.fhv.orchestraria.domain.Imodel.INegativeDutyWish;

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
@Table(name = "negativeDutyWish", schema = "ni128610_1sql8")
public class NegativeDutyWishEntity implements INegativeDutyWish, Serializable {
    private int negativeDutyId;
    private String description;
    private MusicianEntity musician;
    private SeriesOfPerformancesEntity seriesOfPerformances;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "negativeDutyId")
    @Override
    public int getNegativeDutyId() {
        return negativeDutyId;
    }

    public void setNegativeDutyId(int negativeDutyId) {
        this.negativeDutyId = negativeDutyId;
    }

    @Basic
    @Column(name = "description")
    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NegativeDutyWishEntity that = (NegativeDutyWishEntity) o;
        return negativeDutyId == that.negativeDutyId &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeDutyId, description);
    }

    @ManyToOne
    @JoinColumn(name = "musicianId", referencedColumnName = "musicianId", nullable = false)
    @Override
    public MusicianEntity getMusician() {
        return musician;
    }

    public void setMusician(MusicianEntity musician) {
        this.musician = musician;
    }

    @ManyToOne
    @JoinColumn(name = "seriesOfPerformancesId", referencedColumnName = "seriesOfPerformancesId", nullable = false)
    @Override
    public SeriesOfPerformancesEntity getSeriesOfPerformances() {
        return seriesOfPerformances;
    }

    public void setSeriesOfPerformances(SeriesOfPerformancesEntity seriesOfPerformances) {
        this.seriesOfPerformances = seriesOfPerformances;
    }
}