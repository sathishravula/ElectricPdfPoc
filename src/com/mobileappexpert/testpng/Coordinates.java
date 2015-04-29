package com.mobileappexpert.testpng;

/**
 * Created by ehc on 29/4/15.
 */
public class Coordinates {
 private float x;
  private float y;
  private int id;

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "Coordinates{" +
        "x=" + x +
        ", y=" + y +
        ", id=" + id +
        '}';
  }
}
