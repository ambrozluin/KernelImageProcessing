package org.processing;


import java.awt.*;

//VZPOREDNO
public class Worker implements Runnable {

    private final int[][] kernel;
    private String name;
    private final int zacVrstica;
    private final int konVrstica;
    private final int width;
    private final int height;
    private float mult_factor;

    public Worker(String name, int zacVrstica, int konVrstica, int[][] kernel, int width, int height, float mult_factor) {
        this.name = name;
        this.zacVrstica = zacVrstica;
        this.konVrstica = konVrstica;
        this.kernel=kernel;
        this.width = width;
        this.height= height;
        this.mult_factor=mult_factor;
    }

    @Override
    public void run() {
        for(int x=0;x<width;x++)
        {
            for(int y=zacVrstica;y<konVrstica;y++)
            {
                float red=0f, green=0f, blue=0f;
                for(int i=0;i<kernel.length;i++)
                {
                    for(int j=0;j<kernel.length;j++)
                    {
                        // Calculating X and Y coordinates of the pixel to be multiplied with current kernel element
                        // In case of edges of image the '% WIDTH' wraps the image and the pixel from opposite edge is used
                        int imageX = (x - 3 / 2 + i + width) % width;
                        int imageY = (y - 3 / 2 + j + height) % height;

                        int RGB = Convert.input.getRGB(imageX,imageY);
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
                Convert.ara.set(x + (y*width), new Color(outR,outG,outB,1));
            }
        }
         System.out.println("Done "+name);
    }

}
