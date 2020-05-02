package at.fhv.teamb.symphoniacus.presentation;

import at.fhv.teamb.symphoniacus.application.type.DomainUserType;
import at.fhv.teamb.symphoniacus.application.type.MusicianRoleType;
import at.fhv.teamb.symphoniacus.domain.AdministrativeAssistant;
import at.fhv.teamb.symphoniacus.domain.Musician;
import at.fhv.teamb.symphoniacus.persistence.model.AdministrativeAssistantEntity;
import at.fhv.teamb.symphoniacus.persistence.model.MusicianEntity;
import at.fhv.teamb.symphoniacus.persistence.model.MusicianRole;
import at.fhv.teamb.symphoniacus.persistence.model.UserEntity;
import at.fhv.teamb.symphoniacus.presentation.internal.TabPaneEntry;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Valentin
 */
public class MainControllerTest {

    private MainController mainController = new MainController();
    Locale locale = new Locale("en", "UK");
    ResourceBundle bundle = ResourceBundle.getBundle("bundles.language", locale);


    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForMusician() {
        MusicianEntity entity = new MusicianEntity();
        UserEntity user = new UserEntity();
        user.setFirstName("Max");
        entity.setUser(user);
        Musician m = new Musician(entity);
        List<TabPaneEntry> tabs = mainController.getPermittedTabs(
            DomainUserType.DOMAIN_MUSICIAN,
            m
        );
        Assertions.assertTrue(
            tabs.isEmpty(),
            "Should see no view atm"
        );
    }

    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForMusicianWithRoles() {
        // Given: Musician is Musician with Duty Scheduler role
        MusicianEntity entity = new MusicianEntity();
        UserEntity user = new UserEntity();
        user.setFirstName("Max");
        entity.setUser(user);
        MusicianRole role = new MusicianRole();
        role.setMusicianRoleId(2);
        role.setDescription(MusicianRoleType.DUTY_SCHEDULER);
        entity.addMusicianRole(role);
        Musician m = new Musician(entity);

        // When: we call getPermittedTabs from TabPaneController
        List<TabPaneEntry> tabs = mainController.getPermittedTabs(
            DomainUserType.DOMAIN_MUSICIAN,
            m
        );

        // Then: We should see dutySchedulerCalendar and dutySchedule (last one hidden) - Tabs
        Assertions.assertTrue(
            tabs.contains(
                new TabPaneEntry(
                    this.bundle.getString("menu.tab.duty.roster.title"),
                    "/view/dutySchedulerCalendar.fxml"
                )
            ),
            "Should contain dutySchedulerCalendar"
        );

        Assertions.assertTrue(
            tabs.contains(
                new TabPaneEntry(
                    this.bundle.getString("menu.tab.duty.roster.title"),
                    "/view/dutySchedule.fxml")
            ),
            "Should contain duty Schedule view"
        );
    }

    @Test
    public void testGetPermittedTabs_shouldReturnAListOfPermittedTabsForAssistant() {
        AdministrativeAssistantEntity entity = new AdministrativeAssistantEntity();
        AdministrativeAssistant aa = new AdministrativeAssistant(entity);
        List<TabPaneEntry> tabs = mainController.getPermittedTabs(
            DomainUserType.DOMAIN_ADMINISTRATIVE_ASSISTANT,
            aa
        );
        Assertions.assertTrue(
            tabs.contains(
                new TabPaneEntry(
                    this.bundle.getString("menu.tab.duty.roster.title"),
                    "/view/organizationalOfficerCalendarView.fxml"
                )
            ),
            "Should contain organizational officer view"
        );
    }

}
