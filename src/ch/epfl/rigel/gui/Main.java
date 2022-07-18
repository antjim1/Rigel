package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.internationalization.*;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Main program that simulates the sky and its celestial objects during a given period of time
 *
 * @author Antonio Jimenez (314363)
 * @author Alexis Horner (315780)
 */
public class Main extends Application implements LanguageController {
    private static final String TIME_RATE_BOX_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-right";

    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Rigel";

    private static final String HYG_DATABASE_PATH = "/hygdata_v3.csv";
    private static final String ASTERISMS_PATH = "/asterisms.txt";
    private static final ObjectProperty<Language> CURRENT_LANGUAGE = new SimpleObjectProperty<>(Language.FRENCH);
    private static final String CLOCK_LOGO = "\uf017";
    private static final String FULLSCREEN_LOGO = "\uF065";
    private static final String WINDOWED_LOGO = "\uF066";
    private static final String SCREENSHOT_LOGO = "\uF03E";
    private static final String OPEN_PHOTO_LOGO = "\uF06E";
    private static final String RESET_LOGO = "\uf0e2";
    private static final String PLAY_LOGO = "\uf04b";
    private static final String PAUSE_LOGO = "\uf04c";
    private static final Font BUTTON_FONT = loadFont("/Font Awesome 5 Free-Solid-900.otf", 15);

    private static final HorizontalCoordinates INITIAL_OBSERVATION_CENTER =
            HorizontalCoordinates.ofDeg(180.000000000001, 15);
    private static final NamedTimeAccelerator INITIAL_ACCELERATOR = NamedTimeAccelerator.TIMES_300;
    private static final GeographicCoordinates INITIAL_LOCATION = GeographicCoordinates.ofDeg(6.57, 46.52);
    private static final int INITIAL_FOV_DEG = 70;
    private static final String OBSERVER_LOCATION_STYLE = "-fx-pref-width: 60; -fx-alignment: baseline-left;";
    private static final String OBSERVATION_TIME_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-center;";
    private static final String CONTROL_PANEL_STYLE = "-fx-spacing: 4; -fx-padding: 4; -fx-alignment: baseline-center";
    private static final String OBSERVER_POSITION_STYLE = "-fx-spacing: inherit; -fx-alignment: baseline-left";
    private static final String TIME_TEXT_FIELD_STYLE = "-fx-pref-width: 75; -fx-alignment: baseline-right;";
    private static final String DATE_PICKER_STYLE = "-fx-pref-width: 120";
    private static final String ZONE_ID_COMBO_BOX_STYLE = "-fx-pref-width: 180;";

    private static final String SIDE_PANEL_STYLE = "-fx-background-color: LIGHTGRAY; -fx-min-width: 0;" +
            "-fx-max-width: 190; -fx-pref-width: 190; -fx-spacing: 10;";

    private static final String LEFT_PANEL_STYLE = SIDE_PANEL_STYLE + "-fx-padding: 12;";
    private static final String RIGHT_PANEL_STYLE = SIDE_PANEL_STYLE + "-fx-padding: 24;";


    private static final MediaPlayer AMBIANT_MUSIC = mediaPlayer("/ES_Sidelight - Ethan Sloan.wav");
    private static final MediaPlayer SCREENSHOT_SOUND = mediaPlayer("/ES_Camera Shutter 3 - SFX Producer.wav");

    private static final String SCREENSHOT_PATH = "screenshot.png";

    private static final DoubleProperty FX_VOLUME = new SimpleDoubleProperty(1);

    static {
        if (SCREENSHOT_SOUND != null) {
            SCREENSHOT_SOUND.setOnEndOfMedia(SCREENSHOT_SOUND::stop);
            SCREENSHOT_SOUND.volumeProperty().bind(FX_VOLUME);
        }
    }

