package at.fhv.teamb.symphoniacus.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import at.fhv.teamb.symphoniacus.persistence.model.DutyEntity;
import at.fhv.teamb.symphoniacus.persistence.model.Section;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DutyDaoTest {
    private static final Logger LOG = LogManager.getLogger(DutyDaoTest.class);
    private DutyDao dao;

    @BeforeAll
    void initialize() {
        this.dao = new DutyDao();
    }

    @Test
    void testFindAllDutiesForWeek_ShouldReturnNotNull() {
        // When
        List<DutyEntity> list = this.dao.findAllInRange(
            LocalDateTime.of(2020, 3, 30, 0, 0, 0),
            LocalDateTime.of(2020, 4, 5, 0, 0, 0)
        );

        // Then
        assertNotNull(list);
    }

    @Test
    void findAllInRangeWithSection_ShouldReturnNotNull() {
        // Given
        Section s = new Section();
        s.setSectionId(1);

        // When
        List<DutyEntity> list = this.dao.findAllInRangeWithSection(s,
            LocalDateTime.of(2020, 5, 1, 0, 0, 0),
            LocalDateTime.of(2020, 5, 1, 14, 0, 0),
            true, false, false);

        // Then
        LOG.debug("Result size? " + list.size());
        assertNotNull(list);
    }
}
