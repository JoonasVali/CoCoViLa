package ee.ioc.cs.vsle.graphics;

import java.io.*;
import java.util.*;

import java.awt.*;
import ee.ioc.cs.vsle.vclass.*;

public abstract class Shape implements Serializable, Cloneable {

  public int x;
  public int y;

  int difWithMasterX;
  int difWithMasterY;

  public int width;
  public int height;

  String className;

  public ArrayList ports = new ArrayList();
  public ArrayList fields = new ArrayList();

  public ClassGraphics graphics;

  //public abstract void drawSelection();

  public Shape() {
  }

  public Shape(int x, int y, int width, int height, String name) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.className = name;
  }

  public void setFont(Font f) {
  }

  public void setText(String s) {
  }

  public String getText() {
	return null;
  }

  public Font getFont() {
	return null;
  }

  public int getStartX() {
	return 0;
  }

  public int getStartY() {
	return 0;
  }

  public int getStartAngle() {
	return 0;
  }

  public int getArcAngle() {
	return 0;
  }

  public int getEndX() {
	return 0;
  }

  public int getEndY() {
	return 0;
  }

  public ArrayList getShapes() {
   return null;
 }

  public abstract boolean isFilled();

  public abstract boolean isSelected();

  public abstract boolean isInside(int x1, int y1, int x2, int y2);

  public abstract boolean isInsideRect(int x1, int y1, int x2, int y2);

  public abstract void setTransparency(double transparencyPercentage);

  public abstract void setLineType(int lineType);

  public abstract void setColor(Color col);

  public abstract void setStrokeWidth(double strokeWidth);

  public abstract void setMultSize(float s1, float s2);

  public abstract void setSelected(boolean b);

  public abstract void setName(String name);

  public abstract void setPosition(int x, int y);

  public abstract int getX();

  public abstract int getY();

  public abstract Color getColor();

  public abstract String getName();

  public abstract int getRealWidth();

  public abstract int getRealHeight();

  public abstract double getStrokeWidth();

  public abstract double getTransparency();

  public abstract int getLineType();

  public abstract void resize(int deltaW, int deltaH, int cornerClicked);

  public abstract int controlRectContains(int pointX, int pointY);

  public abstract void draw(int x, int y, float Xsize, float Ysize, Graphics g);

  public abstract String toFile(int boundingboxX, int boundingboxY);

  public abstract String toText();

  public abstract String toString();

  public abstract boolean contains(int pointX, int pointY);

  public abstract void setFixed(boolean b);

  public abstract boolean isFixed();

  public Object clone() {
	try {
	  Shape shape = (Shape)super.clone();

	  shape.ports = (ArrayList) ports.clone();

	  shape.fields = (ArrayList) fields.clone();
	  //deep clone each separate field
	  ClassField field;
	  for (int i = 0; i < fields.size(); i++) {
		field = (ClassField) fields.get(i);
		shape.fields.set(i, field.clone());
	  }

	  return shape;
	}
	catch (CloneNotSupportedException e) {
	  e.printStackTrace();
	  return null;
	}
  } // clone

}