    public Main() {
        try {
            Translations.setController(this);
        } catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Gives the language currently chosen by the user.
     *
     * @return the language currently chosen by the user.
     */
    public ReadOnlyObjectProperty<Language> currentLanguageProperty() {
        return CURRENT_LANGUAGE;
    }

    private static Font loadFont(String resourcePath, int size) {
        Font font = null;
        try (InputStream inputStream = resourceStream(resourcePath)) {
            font = Font.loadFont(inputStream, size);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return font == null ? Font.getDefault() : font;
    }

    private static InputStream resourceStream(String resourceName) {
        return Main.class.getResourceAsStream(resourceName);
    }

    private static MediaPlayer mediaPlayer(String audioPath) {
        try {
            return new MediaPlayer(new Media(Main.class.getResource(audioPath).toString()));
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static String format(String format, Object... args) {
        return String.format(Locale.ROOT, format, args);
    }


    /**
     * Constructs the sky canvas manager with the given date, time, zone, observer's coordinates and field of view
     *
     * @param dateTimeBean          Contains the date, time and zone properties
     * @param observerLocationBean  Contains the observer's coordinates properties
     * @param viewingParametersBean Contains the field of view and the center coordinates
     * @return the sky canvas manager constructed with {@code dateTimeBean}, {@code observerLocationBean} and {@code
     * viewingParametersBean}
     * @throws IOException if an I/O error occurs
     */
    private static SkyCanvasManager skyCanvasManager(DateTimeBean dateTimeBean,
                                                     ObserverLocationBean observerLocationBean,
                                                     ViewingParametersBean viewingParametersBean) throws IOException {
        try (InputStream hs = resourceStream(HYG_DATABASE_PATH);
             InputStream as = resourceStream(ASTERISMS_PATH)) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(as, AsterismLoader.INSTANCE)
                    .build();

            return new SkyCanvasManager(catalogue, dateTimeBean, observerLocationBean, viewingParametersBean);
        }
    }

    /**
     * Constructs the control panel containing the graphical interfaces corresponding to the observer's position, the
     * observation date, time and zone, the different passage of time available
     *
     * @param observerLocationBean object containing the observer's position
     * @param dateTimeBean         object containing the observation date, time and zone
     * @param timeAnimator         object containing the different passage of time
     *
     * @return the control panel containing the observer's position, the observation date, time, zone and the different
     * passage of time
     */
    private static Node controlPanel(ObserverLocationBean observerLocationBean, DateTimeBean dateTimeBean,
                                     TimeAnimator timeAnimator) {
        HBox controlBar = new HBox(observerPosition(observerLocationBean),
                observationTime(dateTimeBean, timeAnimator),
                timeRate(timeAnimator, dateTimeBean));
        controlBar.setStyle(CONTROL_PANEL_STYLE);
        return controlBar;
    }

    /**
     * Constructs the sub-panel of the control bar containing the graphical interface corresponding to the observer's
     * position
     *
     * @param observerLocationBean the position of the observer
     *
     * @return the control bar containing the observer's position
     */
    private static Node observerPosition(ObserverLocationBean observerLocationBean) {
        Label longitude = createLabel(Translations.LONGITUDE_LABEL_TEXT);
        Label latitude = createLabel(Translations.LATITUDE_LABEL_TEXT);

        TextField lonTextField = locationTextField(observerLocationBean.lonDegProperty(),
                GeographicCoordinates::isValidLonDeg);
        TextField latTextField = locationTextField(observerLocationBean.latDegProperty(),
                GeographicCoordinates::isValidLatDeg);

        HBox observationPosition = new HBox(longitude, lonTextField, latitude, latTextField);
        observationPosition.setStyle(OBSERVER_POSITION_STYLE);
        return observationPosition;
    }

    /**
     * Constructs the sub-panel of the control bar containing the graphical interface corresponding to the observation
     * date, time and zone
     *
     * @param dateTimeBean the date, time and zone of the observation time
     * @param timeAnimator the time animator that modify the sky
     *
     * @return the control bar containing the observation date, time and zone
     */
    private static Node observationTime(DateTimeBean dateTimeBean, TimeAnimator timeAnimator) {
        Label date = createLabel(Translations.DATE_LABEL_TEXT);

        DatePicker datePicker = new DatePicker();
        datePicker.setStyle(DATE_PICKER_STYLE);
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());

        Label time = createLabel(Translations.TIME_LABEL_TEXT);
        TextField textField = new TextField();
        textField.setStyle(TIME_TEXT_FIELD_STYLE);
        TextFormatter<LocalTime> timeTextFormatter = timeTextFormatter();
        timeTextFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());
        textField.setTextFormatter(timeTextFormatter);

        ComboBox<ZoneId> comboBox = new ComboBox<>();
        comboBox.setStyle(ZONE_ID_COMBO_BOX_STYLE);
        comboBox.valueProperty().bindBidirectional(dateTimeBean.zoneProperty());

        comboBox.setItems(zoneIdList());

        comboBox.disableProperty().bind(timeAnimator.runningProperty());
        textField.disableProperty().bind(timeAnimator.runningProperty());

        HBox observationTimeHBox = new HBox(date, datePicker, time, textField, comboBox);
        observationTimeHBox.setStyle(OBSERVATION_TIME_STYLE);
        return observationTimeHBox;
    }

    /**
     * Constructs the information panel containing the graphical interface displaying to the field of view (left
     * position), the horizontal coordinates of the mouse pointer (right position) and the object name under the pointer
     * (center position)
     *
     * @param skyCanvasManager      the manager of the canvas where the sky is drawn in order to get the coordinates of
     *                              the pointer and the object under it
     * @param viewingParametersBean object containing the field of view property
     *
     * @return the information panel containing the field of view, the coordinates of the pointer and the object under
     * the pointer
     */
    private static Node informationPanel(ViewingParametersBean viewingParametersBean,
                                         SkyCanvasManager skyCanvasManager) {
        Text fovText = createText((fov) -> format("%s %.1f°", Translations.FOV_TEXT, fov),
                viewingParametersBean.fieldOfViewDegProperty(), Translations.FOV_TEXT);

        Text objectUnderMouseText = createText(object -> object == null ? "" : object.info(),
                skyCanvasManager.objectUnderMouseProperty());

        Text azAltText = createText(position -> String.format("%s %.2f°, %s %.2f°",
                Translations.AZIMUT,
                position.azDeg(),
                Translations.ALTITUDE,
                position.altDeg()),
                skyCanvasManager.mouseHorizontalPositionProperty(),
                Translations.AZIMUT, Translations.ALTITUDE);

        BorderPane informationPanel = new BorderPane(objectUnderMouseText, null, azAltText, null, fovText);
        informationPanel.setStyle("-fx-padding: 4; -fx-bac100kground-color: white;");
        return informationPanel;
    }

    /**
     * Constructs the sky area containing graphical interfaces corresponding to the canvas dimension
     *
     * @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @return the sky area containing the canvas of the sky canvas manager given
     */
    private static Node skyArea(SkyCanvasManager skyCanvasManager) {
        Canvas sky = skyCanvasManager.canvas();
        Pane skyArea = new Pane(sky);

        sky.widthProperty().bind(skyArea.widthProperty());
        sky.heightProperty().bind(skyArea.heightProperty());

        skyArea.cursorProperty().bind(skyCanvasManager.cursorProperty());

        return skyArea;
    }

    /**
     * Constructs the sub-panel of the control bar containing the graphical interface corresponding to the different
     * passages of time available and the buttons to pause, resume and reset time
     *
     * @param timeAnimator the time animator that modifies the sky
     *
     * @return the control bar containing the different passages of time available and the pause, resume and reset
     * buttons
     */
    private static Node timeRate(TimeAnimator timeAnimator, DateTimeBean dateTimeBean) {
        ComboBox<NamedTimeAccelerator> acceleratorComboBox = new ComboBox<>();
        ObservableList<NamedTimeAccelerator> accelerators =
                FXCollections.observableList(Arrays.asList(NamedTimeAccelerator.values()),
                                             (accelerator) -> new Observable[] {
                                                     accelerator.nameProperty()});  /* tells the combobox to update its
                                                                                       values when the name changes */
        acceleratorComboBox.setItems(accelerators);

        ObjectProperty<NamedTimeAccelerator> acceleratorProperty = acceleratorComboBox.valueProperty();
        acceleratorProperty.addListener((p, o, n) -> timeAnimator.setAccelerator(n.getAccelerator()));
        acceleratorProperty.set(INITIAL_ACCELERATOR);

        final ObjectProperty<LocalTime> localTimeSnapshot = new SimpleObjectProperty<>(dateTimeBean.getTime());
        final ObjectProperty<LocalDate> localDateSnapshot = new SimpleObjectProperty<>(dateTimeBean.getDate());

        Button resetLastPosition = createButton(RESET_LOGO, event -> {
            dateTimeBean.setTime(localTimeSnapshot.get());
            dateTimeBean.setDate(localDateSnapshot.get());
        });
        timeAnimator.runningProperty().addListener((p, o, n) -> resetLastPosition.setDisable(n));

        EventHandler<ActionEvent> playPauseAction = event -> {
            timeAnimator.setRunning(!timeAnimator.isRunning());
            if (timeAnimator.isRunning()) {  // register date and time
                localTimeSnapshot.set(dateTimeBean.getTime());
                localDateSnapshot.set(dateTimeBean.getDate());
            }
        };

        Button playPauseButton = createButton(PLAY_LOGO, playPauseAction);
        ObservableValue<String> buttonIcon =
                Bindings.createStringBinding(() -> timeAnimator.isRunning() ? PAUSE_LOGO : PLAY_LOGO,
                        timeAnimator.runningProperty());
        playPauseButton.textProperty().bind(buttonIcon);

        Button resetButton = createButton(CLOCK_LOGO, event -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));
        timeAnimator.runningProperty().addListener((p, o, n) -> resetButton.setDisable(n));

        HBox timeRateBox = new HBox(acceleratorComboBox, resetButton, playPauseButton, resetLastPosition);
        timeRateBox.setStyle(TIME_RATE_BOX_STYLE);
        return timeRateBox;
    }

