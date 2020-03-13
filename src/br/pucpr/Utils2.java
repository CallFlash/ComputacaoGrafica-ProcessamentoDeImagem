package br.pucpr;

import java.awt.image.BufferedImage;

import static br.pucpr.Util.*;
import static br.pucpr.Vector3.*;

public class Utils2 {

    public static BufferedImage grayscale(BufferedImage img)
    {
      return filter(img, p ->
      {
          float colorPixel = p.getR()*0.299f + p.getG()*0.587f + p.getB()*0.114f;
          Vector3 pGrayscale = new Vector3(colorPixel, colorPixel, colorPixel);
          return pGrayscale;
      });
    }

    public static BufferedImage bordersSobel(BufferedImage img, int timesErode, boolean[][] kernelErode, int timesDilate, boolean[][] kernelDilate, int timesBrightness, float multiplyForce)
    {
        boolean doErode = false;
        boolean doDilate = false;
        boolean doBrightness = false;

        kernelErode = kernelErode == null ? Kernels.CROSS : kernelErode;
        kernelDilate = kernelDilate == null ? Kernels.CROSS : kernelDilate;

        doErode =  timesErode == 0 ? false : true;
        doDilate =  timesDilate == 0 ? false : true;
        doBrightness =  timesBrightness == 0 ? false : true;


        BufferedImage sobel1 = convolve(img, Kernels.BORDER_SOBEL_GX);
        BufferedImage sobel2 = convolve(img, Kernels.BORDER_SOBEL_GY);
        BufferedImage img2 = filter(sobel1, sobel2, (p, p2) -> p.add(p2));

        if(doErode)
        {
            img2 = erode(img2, timesErode, kernelErode);
        }

        if(doDilate)
        {
            img2 = dilate(img2, timesDilate, kernelDilate);
        }

        if (doBrightness)
        {
            for (int i = 0; i<timesBrightness; i++)
            {
                img2 = filter(img2, p -> p.multiply(multiplyForce));
            }
        }


        return img2;
    }

    public static BufferedImage binarization(BufferedImage img, float treshold)
    {
        img = filter(img, p -> p.set(p.getR()>treshold ? 1 : 0));
        return  img;
    }

    public static BufferedImage invertBinary(BufferedImage img)
    {
        return filter(img, p -> p.set(p.getR()==1? 0 : 1 ));
    }

    public  static  BufferedImage gaussian(BufferedImage img, int times, float[][] kernel)
    {

        kernel = kernel==null? Kernels.SMOOTH_GAUSS : kernel;

        for(int i=0; i<times; i++)
        {
            img = convolve(img, kernel);
        }
        return img;
    }

    public static int minIndexOfHistogram(int []acumulatedHistogram)
    {
        int index = 0;
        for (int element : acumulatedHistogram)
        {
            if (element > 0)
            {
                return index;
            }
            index += 1;
        }
        return 0;
    }

    public static int numOfPixels(int[] acumulatedHistogram)
    {
        return acumulatedHistogram[acumulatedHistogram.length-1];
    }

    public static int accumulatedHistogramValue(int value)
    {
        return 0;
    }

    public static BufferedImage equalizeHistogram_Grayscale(BufferedImage img, int[] acumulatedHistogram)
    {
        int minIndex = minIndexOfHistogram(acumulatedHistogram);
        int numOfPixels = numOfPixels(acumulatedHistogram);

        float constantDivisor = 256-minIndex/numOfPixels-minIndex;

        return filter(img, p -> {
            float pValue = p.getR();
            float pFinal = (pValue-minIndex)/constantDivisor;
            return p.set(pFinal);
        });
    }
}
