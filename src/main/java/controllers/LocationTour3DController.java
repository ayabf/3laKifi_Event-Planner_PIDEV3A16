package controllers;

import Models.Location;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import java.io.ByteArrayInputStream;

public class LocationTour3DController {
    @FXML private SubScene tourScene;
    @FXML private VBox controls;
    @FXML private Button resetButton;
    @FXML private Slider zoomSlider;
    @FXML private Label locationName;
    
    private final Group root3D = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Group cameraGroup = new Group();
    private double mouseOldX, mouseOldY;
    private double mousePosX, mousePosY;
    private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private AnimationTimer animationTimer;
    private Location currentLocation;

    @FXML
    public void initialize() {
        setupCamera();
        setupScene();
        setupControls();
        setupMouseControl();
        startAnimation();
    }

    private void setupCamera() {
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setTranslateZ(-1000);
        camera.setFieldOfView(45);
        camera.setRotate(180);

        cameraGroup.getChildren().add(camera);
        cameraGroup.getTransforms().addAll(rotateX, rotateY);
        root3D.getChildren().add(cameraGroup);
    }

    private void setupScene() {
        tourScene.setRoot(root3D);
        tourScene.setCamera(camera);
        tourScene.setFill(Color.WHITE);

        AmbientLight ambientLight = new AmbientLight(Color.rgb(255, 255, 255, 1.0));
        root3D.getChildren().add(ambientLight);

        addPointLight(0, 0, 500, Color.rgb(255, 255, 255, 1.0));

        addPointLight(0, 0, -500, Color.rgb(255, 255, 255, 1.0));
        addPointLight(-200, 0, -500, Color.rgb(255, 255, 255, 0.8));
        addPointLight(200, 0, -500, Color.rgb(255, 255, 255, 0.8));

        addPointLight(-500, 0, 0, Color.rgb(255, 255, 255, 0.9));
        addPointLight(500, 0, 0, Color.rgb(255, 255, 255, 0.9));

        addPointLight(0, -200, 0, Color.rgb(255, 255, 255, 1.0));
        addPointLight(-200, -200, 0, Color.rgb(255, 255, 255, 0.9));
        addPointLight(200, -200, 0, Color.rgb(255, 255, 255, 0.9));
    }

    private void addPointLight(double x, double y, double z, Color color) {
        PointLight light = new PointLight(color);
        light.setTranslateX(x);
        light.setTranslateY(y);
        light.setTranslateZ(z);
        root3D.getChildren().add(light);
    }

