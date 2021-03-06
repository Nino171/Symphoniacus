package at.fhv.teamb.symphoniacus.presentation;

import at.fhv.teamb.symphoniacus.application.type.MusicianRoleType;
import at.fhv.teamb.symphoniacus.domain.AdministrativeAssistant;
import at.fhv.teamb.symphoniacus.domain.Musician;
import at.fhv.teamb.symphoniacus.persistence.model.AdministrativeAssistantEntity;
import at.fhv.teamb.symphoniacus.persistence.model.MusicianEntity;
import at.fhv.teamb.symphoniacus.persistence.model.MusicianRoleEntity;
import at.fhv.teamb.symphoniacus.persistence.model.UserEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IAdministrativeAssistantEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicianRoleEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IUserEntity;
import at.fhv.teamb.symphoniacus.presentation.internal.TabPaneEntry;
import java.util.Locale;
import java.util.Queue;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the MainController.
 *
 * @author Valentin Goronjic
 */
public class MainControllerTest {

    private MainController mainController = new MainController();
    Locale locale = new Locale("en", "UK");
    ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);

    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForMusician() {
        MusicianEntity entity = new MusicianEntity();
        IUserEntity user = new UserEntity();
        user.setFirstName("Max");
        entity.setUser(user);
        Musician m = new Musician(entity);
        Queue<TabPaneEntry> tabs = mainController.getPermittedTabs(
            m,
            null
        );
        Assertions.assertTrue(
            tabs.contains(
                TabPaneEntry.UNSUPPORTED
            ),
            "Should see no view atm"
        );
    }

    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForMusicianWithRoles() {
        // Given: Musician is Musician with Duty Scheduler role
        MusicianEntity entity = new MusicianEntity();
        IUserEntity user = new UserEntity();
        user.setFirstName("Max");
        entity.setUser(user);
        IMusicianRoleEntity role = new MusicianRoleEntity();
        role.setMusicianRoleId(2);
        role.setDescription(MusicianRoleType.DUTY_SCHEDULER);
        entity.addMusicianRole(role);
        Musician m = new Musician(entity);

        // When: we call getPermittedTabs from TabPaneController
        Queue<TabPaneEntry> tabs = mainController.getPermittedTabs(
            m,
            null
        );

        // Then: We should see dutySchedulerCalendar and dutySchedule (last one hidden) - Tabs
        Assertions.assertTrue(
            tabs.contains(
               TabPaneEntry.DUTY_SCHEDULER_CALENDAR_VIEW
            ),
            "Should contain dutySchedulerCalendar"
        );
    }

    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForAssistant() {
        IAdministrativeAssistantEntity entity = new AdministrativeAssistantEntity();
        AdministrativeAssistant aa = new AdministrativeAssistant(entity);
        Queue<TabPaneEntry> tabs = mainController.getPermittedTabs(
            null,
            aa
        );
        Assertions.assertTrue(
            tabs.contains(
                TabPaneEntry.ORG_OFFICER_CALENDAR_VIEW
            ),
            "Should contain organizational officer view"
        );
    }

}
