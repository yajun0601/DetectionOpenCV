package org.opencv.samples.imagemanipulations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BaseActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG                 = "OCV::Activity";

    public static final int      VIEW_MODE_RGBA      = 0;
    public static final int      VIEW_MODE_HIST      = 1;
    public static final int      VIEW_MODE_CANNY     = 2;
    public static final int      VIEW_MODE_SEPIA     = 3;
    public static final int      VIEW_MODE_SOBEL     = 4;
    public static final int      VIEW_MODE_ZOOM      = 5;
    public static final int      VIEW_MODE_PIXELIZE  = 6;
    public static final int      VIEW_MODE_POSTERIZE = 7;

    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewHist;
    private MenuItem             mItemPreviewCanny;
    private MenuItem             mItemPreviewSepia;
    private MenuItem             mItemPreviewSobel;
    private MenuItem             mItemPreviewZoom;
    private MenuItem             mItemPreviewPixelize;
    private MenuItem             mItemPreviewPosterize;
    private CameraBridgeViewBase mOpenCvCameraView;
    private SurfaceHolder 		 holder;
    private Size                 mSize0;

    private Mat                  mIntermediateMat;
    private Mat                  mMat0;
    private MatOfInt             mChannels[];
    private MatOfInt             mHistSize;
    private int                  mHistSizeNum = 25;
    private MatOfFloat           mRanges;
    private Scalar               mColorsRGB[];
    private Scalar               mColorsHue[];
    private Scalar               mWhilte;
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mSepiaKernel;

    private SeekBar		param1_bar;
    private SeekBar		param2_bar;
    private double threshold1 = 80, threshold2 = 90;
    public static int           viewMode = VIEW_MODE_RGBA;
    
	private ImageView imageView = null;
	private Bitmap bmp = null;
	private TextView m_text = null;
	private String path = null;
	private String svmpath = null;
	private String annpath = null;
	private String imgpath = null;
	static {
	    if (!OpenCVLoader.initDebug()) {
	    } else {
	        System.loadLibrary("imagepro");
	    }
	}
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
	        		//mOpenCvCameraView.enableFpsMeter();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public BaseActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.image_manipulations_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        holder = mOpenCvCameraView.getHolder();
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.plate_locate);
		imageView.setImageBitmap(bmp);
		path = Environment.getExternalStorageDirectory().getAbsolutePath();

		svmpath = path+"/svm.xml";
		annpath = path+"/ann.xml";
		imgpath = path+"/plate_locate.jpg";
		
        param1_bar= (SeekBar) findViewById(R.id.param1_bar);
        param2_bar= (SeekBar) findViewById(R.id.param2_bar);
        final Button btn_capture = (Button) findViewById(R.id.btn_capture);
        final Button btn_canny = (Button) findViewById(R.id.btn_canny);
        final Button btn_calc = (Button) findViewById(R.id.btn_calc);
        final Button btn_save = (Button) findViewById(R.id.btn_save);

        btn_capture.setOnClickListener(new CaptureListener());
        btn_canny.setOnClickListener(new CannyListener());
        btn_calc.setOnClickListener(new CalcListener());
        btn_save.setOnClickListener(new SaveListener());
        param1_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				threshold1 = arg1;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}});
        param2_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
				// TODO Auto-generated method stub
				threshold2 = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}});
    }

    class CaptureListener implements View.OnClickListener
    {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
	        if (mOpenCvCameraView != null){
		        Log.d(TAG, "mOpenCvCameraView enabled:" + mOpenCvCameraView.isEnabled());
	        	if(mOpenCvCameraView.isEnabled())
	        		mOpenCvCameraView.disableView();
	        	else{
	                //mOpenCvCameraView.setCvCameraViewListener((CvCameraViewListener2) this);

	        		mOpenCvCameraView.enableView();
	        		mOpenCvCameraView.invalidate();
	        		mOpenCvCameraView.setEnabled(true);
	        		mOpenCvCameraView.surfaceChanged(holder, 0, 0, 0);
	        		
	        		//mOpenCvCameraView.enableFpsMeter();
	        	}

	        }
	        Log.d(TAG, "mOpenCvCameraView:"+mOpenCvCameraView);
		}
    	
    }
    Mat rgbaInnerWindow;
    class CannyListener implements View.OnClickListener
    {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//viewMode = VIEW_MODE_CANNY;
			//rgbaInnerWindow = rgba.submat(1, rows -1, 0, cols);
            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, threshold1, threshold2);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            //mOpenCvCameraView
            //Imgproc.contourArea(rgbaInnerWindow);
		}
    	
    }
    class CalcListener implements View.OnClickListener
    {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    private static String getTimeStr()
    {
    	long time=System.currentTimeMillis();
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String t=format.format(new Date(time));
        Log.d(TAG,"time:" + t);
        
        return t;
    	
    }
    class SaveListener implements View.OnClickListener
    {
		   // 保存到 sdcard
    	public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
            File f = new File( Environment.getExternalStorageDirectory() +"/opencv/"+bitName + ".jpg");
            FileOutputStream fOut = null;
            try {
                    fOut = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            }
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                    fOut.flush();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            try {
                    fOut.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			//cvSaveImage
			
			Bitmap bmp = null;
			bmp = Bitmap.createBitmap(rgbaInnerWindow.cols(), rgbaInnerWindow.rows(), Config.RGB_565);
			Utils.matToBitmap(rgbaInnerWindow, bmp);
			//savePic(bmp); 

			saveMyBitmap(bmp,getTimeStr());
			//cvCreateCameraCapture
		}
		

    	
    }
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemPreviewRGBA  = menu.add("Preview RGBA");
        mItemPreviewHist  = menu.add("Histograms");
        mItemPreviewCanny = menu.add("Canny");
//        mItemPreviewSepia = menu.add("Sepia");
//        mItemPreviewSobel = menu.add("Sobel");
//        mItemPreviewZoom  = menu.add("Zoom");
//        mItemPreviewPixelize  = menu.add("Pixelize");
//        mItemPreviewPosterize = menu.add("Posterize");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
        if (item == mItemPreviewHist)
            viewMode = VIEW_MODE_HIST;
        else if (item == mItemPreviewCanny)
            viewMode = VIEW_MODE_CANNY;
//        else if (item == mItemPreviewSepia)
//            viewMode = VIEW_MODE_SEPIA;
//        else if (item == mItemPreviewSobel)
//            viewMode = VIEW_MODE_SOBEL;
//        else if (item == mItemPreviewZoom)
//            viewMode = VIEW_MODE_ZOOM;
//        else if (item == mItemPreviewPixelize)
//            viewMode = VIEW_MODE_PIXELIZE;
//        else if (item == mItemPreviewPosterize)
//            viewMode = VIEW_MODE_POSTERIZE;
        return true;
    }

    public void onCameraViewStarted(int width, int height) {
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };
        mWhilte = Scalar.all(255);
        mP1 = new Point();
        mP2 = new Point();

        // Fill sepia kernel
        mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);
    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mIntermediateMat != null)
            mIntermediateMat.release();

        mIntermediateMat = null;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        rgbaInnerWindow = rgba.clone();
        
		System.out.println("entering the jni");
		long startTime = System.currentTimeMillis();

		byte[] resultByte = CarPlateDetection.ImageProc(imgpath,svmpath,annpath);
		System.out.println("proc time :" + (System.currentTimeMillis()-startTime));
		
		String result = null;
		try {
			result = new String(resultByte,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
		m_text.setText(result);
		
/*
        Size sizeRgba = rgba.size();

        

        int rows = (int) sizeRgba.height;
        int cols = (int) sizeRgba.width;
//两边各留 1/8 
        int left = cols / 8;
        int top = rows / 8;

        int width = cols * 3 / 4;
        int height = rows * 3 / 4;

        switch (BaseActivity.viewMode) {
        case BaseActivity.VIEW_MODE_RGBA:
            break;

        case BaseActivity.VIEW_MODE_HIST:
            Mat hist = new Mat();
            int thikness = (int) (sizeRgba.width / (mHistSizeNum + 10) / 5);
            if(thikness > 5) thikness = 5;
            int offset = (int) ((sizeRgba.width - (5*mHistSizeNum + 4*10)*thikness)/2);
            // RGB
            for(int c=0; c<3; c++) {
                Imgproc.calcHist(Arrays.asList(rgba), mChannels[c], mMat0, hist, mHistSize, mRanges);
                Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
                hist.get(0, 0, mBuff);
                for(int h=0; h<mHistSizeNum; h++) {
                    mP1.x = mP2.x = offset + (c * (mHistSizeNum + 10) + h) * thikness;
                    mP1.y = sizeRgba.height-1;
                    mP2.y = mP1.y - 2 - (int)mBuff[h];
                    Core.line(rgba, mP1, mP2, mColorsRGB[c], thikness);
                }
            }
            // Value and Hue
            Imgproc.cvtColor(rgba, mIntermediateMat, Imgproc.COLOR_RGB2HSV_FULL);
            // Value
            Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[2], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for(int h=0; h<mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (3 * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height-1;
                mP2.y = mP1.y - 2 - (int)mBuff[h];
                Core.line(rgba, mP1, mP2, mWhilte, thikness);
            }
            // Hue
            Imgproc.calcHist(Arrays.asList(mIntermediateMat), mChannels[0], mMat0, hist, mHistSize, mRanges);
            Core.normalize(hist, hist, sizeRgba.height/2, 0, Core.NORM_INF);
            hist.get(0, 0, mBuff);
            for(int h=0; h<mHistSizeNum; h++) {
                mP1.x = mP2.x = offset + (4 * (mHistSizeNum + 10) + h) * thikness;
                mP1.y = sizeRgba.height-1;
                mP2.y = mP1.y - 2 - (int)mBuff[h];
                Core.line(rgba, mP1, mP2, mColorsHue[h], thikness);
            }
            break;

        case BaseActivity.VIEW_MODE_CANNY:
            //rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            rgbaInnerWindow = rgba.submat(1, rows -1, 0, cols);
            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, threshold1, threshold2);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            //Imgproc.contourArea(rgbaInnerWindow);
            //rgbaInnerWindow.release();
            break;

        case BaseActivity.VIEW_MODE_SOBEL:
            Mat gray = inputFrame.gray();
            Mat grayInnerWindow = gray.submat(top, top + height, left, left + width);
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.Sobel(grayInnerWindow, mIntermediateMat, CvType.CV_8U, 1, 1);
            Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
            Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            grayInnerWindow.release();
            rgbaInnerWindow.release();
            break;

        case BaseActivity.VIEW_MODE_SEPIA:
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Core.transform(rgbaInnerWindow, rgbaInnerWindow, mSepiaKernel);
            rgbaInnerWindow.release();
            break;

        case BaseActivity.VIEW_MODE_ZOOM:
            Mat zoomCorner = rgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);
            Mat mZoomWindow = rgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);
            Imgproc.resize(mZoomWindow, zoomCorner, zoomCorner.size());
            Size wsize = mZoomWindow.size();
            Core.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0, 255), 2);
            zoomCorner.release();
            mZoomWindow.release();
            break;

        case BaseActivity.VIEW_MODE_PIXELIZE:
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.resize(rgbaInnerWindow, mIntermediateMat, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);
            Imgproc.resize(mIntermediateMat, rgbaInnerWindow, rgbaInnerWindow.size(), 0., 0., Imgproc.INTER_NEAREST);
            rgbaInnerWindow.release();
            break;

        case BaseActivity.VIEW_MODE_POSTERIZE:
            
            //Imgproc.cvtColor(rgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
            //Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 5, 50);
            //Imgproc.cvtColor(mIntermediateMat, rgbaInnerWindow, Imgproc.COLOR_RGB2RGBA);
            
            rgbaInnerWindow = rgba.submat(top, top + height, left, left + width);
            Imgproc.Canny(rgbaInnerWindow, mIntermediateMat, 80, 90);
            rgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);
            Core.convertScaleAbs(rgbaInnerWindow, mIntermediateMat, 1./16, 0);
            Core.convertScaleAbs(mIntermediateMat, rgbaInnerWindow, 16, 0);
            rgbaInnerWindow.release();
            break;
        }
*/
        return rgba;
    }
}