    private void setupControls() {
        resetButton.setOnAction(e -> resetView());
        
        zoomSlider.setMin(0.5);
        zoomSlider.setMax(2.0);
        zoomSlider.setValue(1.0);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            camera.setTranslateZ(-1000 * newVal.doubleValue());
        });
    }

    private void setupMouseControl() {
        tourScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        tourScene.setOnMouseDragged(event -> {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY) / 2);
            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX) / 2);
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });
    }

    public void setLocation(Location location) {
        currentLocation = location;
        locationName.setText(location.getName());
        createLocationModel();
    }

    private void createLocationModel() {
        root3D.getChildren().clear();
        root3D.getChildren().addAll(cameraGroup);

        Box floor = createFloor();

        createWalls();

        addFurniture();

        addDecorations();

        root3D.getChildren().add(floor);
    }

    private Box createFloor() {
        Box floor = new Box(800, 20, 800);
        floor.setTranslateY(150);
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(Color.valueOf("#f4f4f4"));
        floorMaterial.setSpecularColor(Color.WHITE);
        floorMaterial.setSpecularPower(32.0);
        floor.setMaterial(floorMaterial);
        return floor;
    }

    private void createWalls() {
        double wallHeight = 400;
        double roomWidth = 800;
        double wallThickness = 20;

        Box wallBack = createWallWithWindows(roomWidth, wallHeight, wallThickness, 0, -50, -roomWidth/2);

        Box wallLeft = createWallWithDoor(wallThickness, wallHeight, roomWidth, -roomWidth/2, -50, 0);

        Box wallRight = createWallWithWindows(wallThickness, wallHeight, roomWidth, roomWidth/2, -50, 0);

        PhongMaterial wallMaterial = new PhongMaterial();
        if (currentLocation.getImageData() != null) {
            Image locationImage = new Image(new ByteArrayInputStream(currentLocation.getImageData()));
            wallMaterial.setDiffuseMap(locationImage);
            wallMaterial.setDiffuseColor(Color.rgb(255, 255, 255, 1.0));
        } else {
            wallMaterial.setDiffuseColor(Color.rgb(255, 255, 255, 1.0));
        }
        wallMaterial.setSpecularColor(Color.rgb(255, 255, 255, 1.0));
        wallMaterial.setSpecularPower(10.0);

        wallBack.setMaterial(wallMaterial);
        wallLeft.setMaterial(wallMaterial);
        wallRight.setMaterial(wallMaterial);

        root3D.getChildren().addAll(wallBack, wallLeft, wallRight);
    }

    private Box createWallWithWindows(double width, double height, double depth, 
                                    double translateX, double translateY, double translateZ) {
        Box wall = new Box(width, height, depth);
        wall.setTranslateX(translateX);
        wall.setTranslateY(translateY);
        wall.setTranslateZ(translateZ);

        int windowCount = width > height ? 3 : 2;
        double spacing = width / (windowCount + 1);
        
        for (int i = 1; i <= windowCount; i++) {
            double xOffset = width > height ? (i * spacing - width/2) : 0;
            double zOffset = width > height ? 0 : (i * spacing - width/2);
            
            Box window = new Box(80, 120, depth + 10);
            window.setTranslateX(translateX + xOffset);
            window.setTranslateY(translateY - 50);
            window.setTranslateZ(translateZ + zOffset);

            PhongMaterial windowMaterial = new PhongMaterial();
            windowMaterial.setDiffuseColor(Color.rgb(255, 255, 255, 1.0));
            windowMaterial.setSpecularColor(Color.WHITE);
            windowMaterial.setSpecularPower(10.0);
            window.setMaterial(windowMaterial);

            PointLight windowLight = new PointLight(Color.rgb(255, 255, 255, 1.0));
            windowLight.setTranslateX(translateX + xOffset);
            windowLight.setTranslateY(translateY - 50);
            windowLight.setTranslateZ(translateZ + zOffset);
            
            root3D.getChildren().addAll(window, windowLight);
        }

        return wall;
    }

    private Box createWallWithDoor(double width, double height, double depth,
                                 double translateX, double translateY, double translateZ) {
        Box wall = new Box(width, height, depth);
        wall.setTranslateX(translateX);
        wall.setTranslateY(translateY);
        wall.setTranslateZ(translateZ);

        Box door = new Box(width + 10, 250, 100);
        door.setTranslateX(translateX);
        door.setTranslateY(translateY + 75);
        door.setTranslateZ(translateZ + 100);
        
        PhongMaterial doorMaterial = new PhongMaterial();
        doorMaterial.setDiffuseColor(Color.valueOf("#8B4513"));
        door.setMaterial(doorMaterial);
        
        root3D.getChildren().add(door);

        return wall;
    }

    private void addFurniture() {
        int tableCount = currentLocation.getTableSetCount();

        double roomSize = 800;
        double spacing = roomSize / (tableCount + 1);
        
        for (int i = 0; i < tableCount; i++) {
            double x = (i * spacing) - (roomSize/3);
            double z = (i % 2 == 0) ? -200 : 200;
            addTableSet(x, 100, z);
        }
    }

    private void addTableSet(double x, double y, double z) {
        Box tableTop = new Box(120, 10, 120);
        tableTop.setTranslateX(x);
        tableTop.setTranslateY(y);
        tableTop.setTranslateZ(z);
        
        PhongMaterial tableMaterial = new PhongMaterial();
        tableMaterial.setDiffuseColor(Color.valueOf("#8B4513"));
        tableMaterial.setSpecularColor(Color.WHITE);
        tableMaterial.setSpecularPower(32.0);
        tableTop.setMaterial(tableMaterial);

        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                Cylinder leg = new Cylinder(5, 80);
                leg.setTranslateX(x + (i * 50));
                leg.setTranslateY(y + 40);
                leg.setTranslateZ(z + (j * 50));
                leg.setMaterial(tableMaterial);
                root3D.getChildren().add(leg);
            }
        }
        
        root3D.getChildren().add(tableTop);

        addChairsAroundTable(x, y, z);
    }

    private void addChairsAroundTable(double tableX, double tableY, double tableZ) {
        double chairDistance = 80;
        PhongMaterial chairMaterial = new PhongMaterial();
        chairMaterial.setDiffuseColor(Color.valueOf("#A0522D"));
        chairMaterial.setSpecularColor(Color.WHITE);
        chairMaterial.setSpecularPower(32.0);
        
        for (int angle = 0; angle < 360; angle += 90) {
            double x = tableX + chairDistance * Math.cos(Math.toRadians(angle));
            double z = tableZ + chairDistance * Math.sin(Math.toRadians(angle));

            Box seat = new Box(40, 10, 40);
            seat.setTranslateX(x);
            seat.setTranslateY(tableY + 20);
            seat.setTranslateZ(z);
            seat.setMaterial(chairMaterial);

            Box back = new Box(40, 60, 10);
            back.setTranslateX(x);
            back.setTranslateY(tableY - 10);
            back.setTranslateZ(z + 15);
            back.setRotationAxis(Rotate.Y_AXIS);
            back.setRotate(angle);
            back.setMaterial(chairMaterial);
            
            root3D.getChildren().addAll(seat, back);
        }
    }

    private void addDecorations() {
        if (currentLocation.isIncludeCornerPlants()) {
            addCornerPlants();
        }
        
        if (currentLocation.isIncludeCeilingLights()) {
            addCeilingLights();
        }
    }

    private void addCornerPlants() {
        double roomSize = 800;
        double cornerOffset = roomSize/2 - 50;

        addDecorationPlant(-cornerOffset, 50, -cornerOffset);
        addDecorationPlant(cornerOffset, 50, -cornerOffset);
        addDecorationPlant(-cornerOffset, 50, cornerOffset);
        addDecorationPlant(cornerOffset, 50, cornerOffset);
    }

    private void addCeilingLights() {
        Color lightColor;
        try {
            String colorStr = currentLocation.getLightColor();
            if (colorStr == null || colorStr.isEmpty() || colorStr.equals("#FFFF00")) {
                lightColor = Color.WHITE;
            } else {
                lightColor = Color.web(colorStr);
            }
        } catch (Exception e) {
            lightColor = Color.WHITE;
        }

        lightColor = lightColor.deriveColor(1, 1, 1, 0.8);
        
        for (int i = -2; i <= 2; i++) {
            Sphere fixture = new Sphere(15);
            fixture.setTranslateX(i * 200);
            fixture.setTranslateY(-200);
            fixture.setTranslateZ(0);
            
            PhongMaterial fixtureMaterial = new PhongMaterial();
            fixtureMaterial.setDiffuseColor(lightColor);
            fixtureMaterial.setSpecularColor(Color.WHITE);
            fixtureMaterial.setSpecularPower(64.0);
            fixture.setMaterial(fixtureMaterial);

            PointLight pointLight = new PointLight(lightColor.deriveColor(1, 1, 1, 0.6));
            pointLight.setTranslateX(i * 200);
            pointLight.setTranslateY(-180);
            pointLight.setTranslateZ(0);
            
            root3D.getChildren().addAll(fixture, pointLight);
        }
    }

    private void addDecorationPlant(double x, double y, double z) {
        Cylinder pot = new Cylinder(20, 40);
        pot.setTranslateX(x);
        pot.setTranslateY(130);
        pot.setTranslateZ(z);
        
        PhongMaterial potMaterial = new PhongMaterial();
        potMaterial.setDiffuseColor(Color.valueOf("#8B4513"));
        potMaterial.setSpecularColor(Color.WHITE);
        potMaterial.setSpecularPower(32.0);
        pot.setMaterial(potMaterial);

        Sphere foliage = new Sphere(30);
        foliage.setTranslateX(x);
        foliage.setTranslateY(70);
        foliage.setTranslateZ(z);
        
        PhongMaterial foliageMaterial = new PhongMaterial();
        foliageMaterial.setDiffuseColor(Color.valueOf("#228B22"));
        foliageMaterial.setSpecularColor(Color.WHITE);
        foliageMaterial.setSpecularPower(16.0);
        foliage.setMaterial(foliageMaterial);
        
        root3D.getChildren().addAll(pot, foliage);
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                rotateY.setAngle(rotateY.getAngle() + 0.1);
            }
        };
        animationTimer.start();
    }

    private void resetView() {
        RotateTransition rotateTransitionX = new RotateTransition(Duration.seconds(1), cameraGroup);
        rotateTransitionX.setToAngle(20);
        rotateTransitionX.setAxis(Rotate.X_AXIS);
        
        RotateTransition rotateTransitionY = new RotateTransition(Duration.seconds(1), cameraGroup);
        rotateTransitionY.setToAngle(0);
        rotateTransitionY.setAxis(Rotate.Y_AXIS);
        
        rotateTransitionX.play();
        rotateTransitionY.play();
        
        zoomSlider.setValue(1.0);
    }

    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
} 