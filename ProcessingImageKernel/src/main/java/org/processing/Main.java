package org.processing;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Project: Kernel Image Processing
// Author: Ambrož Luin
// Date: 12.01.2021 -> Avgust 2021

public class Main extends Application {

    boolean GUI = true;
    private String imageName = "default";
    private String kernelSelect = "Original";
    public static boolean paralelOn = true;
    private boolean readFromFile = false;
    private  int width=1280;
    private  int height=720;


    @Override
    public void start(Stage primaryStage) throws Exception{
        if(GUI){
            Group root = new Group();
            primaryStage.setTitle("Kernel Image Processing");
            primaryStage.setScene(new Scene(root));
            Show sim = new Show(width ,height);

            HBox hbox = new HBox();

            hbox.setPadding(new Insets(20, 12, 20, 12));
            hbox.setSpacing(10);
            hbox.setStyle("-fx-background-color: #006299;");

            Text imgtext = new Text();
            imgtext.setFont(new Font(18));
            imgtext.setFill(Color.WHITESMOKE);
            imgtext.setText("Or select sample image to process:");

            final FileChooser fileChooser = new FileChooser();

            final Button openButton = new Button("Open a Picture...");
            openButton.setOnAction(
                    new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(final ActionEvent e) {
                            File file = fileChooser.showOpenDialog(primaryStage);
                            if (file != null) {
                                try {
                                    fileSave(file);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                imageName=file.getName();
                                readFromFile=true;
                            }
                            else readFromFile=false;
                        }
                    });

            ChoiceBox cbimg = new ChoiceBox(FXCollections.observableArrayList(
                    "100x100.jpg","200x200.jpg","320x240.jpg","480x360.jpg","1280x720.jpg", "1920x1080.jpg", "2560x1440.jpg", "4096x2160.jpg","7680x4320.jpg","8000x5000.jpg")
            );
            cbimg.setValue("1280x720.jpg");

            cbimg.setPrefSize(120, 20);


            Text cbtext = new Text();
            cbtext.setFont(new Font(18));
            cbtext.setFill(Color.WHITESMOKE);
            cbtext.setText("Select kernel:");

            ChoiceBox cbkernel = new ChoiceBox(FXCollections.observableArrayList(
                    "Original", "Sharpen", "Blur", "EdgeDetect")
            );
            cbkernel.setValue("Original");
            cbkernel.setPrefSize(100, 20);

            ChoiceBox mode = new ChoiceBox(FXCollections.observableArrayList(
                    "Zaporedno", "Vzporedno")
            );
            mode.setValue("Zaporedno");
            mode.setPrefSize(100, 20);

            VBox center = new VBox();
            //center.setStyle("-fx-background-color: #00aaff;");

            Button start = new Button("Start processing");

            hbox.getChildren().addAll(openButton,imgtext,cbimg,cbtext,cbkernel,mode, start);
            center.getChildren().add(hbox);
            center.getChildren().add(sim);

            root.getChildren().add(center);

            start.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    if(!readFromFile) {
                        imageName = (String) cbimg.getValue();
                    }
                    kernelSelect= (String) cbkernel.getValue();
                    String modeSelect = (String) mode.getValue();
                    if(modeSelect=="Zaporedno"){paralelOn = false;}
                    else{ paralelOn=true;}
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            sim.start(imageName,kernelSelect);
                            readFromFile=false;
                        }
                    });
                }
            });

            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                sim.redraw(primaryStage.getWidth(),primaryStage.getHeight());
            });

            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                sim.redraw(primaryStage.getWidth(),primaryStage.getHeight());
            });

            primaryStage.show();
            sim.drawOnCenter(width,height);
        }

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    

    public static void main(String[] args) {
        launch(args);
    }

    public void fileSave(File f) throws IOException {
        String extension = "";
        String fileName=f.getName();
        int i = fileName.lastIndexOf('.');
        if (i >= 0) {
            extension = fileName.substring(i+1);
        }
        BufferedImage b;
        b= ImageIO.read(f);
        ImageIO.write(b,extension,new File("slike/"+f.getName()));
    }

}