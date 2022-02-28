package org.processing;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.processing.Convert;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//SKRBI ZA PRIKAZ SLIKE

public class Show extends Canvas {

    private GraphicsContext gc;
    Image imglefttop=null;
    Image imgrighttop=null;
    Image imgleftbottom=null;
    Image imgrightbottom=null;


    public Show(int width, int height) {
        super(width, height);
        this.gc = this.getGraphicsContext2D();
    }


    public void start(String imgName, String kernelSelect) {

        Convert converted = new Convert(imgName, kernelSelect);
        BufferedImage bufferedImage;
        BufferedImage [] splitedImage;
        boolean flag=false;

        System.out.println("loading");
        gc.setFill(Color.rgb(10,170,255));
        gc.fillRect(0,0,getWidth(),getHeight());
        gc.setFill(Color.rgb(255,255,255));
        gc.setFont(Font.font(18));
        gc.fillText("Processing image please wait..",2*getWidth()/5,getHeight()/2,getWidth()/2);

        try {
            converted.ImageConvert(); // convert image A(original) * B(kernel) = C (image to show)
            bufferedImage = converted.getImage();

            splitedImage =splitImage(bufferedImage);
            imglefttop =  SwingFXUtils.toFXImage(splitedImage[0], null);
            imgrighttop = SwingFXUtils.toFXImage(splitedImage[1], null);
            imgleftbottom =  SwingFXUtils.toFXImage(splitedImage[2], null);
            imgrightbottom = SwingFXUtils.toFXImage(splitedImage[3], null);

            flag=true;

        } catch (Exception e) {
            System.out.println("Image failed to load.");
            e.printStackTrace();
        }

        if (flag){
            gc.drawImage(imglefttop,0, 0, getWidth()/2, getHeight()/2);;
            gc.drawImage(imgrighttop,getWidth()/2,0, getWidth()/2,getHeight()/2);
            gc.drawImage(imgleftbottom,0,getHeight()/2, getWidth()/2,getHeight()/2);
            gc.drawImage(imgrightbottom,getWidth()/2,getHeight()/2, getWidth()/2,getHeight()/2);
            System.out.println("Done");

        }else{
            gc.setFill(Color.rgb(10,170,255));
            gc.fillRect(0,0,getWidth(),getHeight());
            gc.setFill(Color.rgb(255,255,255));
            gc.setFont(Font.font(18));
            gc.fillText("Unable to show image..",2*getWidth()/5,getHeight()/2,getWidth()/2);
        }

    }

    public void drawOnCenter(double width, double height){
        gc.setFill(Color.rgb(10,170,255));
        gc.fillRect(0,0,getWidth(),getHeight());
        gc.setFill(Color.rgb(255,255,255));
        gc.setFont(Font.font(18));
        gc.fillText("Press start to process selected image",2*getWidth()/5,2*getHeight()/5,getWidth()/2);
    }


    public BufferedImage[] splitImage( BufferedImage image ) throws IOException
    {
        int height = image.getHeight();
        int width = image.getWidth();
        int splitcount=4;
        BufferedImage[] splitBufferedImage= new BufferedImage[splitcount];

        // Split image ------------------------------------------
        splitBufferedImage[0] = image.getSubimage(0, 0, width/2, height/2);
        splitBufferedImage[1] = image.getSubimage(width/2, 0, width/2, height/2);
        splitBufferedImage[2] = image.getSubimage(0, height/2, width/2, height/2);
        splitBufferedImage[3] = image.getSubimage(width/2, height/2, width/2, height/2);

        return splitBufferedImage;
    }

    public void redraw(double width, double height){
        this.setWidth(width);
        this.setHeight(height);
        drawOnCenter(width,height);
        gc.drawImage(imglefttop,0, 0, width/2, height/2);;
        gc.drawImage(imgrighttop,width/2,0, width/2,height/2);
        gc.drawImage(imgleftbottom,0,height/2, width/2,height/2);
        gc.drawImage(imgrightbottom,width/2,height/2, width/2,height/2);
    }


    public void resize(double width, double height){
        this.setWidth(width);
        this.setHeight(height);
    }

}





