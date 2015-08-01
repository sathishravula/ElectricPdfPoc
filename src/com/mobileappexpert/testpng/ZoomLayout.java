package com.mobileappexpert.testpng;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ehc on 30/4/15.
 */
public class ZoomLayout extends Activity implements View.OnClickListener {
  View mainView = null;
  float value=1;
  float x=0;
  float y=0;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.zoom);
    mainView =findViewById(R.id.linearLayout2);

    Button buttonZoomOut = (Button)findViewById(R.id.buttonZoomOut);
    Button buttonNormal = (Button)findViewById(R.id.buttonNormal);
    Button buttonZoomIn = (Button)findViewById(R.id.buttonZoomIn);
    Button right = (Button)findViewById(R.id.right);
    Button left = (Button)findViewById(R.id.left);
    Button top = (Button)findViewById(R.id.top);
    Button bottom = (Button)findViewById(R.id.bottom);


    buttonNormal.setOnClickListener(this);
    buttonZoomIn.setOnClickListener(this);
    buttonZoomOut.setOnClickListener(this);
    top.setOnClickListener(this);
    bottom.setOnClickListener(this);
    left.setOnClickListener(this);
    right.setOnClickListener(this);


  }

  /** zooming is done from here */
  public void zoom(Float scaleX,Float scaleY,PointF pivot){
    mainView.setPivotX(pivot.x);
    mainView.setPivotY(pivot.y);
    mainView.setScaleX(scaleX);
    mainView.setScaleY(scaleY);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.buttonNormal:
        x=0;y=0;
        zoom(1f,1f,new PointF(x,y));
        break;
      case R.id.buttonZoomIn:
        value++;
        zoom(value,value,new PointF(x,y));
        break;
      case R.id.buttonZoomOut:
        if(value>1) {
          value--;
          zoom(value, value, new PointF(x, y));
        }
        break;
      case R.id.left:
        x=x-50;
        zoom(value,value,new PointF(x,y));
        break;
      case R.id.right:
        x=x+50;
        zoom(value,value,new PointF(x,y));
        break;
      case R.id.top:
        y=y+50;
        zoom(value,value,new PointF(x,y));
        break;
      case R.id.bottom:
        y=y-50;
        zoom(value,value,new PointF(x,y));
        break;
    }
  }
}
