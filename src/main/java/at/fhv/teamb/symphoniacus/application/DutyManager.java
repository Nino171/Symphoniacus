package at.fhv.teamb.symphoniacus.application;

import at.fhv.teamb.symphoniacus.application.dto.DutyCategoryDto;
import at.fhv.teamb.symphoniacus.application.dto.DutyDto;
import at.fhv.teamb.symphoniacus.application.dto.InstrumentationDto;
import at.fhv.teamb.symphoniacus.application.dto.MusicalPieceDto;
import at.fhv.teamb.symphoniacus.application.dto.SectionDto;
import at.fhv.teamb.symphoniacus.application.dto.SeriesOfPerformancesDto;
import at.fhv.teamb.symphoniacus.domain.Duty;
import at.fhv.teamb.symphoniacus.domain.DutyCategory;
import at.fhv.teamb.symphoniacus.domain.Section;
import at.fhv.teamb.symphoniacus.persistence.PersistenceState;
import at.fhv.teamb.symphoniacus.persistence.dao.DutyCategoryChangeLogDao;
import at.fhv.teamb.symphoniacus.persistence.dao.DutyCategoryDao;
import at.fhv.teamb.symphoniacus.persistence.dao.DutyDao;
import at.fhv.teamb.symphoniacus.persistence.dao.InstrumentationDao;
import at.fhv.teamb.symphoniacus.persistence.dao.MusicianDao;
import at.fhv.teamb.symphoniacus.persistence.dao.SeriesOfPerformancesDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.IDutyCategoryChangeLogDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.IDutyCategoryDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.IDutyDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.IInstrumentationDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.IMusicianDao;
import at.fhv.teamb.symphoniacus.persistence.dao.interfaces.ISeriesOfPerformancesDao;
import at.fhv.teamb.symphoniacus.persistence.model.DutyCategoryChangelogEntity;
import at.fhv.teamb.symphoniacus.persistence.model.DutyEntity;
import at.fhv.teamb.symphoniacus.persistence.model.SectionEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IDutyCategoryChangelogEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IDutyCategoryEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IDutyEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IDutyPositionEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IInstrumentCategoryEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IInstrumentationEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMonthlyScheduleEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicalPieceEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IMusicianEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.ISectionMonthlyScheduleEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.ISeriesOfPerformancesEntity;
import at.fhv.teamb.symphoniacus.persistence.model.interfaces.IWeeklyScheduleEntity;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is responsible for finding {@link DutyEntity} objects based on a range of time and
 * {@link SectionEntity}.
 *
 * @author Nino Heinzle
 * @author Danijel Antonijevic
 * @author Dominic Luidold
 */
public class DutyManager {
    private static final Logger LOG = LogManager.getLogger(DutyManager.class);
    private final DutyPositionManager dutyPositionManager;
    private final MonthlyScheduleManager monthlyScheduleManager;
    private final SectionMonthlyScheduleManager sectionMonthlyScheduleManager;
    private final SeriesOfPerformancesManager seriesOfPerformancesManager;
    private final WeeklyScheduleManager weeklyScheduleManager;
    private final IDutyCategoryChangeLogDao changeLogDao;
    private final ISeriesOfPerformancesDao seriesDao;
    private final IDutyCategoryDao dutyCategoryDao;
    private final IInstrumentationDao instrumentationDao;
    private final IMusicianDao musicianDao;
    protected IDutyDao dutyDao;

    /**
     * Initializes the DutyManager.
     */
    public DutyManager() {
        this.dutyPositionManager = new DutyPositionManager();
        this.monthlyScheduleManager = new MonthlyScheduleManager();
        this.sectionMonthlyScheduleManager = new SectionMonthlyScheduleManager();
        this.seriesOfPerformancesManager = new SeriesOfPerformancesManager();
        this.weeklyScheduleManager = new WeeklyScheduleManager();
        this.changeLogDao = new DutyCategoryChangeLogDao();
        this.dutyDao = new DutyDao();
        this.seriesDao = new SeriesOfPerformancesDao();
        this.dutyCategoryDao = new DutyCategoryDao();
        this.instrumentationDao = new InstrumentationDao();
        this.musicianDao = new MusicianDao();
    }