    //------------------------------------------ BONUS MODIFICATION ----------------------------------------------------
    /**
     * Constructs a panel on the right side of the program with different menus: information and sound
     *
     * @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @return the right panel containing the menus information and sound
     */
    private Node rightPanel(SkyCanvasManager skyCanvasManager) {
        VBox rightPanel = new VBox(new Separator(), informationPanel(skyCanvasManager),
                                   new Separator(), volumePanel(), new Separator());
        rightPanel.setStyle(RIGHT_PANEL_STYLE);
        return rightPanel;
    }

    /**
     * Constructs a menu that provides additional information about the selected object and a button to deselect
     * that object
     *
     *  @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @return the menu containing the additional information
     */
    private Node informationPanel(SkyCanvasManager skyCanvasManager) {
        EventHandler<ActionEvent> stopFocusAction = event -> skyCanvasManager.resetSelectedObject();
        Button stopFocus = createButton("\uF057", stopFocusAction);

        VBox information = centeredText(Translations.INFO);

        Node selectedObjectInfo = selectedObjectInfo(skyCanvasManager);
        stopFocus.visibleProperty().bind(selectedObjectInfo.visibleProperty());
        VBox stopFocusBox = centeredVBox(stopFocus);

        VBox informationBoxWithoutStopBut = new VBox(information, selectedObjectInfo);
        informationBoxWithoutStopBut.setSpacing(10);

        VBox informationBox = new VBox(informationBoxWithoutStopBut, stopFocusBox);
        informationBox.setSpacing(0);

        return informationBox;
    }

