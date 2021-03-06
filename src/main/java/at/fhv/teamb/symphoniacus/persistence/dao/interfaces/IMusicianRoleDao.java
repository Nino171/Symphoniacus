package at.fhv.teamb.symphoniacus.persistence.dao.interfaces;

import at.fhv.teamb.symphoniacus.persistence.Dao;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicianRoleEntity;
import java.util.List;

public interface IMusicianRoleDao extends Dao<IMusicianRoleEntity> {
    List<IMusicianRoleEntity> getAll();
}
