package at.fhv.teamb.symphoniacus.persistence.dao.interfaces;

import at.fhv.teamb.symphoniacus.persistence.Dao;
import at.fhv.teamb.symphoniacus.persistence.model.MusicalPieceEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IDutyEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicalPieceEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IMusicalPieceDao extends Dao<IMusicalPieceEntity> {

    /**
     * Fetches all {@link MusicalPieceEntity} objects.
     *
     * @return A Set of MusicalPieceEntity
     */
    Set<IMusicalPieceEntity> getAll();

    /**
     * Returns all {@link MusicalPieceEntity} objects for a given name.
     *
     * @param name The name to search for
     * @return A musical piece with the same name
     */
    Optional<IMusicalPieceEntity> getMusicalPieceFromName(String name);

    /**
     * Gets the musical pieces of a duty (via Series of Performances).
     *
     * @param dutyEntity The duty for which the musical pieces should be loaded
     * @return List of {@link MusicalPieceEntity} objects (empty when no series of performances)
     */
    List<IMusicalPieceEntity> getMusicalPiecesOfDuty(IDutyEntity dutyEntity);
}
