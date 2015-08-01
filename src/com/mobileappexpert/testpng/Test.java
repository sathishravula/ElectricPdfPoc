package com.mobileappexpert.testpng;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by ehc on 30/4/15.
 */
public class Test extends Activity {
  private ZoomableRelativeLayout root;
  private ScaleGestureDetector scaleGestureDetector;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.test);
    root = (ZoomableRelativeLayout) findViewById(R.id.root);
    scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());
    applyZoom();
  }

  private void applyZoom() {
    root.setOnTouchListener(new View.OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        scaleGestureDetector.onTouchEvent(event);
        return true;
      }
    });
  }

  private class OnPinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

    float startingSpan;
    float endSpan;
    float startFocusX;
    float startFocusY;


    public boolean onScaleBegin(ScaleGestureDetector detector) {
      startingSpan = detector.getCurrentSpan();
      startFocusX = detector.getFocusX();
      startFocusY = detector.getFocusY();
      return true;
    }


    public boolean onScale(ScaleGestureDetector detector) {
      root.scale(detector.getCurrentSpan() / startingSpan, startFocusX, startFocusY);
      return true;
    }

    public void onScaleEnd(ScaleGestureDetector detector) {
      root.restore();
    }
  }

}