    /**
     * Returns the Monday of the week based on the {@link LocalDate}.
     *
     * @param givenDate The date to determine the monday of the week
     * @return A LocalDate representing the monday of the week
     */
    public static LocalDate getLastMondayDate(LocalDate givenDate) {
        // Will always jump back to last monday
        return givenDate.with(DayOfWeek.MONDAY);
    }

    /**
     * Converts {@link DutyEntity} objects to {@link Duty} objects.
     *
     * @param entities The entities to convert
     * @return A List of Duty objects
     */
    public static List<Duty> convertEntitiesToDomainObjects(List<IDutyEntity> entities) {
        List<Duty> duties = new LinkedList<>();
        for (IDutyEntity entity : entities) {
            duties.add(new Duty(entity));
        }
        return duties;
    }

    /**
     * Returns a loaded duty from its id.
     *
     * @param dutyId The identifier of this duty
     * @return A minimal-loaded duty
     */
    public Optional<Duty> loadDutyDetails(Integer dutyId) {
        Optional<IDutyEntity> dutyEntity = this.dutyDao.find(dutyId);
        return dutyEntity.map(Duty::new);
    }

    /**
     * Returns a loaded duty from its id.
     *
     * @param dutyId The identifier of this duty
     * @return A minimal-loaded duty
     */
    public Optional<DutyDto> loadDutyDetailsDto(Integer dutyId) {
        Optional<IDutyEntity> dutyEntity = this.dutyDao.find(dutyId);
        Optional<Duty> duty = dutyEntity.map(Duty::new);
        return duty.map(this::dutyToDto);
    }

    /**
     * Finds all duties within a full week (any Date can be given).
     * converts the given Date the last Monday
     *
     * @param start is any given Date
     * @return a List of all matching duties
     */
    public List<Duty> findAllInWeek(LocalDate start) {
        List<IDutyEntity> entityList = this.dutyDao.findAllInWeek(
            getLastMondayDate(start).atStartOfDay()
        );

        List<Duty> dutyList = new LinkedList<>();
        for (IDutyEntity entity : entityList) {
            dutyList.add(new Duty(entity));
        }

        return dutyList;
    }

    /**
     * Finds all duties in a within the week of a given Date for a section.
     *
     * @param sectionOfUser The section of the current user
     * @param start         A LocalDate that represents the start
     * @return A List of the matching duties
     */
    public List<Duty> findAllInWeekWithSection(
        SectionDto sectionOfUser,
        LocalDate start
    ) {
        SectionEntity sectionEntity = sectionDtoToSectionEntity(sectionOfUser);
        return convertEntitiesToDomainObjects(
            this.dutyDao.findAllInWeekWithSection(
                sectionEntity,
                getLastMondayDate(start).atStartOfDay(),
                false,
                false,
                false
            )
        );
    }

    /**
     * Finds all duties in a specific range of time for a section.
     *
     * @param section The section of the current user
     * @param start   A LocalDate that represents the start
     * @param end     A LocalDate that represents the end
     * @return A List of the matching duties
     */
    public List<Duty> findAllInRangeWithSection(
        SectionDto section,
        LocalDate start,
        LocalDate end
    ) {
        SectionEntity sectionEntity = sectionDtoToSectionEntity(section);
        return convertEntitiesToDomainObjects(
            this.dutyDao.findAllInRangeWithSection(
                sectionEntity,
                start.atStartOfDay(),
                end.atStartOfDay(),
                true,
                false,
                false
            )
        );
    }

    /**
     * Finds all duties in a specific range of time.
     *
     * @param start A LocalDate that represents the start
     * @param end   A LocalDate that represents the end
     * @return A List of the matching duties
     */
    public List<Duty> findAllInRange(LocalDate start, LocalDate end) {
        return convertEntitiesToDomainObjects(
            this.dutyDao.findAllInRange(
                start.atStartOfDay(),
                end.atStartOfDay()
            )
        );
    }