    /**
     * Write all the additional information provided by the selected object (clicked object) provided by
     * SkyCanvasManager and allows it to be updated in real time
     *
     *  @param skyCanvasManager the sky canvas manager containing the canvas and which provides the selected object
     *
     * @return TextFlow containing all additional information
     */
    private static Node selectedObjectInfo(SkyCanvasManager skyCanvasManager) {
        ObservableValue<CelestialObject> selectedObjectProperty = skyCanvasManager.selectedObjectProperty();

        Text name = createText(object -> format("%s %s%n", Translations.NAME, object.name()),
                selectedObjectProperty, Translations.NAME);

        Text equText = createText(object -> {
            EquatorialCoordinates equ = object.equatorialPos();
            return format("dec%s %.2f°, ra%s %.2f°%n",
                    Translations.COLON, equ.decDeg(),
                    Translations.COLON, equ.raDeg());
        }, selectedObjectProperty, Translations.COLON);
        Text magnitude = createTextLine(FloatAttribute.Type.MAGNITUDE, selectedObjectProperty);
        Text angularSize = createTextLine(FloatAttribute.Type.ANGULAR_SIZE, selectedObjectProperty);
        Text phase = createTextLine(FloatAttribute.Type.PHASE, selectedObjectProperty);

        TextFlow textFlow = new TextFlow(name, equText, magnitude, angularSize, phase);
        textFlow.visibleProperty().bind(Bindings.createBooleanBinding(() -> selectedObjectProperty.getValue() != null,
                selectedObjectProperty));
        return textFlow;
    }

    /**
     * Creates a menu with two sliders to control the background music and sound effects such as screen capture
     *
     * @return the menu containing the two sliders
     */
    private Node volumePanel() {
        VBox volume = centeredText(Translations.VOLUME);

        VBox sliders = new VBox();
        sliders.setSpacing(20);

        if (AMBIANT_MUSIC != null) {
            VBox sliderWithMusic = volumeSlider(Translations.BACKGROUND_MUSIC, AMBIANT_MUSIC.volumeProperty());
            sliders.getChildren().add(sliderWithMusic);
        }
        if (SCREENSHOT_SOUND != null) {
            VBox sliderFXSounds = volumeSlider(Translations.FX_SOUNDS, FX_VOLUME);
            sliders.getChildren().add(sliderFXSounds);
        }

        VBox sliderWithText = new VBox(volume, sliders);
        sliderWithText.setSpacing(10);

        return sliderWithText;
    }

    //---------------------------------------------- Left panel --------------------------------------------------------

