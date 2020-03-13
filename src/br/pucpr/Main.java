package br.pucpr;

import java.awt.image.BufferedImage;

import static br.pucpr.Util.*;
import static br.pucpr.Utils2.*;

public class Main {
    public static void main(String[] args) {
        String nameImage = "spiderman";
        BufferedImage img1 = load("/img/cor/" + nameImage + ".jpg");

        BufferedImage img2 = grayscale(img1);

        var histogramImg2 = histogram(img2);

        //BufferedImage img4 = equalizeHistogram_Grayscale(img2, accumHistogram(histogramImg2));

        img2 = bordersSobel(img2, 0, null, 1, null, 2, 1.5f);
        img2 = binarization(img2, 0.8f);
        img2 = invertBinary(img2);
        img2 = gaussian(img2, 3, null);

        BufferedImage img3 = filter(img1, img2, (p, p2) -> p.multiply(p2));

        save("histogram", drawHistogram(histogramImg2));
        save("result", img3);
    }
}
