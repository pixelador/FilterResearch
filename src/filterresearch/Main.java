/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filterresearch;

import java.awt.image.BufferedImage;

/**
 *
 * @author Erik
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedImage inputImage = ImageIo.readImage("lena.jpg");
        BufferedImage grayImage = ImageIo.toGray(inputImage);
        BufferedImage outImage;
        
        byte[][] input = ImageIo.getGrayByteImageArray2DFromBufferedImage(grayImage);
        byte[][] output = new byte[input.length][input[0].length];
        
        BilateralFilter biFilter = new BilateralFilter(15.0, 8.0, input);
        
        for (int i = 0; i < output.length; i++) {
            for (int j = 0; j < output[0].length; j++) {
                output[i][j] = (byte)biFilter.apply(i, j);
            }
        }
        
        outImage = ImageIo.setGrayByteImageArray2DToBufferedImage(output);
        ImageIo.writeImage(outImage, "jpg", "lena_bilat_15_8.jpg");
        
    }
    
}
