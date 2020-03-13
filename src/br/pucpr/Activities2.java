package br.pucpr;

import java.awt.image.BufferedImage;
import static br.pucpr.Vector3.*;
import static br.pucpr.Util.*;

public class Activities2 {
    /**
     * Difficulty: AVERAGE
     *
     * Calculate the border image applying Gx and Gy gradients.
     * Final pixel color will be calculated as Math.sqrt(gx, gy).
     *
     * @param img Image
     * @param gradientX Gradient x kernel
     * @param gradientY Gradient y kernel
     * @return Calculated borders
     */
    public static BufferedImage border(BufferedImage img, float[][] gradientX, float[][] gradientY) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        return out;
    }

    /**
     * Difficulty: AVERAGE
     *
     * Create a reconstruction morphological filter
     * 1. Dilate the img
     * 2. Remove pixels in dilated img that are not present in the original
     * 3. Repeat the process until there's no changes in img
     *
     * @param img The image to be processed
     * @param original Original image
     */
    public static BufferedImage reconstruct(BufferedImage img, BufferedImage original) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        return out;
    }

    public static BufferedImage sobel(BufferedImage img) {
        return border(img, Kernels.BORDER_SOBEL_GX, Kernels.BORDER_SOBEL_GY);
    }

    public static BufferedImage prewitt(BufferedImage img) {
        return border(img, Kernels.BORDER_PREWITT_GX, Kernels.BORDER_PREWITT_GY);
    }
}
