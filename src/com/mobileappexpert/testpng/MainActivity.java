package com.mobileappexpert.testpng;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements OnClickListener {

  Context context = MainActivity.this;

  int[] assembly = {R.drawable.swch, R.drawable.cable,
      R.drawable.ceilingfan, R.drawable.ic_launcher};
  private static final String DEBUG_TAG = "Main";

  int selected = -1;
  int selectedImage = 0;
  int selectedImageRt = 0;

  ArrayList<HashMap<String, Double>> placeAssemblies;
  private RelativeLayout relativeLayout;
  private View removeView;
  private SharedPreferences pref;
  private SharedPreferences.Editor editor;
  HashMap<Integer, Integer> iconMapping = new HashMap<Integer, Integer>();
  private ZoomableRelativeLayout root;
  private ScaleGestureDetector scaleGestureDetector;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pref = getSharedPreferences("data", MODE_MULTI_PROCESS);
    editor = pref.edit();
    placeAssemblies = new ArrayList<HashMap<String, Double>>();
    findViewById(R.id.img1).setOnClickListener(this);
    findViewById(R.id.img11).setOnClickListener(this);
    findViewById(R.id.img2).setOnClickListener(this);
    findViewById(R.id.img3).setOnClickListener(this);
    findViewById(R.id.img6).setOnClickListener(this);
    findViewById(R.id.img4).setOnClickListener(onClickListener);
    findViewById(R.id.img5).setOnClickListener(onClickListener);

    relativeLayout = (RelativeLayout) findViewById(R.id.layLayerImage);
//    root = (ZoomableRelativeLayout) findViewById(R.id.root);
//    scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());
//    applyZoom();
    relativeLayout.setOnTouchListener(onTouchListener);
  }

  private void applyZoom() {
    root.setOnTouchListener(new OnTouchListener() {

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        scaleGestureDetector.onTouchEvent(event);
        Log.d("test123", "onTouch");
        return true;
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    switch (id) {
      case R.id.action_settings:
        break;
      case R.id.save:
        int count = relativeLayout.getChildCount();
        ArrayList<Coordinates> arrayList = new ArrayList<Coordinates>();
        for (int index = 0; index < count; index++) {
          View view = relativeLayout.getChildAt(index);
          Coordinates coordinates = new Coordinates();
          coordinates.setX(view.getX());
          coordinates.setY(view.getY());
          int id1 = iconMapping.get(view.getId());
          Log.d("test123", "iconMapping:" + iconMapping);
          coordinates.setId(id1);
          arrayList.add(coordinates);
        }
        editor.putString("data", new Gson().toJson(arrayList)).commit();
        break;
      case R.id.clear:
        relativeLayout.removeAllViews();
        placeAssemblies.clear();
        iconMapping.clear();
        break;
      case R.id.render:
        String json = pref.getString("data", "");
        Log.d("test123", "json" + json);
        if (json != null && !json.isEmpty()) {
          ArrayList<Coordinates> coordinates = new Gson().fromJson(json, new TypeToken<ArrayList<Coordinates>>() {
          }.getType());
          for (Coordinates coordinates1 : coordinates) {
            renderIcon(coordinates1);
          }
        }
        break;
//      case R.id.zoom:
//        if (item.getTitle().toString().equalsIgnoreCase("enableZoom")) {
//          relativeLayout.setOnTouchListener(null);
//          applyZoom();
//          item.setTitle("disableZoom");
//        }else{
//          relativeLayout.setOnTouchListener(onTouchListener);
//          root.setOnTouchListener(null);
//          item.setTitle("enableZoom");
//        }

    }

    return super.onOptionsItemSelected(item);
  }

  private void renderIcon(Coordinates coordinates1) {
    ImageView img = new ImageView(context);
    img.setLayoutParams(new LayoutParams(70, 70));
    img.setImageResource(assembly[coordinates1.getId()]);

    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
        70, 70);
    params.leftMargin = (int) coordinates1.getX();
    params.topMargin = (int) coordinates1.getY();
    int key = 1000 + placeAssemblies.size();
    img.setId(key);

    HashMap<String, Double> hashMap = new HashMap<String, Double>();
    hashMap.put("key", (double) key);
    hashMap.put("left", (double) coordinates1.getX());
    hashMap.put("top", (double) coordinates1.getY());
    placeAssemblies.add(hashMap);
    img.setTag(placeAssemblies.size() - 1);
    relativeLayout.addView(img, params);
    iconMapping.put(key, coordinates1.getId());
    img.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Toast.makeText(context, "Test",
            Toast.LENGTH_SHORT).show();
        removeView = v;
        if (selectedImage == 0) {
          selectedImage = v.getId();
          selectedImageRt = v.getId();

          v.setBackgroundResource(R.drawable.bg);
        } else {

          findViewById(selectedImage)
              .setBackgroundResource(
                  R.drawable.bg_white);

          selectedImage = 0;
          selectedImageRt = 0;
        }

      }
    });
  }

  @Override
  public void onClick(View v) {
    // TODO ImageView Click Events for Assemblies

    switch (v.getId()) {
      case R.id.img1:

        selected = 0;
        selectedImageRt = 0;
        break;
      case R.id.img11:

        selected = 1;
        selectedImageRt = 0;
        break;
      case R.id.img2:

        selected = 2;
        selectedImageRt = 0;
        break;
      case R.id.img3:

        selected = 3;
        selectedImageRt = 0;
        break;

      case R.id.img6:
        if (removeView != null) {
          relativeLayout.removeView(removeView);
          selectedImage = 0;
          selectedImageRt = 0;
          int index = (Integer) removeView.getTag();
          Log.d("test123", "size:" + placeAssemblies.size() + "    tag:" + index);
          placeAssemblies.remove(index);
          removeView = null;
          Log.d("test123", "size after:" + placeAssemblies.size());
        } else {
          Toast.makeText(getApplicationContext(), "Please select icon to remove", Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }
   /* if (selectedImage != 0 && selected != -1) {
      ((ImageView) findViewById(selectedImage))
          .setImageResource(assembly[selected]);
      selectedImage = 0;
      selected = -1;
    }*/


  }

  OnClickListener onClickListener = new OnClickListener() {

    @Override
    public void onClick(View v) {

      switch (v.getId()) {

        case R.id.img4:
          selected = -1;
          if (selectedImageRt != 0) {
            rotate((ImageView) findViewById(selectedImageRt), -90);

          }
          selectedImage = 0;
          break;
        case R.id.img5:

          selected = -1;
          if (selectedImageRt != 0) {
            rotate((ImageView) findViewById(selectedImageRt), 90);

          }
          selectedImage = 0;
          break;

        default:
          break;
      }


    }
  };


  private void rotate(ImageView imgview, float degree) {

    Matrix matrix = new Matrix();
    matrix.postScale(imgview.getScaleX(), imgview.getScaleY());
    matrix.postRotate(degree);

    try {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) imgview
          .getDrawable();

      Bitmap bitmap = bitmapDrawable.getBitmap();

      Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
          bitmap.getWidth(), bitmap.getHeight(), matrix, true);
      imgview.setImageBitmap(resizedBitmap);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  int[][] array;
  OnTouchListener onTouchListener = new OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      // TODO Touch Events for Assembly to Places
      switch (event.getAction()) {
        case (MotionEvent.ACTION_DOWN):
          if (selected != -1) {

            Log.d(DEBUG_TAG, "Action was DOWN");
            Toast.makeText(context,
                "X:Y: " + event.getX() + ":" + event.getY(),
                Toast.LENGTH_SHORT).show();
            ImageView img = new ImageView(context);
            img.setLayoutParams(new LayoutParams(70, 70));
            img.setImageResource(assembly[selected]);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                70, 70);
            params.leftMargin = (int) event.getX();
            params.topMargin = (int) event.getY();
            int key = 1000 + placeAssemblies.size();
            img.setId(key);

            double left = event.getX();
            double top = event.getY();

            HashMap<String, Double> hashMap = new HashMap<String, Double>();
            hashMap.put("key", (double) key);
            hashMap.put("left", left);
            hashMap.put("top", top);

            boolean fChCreate = false;
            for (int i = 0; i < placeAssemblies.size(); i++) {
              HashMap<String, Double> hashMap2 = placeAssemblies
                  .get(i);

              Rect r1 = new Rect((int) left, (int) top,
                  (int) left + 70, (int) top + 70);

              double left1;
              double top1;
              left1 = hashMap2.get("left");
              top1 = hashMap2.get("top");
              Rect r2 = new Rect((int) left1, (int) top1,
                  (int) left1 + 70, (int) top1 + 70);

              fChCreate = r1.intersect(r2);
              if (fChCreate) {
                Log.d("test123", "already icon placed");
                break;
              }


            }
            if (!fChCreate) {
              placeAssemblies.add(hashMap);
              img.setTag(placeAssemblies.size() - 1);
              ((RelativeLayout) v).addView(img, params);
              iconMapping.put(key, selected);
              img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                  Toast.makeText(context, "Test",
                      Toast.LENGTH_SHORT).show();
                  removeView = v;
                  if (selectedImage == 0) {
                    selectedImage = v.getId();
                    selectedImageRt = v.getId();

                    v.setBackgroundResource(R.drawable.bg);
                  } else {

                    findViewById(selectedImage)
                        .setBackgroundResource(
                            R.drawable.bg_white);

                    selectedImage = 0;
                    selectedImageRt = 0;
                  }

                }
              });
            }
            selected = -1;

          }
          return true;
        case (MotionEvent.ACTION_MOVE):
          Log.d(DEBUG_TAG, "Action was MOVE");
          return true;
        case (MotionEvent.ACTION_UP):
          Log.d(DEBUG_TAG, "Action was UP");
          return true;
        case (MotionEvent.ACTION_CANCEL):
          Log.d(DEBUG_TAG, "Action was CANCEL");
          return true;
        case (MotionEvent.ACTION_OUTSIDE):
          Log.d(DEBUG_TAG, "Movement occurred outside bounds "
              + "of current screen element");
          return true;
        default:
          return onTouchEvent(event);
      }
    }
  };


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
