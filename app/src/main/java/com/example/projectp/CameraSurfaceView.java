package com.example.projectp;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    SurfaceHolder holder;
    Camera camera = null;

    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    private void init(Context context){
        holder = getHolder();
        holder.addCallback(this);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        camera = Camera.open();
        Camera.Parameters params = camera.getParameters();
        params.set("camera-id",1);
        params.setPreviewFpsRange(15000, 30000);
        params.setPreviewSize(1280, 720);
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        params.setPreviewFormat(ImageFormat.NV21);
        params.setPreviewFrameRate(30);
        camera.setDisplayOrientation(90);
        camera.setParameters(params);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.startPreview();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
    public void capture(Camera.PictureCallback callback){
        if(camera != null)
            camera.takePicture(null, null, callback);
    }
}
