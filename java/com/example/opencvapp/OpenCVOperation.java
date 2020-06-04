package com.example.opencvapp;
import android.graphics.Bitmap;
import android.util.Log;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import static java.lang.Math.PI;
import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.StrictMath.max;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;

public class OpenCVOperation {

    private final static String TAG = "OpenCVOperation";

    public static Bitmap make_negative(Mat img, Bitmap mBitmap){
        int width = img.width();
        int height = img.height();

        int new_width = width/5;
        int new_height = height/5;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);


        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);

            double R = 180-rgb[0];
            double G = 180-rgb[1];
            double B = 180-rgb[2];

            double[] new_rgb = {R+G+B, G+R+B, R+G+B};
            dst.put(x, y, new_rgb);
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }
    public static Bitmap gamma_corection(Mat img, Bitmap mBitmap){
        double gamma = 0.5;
        int width = img.width();
        int height = img.height();

        int new_width = width/5;
        int new_height = height/5;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);


        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);
            double val1 = 255.0 * pow(rgb[0]/255.0, gamma);
            double val2 = 255.0 * pow(rgb[1]/255.0, gamma);
            double val3 = 255.0 * pow(rgb[2]/255.0, gamma);

            if (val1 >= 0 && val1 <= 255 && val2 >= 0 && val2 <= 255 && val3 >= 0 && val3 <= 255) {
                double [] vals = {val1,val2,val3};
                dst.put(x, y, vals);
            }
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }
    private static ArrayList<Mat> rgbToHsv(Mat src) {
        int height = src.rows();
        int width = src.cols();
        double r, g, b, M, m, C, V, S, H = 0, H_norm, V_norm, S_norm;
        Mat h_m = new Mat(height, width, CvType.CV_8UC1);
        Mat s_m = new Mat(height, width, CV_8UC1);
        Mat v_m = new Mat(height, width, CV_8UC1);

        ArrayList<Mat> mats = new ArrayList<>();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {

                double[] index = src.get(i, j);

                r = index[0]/255;
                g = index[1] /255;
                b = index[2] /255;

                M = max(max(r, g), b);
                m = min(min(r, g), b);

                C = M - m;

                V = M;

                if (V != 0) {
                    S = C / V;
                } else S = 0;

                if (C != 0) {
                    if (M == r) {
                        H = 60 * (g - b) / C;
                    }
                    if (M == g) {
                        H = 120 + 60 * (b - r) / C;
                    }
                    if (M == b) {
                        H = 240 + 60 * (r - g) / C;
                    }
                } else {
                    H = 0;
                }
                if (H != 0) {
                    H = H + 360;
                }

                H_norm = H * 255 / 360;
                S_norm = S * 255;
                V_norm = V * 255;

                h_m.put(i,j,H_norm);
                s_m.put(i,j,S_norm);
                v_m.put(i,j,V_norm);

            }
        }

        mats.add(h_m);
        mats.add(s_m);
        mats.add(v_m);
        return mats;
    }


    private static ArrayList<ArrayList<Integer>> calc_hist(Mat img){
        int width = img.width();
        int height = img.height();

        Mat h_m, s_m, v_m;

        ArrayList<Mat> mats;

        ArrayList<ArrayList<Integer>> hists = new ArrayList<>();

        ArrayList<Integer> rhist = new ArrayList<>(256);
        ArrayList<Integer> ghist = new ArrayList<>(256);
        ArrayList<Integer> bhist = new ArrayList<>(256);

        int new_width = width/10;
        int new_height = height/10;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);

        Log.d(TAG,"Lungime imagine resize: " + new_height + " Latime imagine resize: "+ new_width);

        Log.d(TAG, "Atata e lungimea lor inainte: " + rhist.size() + " " + ghist.size() + " " + bhist.size());

        mats = rgbToHsv(resizeimage);
        h_m = mats.get(0);
        s_m = mats.get(1);
        v_m = mats.get(2);

        for(int i = 0; i < 256; i++){
            rhist.add(i,0);
            ghist.add(i,0);
            bhist.add(i,0);
        }

        Log.d(TAG, "Atata e lungimea lor dupa: " + rhist.size() + " " + ghist.size() + " " + bhist.size());

        for (int i = 0; i < new_height; i++) {
            for (int j = 0; j < new_width; j++) {
                double[] indexh = h_m.get(i, j);
                double[] indexs = s_m.get(i, j);
                double[] indexv = v_m.get(i, j);

                int elem_r = rhist.get((int)indexh[0]) + 1;
                int elem_g = ghist.get((int)indexs[0]) + 1;
                int elem_b = bhist.get((int)indexv[0]) + 1;

                rhist.set((int)indexh[0],elem_r);
                ghist.set((int)indexs[0],elem_g);
                bhist.set((int)indexv[0],elem_b);
            }
        }

        Log.d(TAG, "!!!!!!!!!!!!!Atata e lungimea lor dupa dupa: !!!!!!!!!!!!!! " + rhist.size() + " " + ghist.size() + " " + bhist.size());

        hists.add(rhist);
        hists.add(ghist);
        hists.add(bhist);

        return hists;
    }

    public static Bitmap contrast(Mat img,Bitmap mBitmap,float maximOut){
        int width = img.width();
        int height = img.height();

        float minimOut = 0;

        int new_width = width/10;
        int new_height = height/10;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);

        ArrayList<ArrayList<Integer>> hists = calc_hist(img);

        ArrayList<Integer> r_hist = hists.get(0);
        ArrayList<Integer> g_hist = hists.get(1);
        ArrayList<Integer> b_hist = hists.get(2);

        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        int r_minimIn = 0;
        int r_maximIn = 255;

        int g_minimIn = 0;
        int g_maximIn = 255;

        int b_minimIn = 0;
        int b_maximIn = 255;

        while (r_hist.get(r_minimIn) == 0)
            r_minimIn++;

        while (r_hist.get(r_maximIn) == 0)
            r_maximIn--;

        while (g_hist.get(g_minimIn) == 0)
            g_minimIn++;

        while (g_hist.get(g_maximIn) == 0)
            g_maximIn--;

        while (b_hist.get(b_minimIn) == 0)
            b_minimIn++;

        while (b_hist.get(b_maximIn) == 0)
            b_maximIn--;

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);

            double R = rgb[0];
            double G = rgb[1];
            double B = rgb[2];

            double new_R = minimOut + (float)(R - r_minimIn)*((float)(maximOut - minimOut)/(float)(r_maximIn - r_minimIn));
            double new_G = minimOut + (float)(G - g_minimIn)*((float)(maximOut - minimOut)/(float)(g_maximIn - g_minimIn));
            double new_B = minimOut + (float)(B - b_minimIn)*((float)(maximOut - minimOut)/(float)(b_maximIn - b_minimIn));

            if(new_R > 255){
                new_R = 255;
            }else if (new_R < 0){
                new_R = 0;
            }

            if(new_G > 255){
                new_G = 255;
            }else if (new_G < 0){
                new_G = 0;
            }

            if(new_B > 255){
                new_B = 255;
            }else if (new_B < 0){
                new_B = 0;
            }

            double[] new_rgb = {new_R, new_G, new_B};
            dst.put(x, y, new_rgb);
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }

    public static Bitmap grey(Mat img, Bitmap mBitmap){
        int width = img.width();
        int height = img.height();

        int new_width = width/5;
        int new_height = height/5;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);


        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);
            double rgb_color = (rgb[0]+rgb[1]+rgb[2])/3;
            double[] new_rgb = {rgb_color, rgb_color, rgb_color};
            dst.put(x, y, new_rgb);
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }

    public static Bitmap thresholding(Mat img, Bitmap mBitmap){
        int width = img.width();
        int height = img.height();

        int new_width = width/5;
        int new_height = height/5;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);


        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);

            double R = rgb[0];
            double G = rgb[1];
            double B = rgb[2];

            if(R < 127){
                R = 0;
                G = 0;
                B = 0;
            }else{
                G = 255;
                R = 255;
                B = 255;
            }

            double[] new_rgb = {R, G, B};
            dst.put(x, y, new_rgb);
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }
    public static Bitmap doBrightness(Mat img,Bitmap mBitmap,int value)
    {
        int width = img.width();
        int height = img.height();

        Log.d(TAG,"INITIAL : Lungime si latime: "+height+" "+width);

        int new_width = width/5;
        int new_height = height/5;

        Mat resizeimage = new Mat(new_height,new_width, CV_8UC3);;
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);

        Log.d(TAG,"RESIZE: Lungime si latime: "+new_height+" "+new_width);

        Mat dst = new Mat(new_height,new_width, CV_8UC3);

        for (int x = 0; x < new_height; x++)
        { for (int y = 0; y < new_width; y++)
        {
            double[] rgb = resizeimage.get(x, y);

            double R = rgb[0];
            double G = rgb[1];
            double B = rgb[2];

            R += value;

            if (R > 255) {
                R = 255;
            } else if (R < 0) {
                R = 0;
            }

            G += value;
            if (G > 255) {
                G = 255;
            } else if (G < 0) {
                G = 0;
            }

            B += value;
            if (B > 255) {
                B = 255;
            } else if (B < 0) {
                B = 0;
            }
            double[] new_rgb = {R, G, B};
            dst.put(x, y, new_rgb);
        }
        }
        Mat resize_dst = new Mat();
        Imgproc.resize(dst,resize_dst,new Size(width,height),0,0,Imgproc.INTER_CUBIC);
        Bitmap bmOut = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Utils.matToBitmap(resize_dst,bmOut);
        return bmOut;
    }
    private static Mat convolution(Mat resizeimage,int w,double[][] filter){
        Mat dst = new Mat(resizeimage.rows(),resizeimage.cols(), CV_8UC3);
        int k = (w - 1) /2;
        for(int i = k; i <resizeimage.rows()-k; i++){
            for(int j = k; j <resizeimage.cols()-k; j++){
                int R = 0;
                int G = 0;
                int B = 0;
                for(int u = -k; u <=k; u++){
                    for(int v = -k; v <=k; v++){
                        double[] rgb = resizeimage.get(i + u, j + v);
                        R+=rgb[0]*filter[u+k][v+k];
                        G+=rgb[1]*filter[u+k][v+k];
                        B+=rgb[2]*filter[u+k][v+k];
                    }
                }
                double[] new_rgb = {R, G, B};
                dst.put(i, j, new_rgb);
            }
        }
        return dst;
    }

    public static Bitmap gaussianFilter(Mat img, Bitmap mBitmap,int w){

        int width = img.cols();
        int height = img.rows();

        int new_width = width/10;
        int new_height = height/10;

        Mat resizeimage = new Mat(new_height,new_width, CvType.CV_8UC3);
        Log.d(TAG,"TIP resizeIMG "+ resizeimage.channels());
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);

        Mat dst;

        float sigma =(float)w/6;
        int k = w/2;
        double[][] G=new double[11][11];
        for(int x=0; x<w; x++){
            for(int y=0; y<w; y++){
                G[x][y] = 1.0/(2 * PI*sigma*sigma) * exp(-(((x - k)*(x - k) + (y - k)*(y - k)) / (2 * sigma*sigma)));
            }
        }

        dst = convolution(resizeimage,w,G);

        Bitmap newBitmap = Bitmap.createBitmap(dst.cols(),dst.rows(), mBitmap.getConfig());
        Utils.matToBitmap(dst,newBitmap);
        return newBitmap;
    }

    public static Bitmap edgeFilter(Mat img,Bitmap mBitmap){
        int width = img.cols();
        int height = img.rows();

        int new_width = width/10;
        int new_height = height/10;

        Mat resizeimage = new Mat(new_height,new_width, CvType.CV_8UC3);
        Log.d(TAG,"TIP resizeIMG "+ resizeimage.channels());
        Size sz = new Size(new_width,new_height);
        Imgproc.resize(img, resizeimage, sz,0, 0, Imgproc.INTER_AREA);

        Mat dst;

        double[][] G= {{-1, -1, -1},{-1, 9, -1},{-1, -1, -1}};
        dst = convolution(resizeimage,3,G);

        Bitmap newBitmap = Bitmap.createBitmap(dst.cols(),dst.rows(), mBitmap.getConfig());
        Utils.matToBitmap(dst,newBitmap);
        return newBitmap;
    }

    public static Mat format_image(Mat img1, Mat img2, Mat img3, Mat img4){
        Mat output = new Mat(400,400, CV_8UC3);
        for (int x = 0; x < 200; x++) {
            for (int y = 0; y < 200; y++) {
                double[] rgb = img1.get(x, y);
                double R = rgb[0];
                double G = rgb[1];
                double B = rgb[2];
                double[] new_rgb = {R,G,B};
                output.put(x, y, new_rgb);
            }
        }
        for (int x = 0; x < 200; x++) {
            for (int y = 200; y < 400; y++) {
                double[] rgb = img2.get(x, y-200);
                double R = rgb[0];
                double G = rgb[1];
                double B = rgb[2];
                double[] new_rgb = {R,G,B};
                output.put(x, y, new_rgb);
            }
        }
        for (int x = 200; x < 400; x++) {
            for (int y = 0; y < 200; y++) {
                double[] rgb = img3.get(x-200, y);
                double R = rgb[0];
                double G = rgb[1];
                double B = rgb[2];
                double[] new_rgb = {R,G,B};
                output.put(x, y, new_rgb);
            }
        }
        for (int x = 200; x < 400; x++) {
            for (int y = 200; y < 400; y++) {
                double[] rgb = img4.get(x-200, y-200);
                double R = rgb[0];
                double G = rgb[1];
                double B = rgb[2];
                double[] new_rgb = {R,G,B};
                output.put(x, y, new_rgb);
            }
        }
        return output;
    }
}