    /**
     * Constructs a panel on the left side of the program with different menus: general, navigation, drawing
     *
     * @param skyCanvasManager the sky canvas manager containing the canvas
     * @param stage the current stage that is being used
     *
     * @return the left panel containing the menus general, navigation and drawing
     */
    private Node leftPanel(SkyCanvasManager skyCanvasManager, Stage stage) {
            VBox leftPanel = new VBox(new Separator(), generalPanel(skyCanvasManager, stage),
                new Separator(), navigationPanel(skyCanvasManager),
                new Separator(), paintingPanel(skyCanvasManager),
                new Separator());
        leftPanel.setStyle(LEFT_PANEL_STYLE);
        return leftPanel;
    }

    /**
     * Constructs a menu with different buttons to take a screenshot, display the screenshot, full screen and
     * windows mode
     *
     *  @param skyCanvasManager the sky canvas manager containing the canvas
     *  @param stage the current stage that is being used
     *
     * @return the menu containing the different buttons
     */
    private Node generalPanel(SkyCanvasManager skyCanvasManager, Stage stage) {
        ComboBox<Language> languageComboBox = new ComboBox<>(FXCollections.observableList(List.of(Language.values())));
        languageComboBox.valueProperty().bindBidirectional(Main.CURRENT_LANGUAGE);

        Button fullscreen = createButton(FULLSCREEN_LOGO, event -> stage.setFullScreen(true));
        Button windowedMode = createButton(WINDOWED_LOGO, event -> stage.setFullScreen(false));

        HBox screenControl = new HBox(fullscreen, windowedMode);
        screenControl.setAlignment(Pos.TOP_CENTER);
        screenControl.setSpacing(6);

        Button screenshotButton = createButton(SCREENSHOT_LOGO, event -> doTakeScreenShot(skyCanvasManager));
        Button openPhotoButton = createButton(OPEN_PHOTO_LOGO, event -> openScreenshot(stage));

        HBox screenshot = new HBox(screenshotButton, openPhotoButton);
        screenshot.setAlignment(Pos.TOP_CENTER);
        screenshot.setSpacing(6);

        VBox control = centeredVBox(screenshot, screenControl, languageComboBox);
        control.setSpacing(5);

        VBox controlTextVBox = centeredText(Translations.GENERAL);

        VBox controlWithText = new VBox(controlTextVBox, control);
        controlWithText.setSpacing(10);

        return controlWithText;
    }

    /**
     * Constructs a menu with two sliders that are linked to the mouse sensitivity once the mouse movement is activated
     * (ALT to activate and CONTROL to deactivate) and the second slider linked to the FOV
     *
     * @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @return the menu containing the sliders
     */
    private Node navigationPanel(SkyCanvasManager skyCanvasManager) {
        VBox mouseSensitivitySlider = createSlider(Translations.MOUSE_SENSITIVITY, skyCanvasManager.maxSpeedProperty(),
                MouseNavigator.MAX_SPEED_INTERVAL);
        VBox fovSlider = createSlider(Translations.FOV_TEXT, skyCanvasManager.fovDegProperty(),
                SkyCanvasManager.FOV_INTERVAL_DEG);

        VBox sensibilitySliders = new VBox(mouseSensitivitySlider, fovSlider);
        sensibilitySliders.setSpacing(20);

        Text sensibilityText = createText(Translations.NAVIGATION);
        VBox sensibilityTextVBox = centeredVBox(sensibilityText);

        VBox sensibilityWithText = new VBox(sensibilityTextVBox, sensibilitySliders);
        sensibilityWithText.setSpacing(10);

        return sensibilityWithText;
    }