    /**
     * Returns a List of {@link Duty} objects for a {@link Section}.
     *
     * @return A List of duties
     */
    public List<Duty> getOtherDutiesForSopOrSection(
        Duty duty,
        SectionDto section,
        Integer numberOfDuties
    ) {
        // Look whether it is a SoP or not.
        if (duty == null) {
            LOG.error("Cannot getLastDuties when duty is null");
            return new LinkedList<>();
        }

        ISeriesOfPerformancesEntity sop = duty
            .getEntity()
            .getSeriesOfPerformances();

        List<IDutyEntity> resultList;
        if (sop.getSeriesOfPerformancesId() != null) {
            // get last duties for this SoP
            resultList = this.dutyDao.getOtherDutiesForSeriesOfPerformances(
                sop,
                duty.getEntity().getStart(),
                numberOfDuties
            );
        } else {
            SectionEntity sectionEntity = sectionDtoToSectionEntity(section);
            // get last duties of section
            resultList = this.dutyDao.getOtherDutiesForSection(
                duty.getEntity(),
                sectionEntity,
                numberOfDuties
            );
        }

        if (resultList == null || resultList.isEmpty()) {
            LOG.error("No results found for getOtherDutiesForSopOrSection");
            return new LinkedList<>();
        }

        return convertEntitiesToDomainObjects(resultList);
    }

    /**
     * Creates a new {@link DutyEntity} object based on given data.
     *
     * @param description The description to use
     * @param timeOfDay   The time of day description
     * @param start       The start of the duty
     * @param end         The end of the duty
     * @return A duty domain object
     */
    private IDutyEntity createDutyEntity(
        String description,
        String timeOfDay,
        LocalDateTime start,
        LocalDateTime end
    ) {
        // Get monthly schedule entity
        IMonthlyScheduleEntity monthlyScheduleEntity =
            this.monthlyScheduleManager.createIfNotExists(YearMonth.from(start.toLocalDate()));
        // Get weekly schedule entity
        IWeeklyScheduleEntity weeklyScheduleEntity =
            this.weeklyScheduleManager.createIfNotExists(start.toLocalDate(), start.getYear());

        // Add weekly schedule to monthly schedule and vice versa
        monthlyScheduleEntity.addWeeklySchedule(weeklyScheduleEntity);

        // Create duty entity
        IDutyEntity dutyEntity = new DutyEntity();

        // Add duty to weekly schedule and vice versa
        weeklyScheduleEntity.addDuty(dutyEntity);

        // Fill duty entity with data
        dutyEntity.setDescription(description);
        dutyEntity.setTimeOfDay(timeOfDay);
        dutyEntity.setStart(start);
        dutyEntity.setEnd(end);
        for (ISectionMonthlyScheduleEntity sectionMonthlySchedule :
            this.sectionMonthlyScheduleManager.createIfNotExist(
                start.getYear(),
                start.getMonthValue(),
                monthlyScheduleEntity
            )
        ) {
            dutyEntity.addSectionMonthlySchedule(sectionMonthlySchedule);
        }

        // Return entity
        return dutyEntity;
    }

