package app_3d;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class Main extends Application {
    private static final int WIDTH = 1400;
    private static final int HEIGHT = 800;

    private double anchorX, anchorY;
    private double anchorAngleX;
    private double anchorAngleY;
    private final DoubleProperty angleX = new SimpleDoubleProperty(0);
    private final DoubleProperty angleY = new SimpleDoubleProperty(0);

    private void initMouseControl(SmartGroup smartGroup, Scene scene)
    {
        Rotate xRotate;
        Rotate yRotate;

        smartGroup.getTransforms().addAll(
                xRotate = new Rotate(0,Rotate.X_AXIS),
                yRotate = new Rotate(0,Rotate.Y_AXIS)
        );

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);


        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        scene.setOnMouseDragged(event -> {
            angleX.set(anchorAngleX - (anchorY-event.getSceneY()));
            angleY.set(anchorAngleY + anchorX-event.getSceneX());
        });

        //zoom za pomocą scrolla
        scene.setOnScroll(event -> {
            if (event.getDeltaY() >0)
                smartGroup.translateZProperty().set(smartGroup.getTranslateZ() + 100);

            if (event.getDeltaY() <0)
               smartGroup.translateZProperty().set(smartGroup.getTranslateZ() - 100);
        });
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        Camera camera = new PerspectiveCamera(true);
        camera.setNearClip(1);
        camera.setFarClip(1000);
        camera.translateZProperty().set(-1000);
        
        SmartGroup world = new SmartGroup();
        world.getChildren().add(prepareEarth());
        
        Scene scene = new Scene(world, WIDTH, HEIGHT,true);
        scene.setFill(Color.SILVER);
        scene.setCamera(camera);



        primaryStage.setTitle("Earth Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private Node prepareEarth() {
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseMap(new Image(new File("earth-texture.jpg").toURI().toString()));
        Sphere sphere = new Sphere(150);
        sphere.setMaterial(material);

        return sphere;
    }

    private Node prepareSecondBox() {
        PhongMaterial material = new PhongMaterial();



        Image image1 = new Image(new File("wood.jpg").toURI().toString());
        Image image2 = new Image(new File("texture3.jpg").toURI().toString());
        material.setDiffuseMap(image1);

        Box box = new Box(20,100,100);
        box.setMaterial(material);
        return box;
    }

    private final PointLight pointLight = new PointLight();

    private Node[] prepareLightSource() {
//        AmbientLight ambientLight = new AmbientLight();
//        ambientLight.setColor(Color.AQUA);
//        return ambientLight;


        pointLight.setColor(Color.RED);
        pointLight.getTransforms().add(new Translate(0,-50,100));
        pointLight.setRotationAxis(Rotate.X_AXIS);

        Sphere sphere = new Sphere(2);
        sphere.getTransforms().setAll(pointLight.getTransforms());
        sphere.rotateProperty().bind(pointLight.rotateProperty());
        sphere.rotationAxisProperty().bind(pointLight.rotationAxisProperty());

        return new Node[]{pointLight,sphere};
    }
}


class SmartGroup extends Group{
    Rotate r;
    Transform t= new Rotate();

    void rotateByX(int ang)
    {
        r = new Rotate(ang,Rotate.X_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(t);
    }

    void rotateByY(int ang)
    {
        r = new Rotate(ang,Rotate.Y_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().addAll(t);
    }

}