    /**
     * Constructs a menu composed by checkbox that allows to activate or deactivate the celestial objects that are
     * going to be visualized. It also allows you to change the color of the asterisms and the horizon throughout
     * color pickers.
     *
     *  @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @return the menu containing the different checkbox
     */
    private Node paintingPanel(SkyCanvasManager skyCanvasManager) {
        CheckBox stars = createCheckBox(Translations.STARS, skyCanvasManager.drawStarsProperty());
        CheckBox planets = createCheckBox(Translations.PLANETS, skyCanvasManager.drawPlanetsProperty());
        CheckBox sun = createCheckBox(Translations.SUN_NAME, skyCanvasManager.drawSunProperty());
        CheckBox moon = createCheckBox(Translations.MOON_NAME, skyCanvasManager.drawMoonProperty());
        CheckBox horizon = createCheckBox(Translations.HORIZON, skyCanvasManager.drawHorizonProperty());
        CheckBox asterism = createCheckBox(Translations.ASTERISMS, skyCanvasManager.drawAsterismsProperty());

        VBox showItems = new VBox(asterism, stars, horizon);
        showItems.setAlignment(Pos.TOP_LEFT);

        VBox showItems2 = new VBox(sun, moon, planets);
        showItems2.setAlignment(Pos.TOP_LEFT);

        HBox showItemsGeneral = new HBox(showItems, showItems2);
        showItemsGeneral.setSpacing(2);

        ColorPicker asterismsColor = createColorPicker(skyCanvasManager.asterismColorProperty());
        Text asterismsColorText = createText(Translations.ASTERISM_COLOR);

        ColorPicker horizonColor = createColorPicker(skyCanvasManager.horizonColorProperty());
        Text horizonColorText = createText(Translations.HORIZON_COLOR);

        VBox asterismHorizonLabel = new VBox(asterismsColorText, horizonColorText);
        asterismHorizonLabel.setSpacing(12);
        VBox asterismHorizonColor = new VBox(asterismsColor, horizonColor);

        HBox coloring = new HBox(asterismHorizonLabel, asterismHorizonColor);
        coloring.setAlignment(Pos.TOP_LEFT);
        coloring.setSpacing(5);

        VBox painting = new VBox(showItemsGeneral, coloring);
        painting.setSpacing(6);

        Text paintingText = createText(Translations.PAINTING);
        VBox paintingWithText = new VBox(paintingText);
        paintingWithText.setAlignment(Pos.TOP_CENTER);

        VBox paintingWithLabel = new VBox(paintingWithText, painting);
        paintingWithLabel.setSpacing(10);

        return paintingWithLabel;
    }

    // ---------------------------------------- Screenshot auxiliary ---------------------------------------------------