    /**
     * Persists a new {@link Duty} object to the database.
     *
     * <p>The method will subsequently change the {@link PersistenceState} of the object
     * from {@link PersistenceState#EDITED} to {@link PersistenceState#PERSISTED}, provided
     * that the database update was successful.
     *
     * @param newDuty       The new Duty to create
     * @param pointsChanged Whether the points vary from default points
     * @return A ValidationResult containing the validated DutyDto, if successful
     */
    public ValidationResult<DutyDto> createNewDuty(
        DutyDto newDuty,
        boolean pointsChanged
    ) {
        // Create Duty entity
        IDutyEntity dutyEntity = createDutyEntity(
            newDuty.getDescription(),
            Duty.calculateTimeOfDay(newDuty.getStart()),
            newDuty.getStart(),
            newDuty.getEnd()
        );

        // Fill Duty Entity with data from DTO
        this.fillNewDutyWithData(newDuty, dutyEntity);

        // Domain object validation
        Duty duty = new Duty(dutyEntity);
        ValidationResult<DutyDto> validationResult = duty.isValid();
        if (!validationResult.isValid()) {
            LOG.error("Duty to persist is not valid");
            validationResult.setMessage("Persisting the new Duty failed. Please try again..");
            return validationResult;
        }

        // Validation OK, persist Duty
        Optional<IDutyEntity> persistedDuty = this.dutyDao.persist(dutyEntity);

        // Update points if user has updated them
        if (pointsChanged) {
            this.updateDutyCategoryPoints(dutyEntity, newDuty.getPoints());
        }

        DutyDto.DutyDtoBuilder dutyDtoBuilder = new DutyDto.DutyDtoBuilder()
            .withDutyId(dutyEntity.getDutyId())
            .withStart(dutyEntity.getStart())
            .withEnd(dutyEntity.getEnd())
            .withTimeOfDay(dutyEntity.getTimeOfDay())
            .withDescription(dutyEntity.getDescription());

        boolean persisted = persistedDuty.isPresent();
        if (persisted) {
            LOG.debug(
                "Persisted duty {{}, '{}'}",
                dutyEntity.getDutyId(),
                dutyEntity.getDescription()
            );
            dutyDtoBuilder.withPersistenceState(PersistenceState.PERSISTED);
        } else {
            LOG.error(
                "Could not persist duty {{}, '{}'}",
                dutyEntity.getDutyId(),
                dutyEntity.getDescription()
            );
            dutyDtoBuilder.withPersistenceState(PersistenceState.EDITED);
        }
        validationResult.setValid(persisted);
        validationResult.setPayload(dutyDtoBuilder.build());

        return validationResult;
    }

    /**
     * Persists an existing {@link Duty} object to the database.
     *
     * <p>The method will subsequently change the {@link PersistenceState} of the object
     * from {@link PersistenceState#EDITED} to {@link PersistenceState#PERSISTED}, provided
     * that the database update was successful.
     *
     * @param duty The duty to update
     */
    public void update(Duty duty) {
        Optional<IDutyEntity> persisted = this.dutyDao.update(duty.getEntity());

        if (persisted.isPresent()) {
            duty.setPersistenceState(PersistenceState.PERSISTED);
            LOG.debug(
                "Persisted duty {{}, '{}'}",
                duty.getEntity().getDutyId(),
                duty.getTitle()
            );
        } else {
            LOG.error(
                "Could not persist duty {{}, '{}'}",
                duty.getEntity().getDutyId(),
                duty.getTitle()
            );
        }
    }

    /**
     * Checks whether a duty with the given parameters exists or not.
     *
     * @param seriesOfPerformances given Series of Performances from searched Duty.
     * @param instrumentations     given instrumentation from searched Duty.
     * @param startingDate         given starting Date from the searched Duty.
     * @param endingDate           given ending Date from searched Duty.
     * @param category             given dutyCategory from searched Duty.
     * @return whether this specific duty exists or not
     */
    public boolean doesDutyAlreadyExists(
        SeriesOfPerformancesDto seriesOfPerformances,
        List<InstrumentationDto> instrumentations,
        LocalDateTime startingDate,
        LocalDateTime endingDate,
        DutyCategoryDto category) {

        Optional<IDutyCategoryEntity> dutyCat = this.dutyCategoryDao.find(
            category.getDutyCategoryId()
        );

        //Convert List of instDTO to instEntity
        List<IInstrumentationEntity> newInstrumentations = new LinkedList<>();
        Optional<IInstrumentationEntity> inst = Optional.empty();

        for (InstrumentationDto i : instrumentations) {
            inst = this.instrumentationDao.find(i.getInstrumentationId());
            inst.ifPresent(newInstrumentations::add);
        }

        Optional<ISeriesOfPerformancesEntity> series = this.seriesDao.find(
            seriesOfPerformances.getSeriesOfPerformancesId()
        );

        if (series.isPresent() && dutyCat.isPresent() && !newInstrumentations.isEmpty()) {
            return this.dutyDao.doesDutyAlreadyExists(
                series.get(),
                newInstrumentations,
                startingDate,
                endingDate,
                dutyCat.get()
            );
        } else {
            return false;
        }
    }

