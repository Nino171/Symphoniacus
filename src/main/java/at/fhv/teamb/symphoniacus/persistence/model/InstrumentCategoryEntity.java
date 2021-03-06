package at.fhv.teamb.symphoniacus.persistence.model;

import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IContractualObligationEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IInstrumentCategoryEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IInstrumentationPositionEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicianEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "instrumentCategory")
public class InstrumentCategoryEntity implements IInstrumentCategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instrumentCategoryId")
    private Integer instrumentCategoryId;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "instrumentCategory", targetEntity = ContractualObligationEntity.class)
    private List<IContractualObligationEntity> contractualObligations = new LinkedList<>();

    @ManyToMany(mappedBy = "instrumentCategories", targetEntity = MusicianEntity.class)
    private List<IMusicianEntity> musicians = new LinkedList<>();

    @OneToMany(mappedBy = "instrumentCategory", targetEntity = InstrumentationPositionEntity.class)
    private List<IInstrumentationPositionEntity> instrumentationPositions = new LinkedList<>();

    @Override
    public Integer getInstrumentCategoryId() {
        return this.instrumentCategoryId;
    }

    @Override
    public void setInstrumentCategoryId(Integer instrumentCategoryId) {
        this.instrumentCategoryId = instrumentCategoryId;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<IContractualObligationEntity> getContractualObligations() {
        return this.contractualObligations;
    }

    @Override
    public void addContractualObligation(IContractualObligationEntity contractualObligation) {
        this.contractualObligations.add(contractualObligation);
        contractualObligation.setInstrumentCategory(this);
    }

    @Override
    public void removeContractualObligation(IContractualObligationEntity contractualObligation) {
        this.contractualObligations.remove(contractualObligation);
        contractualObligation.setInstrumentCategory(null);
    }

    @Override
    public void addMusician(IMusicianEntity musician) {
        this.musicians.add(musician);
        musician.getInstrumentCategories().add(this);
    }

    @Override
    public void removeMusician(IMusicianEntity musician) {
        this.musicians.remove(musician);
        musician.removeInstrumentCategory(this);
    }

    public List<IMusicianEntity> getMusicians() {
        return musicians;
    }

    public void setMusicians(
        List<IMusicianEntity> musicians
    ) {
        this.musicians = musicians;
    }

    @Override
    public void addInstrumentationPosition(IInstrumentationPositionEntity instrumentationPosition) {
        this.instrumentationPositions.add(instrumentationPosition);
        instrumentationPosition.setInstrumentCategory(this);
    }

    @Override
    public void removeInstrumentationPosition(
        IInstrumentationPositionEntity instrumentationPosition
    ) {
        this.instrumentationPositions.remove(instrumentationPosition);
        instrumentationPosition.setInstrumentCategory(null);
    }
}