    /**
     * Takes a screenshot and saves it under the name "Screenshot1.png", at the same time plays 'the screenshot sound'
     *
     * @param skyCanvasManager the sky canvas manager containing the canvas
     *
     * @throws UncheckedIOException if an I/O error occurs
     */
    private static void doTakeScreenShot(SkyCanvasManager skyCanvasManager) {
        Canvas skyArea = skyCanvasManager.canvas();
        WritableImage fxImage =
                skyArea.snapshot(null, null);
        BufferedImage swingImage =
                SwingFXUtils.fromFXImage(fxImage, null);
        try {
            ImageIO.write(swingImage, "png", new File(SCREENSHOT_PATH));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        playScreenshotSound();
    }

    /**
     * Find the file saved under the name 'screenshot.png' and open it in a new stage
     *
     *  @param stage the current stage that is being used
     */
    private static void openScreenshot(Stage stage) {
        Image image = new Image(new File(SCREENSHOT_PATH).toURI().toString());
        ImageView imv = new ImageView(image);
        imv.setPreserveRatio(true);
        imv.setSmooth(true);
        imv.setCache(true);

        Stage dialog = new Stage();
        dialog.initModality(Modality.NONE);
        dialog.initOwner(stage);
        dialog.setMinWidth(400);
        dialog.setMinHeight(300);
        dialog.setWidth(800);
        dialog.setHeight(600);
        dialog.titleProperty().bind(Translations.SCREENSHOT);

        StackPane pane = new StackPane(imv);
        pane.setAlignment(Pos.CENTER);
        imv.fitWidthProperty().bind(pane.widthProperty());
        imv.fitHeightProperty().bind(pane.heightProperty());

        Scene scene = new Scene(pane);
        dialog.setScene(scene);
        dialog.show();
    }

    /**
     * Start the screen capture sound
     */
    private static void playScreenshotSound() {
        if (SCREENSHOT_SOUND != null) {
            SCREENSHOT_SOUND.play();
        }
    }

    //------------------------------------------------- START ----------------------------------------------------------

    /**
     * Initializes the sky with the default date, time, zone, observer position and field of view
     *
     * @param primaryStage It is the first stage created by JavaFX in which the program will be built
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        DateTimeBean dateTimeBean = new DateTimeBean();
        dateTimeBean.setZonedDateTime(ZonedDateTime.now());

        TimeAnimator timeAnimator = new TimeAnimator(dateTimeBean);

        ObserverLocationBean observerLocationBean = new ObserverLocationBean();
        observerLocationBean.setCoordinates(INITIAL_LOCATION);

        ViewingParametersBean viewingParametersBean = new ViewingParametersBean();
        viewingParametersBean.setCenter(INITIAL_OBSERVATION_CENTER);
        viewingParametersBean.setFieldOfViewDeg(INITIAL_FOV_DEG);

        SkyCanvasManager skyCanvasManager = skyCanvasManager(dateTimeBean, observerLocationBean, viewingParametersBean);

        Node skyArea = skyArea(skyCanvasManager);

        SplitPane splitPane = new SplitPane(leftPanel(skyCanvasManager, primaryStage), skyArea,
                rightPanel(skyCanvasManager));
        splitPane.setDividerPositions(0.3f, 0.6f, 0.9f);
        BorderPane mainPane = new BorderPane(
                splitPane,
                controlPanel(observerLocationBean, dateTimeBean, timeAnimator),
                null,
                informationPanel(viewingParametersBean, skyCanvasManager), null);

        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setTitle(WINDOW_TITLE);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setFullScreen(false);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        primaryStage.show();

        skyArea.requestFocus();

        if (AMBIANT_MUSIC != null) {
            AMBIANT_MUSIC.play();
            AMBIANT_MUSIC.setCycleCount(MediaPlayer.INDEFINITE);
        }
    }

    //------------------------------------------- Creation auxiliary ---------------------------------------------------

    /**
     * Auxiliary method for creating checkbox
     *
     * @param name The name associated with the checkbox on the right, which will be translated
     * @param selectedProperty The object that will be linked to the checkbox
     *
     * @return The checkbox linked to the given object
     */
    private static CheckBox createCheckBox(Translation name, BooleanProperty selectedProperty) {
        CheckBox checkBox = new CheckBox(name.get());
        checkBox.textProperty().bind(name);
        selectedProperty.bind(checkBox.selectedProperty());
        checkBox.setSelected(true);
        return checkBox;
    }

    /**
     * Auxiliary method for creating a text
     *
     * @param name The translations containing the various translations
     *
     * @return The text linked to the different translations
     */
    private static Text createText(Translation name) {
        Text text = new Text(name.get());
        text.textProperty().bind(name);
        return text;
    }

    /**
     * Creates a button with the given text that performs the event given when pressed
     *
     * @param text The text that will appear on the button
     * @param actionHandler The action that will occur when it is pressed
     *
     * @return The button with the given text which performs the event given when pressed
     */
    private static Button createButton(String text, EventHandler<ActionEvent> actionHandler) {
        Button button = new Button(text);
        button.setFont(BUTTON_FONT);
        button.setOnAction(actionHandler);
        return button;
    }

    /**
     * Creates a text containing the given attribute (magnitude, phase etc) which is translatable from the currently
     * selected object
     *
     * @param attributeType The type of the attribute (e.g. magnitude, phase etc)
     * @param objectProperty The selected object
     *
     * @return the text containing the given attribute in the selected language from the currently selected objet
     */
    private static Text createTextLine(FloatAttribute.Type attributeType,
                                       ObservableValue<CelestialObject> objectProperty) {
        Text text = new Text();

        ObjectBinding<Translation> translationBinding = Bindings.createObjectBinding(
                () -> {
                    CelestialObject object = objectProperty.getValue();
                    if (object == null) return null;
                    FloatAttribute attribute = object.getAttribute(attributeType);
                    if (attribute == null) return null;
                    return attribute.text();
                }, objectProperty);

        text.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    Translation translation = translationBinding.get();
                    if (translation == null) return "";
                    return translation.get() + "\n";
                }, translationBinding));
        return text;
    }

    /**
     * Creates a text with the given function and linked to the given property.
     *
     * @param stringSupplier the function that defines the text format
     * @param objectProperty the property of the object to which the text will be linked
     * @param otherDependencies the other observable values the text depends on
     * @param <T> the type of the object to be displayed
     *
     * @return a new instance of {@code Text} that automatically updates when its dependencies change and that displays
     * the value given by its string supplier.
     */
    private static <T> Text createText(Function<T, String> stringSupplier,
                                       ObservableValue<T> objectProperty,
                                       Observable... otherDependencies) {
        Observable[] dependencies = new Observable[otherDependencies.length + 1];
        System.arraycopy(otherDependencies, 0, dependencies, 0, otherDependencies.length);
        dependencies[dependencies.length - 1] = objectProperty;

        Text text = new Text();
        text.textProperty().bind(Bindings.createStringBinding(() -> {
            T object = objectProperty.getValue();
            if (object == null) return "";
            return stringSupplier.apply(object);
        }, dependencies));
        return text;
    }

    /**
     * Creates a TextField with the value property and the text formatter using the given validator
     *
     * @param property the property containing the value linked to the text field
     * @param valueValidator The validator to be used in the text formatter
     *
     * @return A text field linked to the given property and using the given validator for the text formatter
     */
    private static TextField locationTextField(DoubleProperty property, DoublePredicate valueValidator) {
        TextField textField = new TextField();
        textField.setStyle(OBSERVER_LOCATION_STYLE);

        TextFormatter<Number> formatter = locationTextFormatter(valueValidator);
        formatter.valueProperty().bindBidirectional(property);
        textField.setTextFormatter(formatter);

        return textField;
    }

    /**
     * Creates a label with the given translatable text and links its property to that text
     *
     * @param text The translatable text that will be used in the label
     *
     * @return the label with the given translatable text
     */
    private static Label createLabel(Translation text) {
        Label label = new Label(text.get());
        label.textProperty().bind(text);
        return label;
    }

    /**
     * Creates a box lined up in the middle
     *
     * @param nodes the nodes that make up the box
     *
     * @return the box composed of the given nodes aligned in the center
     */
    private static VBox centeredVBox(Node... nodes) {
        VBox box = new VBox(nodes);
        box.setAlignment(Pos.TOP_CENTER);
        return box;
    }

    /**
     * Creates a box composed solely of centrally-aligned text
     *
     * @param text The text from which the box will be composed and which will be translated
     *
     * @return a VBox with the translatable text in the middle
     */
    private static VBox centeredText(Translation text) {
        return centeredVBox(createText(text));
    }

    /**
     * Auxiliary method for creating a colorPicker
     *
     * @param colorProperty The color of the object to which the color picker is to be linked
     *
     * @return 30x30 square color picker linked to the color of that object
     */
    private static ColorPicker createColorPicker(ObjectProperty<Color> colorProperty) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(colorProperty.get());
        colorProperty.bind(colorPicker.valueProperty());
        colorPicker.setMaxWidth(30);
        colorPicker.setMaxHeight(30);
        return colorPicker;
    }

    /**
     * Auxiliary method for creating a slider
     *
     * @param text The title of the slider that will go right on top of it and which will be translated
     * @param valueProperty The values that the slider will take
     * @param valueInterval the interval of accepted values
     *
     * @return a VBox containing the title and slider
     */
    private static VBox createSlider(Translation text, DoubleProperty valueProperty, ClosedInterval valueInterval) {
        Text sliderText = createText(text);
        Slider slider = new Slider(valueInterval.low(), valueInterval.high(), valueProperty.get());
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.valueProperty().bindBidirectional(valueProperty);
        VBox sliderWithText = new VBox(sliderText, slider);
        sliderWithText.setSpacing(6);
        return sliderWithText;
    }

    /**
     * Creates a slider for the control of the sound that took the values between 0 and 1 and will be linked to the
     * volume of a particular sound
     *
     * @param name The title of the slider that will be translated
     * @param volume the volume property of the object to be linked to the slider
     *
     * @return the title slider name linked to the volume property given
     */
    private static VBox volumeSlider(Translation name, DoubleProperty volume) {
        Slider slider = new Slider(0, 1, volume.get());
        volume.bind(slider.valueProperty());
        return new VBox(createText(name), slider);
    }

    //------------------------------------- Location field auxiliary ---------------------------------------------------

    /**
     * Creates a text formatter for the location field
     *
     * @return text formatter for the location field
     */
    private static TextFormatter<Number> locationTextFormatter(DoublePredicate valueValidator) {
        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> valueFilter = (change -> {
            try {
                String newText = change.getControlNewText();
                double newValue = stringConverter.fromString(newText)
                        .doubleValue();
                return valueValidator.test(newValue)
                        ? change
                        : null;
            } catch (Exception e) {
                return null;
            }
        });
        return new TextFormatter<>(stringConverter, 0, valueFilter);
    }


    //------------------------------------- Observation position auxiliary ---------------------------------------------

    /**
     * Creates a text formatter for the time field
     *
     * @return text formatter for the time field
     */
    private static TextFormatter<LocalTime> timeTextFormatter() {
        DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        return new TextFormatter<>(stringConverter);
    }

    //------------------------------------ Observation time auxiliary --------------------------------------------------

    /**
     * Sorts the list of id zones in alphabetic order
     *
     * @return observable list of zone IDs sorted in alphabetic order
     */
    private static ObservableList<ZoneId> zoneIdList() {
        List<String> aList = new ArrayList<>(ZoneId.getAvailableZoneIds());
        Collections.sort(aList);
        return FXCollections.observableList(aList.stream()
                .map(ZoneId::of)
                .collect(Collectors.toUnmodifiableList()));
    }
}