    /**
     * Converts a DutyDto to Duty Domain Object.
     *
     * @param duty given DutyDto
     * @return Duty Domain Object
     */
    public Duty getDutyByDutyDto(DutyDto duty) {
        Optional<IDutyEntity> dutyEntity = this.dutyDao.find(duty.getDutyId());

        if (dutyEntity.isPresent()) {
            return new Duty(dutyEntity.get());
        } else {
            LOG.error("A Duty with given DutyId from Dto does not exists");
            return null;
        }
    }

    /**
     * Finds all unscheduled duties for a musician.
     *
     * @param userId User Identifier of Musician
     * @return Duties found
     */
    public Set<DutyDto> findFutureUnscheduledDutiesForMusician(Integer userId) {
        LOG.debug(userId);
        Optional<IMusicianEntity> musicianEntity = this.musicianDao.findMusicianByUserId(userId);

        if (musicianEntity.isEmpty()) {
            LOG.error("Did not find user");
            return new HashSet<>();
        }

        // Get instrument categories of User
        IMusicianEntity musician = musicianEntity.get();
        List<IInstrumentCategoryEntity> categories = musician.getInstrumentCategories();

        // Planned duties for this section
        // where we can still make wishes
        // still need to check instrument category of Musician!
        List<IDutyEntity> plannedDutyEntities = this.dutyDao.findFutureUnscheduledDuties(
            musician.getSection()
        );
        LOG.debug("Found {} planned duties", plannedDutyEntities.size());

        Set<IDutyEntity> dutiesForMusician = new HashSet<>();
        for (IDutyEntity dutyEntity : plannedDutyEntities) {
            Set<IDutyPositionEntity> dutyPositionEntities = dutyEntity.getDutyPositions();
            for (IDutyPositionEntity dpe : dutyPositionEntities) {
                IInstrumentCategoryEntity ice = dpe
                    .getInstrumentationPosition()
                    .getInstrumentCategory();

                if (categories.contains(ice)) {
                    LOG.debug(
                        "DutyPosition {} fits for {}",
                        dpe.getInstrumentationPosition().getPositionDescription(),
                        ice.getDescription());
                    dutiesForMusician.add(dutyEntity);
                    break;
                }
            }
        }

        LOG.debug("Found {} duties for musician", dutiesForMusician.size());

        // Convert to DTO
        Set<DutyDto> result = new HashSet<>();
        for (IDutyEntity dutyEntity : dutiesForMusician) {
            DutyDto dutyDto = new DutyDto.DutyDtoBuilder()
                .withDutyId(dutyEntity.getDutyId())
                .withDescription(dutyEntity.getDescription())
                .withDutyCategory(
                    new DutyCategoryDto.DutyCategoryDtoBuilder(dutyEntity.getDutyId())
                        .withType(dutyEntity.getDutyCategory().getType())
                        .withPoints(dutyEntity.getDutyCategory().getPoints())
                        .build()
                )
                .withStart(dutyEntity.getStart())
                .withEnd(dutyEntity.getEnd())
                .withSeriesOfPerformances(
                    this.seriesOfPerformancesManager.convertSopToDto(
                        dutyEntity.getSeriesOfPerformances()
                    )
                ).build();
            result.add(dutyDto);
        }
        return result;
    }

