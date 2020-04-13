package at.fhv.teamb.symphoniacus.persistence.model;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Table;

@Entity
@Table(name = "musician")
@NamedEntityGraph(
    name="musician-with-collections",
    attributeNodes = {
        @NamedAttributeNode("userId"),
        @NamedAttributeNode("section"),
        @NamedAttributeNode("musicianRoles")
    }
)
public class Musician {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musicianId")
    private Integer musicianId;

    @ManyToMany
    @JoinTable(
        name = "musicianRole_musician",
        joinColumns = {
            @JoinColumn(name = "musicianId")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "musicianRoleId")
        }
    )
    private List<MusicianRole> musicianRoles = new LinkedList<>();

    @Column(name = "userId")
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sectionId")
    private Section section;

    public Integer getMusicianId() {
        return this.musicianId;
    }

    public void setMusicianId(Integer musicianId) {
        this.musicianId = musicianId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<MusicianRole> getMusicianRoles() {
        return this.musicianRoles;
    }

    public void addMusicianRole(MusicianRole role) {
        this.musicianRoles.add(role);
        role.addMusician(this);
    }

    public void removeMusicianRole(MusicianRole role) {
        this.musicianRoles.remove(role);
        role.removeMusician(this);
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public String toString() {
        return "Musician{" +
            "musicianId=" + musicianId +
            ", musicianRoles=" + musicianRoles +
            ", userId=" + userId +
            ", section=" + section +
            '}';
    }

}
