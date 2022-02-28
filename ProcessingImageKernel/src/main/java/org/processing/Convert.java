package org.processing;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;

// PRETVORBA SLIKE ZAPOREDNO ALI VZPOREDNO

public class Convert{
    private String imgName;
    private String kernelSelect;
    private String filenameRead;
    private String filenameWrite;
    public static BufferedImage input;
    public static BufferedImage output = null;
    public static AtomicReferenceArray ara;


    //kernel samples
    private int[][] original = {{0, 0 , 0},
                                {0, 1, 0},
                                {0, 0 , 0}};
    private int[][] sharpen =   {{0, -1, 0},
                                {-1, 5, -1},
                                {0, -1, 0}};
    private int[][] blur = {{1, 2, 1},
                            {2, 4, 2},
                            {1, 2, 1}};
    private int[][] edgedetect = {{-1, -1, -1},
                                  {-1, 8, -1},
                                  {-1, -1, -1}};

    public Convert(String imgName, String kernelSelect){
        this.imgName=imgName;
        this.kernelSelect=kernelSelect;
        this.filenameRead="slike/"+imgName;
        this.filenameWrite="processed/"+imgName;
    }

    public void ImageConvert() throws Exception{
        int [][] kernel;
        int order = 3; // red matrike
        float mult_factor = 1.0f; //faktor
        System.out.println(imgName);
        //izbira matrike B
        switch (kernelSelect){
            case "EdgeDetect":
                kernel=edgedetect;
                break;
            case "Blur":
                mult_factor=0.0625f;
                kernel=blur;
                break;
            case "Sharpen":
                kernel=sharpen;
                break;
            default:
                kernel=original;
        }

       // System.out.println(filenameRead);
        input = ImageIO.read(new File(filenameRead));

        int WIDTH = input.getWidth();
        int HEIGHT = input.getHeight();

        // This part we want to run quicker
        if(!Main.paralelOn){
            double t0 = System.currentTimeMillis();
            output=sequentially(WIDTH,HEIGHT,kernel, mult_factor ,order);
            double t1 = System.currentTimeMillis()-t0;
            System.out.println("new pic processed in SEQ: "+t1+"ms"); // end
        }
        else {
            double t2 = System.currentTimeMillis();
            output = new BufferedImage(WIDTH, HEIGHT, input.getType());
            ara = new AtomicReferenceArray(WIDTH*HEIGHT);

            int numOfCores = Runtime.getRuntime().availableProcessors()-2; //Doloci stevilo processov
            System.out.println("Stevilo processov: "+numOfCores);

            int vrstice = (HEIGHT/numOfCores);
            int lastVrstica=(HEIGHT/numOfCores);
            if(HEIGHT%numOfCores!=0){
                vrstice = (HEIGHT/numOfCores);
                lastVrstica = vrstice+(HEIGHT%numOfCores);
            }

            ExecutorService executorService = Executors.newFixedThreadPool(numOfCores);
            for (int i = 0; i < numOfCores-1  ; i++) {
                 executorService.execute(new Worker("Thread: "+i,i*vrstice,i*vrstice+vrstice,kernel, WIDTH,HEIGHT,mult_factor));
            }
            executorService.execute(new Worker("Thread: "+numOfCores,(numOfCores-1)*vrstice,(numOfCores-1)*vrstice+lastVrstica,kernel, WIDTH,HEIGHT,mult_factor));
            executorService.shutdown();
            try{
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }catch (InterruptedException ie){ ie.printStackTrace();}

            for (int i = 0; i < WIDTH*HEIGHT; i++) {
                output.setRGB(i%WIDTH, i/(WIDTH)  ,((Color) ara.get(i)).getRGB());
            }
            double t3 = System.currentTimeMillis()-t2;
            System.out.println("New pic processed in PARALEL: "+t3+"ms"); // end
        }

        //Write image
        ImageIO.write(output,getExtension(imgName), new File(filenameWrite));
    }



    public BufferedImage sequentially(int WIDTH, int HEIGHT, int [][] kernel, float mult_factor, int order){
        output = new BufferedImage(WIDTH, HEIGHT, input.getType());
        //Image processing
        for(int x=0;x<WIDTH;x++)
        {
            for(int y=0;y<HEIGHT;y++)
            {
                float red=0f, green=0f, blue=0f;

                for(int i=0;i<order;i++)
                {
                    for(int j=0;j<order;j++)
                    {
                        // Calculating X and Y coordinates of the pixel to be multiplied with current kernel element
                        // In case of edges of image the '% WIDTH' wraps the image and the pixel from opposite edge is used
                        int imageX = (x - order / 2 + i + WIDTH) % WIDTH;
                        int imageY = (y - order / 2 + j + HEIGHT) % HEIGHT;

                        int RGB = input.getRGB(imageX,imageY);
                        int R = (RGB >> 16) & 0xff; // vrednost za rdeco
                        int G = (RGB >> 8) & 0xff;	// vrednost za zeleno
                        int B = (RGB) & 0xff;		// vrednost za modro

                        // RGB is multiplied with current kernel element and added on to the variables red, blue and green
                        red += (R*kernel[i][j]);
                        green += (G*kernel[i][j]);
                        blue += (B*kernel[i][j]);
                    }
                }
                int outR, outG, outB;

                outR = Math.min(Math.max((int)(red*mult_factor),0),255);
                outG = Math.min(Math.max((int)(green*mult_factor),0),255);
                outB = Math.min(Math.max((int)(blue*mult_factor),0),255);

                // Pixel is written to output image
                output.setRGB(x,y,new Color(outR,outG,outB).getRGB());
            }
        }
        return output;
    }

    public BufferedImage getImage() throws Exception{
        BufferedImage bi = output;
        return bi;
    }
    public String getExtension(String name) {
        String extension = "";

        int i = name.lastIndexOf('.');
        if (i > 0) {
            extension = name.substring(i+1);
        }
        return extension;
    }

}

