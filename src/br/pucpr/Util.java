package br.pucpr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;
import java.util.function.UnaryOperator;

public class Util {

    public static final Vector3 LUMINANCE_WEIGHTS = new Vector3(0.299f, 0.587f, 0.114f);

    /**
     * Salva a imagem no disco.
     */
    public static void save(String name, BufferedImage img) {
        try {
            ImageIO.write(img, "png", new File(name + ".png"));
            System.out.printf("Salvo %s.png%n", name);
        } catch (IOException e) {
            System.err.println("Não foi possível salvar a imagem " + name);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static BufferedImage load(String name) {
        try {
            return ImageIO.read(Util.class.getResourceAsStream(name));
        } catch (IOException e) {
            System.err.println("Não foi possível carregar: " + name);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    public static BufferedImage load(File name) {
        try {
            return ImageIO.read(name);
        } catch (IOException e) {
            System.err.println("Não foi possível carregar: " + name);
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**
     * Faz o for sobre a imagem, aplicando a operação unária.
     */
    public static BufferedImage filter(BufferedImage img, UnaryOperator<Vector3> op) {
        //Cria a imagem de saída
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        //Percorre a imagem de entrada
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //Lê o pixel
                Vector3 pixel = new Vector3(img.getRGB(x, y));

                //Aplica a operação passada por parâmetro
                Vector3 o = op.apply(pixel).clamp();

                //Define a cor na imagem de saída
                out.setRGB(x, y, o.getRGB());
            }
        }
        return out;
    }

    /**
     * Faz o for sobre a imagem, aplicando a operação binária.
     */
    public static BufferedImage filter(BufferedImage img1, BufferedImage img2, BinaryOperator<Vector3> op) {
        //Garante que só pixels válidos serão acessados, mesmo que as imagens tenham tamanhos diferentes
        int w = Math.min(img1.getWidth(), img2.getWidth());
        int h = Math.min(img1.getHeight(), img2.getHeight());

        //Cria a imagem de saída
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //Percorre as imagens
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                //Le os pixels das imagens 1 e 2
                Vector3 p1 = new Vector3(img1.getRGB(x, y));
                Vector3 p2 = new Vector3(img2.getRGB(x, y));

                //Aplica a operação binária passada por parâmetro para calcular a cor de saída
                Vector3 o = op.apply(p1, p2).clamp();

                //Define a cor na imagem de saída
                out.setRGB(x, y, o.getRGB());
            }
        }
        return out;
    }

    /**
     * Ajusta o índice quando está fora da imagem utilizando a politica de mirror
     */
    private static int mirrorIndex(int idx, int limit) {
        if (idx < 0) return 1;
        if (idx >= limit) return limit - 2;
        return idx;
    }

    /**
     * Realiza a operação de convolução, isto é a aplicação do kernel sobre a imagem. Para cada pixel da imagem,
     * é calculada uma média ponterada entre ele e seus vizinhos. O kernel contém os pesos dessa média.
     */
    public static BufferedImage convolve(BufferedImage img, float[][] kernel) {
        //Cria a imagem de saída
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

        //Percorre a imagem de entrada
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //Valores de r, g e b finais
                Vector3 outPixel = new Vector3();
                //Para cada pixel percorrido na imagem, precisamos percorrer os seus 9 vizinhos
                //O peso desses vizinhos estão descritos no kernel, por isso, fazemos um for para percorrer o kernel
                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {
                        //Observe que os índices de kx e ky variam de 0 até 2. Já os vizinhos de x seriam
                        //x+(-1), x+0 + x+1. Por isso, subtraímos 1 de kx e ky para chegar no vizinho.
                        int px = mirrorIndex(x + (kx-1), img.getWidth());
                        int py = mirrorIndex(y + (ky-1), img.getHeight());

                        //Obtemos o pixel vizinho
                        Vector3 pixel = new Vector3(img.getRGB(px, py));
                        //E somamos ele as cores finais multiplicadas pelo seu respectivo peso no kernel
                        outPixel.add(pixel.multiply(kernel[kx][ky]));
                    }
                }

                //Calculamos a cor final
                out.setRGB(x, y, outPixel.clamp().getRGB());
            }
        }
        return out;
    }

    /**
     * Operação de erosão morfológica.
     * Nesta operação buscamos entre o pixel e seus vizinhos aqueles com o tom de cinza mais escuro (de menor valor).
     * Os pixels considerados na busca são aqueles marcados com true no kernel.
     *
     * @param img A imagem a ser processada
     * @param kernel O kernel a ser usado. Caso null seja passado, Kernels.CROSS será usado.
     * @return A imagem erodida
     */
    public static BufferedImage erode(BufferedImage img, boolean[][] kernel) {
        //Cria a imagem de saída
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        kernel = kernel == null ? Kernels.CROSS : kernel;
        //Percorre a imagem de entrada
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //A erosão busca pelo pixel de menor valor
                float min = 1.0f;
                //Para cada pixel percorrido na imagem, precisamos percorrer os seus 9 vizinhos
                //Os vizinhos que serão considerados estão marcados como true no kernel
                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {
                        //Observe que os índices de kx e ky variam de 0 até 2. Já os vizinhos de x seriam
                        //x+(-1), x+0 + x+1. Por isso, subtraímos 1 de kx e ky para chegar no vizinho.
                        int px = x + (kx-1);
                        int py = y + (ky-1);

                        //Nas bordas, px ou py podem acabar caindo fora da imagem. Quando isso ocorre, pulamos para o
                        // próximo pixel.
                        if (px < 0 || px >= img.getWidth() || py < 0 || py >= img.getHeight()) {
                            continue;
                        }

                        //Obtem o tom de cinza do pixel
                        float tone = new Vector3(img.getRGB(px, py)).dot(LUMINANCE_WEIGHTS);

                        //Se ele for mais escuro que o menor já encontrado, substitui
                        if (kernel[kx][ky] && tone < min) {
                            min = tone;
                        }
                    }
                }

                //Define essa cor na imagem de saída.
                out.setRGB(x, y, new Vector3().set(min).getRGB());
            }
        }
        return out;
    }

    /**
     * Aplica a erosao times vezes.
     */
    public static BufferedImage erode(BufferedImage img, int times, boolean[][] kernel) {
        BufferedImage out = img;
        for (int i = 0; i < times; i++) {
            out = erode(out, kernel);
        }
        return out;
    }

    /**
     * Operação de dilatação morfológica.
     * Nesta operação buscamos entre o pixel e seus vizinhos aqueles com o tom de cinza mais escuro (de menor valor).
     * Os pixels considerados na busca são aqueles marcados com true no kernel.
     *
     * @param img A imagem a ser processada
     * @param kernel O kernel a ser usado. Caso null seja passado, Kernels.CROSS será usado.
     * @return A imagem dilatada
     */
    public static BufferedImage dilate(BufferedImage img, boolean[][] kernel) {
        //Cria a imagem de saída
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        kernel = kernel == null ? Kernels.CROSS : kernel;

        //Percorre a imagem de entrada
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //A dilatação busca pelo pixel de maior valor
                float max = 0;
                //Para cada pixel percorrido na imagem, precisamos percorrer os seus 9 vizinhos
                //Os vizinhos que serão considerados estão marcados como true no kernel
                for (int ky = 0; ky < 3; ky++) {
                    for (int kx = 0; kx < 3; kx++) {
                        //Observe que os índices de kx e ky variam de 0 até 2. Já os vizinhos de x seriam
                        //x+(-1), x+0 + x+1. Por isso, subtraímos 1 de kx e ky para chegar no vizinho.
                        int px = x + (kx-1);
                        int py = y + (ky-1);

                        //Nas bordas, px ou py podem acabar caindo fora da imagem. Quando isso ocorre, pulamos para o
                        // próximo pixel.
                        if (px < 0 || px >= img.getWidth() || py < 0 || py >= img.getHeight()) {
                            continue;
                        }

                        //Obtem o tom de cinza do pixel
                        float tone = new Vector3(img.getRGB(px, py)).dot(LUMINANCE_WEIGHTS);

                        //Se ele for mais claro que o maior já encontrado, substitui
                        if (kernel[kx][ky] && tone > max) {
                            max = tone;
                        }
                    }
                }

                //Define essa cor na imagem de saída.
                out.setRGB(x, y, new Vector3().set(max).getRGB());
            }
        }
        return out;
    }

    /**
     * Aplica a dilatação times vezes.
     */
    public static BufferedImage dilate(BufferedImage img, int times, boolean[][] kernel) {
        BufferedImage out = img;
        for (int i = 0; i < times; i++) {
            out = dilate(out, kernel);
        }
        return out;
    }

    /**
     * Abertura morfológica.
     * Trata-se de várias erosões seguidas do mesmo número de dilatações. Isso faz com que áreas pequenas da imagem
     * tendam a desaparecer, e estruturas maiores sejam mantidas.
     */
    public static BufferedImage open(BufferedImage img, int times, boolean[][] kernel) {
        return dilate(erode(img, times, kernel), times, kernel);
    }

    /**
     * Fechamento morfológico.
     * Trata-se de várias dilatações seguidas do mesmo número de erosões. Isso faz com que "buracos" pequenos na imagem
     * tendam a desaparecer.
     */
    public static BufferedImage close(BufferedImage img, int times, boolean[][] kernel) {
        return erode(dilate(img, times, kernel), times, kernel);
    }

    /**
     * Calculates the histogram of an image
     *
     * @param img The image to calculate histogram
     * @param colorMapper Extract an integer in the interval 0..255 to use in histogram calculation.
     *
     * @return The calculated histogram
     */
    public static int[] histogram(BufferedImage img, Function<Color, Integer> colorMapper) {
        //Criamos o histograma com um índice para cada tom de cinza
        int[] hist = new int[256];

        //Percorremos a imagem somando 1 a cada tom de cinza encontrado
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                //Obtem o tom de cinza do pixel (x,y)
                int tom = colorMapper.apply(new Color(img.getRGB(x, y)));

                //Soma 1 no índice correspondente ao tom no histograma
                hist[tom] += 1;
            }
        }
        return hist;
    }

    /**
     * Calculates the histogram of a grayscale image
     * @param img The image to calculate histogram
     * @return The calculated histogram
     */
    public static int[] histogram(BufferedImage img) {
        return histogram(img, Color::getGreen);
    }

    public static int[] accumHistogram(int[] histogram) {
        int[] accum = new int[histogram.length];

        //O primeiro índice do histograma normal e do acumulado são iguais
        accum[0] = histogram[0];

        //A partir do índice 1, soma o valor anterior ao atual
        for (int i = 1; i < histogram.length; i++) {
            accum[i] = histogram[i] + accum[i-1];
        }

        return accum;
    }

    public static BufferedImage drawHistogram(int[] histogram) {
        //Vamos procurar o maior valor do histograma, para que a barrinha dele tenha altura 600
        int max = 0;
        for (int value : histogram) {
            if (max < value) {
                max = value;
            }
        }

        //Calculamos a proporção.
        float prop = 600.0f / max;

        //Agora vamos desenhar o gráfico. Iremos criar 2 barrinhas verticais para cada indice do histograma.
        BufferedImage out = new BufferedImage(512, 600, BufferedImage.TYPE_INT_RGB);

        //Desenhamos a linha usando o objeto Graphics2D, como sugerido no enunciado
        Graphics2D g = out.createGraphics();
        for (int i = 0; i < 512; i++) {
            //Calculamos a altura da linha com base no histograma
            int idx = i / 2; //Indice do histograma sendo desenhado
            int h = 600 - (int) (histogram[idx] * prop); //Invertido pois a altura 0 é no topo da imagem

            //Desenhamos uma linha
            g.drawLine(i, h, i, 599);
        }
        //Boa prática: chamar dispose no objeto graphics
        g.dispose();
        return out;
    }
}
