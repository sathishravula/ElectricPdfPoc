package com.mobileappexpert.testpng;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements OnClickListener, View.OnDragListener {

  private static final String LOGCAT = "test111";
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
  private RelativeLayout root;
  private ScaleGestureDetector scaleGestureDetector;
  public int currentX, currentY;


  float value = 1;
  float x = 0;
  float y = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    pref = getSharedPreferences("data", MODE_MULTI_PROCESS);
    editor = pref.edit();
    placeAssemblies = new ArrayList<HashMap<String, Double>>();
    findViewById(R.id.switch_image).setOnClickListener(this);
    findViewById(R.id.cabel).setOnClickListener(this);
    findViewById(R.id.fan).setOnClickListener(this);
    findViewById(R.id.ic_launcher).setOnClickListener(this);
    findViewById(R.id.cross).setOnClickListener(this);

    Button buttonZoomOut = (Button) findViewById(R.id.buttonZoomOut);
    Button buttonZoomIn = (Button) findViewById(R.id.buttonZoomIn);
    buttonZoomIn.setOnClickListener(this);
    buttonZoomOut.setOnClickListener(this);


    relativeLayout = (RelativeLayout) findViewById(R.id.planLayout);
    root = (RelativeLayout) findViewById(R.id.root);

//    scaleGestureDetector = new ScaleGestureDetector(this, new OnPinchListener());
//    applyZoom();
    relativeLayout.setOnTouchListener(onTouchListener);
    relativeLayout.setOnDragListener(this);
  }


  /**
   * zooming is done from here
   */
  public void zoom(Float scaleX, Float scaleY, PointF pivot) {
    relativeLayout.setPivotX(pivot.x);
    relativeLayout.setPivotY(pivot.y);
    relativeLayout.setScaleX(scaleX);
    relativeLayout.setScaleY(scaleY);
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
          Log.d("test123", "iconMapping:" + iconMapping);
          Log.d("test123", "id:" + view.getId());
          Integer id1 = iconMapping.get(view.getId());
          if (id1 != null) {
            coordinates.setId(id1);
            arrayList.add(coordinates);
          }
        }
        editor.putString("data", new Gson().toJson(arrayList)).commit();
        convertImage();
        break;
      case R.id.clear:
