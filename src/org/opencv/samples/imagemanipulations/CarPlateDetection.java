package org.opencv.samples.imagemanipulations;


public class CarPlateDetection {
	public static native byte[] ImageProc(String imgpath, String svmpath, String annpath);
    /* A native method that is implemented by the
     * 'hello-jni' native library, which is packaged
     * with this application.
     */
    public native static String  stringFromJNI();

}
