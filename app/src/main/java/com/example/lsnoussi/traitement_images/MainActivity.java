package com.example.lsnoussi.traitement_images;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;


import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Bitmap b;
    private Button grayButton;
    private Button dynamic_extensionButton;
    private Button coloriserButton;
    private Button histo_grayButton;
    private Button histo_rgbButton ;
    private Button flouButton ;
    private Button resetButton ;
    private Button gaussButton ;
    private Button over_exposureButton;
    private Bitmap bmp;
    private ImageView img;

    // I - Effects


    //effects_griser :

    /**
     *  @param bmp
               * take a bitmap as a parameter.
     *  function to gray a bitmap using a tab of pixels */


    public void toGray2(Bitmap bmp){
       int max =0;
       int min = 255;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0,0 ,bmp.getWidth(),bmp.getHeight());
        for(int i =0 ; i < pixels.length; ++i) {
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            int mean = (int) (0.3*r+ 0.59*g+ b*0.11);
            pixels[i] = Color.rgb(mean,mean,mean);
        }
        bmp.setPixels(pixels,0,bmp.getWidth(), 0,0 ,bmp.getWidth(),bmp.getHeight());
    }

     /**
     *  @param bmp
               * take a bitmap as a parameter.
     *  function to gray a bitmap by going through every pixel */
     public void  toGray(Bitmap bmp){

        for(int h =0 ; h < bmp.getHeight(); ++h){
            for(int w =0 ; w < bmp.getWidth(); ++w){
                int pixel = bmp.getPixel(w,h);
                int red = Color.red(pixel);
                int green = Color.green(pixel);
                int b = Color.blue(pixel);
                int mean = (int) (0.3*red + 0.59*green + b*0.11);
                bmp.setPixel(w,h,Color.rgb(mean,mean,mean));

            }
        }
    }



    //effects_colorize

    /**

     *  @param bmp
               * take a bitmap as a parameter.
     *  function to put a random colored filter on a bitmap */

    public void colorize (Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[w * h];

        Random ran = new Random();

        // possibility for hue [0..360]
        int nbr = ran.nextInt(360);
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());

        for (int i = 0; i < h * w; ++i) {
            int p = pixels[i];
            int r = Color.red(p);
            int g = Color.green(p);
            int b = Color.blue(p);

            float[] hsv = new float[3];


            Color.RGBToHSV(r, g, b, hsv);
            hsv[0] = nbr;
            hsv[1] = 1.0f;

            pixels[i] = Color.HSVToColor(hsv);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }


    // II-Contrast


    // function that calculates the histogram of a bitmap given :

    /**
     *  @param bmp
               * take a bitmap as a parameter.
     *  @return a tab filled with the numb of pixels with gray level.
     * function that calculates the histogram of a bitmap given */

    public static int[] histogram(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] hist = new int[256];
        int[] pixels = new int[h*w];

        bmp.getPixels(pixels,0,w,0,0,w,h);

        for (int x = 0; x < pixels.length; ++x) {
            int R = Color.red(pixels[x]);
            int G = Color.green(pixels[x]);
            int B = Color.blue(pixels[x]);

            int gray = (int)(0.3*R+0.59*G+0.11*B);

            hist[gray] = hist[gray] + 1 ;
        }

        return hist;


}

    /**
     *  @param bmp
               * take a bitmap as a parameter.
     *  @return a tab with max and min
     *  function to calculate the index of min & max of the histogram   */

    public static int[] Min_Max_Values(Bitmap bmp) {

        int[] hist = histogram(bmp);
        int[] D = new int[2];

        int min = 0;
        int max = 0;

        int maxH = hist[0];
        int minH = hist[0];

        for (int i = 0; i < hist.length; ++i) {
            if (hist[i] > maxH) {
                max = i;
            } else if (hist[i] < minH) {
                min = i;
            }
        }

        D[0] = max;
        D[1] = min;

        return D;
    }

    //linear extention

    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  @return a bitmap.
     *  function that calculates a linear transformation between [min,max] over 256 level of gray*/

    public Bitmap dynamic_extention(Bitmap bmp) {

        toGray(bmp);

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];
        int[] D = Min_Max_Values(bmp);

        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        Bitmap last = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());


        // Applies linear extension of dynamics to the bitmap

        for (int i = 0; i < pixels.length; ++i) {
            int R = 255 * ((Color.red(pixels[i])) - D[1]) / (D[0] - D[1]);
            int G = 255 * ((Color.green(pixels[i])) - D[1]) / (D[0] - D[1]);
            int B = 255 * ((Color.blue(pixels[i])) - D[1]) / (D[0] - D[1]);
            pixels[i] = Color.rgb(R, G, B);
        }

        last.setPixels(pixels, 0, w, 0, 0, w, h);
        return last ;



    }

    public Bitmap dynamic_extention1(Bitmap bmp) {
        toGray(bmp);
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        Bitmap last = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        int r_max = 0 ;
        int g_max = 0;
        int b_max = 0 ;

        int r_min = 0 ;
        int g_min = 0;
        int b_min = 0 ;

        int R0 = Color.red(pixels[0]);
        int G0 = Color.green(pixels[0]);
        int B0 = Color.blue(pixels[0]);

        for (int i = 0; i < pixels.length; ++i) {
            if (Color.red(pixels[i])> R0) {
                r_max= Color.red(pixels[i]);
            } else if ( Color.red(pixels[i]) < R0) {
                r_min = Color.red(pixels[i]);
            }


            if (Color.green(pixels[i])> G0) {
                g_max= Color.green(pixels[i]);
            } else if ( Color.green(pixels[i]) < G0) {
                g_min = Color.green(pixels[i]);
            }


            if (Color.blue(pixels[i])> B0) {
                b_max= Color.blue(pixels[i]);
            } else if ( Color.blue(pixels[i]) < B0) {
                b_min = Color.blue(pixels[i]);
            }

        }
        // Applies linear extension of dynamics to the bitmap

        for (int i = 0; i < pixels.length; ++i) {
            int R = 255 * ((Color.red(pixels[i])) - r_min) / (r_max - r_min);
            int G = 255 * ((Color.green(pixels[i])) - g_min) / (g_max - g_min);
            int B = 255 * ((Color.blue(pixels[i])) - b_min) / (b_max - b_min);
            pixels[i] = Color.rgb(R, G, B);
        }

        last.setPixels(pixels, 0, w, 0, 0, w, h);
        System.out.println("minRED" + r_min  + ", maxRED" + r_max );
        System.out.println("minBlue" + b_min  + ", maxblue" + b_max );
        System.out.println("mingreen" + g_min  + ", maxgreen" + g_max );
        return last ;



    }
     /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function that forces the levels of gray to be organized between 0 and 255 */

    public void histogramEqualization_gray(Bitmap bmp) {

        toGray2(bmp); //gray filter
        int w = bmp.getWidth();
        int h = bmp.getHeight();


        int[] pixels = new int[h * w];

        int[] hist = histogram(bmp);


        int[] C = new int[hist.length];
        C[0] = hist[0];
        for (int i = 1; i < hist.length; ++i) {
            C[i] = C[i - 1] + hist[i];  // histogram's sum
        }


    //equalization:
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);



        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);  // transformation of the gray level
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }


        bmp.setPixels(pixels, 0, w, 0, 0, w, h);



    }
    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  same thing as histogramEqualization_gray but without graying the bitmap first */


    public void histogramEqualization_RGB(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        int[] hist = histogram(bmp);
        int[] C = new int[hist.length];
        C[0] = hist[0];
        for (int i = 1; i < hist.length; ++i) {
            C[i] = C[i - 1] + hist[i];
        }
        //equalization:
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);
            R = C[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }

    /**
     *
     * @param bmp
     * take a bitmap as a parameter.
     * function that brighten a picture using an histogram for every R,G,B
     */

    void over_exposure(Bitmap bmp) {

        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] pixels = new int[h * w];


        int[] hist_red = new int[256];
        int[] hist_green = new int[256];
        int[] hist_blue = new int[256];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int coeff = 10;

        for (int i = 0; i < pixels.length; ++i) {
            hist_red[(Color.red(pixels[i]) * 255 - coeff )/ 255] += 1;
            hist_green[(Color.green(pixels[i]) * 255 - coeff )/ 255] += 1;
            hist_blue[(Color.blue(pixels[i]) * 255 - coeff) / 255] += 1;
         }

        int[] C_red = new int[hist_red.length];
        int[] C_green = new int[hist_green.length];
        int[] C_blue = new int[hist_blue.length];


        C_red[0] = hist_red[0];
        C_green[0] = hist_green[0];
        C_blue[0] = hist_blue[0];


        for (int i = 1; i < hist_red.length; ++i) {
            C_red[i] = C_red[i - 1] + hist_red[i];
            C_green[i] = C_green[i - 1] + hist_green[i];
            C_blue[i] = C_blue[i - 1] + hist_blue[i];
        }




        //equalization:
       
        for (int i = 0; i < pixels.length; ++i) {
            int R = Color.red(pixels[i]);
            R = C_red[R] * 255 / pixels.length;
            int G = Color.green(pixels[i]);
            G = C_green[G] * 255 / pixels.length;
            int B = Color.blue(pixels[i]);
            B = C_blue[B] * 255 / pixels.length;

            pixels[i] = Color.rgb(R, G, B);
        }

        bmp.setPixels(pixels, 0, w, 0, 0, w, h);


    }


    /*    III- Convolution :   */

    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function to blur using kernel matrix */

     public void Moyenneur(Bitmap bmp) {

        int[][] Matrix = new int[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Matrix[i][j] = 1;
            }
        }

        int width = bmp.getWidth();
        int height = bmp.getHeight();



        int sumR, sumG, sumB = 0;
        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);



        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;

                int index=0;

                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {
                        index = (y+v)*width +(x+u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }

                sumR = sumR / 9;

                sumG = sumG / 9;

                sumB = sumB / 9;

                pixels[index] =  Color.rgb(sumR, sumG, sumB);

            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);



    }
    /**
     *  @param bmp
     * take a bitmap as a parameter.
     *  function to blur using gauss matrix */

    public void Gauss_convolution(Bitmap bmp) {


        int[][] Matrix = new int[][] {
                {1,2,1},
                {2,4,2},
                {1,2,1}
        };

        int width = bmp.getWidth();
        int height = bmp.getHeight();



        int sumR, sumG, sumB = 0;

        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);


        for (int x = 1; x < width - 1; ++x) {
            for (int y = 1; y < height - 1; ++y) {

                sumR = sumG = sumB = 0;
                int index=0;


                for (int u = -1; u <= 1; ++u) {
                    for (int v = -1; v <= 1; ++v) {

                        index = (y+v)*width +(x+u);
                        sumR += Color.red(pixels[index]) * Matrix[u + 1][v + 1];
                        sumG += Color.green(pixels[index]) * Matrix[u + 1][v + 1];
                        sumB += Color.blue(pixels[index]) * Matrix[u + 1][v + 1];
                    }
                }


                sumR = sumR / 16;

                sumG = sumG / 16;

                sumB = sumB / 16;


                pixels[index] =  Color.rgb(sumR, sumG, sumB);

            }
        }

        bmp.setPixels(pixels, 0, width, 0, 0, width, height);




}

   /* public void Sobel_edgeDetection(Bitmap bmp){
        int[][] matrixSobelH1 = {
            {-1,     0,  1},
            {-2,     0,  2},
            {-1,     0,  1}
    };

    int[][] matrixSobelH2 = {
            {-1,    -2,     -1},
            {0,     0,      0},
            {1,     2,      1}
    };
        int width = bmp.getWidth();
        int height = bmp.getHeight();



        int sumR, sumG, sumB = 0;

        int[] pixels = new int [width*height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);



    }*/



   // OnClick button for every filter :

    private View.OnClickListener grayButtonlistner = new View.OnClickListener() {

        public void onClick(View v) {
            toGray2(b);
        }
    };
    private View.OnClickListener coloriserButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            colorize(b);
        }
    };
    private View.OnClickListener histo_grayButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            histogramEqualization_gray(b);
        }
    };
    private View.OnClickListener histo_rgbButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            histogramEqualization_RGB(b);
        }
    };
    private View.OnClickListener flouButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            Moyenneur(b);
        }
    };

    private View.OnClickListener gaussButtonListener = new View.OnClickListener() {

        public void onClick(View v) {
            Gauss_convolution(b);
        }
    };
    private View.OnClickListener dynamic_extentionButtonlistner = new View.OnClickListener() {

        public void onClick(View v) {
            dynamic_extention1(b);
        }
    };
    private View.OnClickListener over_exposureButtonlistener = new View.OnClickListener() {

        public void onClick(View v) {
            over_exposure(b);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inMutable = true;   //otherwise errors with setPixel()

        b = BitmapFactory.decodeResource(getResources(),R.drawable.test,options);
        bmp = b.copy(Bitmap.Config.ARGB_8888, true); // copy the original bitmap so we can reset it
        img = (ImageView) findViewById(R.id.imageView);
        img.setImageBitmap(b);


        grayButton = (Button) findViewById(R.id.griser);
        grayButton.setOnClickListener(grayButtonlistner);

        coloriserButton = (Button) findViewById(R.id.coloriser);
        coloriserButton.setOnClickListener(coloriserButtonListener);

        histo_grayButton = (Button) findViewById(R.id.histo_gray);
        histo_grayButton.setOnClickListener(histo_grayButtonListener);

        histo_rgbButton = (Button) findViewById(R.id.histo_rgb);
        histo_rgbButton.setOnClickListener(histo_rgbButtonListener);

        flouButton = (Button) findViewById(R.id.moyenneur);
        flouButton.setOnClickListener(flouButtonListener);

        gaussButton = (Button) findViewById(R.id.filtre_gauss);
        gaussButton.setOnClickListener(gaussButtonListener);

        dynamic_extensionButton = (Button) findViewById(R.id.dynamic_extension);
        dynamic_extensionButton.setOnClickListener(dynamic_extentionButtonlistner);

        over_exposureButton = (Button) findViewById(R.id.surexposition);
        over_exposureButton.setOnClickListener(over_exposureButtonlistener);

        resetButton = (Button) findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                b = bmp.copy(Bitmap.Config.ARGB_8888, true);
                img.setImageBitmap(b);

        }
      });


    }



}
