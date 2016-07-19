package com.example.wbch.proyectopds_test;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import android.view.WindowManager;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.ANN_MLP;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity_show_camera extends AppCompatActivity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;

    View decorView = null;

    int frameNumber = 0;
    Point point1 = new Point();
    Point point2 = new Point();
    boolean p1 = true;
    boolean havePoints = false;

    // para detectar señales mediante puntos de interes
    FeatureDetector detector = null;
    MatOfKeyPoint keypoints1 = null;
    DescriptorExtractor descriptor = null;
    Mat descriptors1 = null;

    MatOfKeyPoint keypoints2 = null;
    Mat descriptors2 = null;

    MatOfKeyPoint keypoints3 = null;
    Mat descriptors3 = null;

    // variables
    Mat frameRGB = null;
    Mat image = null;
    Mat newFrameHSV = null;
    Mat lowerRed = null;
    Mat upperRed = null;
    Mat redHue = null;
    Mat blue = null;
    Mat yellow = null;
    Mat white = null;

    int num = 0;

    boolean loadData = true;

    // knn
    KNearest knn = null;

    // mat temp
    Mat redtmp = null;
    Mat bluetmp = null;
    Mat yellowtmp = null;
    Mat azulMat = null;
    Mat rojoMat = null;
    Mat amarilloMat = null;
    Mat senalMat = null;
    Mat matSenal = null;
    Mat matSenal3 = null;
    Mat matMensaje = null;

    // mensajes
    Mat msjI4 = null;
    int numMsj = -1;

    int loadSenal = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public MainActivity_show_camera() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        decorView = getWindow().getDecorView();
        setContentView(R.layout.show_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setMaxFrameSize(768, 432);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }


    public void onCameraViewStarted(int width, int height) {
    }


    public void onCameraViewStopped() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                double newX = x / 1.66667;
                double newY = y / 1.66667;


                Log.i(TAG, "OpenCV touch down" + newX + "-" + newY);

                if (newX  >= 668 && newY >= 0 && newY <= 100) {
                    numMsj = 4;
                    return false;
                }

                if (p1) {
                    point1.x = newX;
                    point1.y = newY;
                    p1 = false;
                    havePoints = false;
                } else {
                    point2.x = newX;
                    point2.y = newY;
                    p1 = true;
                    havePoints = true;

                    refreshMATs();
                }
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }

    private Rect getROI() {
        if (havePoints) {
            return new Rect(point1, point2);
        }
        return null;
    }

    private void refreshMATs() {
        image = null;
        newFrameHSV = null;
        lowerRed = null;
        upperRed = null;
        redHue = null;
        blue = null;
        yellow = null;
        white = null;
    }

    private Rect getBoundingRect(Mat greyImage) {
        Imgproc.GaussianBlur(greyImage, greyImage, new Size(3, 3), 2);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        // encontramos contornos
        Imgproc.findContours(greyImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        if (contours.size() < 2)
            return null;

        Log.i(TAG, "OpenCV contour number " + contours.size());

        double max1 = 0;
        double max2 = 0;
        int pos1 = 0;
        int pos2 = 0;
        double area;

        for (int i = 0; i < contours.size(); i++) {
            Log.i(TAG, "OpenCV contour number " + i + " - contour area " + Imgproc.contourArea(contours.get(i)));

            area = Imgproc.contourArea(contours.get(i));

            if (area > max1) {
                max2 = max1;
                pos2 = pos1;
                max1 = area;
                pos1 = i;
            } else {
                if (area > max2) {
                    max2 = area;
                    pos2 = i;
                }
            }
        }

        //esperamos encontrar un contorno
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(pos2).toArray());

        double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

        //Convert back to MatOfPoint
        MatOfPoint points = new MatOfPoint(approxCurve.toArray());

        // Get bounding rect of contour
        return Imgproc.boundingRect(points);
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        if (loadData) {
            //saveTrainData();
            readTrainData();
            detectarCaracteristicas();
            cargarSenales();
            cargarMensajes();
            loadData = false;
        }

        Mat frame = inputFrame.rgba();

        if (frameRGB == null)
            frameRGB = new Mat(frame.rows(), frame.cols(), CvType.CV_8UC3);

        Imgproc.cvtColor(frame, frameRGB, Imgproc.COLOR_RGBA2RGB);

        // dibujamos un rectangulo para la parte seleccionada
        if (havePoints) {

            // recortamos la imagen solo para procesar lo que el usuario selecciono
            Mat newFrame = new Mat(frameRGB, getROI());
            Imgproc.GaussianBlur(newFrame, newFrame, new Size(3, 3), 2);

            Mat subMat = frameRGB.submat(getROI());

            if (senalMat == null)
                senalMat = frameRGB.submat(new Rect(0,332,100,100));

            if (frameNumber % 10 == 0) {
                detector = FeatureDetector.create(FeatureDetector.ORB);
                keypoints1 = new MatOfKeyPoint();
                descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                descriptors1 = new Mat();

                detector.detect(newFrame, keypoints1);
                descriptor.compute(newFrame, keypoints1, descriptors1);

                MatOfDMatch matches = new MatOfDMatch();
                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

                matcher.match(descriptors1, descriptors2, matches);

                List<DMatch> listD = matches.toList();
                List<DMatch> listB = new ArrayList<>();
                double min_dist = 40;

                for( int i = 0; i < descriptors1.rows(); i++ ) {
                    if( listD.get(i).distance < min_dist ) {
                        listB.add(listD.get(i));
                    }
                }

                if (listB.size() > 20)
                    loadSenal = 1;
                else
                    loadSenal = 0;

                if (loadSenal == 0) {
                    matcher.match(descriptors1, descriptors3, matches);

                    listD = matches.toList();
                    listB = new ArrayList<>();

                    for( int i = 0; i < descriptors1.rows(); i++ ) {
                        if( listD.get(i).distance < min_dist ) {
                            listB.add(listD.get(i));
                        }
                    }

                    if (listB.size() > 20)
                        loadSenal = 3;
                    else
                        loadSenal = 0;
                }

                Log.i(TAG, "OpenCV matches number " + listB.size());

                frameNumber = frameNumber >= 30 ? 0 : frameNumber;
            }

            if (loadSenal != 0) {
                if (loadSenal == 1)
                    matSenal.copyTo(senalMat);
                else if (loadSenal == 3)
                    matSenal3.copyTo(senalMat);
                Imgproc.rectangle(frameRGB, point1, point2, new Scalar(255, 255, 0), 2);
                frameNumber++;
                return frameRGB;
            }

            if (azulMat == null)
                azulMat = frameRGB.submat(new Rect(0,0,100,100));

            if (rojoMat == null)
                rojoMat = frameRGB.submat(new Rect(668,0,100,100));

            if (amarilloMat == null)
                amarilloMat = frameRGB.submat(new Rect(668,332,100,100));

            if (matMensaje == null)
                matMensaje = frameRGB.submat(new Rect(100,332,568,100));

            if (image == null)
                image = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC3);

            if (redtmp == null)
                redtmp = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC3);

            if (bluetmp == null)
                bluetmp = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC3);

            if (yellowtmp == null)
                yellowtmp = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC3);

            // convertimos la imagen a grises // esto es solo debug
            /*Mat newFrameGrey = new Mat(newFrame.rows(), newFrame.cols(), CvType.CV_8UC1);
            Imgproc.cvtColor(newFrame, newFrameGrey, Imgproc.COLOR_RGBA2GRAY);
            Imgproc.cvtColor(newFrameGrey, image, Imgproc.COLOR_GRAY2RGBA);

            image.copyTo(subMat);*/

            if (newFrameHSV == null)
                newFrameHSV = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC3);

            Imgproc.cvtColor(newFrame, newFrameHSV, Imgproc.COLOR_RGB2HSV);

            // para el color rojo
            if (lowerRed == null)
                lowerRed = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC1);
            if (upperRed == null)
                upperRed = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC1);

            Core.inRange(newFrameHSV, new Scalar(0, 80, 80), new Scalar(7, 255, 255), lowerRed);
            Core.inRange(newFrameHSV, new Scalar(170, 80, 80), new Scalar(180, 255, 255), upperRed);

            if (redHue == null)
                redHue = new Mat(newFrame.rows(), newFrame.cols(), CvType.CV_8UC1);

            // para el color azul
            if (blue == null)
                blue = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC1);

            Core.inRange(newFrameHSV, new Scalar(95, 60, 30), new Scalar(132, 255, 255), blue);

            // para el color amarillo
            if (yellow == null)
                yellow = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC1);

            Core.inRange(newFrameHSV, new Scalar(25, 80, 50), new Scalar(35, 255, 255), yellow);

            // para el color blanco
            if (white == null)
                white = new Mat(subMat.rows(), subMat.cols(), CvType.CV_8UC1);

            Core.inRange(newFrameHSV, new Scalar(0, 0, 190), new Scalar(360, 50, 255), white);

            Core.addWeighted(lowerRed, 1.0, upperRed, 1.0, 0.0, redHue);
            //Imgproc.cvtColor(redHue, image, Imgproc.COLOR_GRAY2RGB);
            //image.copyTo(subMat);

            //Imgproc.cvtColor(blue, image, Imgproc.COLOR_GRAY2RGB);
            //image.copyTo(subMat);

            //Imgproc.cvtColor(yellow, image, Imgproc.COLOR_GRAY2RGB);
            //image.copyTo(subMat);

            //Imgproc.cvtColor(white, image, Imgproc.COLOR_GRAY2RGB);
            //image.copyTo(subMat);

            //Mat all = new Mat(newFrame.rows(), newFrame.cols(), CvType.CV_8UC1);
            //Core.addWeighted(redHue, 1.0, blue, 1.0, 0.0, all);
            //Core.addWeighted(all, 1.0, yellow, 1.0, 0.0, all);
            //Core.addWeighted(all, 1.0, white, 1.0, 0.0, all);

            //Imgproc.cvtColor(all, image, Imgproc.COLOR_GRAY2RGB);
            //image.copyTo(subMat);

            Imgproc.cvtColor(redHue, redtmp, Imgproc.COLOR_GRAY2RGB);
            Imgproc.cvtColor(blue, bluetmp, Imgproc.COLOR_GRAY2RGB);
            Imgproc.cvtColor(yellow, yellowtmp, Imgproc.COLOR_GRAY2RGB);

            Imgproc.rectangle(frameRGB, point1, point2, new Scalar(255, 255, 0), 2);

            if (numMsj != -1)
                switch (numMsj) {
                    case 4:
                        Log.i(TAG, "Mostrando imagen mensaje 4");
                        msjI4.copyTo(matMensaje);
                        break;
                }

            drawNumbers();

            /*Rect r = getBoundingRect(redHue);
            if (r != null) {
                Imgproc.rectangle(frameRGB, new Point(r.x + point1.x, r.y + point1.y), new Point(r.x + r.width + point1.x, r.y + r.height + point1.y), new Scalar(255, 0, 0), 2);
                Mat number = new Mat(image, r);
                Mat numberResize = new Mat();
                Imgproc.resize( number, numberResize, new Size(10,10) );

                if (num  < 50) {
                    Bitmap bmp;
                    //Mat tmp = new Mat(number.rows(), number.cols(), CvType.CV_8UC3);
                    //Imgproc.cvtColor(number, tmp, Imgproc.COLOR_GRAY2RGB);
                    bmp = Bitmap.createBitmap(numberResize.cols(), numberResize.rows(), Bitmap.Config.RGB_565);
                    Utils.matToBitmap(numberResize, bmp);
                    saveImage(bmp, "bmp" + num);
                    num ++;
                }
            }*/
        }

        frameNumber++;

        return frameRGB;
    }

    private void cargarSenales() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + "/images/senales/clase1_explosivos.jpg";
        matSenal = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Imgproc.cvtColor(matSenal, matSenal, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(matSenal, matSenal, new Size(100, 100));

        path = root + "/images/senales/clase3_inflamable.jpg";
        matSenal3 = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Imgproc.cvtColor(matSenal3, matSenal3, Imgproc.COLOR_BGR2RGB);
        Imgproc.resize(matSenal3, matSenal3, new Size(100, 100));
    }

    private void cargarMensajes() {
        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + "/images/mensajes/MensajeI4.jpg";
        msjI4 = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Imgproc.cvtColor(msjI4, msjI4, Imgproc.COLOR_BGR2RGB);
        //Imgproc.resize(msjI4, msjI4, new Size(100, 100));
    }

    private void detectarCaracteristicas() {
        detector = FeatureDetector.create(FeatureDetector.ORB);
        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        keypoints2 = new MatOfKeyPoint();
        descriptors2 = new Mat();

        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + "/images/senales/clase1_explosivos.jpg";
        Mat mat = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        detector.detect(mat, keypoints2);
        descriptor.compute(mat, keypoints2, descriptors2);

        keypoints3 = new MatOfKeyPoint();
        descriptors3 = new Mat();

        path = root + "/images/senales/clase3_inflamable.jpg";
        mat = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        detector.detect(mat, keypoints3);
        descriptor.compute(mat, keypoints3, descriptors3);
    }

    private void drawNumbers() {
        Mat numberResize = new Mat();
        Mat number;

        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + "/images/resultados/";
        Mat mat;

        // rojo
        Rect r = getBoundingRect(redHue);
        if (r != null) {
            Imgproc.rectangle(frameRGB, new Point(r.x + point1.x, r.y + point1.y), new Point(r.x + r.width + point1.x, r.y + r.height + point1.y), new Scalar(255, 0, 0), 2);
            number = new Mat(redtmp, r);

            Imgproc.resize(number, numberResize, new Size(10,10));
            Imgproc.cvtColor(numberResize, numberResize, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(numberResize, numberResize, 127, 255, Imgproc.THRESH_BINARY);

            numberResize.convertTo(numberResize, CvType.CV_32FC1);
            numberResize = numberResize.reshape(1, 1);
            Mat result = new Mat(1, 1, CvType.CV_8U);

            float f = knn.findNearest(numberResize, 3, result);

            Log.i(TAG, "OpenCV knn rojo devuelve " + f);

            if (f >= 0 && f <= 4) {
                mat = Imgcodecs.imread(path + "rojo" + ((int)f) + ".jpg", Imgcodecs.CV_LOAD_IMAGE_COLOR);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
                mat.copyTo(rojoMat);
            }
        }

        // azul
        r = getBoundingRect(blue);
        if (r != null) {
            Imgproc.rectangle(frameRGB, new Point(r.x + point1.x, r.y + point1.y), new Point(r.x + r.width + point1.x, r.y + r.height + point1.y), new Scalar(255, 0, 0), 2);
            number = new Mat(bluetmp, r);

            Imgproc.resize(number, numberResize, new Size(10,10));
            Imgproc.cvtColor(numberResize, numberResize, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(numberResize, numberResize, 127, 255, Imgproc.THRESH_BINARY);

            numberResize.convertTo(numberResize, CvType.CV_32FC1);
            numberResize = numberResize.reshape(1, 1);
            Mat result = new Mat(1, 1, CvType.CV_8U);

            float f = knn.findNearest(numberResize, 3, result);

            Log.i(TAG, "OpenCV knn azul devuelve " + f);

            if (f >= 0 && f <= 4) {
                mat = Imgcodecs.imread(path + "azul" + ((int)f) + ".jpg", Imgcodecs.CV_LOAD_IMAGE_COLOR);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
                mat.copyTo(azulMat);
            }
        }

        // amarillo
        r = getBoundingRect(yellow);
        if (r != null) {
            Imgproc.rectangle(frameRGB, new Point(r.x + point1.x, r.y + point1.y), new Point(r.x + r.width + point1.x, r.y + r.height + point1.y), new Scalar(255, 0, 0), 2);
            number = new Mat(yellowtmp, r);

            Imgproc.resize(number, numberResize, new Size(10,10));
            Imgproc.cvtColor(numberResize, numberResize, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(numberResize, numberResize, 127, 255, Imgproc.THRESH_BINARY);

            numberResize.convertTo(numberResize, CvType.CV_32FC1);
            numberResize = numberResize.reshape(1, 1);
            Mat result = new Mat(1, 1, CvType.CV_8U);

            float f = knn.findNearest(numberResize, 3, result);

            Log.i(TAG, "OpenCV knn amarillo devuelve " + f);

            if (f >= 0 && f <= 4) {
                mat = Imgcodecs.imread(path + "amarillo" + ((int)f) + ".jpg", Imgcodecs.CV_LOAD_IMAGE_COLOR);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB);
                mat.copyTo(amarilloMat);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void saveImage(Bitmap finalBitmap, String name) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/images");
        myDir.mkdirs();

        String fname = name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTxt(List<int[]> data, String filename) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/images/data");
        myDir.mkdirs();

        String fname = filename + ".txt";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();

        try {
            FileWriter writer = new FileWriter(file);

            int size1 = data.size();
            int size2 = data.get(0).length;

            for (int i = 0; i < size1; i++) {
                for (int j = 0; j < size2; j++) {
                    writer.append(Integer.toString(data.get(i)[j]));
                }
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.i(TAG, "Hay un error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void readTrainData() {

        Mat trainData = new Mat(0, 100, CvType.CV_32FC1); // 250 * 100 (50 filas por cada numero, 5 numeros, 100 columnas por fila)
        Mat trainClasses = new Mat(250, 1, CvType.CV_32FC1);
        Mat mat0 = new Mat();
        float[] myint = new float[250];

        String root = Environment.getExternalStorageDirectory().toString();

        for (int i = 0; i < 5; i++) {
            String dir = root + "/images/" + i + "/bmp";

            for (int j = 0; j < 50; j++) {
                String filename = dir + j + ".jpg";

                mat0 = Imgcodecs.imread(filename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

                mat0.convertTo(mat0, CvType.CV_32FC1);
                mat0 = mat0.reshape(1, 1);

                trainData.push_back(mat0);
                myint[i * 50 + j] = (float) i;
            }
        }

        trainClasses.put(0, 0, myint);


        knn = KNearest.create();
        knn.train(trainData, Ml.ROW_SAMPLE, trainClasses);

        /* prueba de predicción */
        Mat result = new Mat(1, 1, CvType.CV_8U);
        Mat test = Imgcodecs.imread(root + "/images/3/bmp1.jpg" , Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        test.convertTo(test, CvType.CV_32FC1);

        test = test.reshape(1, 1);

        float f = knn.findNearest(test, 3, result);

        double[] val = result.get(0,0);

        Log.i(TAG, "OpenCV knn devuelve " + val[0]);
    }

    private void saveTrainData() {
        String root = Environment.getExternalStorageDirectory().toString();

        Mat mat0 = new Mat(10, 10, CvType.CV_8UC1);
        List<int[]> listImages = new ArrayList<>();

        for (int f = 0; f < 50; f++) {
            String name = root + "/images/4/bmp" + f + ".jpg";
            mat0 = Imgcodecs.imread(name, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            Imgproc.threshold(mat0, mat0, 127, 255, Imgproc.THRESH_BINARY);

            int[] arrayInt = new int[100];

            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++) {
                    double[] val = mat0.get(i, j);
                    arrayInt[i * 10 + j] = val[0] == 255.0 ? 1 : 0;  // 0 negro 1 blanco
                }

            listImages.add(arrayInt);
        }

        saveTxt(listImages, "train4");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainActivity_show_camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.wbch.proyectopds_test/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "MainActivity_show_camera Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.wbch.proyectopds_test/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
