package at.fhv.teamb.symphoniacus.presentation;

import at.fhv.teamb.symphoniacus.application.DutyScheduleManager;
import at.fhv.teamb.symphoniacus.domain.ActualSectionInstrumentation;
import at.fhv.teamb.symphoniacus.domain.Duty;
import at.fhv.teamb.symphoniacus.domain.DutyPosition;
import at.fhv.teamb.symphoniacus.domain.Musician;
import at.fhv.teamb.symphoniacus.domain.Section;
import at.fhv.teamb.symphoniacus.presentation.internal.ScheduleButtonTableCell;
import at.fhv.teamb.symphoniacus.presentation.internal.DutyPositionMusicianTableModel;
import at.fhv.teamb.symphoniacus.presentation.internal.MusicianTableModel;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

public class DutyScheduleController implements Initializable, Controllable {

    private static final Logger LOG = LogManager.getLogger(DutyScheduleController.class);
    private Duty duty;
    private Section section;
    private DutyScheduleManager dutyScheduleManager;
    private ActualSectionInstrumentation actualSectionInstrumentation;
    private DutyPosition selectedDutyPosition;

    @FXML
    private AnchorPane dutySchedule;

    @FXML
    private Button scheduleBackBtn;

    @FXML
    private Label dutyTitle;

    @FXML
    private TableView<DutyPositionMusicianTableModel> positionsTable;

    @FXML
    private TableView<MusicianTableModel> musicianTableWithRequests;

    @FXML
    private TableView<MusicianTableModel> musicianTableWithoutRequests;

    @FXML
    private SplitPane rightSplitPane;

    @FXML
    private SplitPane leftSplitPane;

    @FXML
    private TableColumn<MusicianTableModel, Button> columnSchedule2;

    @FXML
    private TableColumn<DutyPositionMusicianTableModel, Button> columnUnsetPosition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.registerController();
        this.dutyScheduleManager = new DutyScheduleManager();

        this.dutySchedule.setVisible(false);
        this.scheduleBackBtn.setOnAction(e -> {
            this.hide();
            MasterController mc = MasterController.getInstance();
            CalendarController cc = (CalendarController) mc.get("CalendarController");
            cc.show();
        });