//        relativeLayout.removeAllViews();
        for (int index = 1; index < relativeLayout.getChildCount(); index++)
          relativeLayout.removeViewAt(index);
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
    }

    return super.onOptionsItemSelected(item);
  }

  private void convertImage() {
    Image image = null;
    Document document = new Document();
    String directoryPath = Environment.getExternalStorageDirectory().toString();
    File newPdfFile = new File(directoryPath, "plan.pdf");
    try {
      PdfWriter.getInstance(document, new FileOutputStream(newPdfFile));
      document.open();
    } catch (FileNotFoundException fileNotFoundException) {
      Log.d("test11", "# Exception caz of fileOutputStream : " + fileNotFoundException);
    } catch (DocumentException documentException) {
      Log.d("test11", "# Exception caz of document.add : " + documentException);
    }


    try {
      Bitmap bitmap = loadBitmapFromView(relativeLayout, relativeLayout.getWidth(), relativeLayout.getHeight());
//      Bitmap.createScaledBitmap(bitmap, 4960, 7016, false);
      bitmap = Bitmap.createScaledBitmap(bitmap, 500, 800, false);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
      byte[] byteArray = stream.toByteArray();
      image = Image.getInstance(byteArray);
      document.add(image);
//            document.setMargins(8,8,8,8);
      Log.d("test11", "document created");
//        document.add(image);
    } catch (DocumentException documentException) {
      Log.d("test11", "# Exception because of document.add : " + documentException);
    } catch (MalformedURLException e) {
      e.printStackTrace();
      Log.d("test11", "# Exception because of document.add : " + e);
    } catch (IOException e) {
      e.printStackTrace();
    }
    document.close();
  }

  public static Bitmap loadBitmapFromView(View v, int width, int height) {
    Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas c = new Canvas(b);
//    v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
    v.draw(c);
    return b;
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
    img.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        relativeLayout.removeView(v);
        selectedImage = 0;
        selectedImageRt = 0;
        int index = (Integer) v.getTag();
        placeAssemblies.remove(index);
        return false;
      }
    });
    /*img.setOnClickListener(new OnClickListener() {

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
    });*/
  }

  @Override
  public void onClick(View v) {
    // TODO ImageView Click Events for Assemblies

    switch (v.getId()) {
      case R.id.switch_image:

        selected = 0;
        selectedImageRt = 0;
        break;
      case R.id.cabel:

        selected = 1;
        selectedImageRt = 0;
        break;
      case R.id.fan:

        selected = 2;
        selectedImageRt = 0;
        break;
      case R.id.ic_launcher:

        selected = 3;
        selectedImageRt = 0;
        break;

      case R.id.buttonNormal:
        x = 0;
        y = 0;
        zoom(1f, 1f, new PointF(x, y));
        break;
      case R.id.buttonZoomIn:
        value++;
        zoom(value, value, new PointF(currentX, currentY));
        break;
      case R.id.buttonZoomOut:
        if (value > 1) {
          value--;
          zoom(value, value, new PointF(currentX, currentY));
        }
        break;
      case R.id.left:
        x = x - 50;
        zoom(value, value, new PointF(x, y));
        break;
      case R.id.right:
        x = x + 50;
        zoom(value, value, new PointF(x, y));
        break;
      case R.id.top:
        y = y + 50;
        zoom(value, value, new PointF(x, y));
        break;
      case R.id.bottom:
        y = y - 50;
        zoom(value, value, new PointF(x, y));
        break;
      case R.id.cross:
        if (removeView != null) {
          relativeLayout.removeView(removeView);
          selectedImage = 0;
          selectedImageRt = 0;
          int index = (Integer) removeView.getTag();
          try {
            placeAssemblies.remove(index);
          } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
          }
        }
        break;
    }
  }

  public boolean onDrag(View layoutview, DragEvent dragevent) {
    int action = dragevent.getAction();
    switch (action) {
      case DragEvent.ACTION_DRAG_STARTED:
        Log.d(LOGCAT, "Drag event started");
        break;
      case DragEvent.ACTION_DRAG_ENTERED:
        Log.d(LOGCAT, "Drag event entered into " + layoutview.toString());
        break;
      case DragEvent.ACTION_DRAG_EXITED:
        Log.d(LOGCAT, "Drag event exited from " + layoutview.toString());
        break;
      case DragEvent.ACTION_DROP:
        Log.d(LOGCAT, "Dropped");
        View view = (View) dragevent.getLocalState();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = (int) dragevent.getY() - view.getHeight() / 2;
        params.leftMargin = (int) dragevent.getX() - view.getWidth() / 2;
        Log.d(LOGCAT, "X4:" + dragevent.getX() + "  Y4:" + dragevent.getY());
        view.setLayoutParams(params);
        view.setVisibility(View.VISIBLE);
//        Log.d(LOGCAT, "marginTop:" + ((int) dragevent.getY() - view.getHeight() / 2) + "  marginLeft:" + ((int) dragevent.getX() - view.getWidth() / 2));
//        Log.d(LOGCAT, "ViewX:" + view.getX() + "  ViewY:" + view.getY());
//        Log.d(LOGCAT, "ViewWidth:" + view.getWidth() + "  ViewHeight:" + view.getHeight());
        view.setBackgroundColor(Color.RED);
        break;
      case DragEvent.ACTION_DRAG_ENDED:
        Log.d(LOGCAT, "Drag ended");
        break;
      default:
        break;
    }
    return true;
  }

  private final class DragTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View view, MotionEvent event) {
     /* Log.d(LOGCAT, "MeasuredWidth:" + view.getScaleX() + "MeasuredHeight:" + view.getScaleY());
      Log.d(LOGCAT, "Width:" + view.getWidth() + "Height:" + view.getHeight());
//      RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//          (int)(70*value), (int)(70*value));
//      view.setLayoutParams(params);
      View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
      view.startDrag(null, shadowBuilder, view, 0);
      view.setVisibility(View.INVISIBLE);*/
      return true;
    }
  }

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
  private boolean isLongPress;
  OnTouchListener onTouchListener = new OnTouchListener() {

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      // TODO Touch Events for Assembly to Places
      switch (event.getAction()) {
        case (MotionEvent.ACTION_DOWN):
          currentX = (int) event.getRawX();
          currentY = (int) event.getRawY();
          Log.d(LOGCAT, "X:" + event.getX() + "  Y:" + event.getY());
          if (selected != -1) {

            Log.d(DEBUG_TAG, "Action was DOWN");
            Toast.makeText(context,
                "X:Y: " + event.getX() + ":" + event.getY(),
                Toast.LENGTH_SHORT).show();

            ImageView img = new ImageView(context);
//            img.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            img.setImageResource(assembly[selected]);
//            img.setOnTouchListener(new DragTouchListener());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                70, 70);
            params.leftMargin = (int) event.getX() - 35;
            params.topMargin = (int) event.getY() - 35;
            int key = 1000 + placeAssemblies.size();
            img.setId(key);

            double left = event.getX();
            double top = event.getY();

            HashMap<String, Double> hashMap = new HashMap<String, Double>();
            hashMap.put("key", (double) key);
            hashMap.put("left", left);
            hashMap.put("top", top);

            boolean fChCreate = false;
//            for (int i = 0; i < placeAssemblies.size(); i++) {
//              HashMap<String, Double> hashMap2 = placeAssemblies
//                  .get(i);
//
//              Rect r1 = new Rect((int) left, (int) top,
//                  (int) left + 70, (int) top + 70);
//
//              double left1;
//              double top1;
//              left1 = hashMap2.get("left");
//              top1 = hashMap2.get("top");
//              Rect r2 = new Rect((int) left1, (int) top1,
//                  (int) left1 + 70, (int) top1 + 70);
//
//              fChCreate = r1.Intersect(r2);
//              if (fChCreate) {
//                Log.d("test123", "already icon placed");
//                break;
//              }
//
//
//            }
            if (!fChCreate) {
              placeAssemblies.add(hashMap);
              img.setTag(placeAssemblies.size() - 1);
              ((RelativeLayout) v).addView(img, params);
              iconMapping.put(key, selected);
              img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                  View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                  v.startDrag(null, shadowBuilder, v, 0);
                  v.setVisibility(View.INVISIBLE);
                  return true;
                  /*isLongPress = true;
                  relativeLayout.removeView(v);
                  selectedImage = 0;
                  selectedImageRt = 0;
                  int index = (Integer) v.getTag();
                  try {
                    placeAssemblies.remove(index);
                  } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                  }
                  return true;*/
                }
              });
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
          if (!isLongPress) {
            int x2 = (int) event.getRawX();
            int y2 = (int) event.getRawY();
            Log.d(LOGCAT, "X1:" + currentX + "  Y1:" + currentY);
            Log.d(LOGCAT, "X2:" + x2 + "  Y2:" + y2);
            Log.d(LOGCAT, "X3:" + event.getX() + "  Y3:" + event.getY());
            relativeLayout.scrollBy(currentX - x2, currentY - y2);
            Log.d(LOGCAT, "getLeft:" + relativeLayout.getScrollX() + "  getRight:" + relativeLayout.getScrollY());


            currentX = x2;
            currentY = y2;
          }

          Log.d(DEBUG_TAG, "Action was MOVE");
          return true;
        case (MotionEvent.ACTION_UP):
          isLongPress = false;
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
}