    /**
     * Convert a Duty to a DutyDto.
     *
     * @param duty to convert.
     * @return Dto of given Duty.
     */
    private DutyDto dutyToDto(Duty duty) {
        Set<MusicalPieceDto> musicalPieces = new HashSet<>();

        for (IMusicalPieceEntity mp : duty
            .getEntity().getSeriesOfPerformances().getMusicalPieces()) {
            MusicalPieceDto musicalPieceDto =
                new MusicalPieceDto.MusicalPieceDtoBuilder(mp.getMusicalPieceId())
                    .withName(mp.getName())
                    .withCategory(mp.getCategory())
                    .build();
            musicalPieces.add(musicalPieceDto);
        }

        SeriesOfPerformancesDto seriesOfPerformancesDto =
            new SeriesOfPerformancesDto.SeriesOfPerformancesDtoBuilder(
                duty.getEntity().getSeriesOfPerformances().getSeriesOfPerformancesId()
            )
                .withDescription(duty.getEntity().getSeriesOfPerformances().getDescription())
                .withMusicalPieces(musicalPieces)
                .build();

        DutyCategoryDto dutyCategory = new DutyCategoryDto.DutyCategoryDtoBuilder(
            duty.getEntity().getDutyCategory().getDutyCategoryId()
        ).withType(duty.getEntity().getDutyCategory().getType()).build();

        return new DutyDto.DutyDtoBuilder()
            .withDutyId(duty.getEntity().getDutyId())
            .withDescription(duty.getEntity().getDescription())
            .withTimeOfDay(duty.getEntity().getTimeOfDay())
            .withDutyCategory(dutyCategory)
            .withStart(duty.getEntity().getStart())
            .withEnd(duty.getEntity().getEnd())
            .withSeriesOfPerformances(seriesOfPerformancesDto)
            .build();
    }

    /**
     * Fills a {@link DutyEntity} with data.
     *
     * @param newDuty    The DutyDto with data
     * @param dutyEntity The Duty Entity to fill
     */
    private void fillNewDutyWithData(DutyDto newDuty, IDutyEntity dutyEntity) {
        Optional<ISeriesOfPerformancesEntity> newSeries = this.seriesDao.find(
            newDuty.getSeriesOfPerformances().getSeriesOfPerformancesId()
        );
        Optional<IDutyCategoryEntity> newCategory = this.dutyCategoryDao.find(
            newDuty.getDutyCategory().getDutyCategoryId()
        );
        if (newSeries.isPresent() && newCategory.isPresent()) {
            dutyEntity.setSeriesOfPerformances(newSeries.get());
            dutyEntity.setDutyCategory(newCategory.get());
        } else {
            LOG.error(
                "SeriesOfPerformances and/or DutyCategory are missing for duty {}, "
                    + "this should not have happened",
                dutyEntity.getDutyId()
            );
        }

        this.dutyPositionManager.createDutyPositions(
            convertInstrumentationToEntityObjects(newDuty.getInstrumentations()),
            dutyEntity
        );
    }

    /**
     * Updates the points of a {@link DutyCategory}.
     *
     * @param dutyEntity The Duty Entity to use
     * @param points     The new amount of points
     */
    private void updateDutyCategoryPoints(IDutyEntity dutyEntity, int points) {
        if (this.changeLogDao.doesLogAlreadyExists(dutyEntity)) {
            Optional<IDutyCategoryChangelogEntity> changeLog =
                this.changeLogDao.getChangelogByDetails(dutyEntity);
            if (changeLog.isPresent()) {
                changeLog.get().setPoints(points);
                changeLogDao.update(changeLog.get());
            } else {
                LOG.error("Returned changelog is null but shouldn't be null! @save");
            }
        } else {
            IDutyCategoryChangelogEntity changeLog = new DutyCategoryChangelogEntity();
            changeLog.setDutyCategory(dutyEntity.getDutyCategory());
            changeLog.setPoints(points);
            changeLog.setStartDate(dutyEntity.getStart().toLocalDate());
            changeLogDao.persist(changeLog);
        }
    }

    private Set<IInstrumentationEntity> convertInstrumentationToEntityObjects(
        Set<InstrumentationDto> instrumentations
    ) {
        Set<IInstrumentationEntity> newInstrumentations = new LinkedHashSet<>();
        for (InstrumentationDto i : instrumentations) {
            Optional<IInstrumentationEntity> newInst = this.instrumentationDao
                .find(i.getInstrumentationId());
            newInst.ifPresent(newInstrumentations::add);
        }
        return newInstrumentations;
    }

    private SectionEntity sectionDtoToSectionEntity(SectionDto section) {
        SectionEntity sectionEntity = new SectionEntity();
        sectionEntity.setSectionId(section.getSectionId());
        sectionEntity.setSectionShortcut(section.getSectionShortcut());
        sectionEntity.setDescription(section.getDescription());
        return sectionEntity;
    }
}