        // add selected item click listener
        this.musicianTableWithRequests
            .getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        addMusicianToPosition(
                            this.actualSectionInstrumentation,
                            newValue.getMusician(),
                            this.selectedDutyPosition
                        );
                    }
                }
            );

        this.musicianTableWithoutRequests.setOnMouseClicked((MouseEvent event) -> {
            // add selected item click listener
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {

                MusicianTableModel mtm =
                    this.musicianTableWithoutRequests.getSelectionModel().getSelectedItem();
                addMusicianToPosition(
                    this.actualSectionInstrumentation,
                    mtm.getMusician(),
                    this.selectedDutyPosition
                );
            }
        });

        this.columnSchedule2.setCellFactory(
            ScheduleButtonTableCell.<MusicianTableModel>forTableColumn(
                "Schedule",
                (MusicianTableModel mtm) -> {
                    LOG.debug("Schedule btn without requests has been pressed");
                    addMusicianToPosition(
                        this.actualSectionInstrumentation,
                        mtm.getMusician(),
                        this.selectedDutyPosition
                    );
                    return mtm;
                }
            )
        );

        this.columnUnsetPosition.setCellFactory(
            ScheduleButtonTableCell.<DutyPositionMusicianTableModel>forTableColumn(
                "Unset",
                (DutyPositionMusicianTableModel dpmtm) -> {
                    LOG.debug("Unset musician button has been pressed");

                    Optional<Musician> assignedMusician = dpmtm.getDutyPosition()
                        .getAssignedMusician();

                    this.selectedDutyPosition = dpmtm.getDutyPosition();

                    if (assignedMusician.isPresent()) {
                        this.dutyScheduleManager.removeMusicianFromPosition(
                            this.actualSectionInstrumentation,
                            assignedMusician.get(),
                            this.selectedDutyPosition
                        );
                        this.positionsTable.refresh();
                    }
                    return dpmtm;
                }
            )
        );

        this.positionsTable
            .getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setActualPosition(
                            newValue.getDutyPosition()
                        );
                    }
                }
            );
    }

    @Override
    public void registerController() {
        MasterController mc = MasterController.getInstance();
        mc.put("DutyScheduleController", this);
    }

    @Override
    public void show() {
        this.initDutyPositionsTableWithMusicians();
        this.dutySchedule.setVisible(true);
    }

    @Override
    public void hide() {
        this.dutySchedule.setVisible(false);
        //TODO remove listener positionTable
        //positionsTable.getSelectionModel().clearSelection();
    }

    private void initMusicianTableWithoutRequests() {
        List<Musician> list = this.dutyScheduleManager.getMusiciansAvailableForPosition(
            this.actualSectionInstrumentation.getDuty(),
            this.selectedDutyPosition,
            Boolean.FALSE
        );

        List<MusicianTableModel> guiList = new LinkedList<>();
        int i = 0;
        int selectedIndex = 0;
        MusicianTableModel selected = null;
        for (Musician domainMusician : list) {
            MusicianTableModel mtm = new MusicianTableModel(domainMusician);
            guiList.add(mtm);
            if (this.selectedDutyPosition.getAssignedMusician().isPresent()) {
                LOG.debug("There is already a musician assigned for this position");
                if (domainMusician.getShortcut().equals(
                    this.selectedDutyPosition.getAssignedMusician().get().getShortcut()
                )) {
                    LOG.debug("Selecting index {}", i);
                    selectedIndex = i;
                    selected = mtm;
                }
            }
            i++;
        }

        ObservableList<MusicianTableModel> observableListWithoutRequests =
            FXCollections.observableArrayList();
        observableListWithoutRequests.addAll(guiList);
        this.musicianTableWithoutRequests.setItems(observableListWithoutRequests);

        // auto select current musician.
        if (selected != null) {
            this.musicianTableWithoutRequests.requestFocus();
            this.musicianTableWithoutRequests.getSelectionModel().select(selected);
            this.musicianTableWithoutRequests.scrollTo(selectedIndex);
        }
    }

    private void initMusicianTableWithRequests() {
        List<Musician> list = this.dutyScheduleManager.getMusiciansAvailableForPosition(
            this.actualSectionInstrumentation.getDuty(),
            this.selectedDutyPosition,
            Boolean.TRUE
        );

        System.out.println("musician available: " + list.size());

        List<MusicianTableModel> guiList = new LinkedList<>();
        for (Musician domainMusician : list) {
            guiList.add(new MusicianTableModel(domainMusician));
        }
        ObservableList<MusicianTableModel> observableList =
            FXCollections.observableArrayList();
        observableList.addAll(guiList);
        this.musicianTableWithoutRequests.setItems(observableList);
    }

    private void initDutyPositionsTableWithMusicians() {
        Optional<ActualSectionInstrumentation> actualSectionInstrumentation = this
            .dutyScheduleManager
            .getInstrumentationDetails(
                this.duty,
                section
            );

        if (!actualSectionInstrumentation.isPresent()) {
            LOG.error("Found no asi for duty");
            return;
        }
        this.actualSectionInstrumentation = actualSectionInstrumentation.get();
        this.duty = this.actualSectionInstrumentation.getDuty();

        ObservableList<DutyPositionMusicianTableModel> observablePositionList =
            FXCollections.observableArrayList();
        List<DutyPosition> positionList =
            this.actualSectionInstrumentation.getDuty().getDutyPositions();

        for (DutyPosition dp : positionList) {
            // TODO
            observablePositionList.add(
                new DutyPositionMusicianTableModel(dp)
            );
        }
        this.positionsTable.setItems(observablePositionList);
    }

    protected void addMusicianToPosition(
        ActualSectionInstrumentation asi,
        Musician newMusician,
        DutyPosition dutyPosition
    ) {
        //TODO Fix OutOfBound Exceptins
        Optional<Musician> oldMusician = Optional.empty();
        if (actualSectionInstrumentation.getDuty().getDutyPositions().get(
            actualSectionInstrumentation
                .getDuty()
                .getDutyPositions()
                .indexOf(dutyPosition)
        ).getAssignedMusician().isPresent()) {
            oldMusician = actualSectionInstrumentation.getDuty().getDutyPositions().get(
                actualSectionInstrumentation
                    .getDuty()
                    .getDutyPositions()
                    .indexOf(dutyPosition)
            ).getAssignedMusician();
        }

        LOG.debug(
            "New musician for position {} is: {}",
            dutyPosition.getEntity().getInstrumentationPosition().getPositionDescription(),
            newMusician.getFullName()
        );
        this.dutyScheduleManager.assignMusicianToPosition(
            asi,
            newMusician,
            dutyPosition
        );
        Notifications.create()
            .title("Musician set")
            .text("Duty position has been updated.")
            .position(Pos.CENTER)
            .hideAfter(new Duration(2000))
            .show();

        // this is obviously not good
        this.initDutyPositionsTableWithMusicians();
        this.initMusicianTableWithoutRequests();
    }

    private void setActualPosition(DutyPosition dutyPosition) {
        LOG.debug("Current DutyPosition: " + dutyPosition
            .getEntity()
            .getInstrumentationPosition()
            .getPositionDescription() + "  Current Object: " + this
        );

        this.selectedDutyPosition = dutyPosition;
        // TODO enable this when requests are implemented
        // this.initMusicianTableWithRequests();
        this.initMusicianTableWithoutRequests();
    }

    /**
     * Set the actual Duty for Controller.
     *
     * @param duty actual Duty.
     */
    public void setDuty(Duty duty) {
        this.duty = duty;

        LOG.debug("Binding duty title to: " + duty.getTitle());
        this.dutyTitle.textProperty().bind(
            new SimpleStringProperty(
                duty
                    .getEntity()
                    .getStart()
                    .format(
                        DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")
                    )
                    + " - "
                    + duty.getTitle()
            )
        );
    }

    public void setSection(Section section) {
        this.section = section;
    }
}