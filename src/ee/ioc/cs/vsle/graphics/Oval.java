package ee.ioc.cs.vsle.graphics;

import java.io.*;

import java.awt.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

public class Oval extends Shape implements Serializable {

  /**
   * Color of the oval.
   */
  Color color;

  /**
   * Transparency of the oval.
   */
  float transparency = (float) 1.0;

  /**
   * Name of the shape.
   */
  String name;

  /**
   * Indicates if the oval is drawn filled or not.
   */
  boolean filled = false;

  /**
   * Boolean value indicating if the shape is selected or not.
   */
  private boolean selected = false;

  /**
   * Boolean value indicating if the shape is drawn antialiased or not.
   */
  private boolean antialiasing = true;

  /**
   * Shape graphics.
   */
  Graphics2D g2;

  /**
   * Percentage for resizing, 1 means real size.
   */
  private float size = 1;

  /**
   * Alpha value of a color, used
   * for defining the transparency of a filled shape.
   */
  private float alpha;

  /**
   * Rotation angle in degrees.
   */
  double rotation = 0.0;

  /**
   * Line weight, logically equals to stroke width.
   */
  private float lineWeight;

  /**
   * Shape constructor.
   * @param x int - x coordinate of the shape.
   * @param y int - y coordinate of the shape.
   * @param width int - width of the shape.
   * @param height int - height of the shape.
   * @param colorInt int - color of the shape.
   * @param fill boolean - the shape is filled or not.
   * @param strokeWidth double - shape line weight.
   * @param transp double - shape transparency percentage.
   */
  public Oval(int x, int y, int width, int height, int colorInt, boolean fill,
			  double strokeWidth, double transp) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	this.color = new Color(colorInt);
	this.filled = fill;
	this.transparency = (float) transp;
	setStrokeWidth(strokeWidth);
  } // Oval

  public String toString() {
	return getName();
  } // toString

  public String getName() {
	return this.name;
  } // getName

  public int getRealHeight() {
	return (int) (getHeight() * getSize());
  } // getRealHeight

  public int getRealWidth() {
	return (int) (getWidth() * getSize());
  } // getRealWidth

  public float getSize() {
	return this.size;
  } // getSize

  public void setAntialiasing(boolean b) {
	this.antialiasing = b;
  } // setAntialiasing

  public boolean contains(int pointX, int pointY) {
	if (pointX > x + size && pointY > y + size && pointX < x + width + size && pointY < y + height + size) {
		return true;
	}
	return false;
  } // contains

  public void setName(String s) {
	this.name = s;
  } // setName

  public void setMultSize(float s) {
	this.size = getSize() * s;
  } // setMultSize

  public void setSelected(boolean b) {
	this.selected = b;
  } // setSelected

  public void setPosition(int x, int y) {
	this.x = x;
	this.y = y;
  } // setPosition

  public boolean isInside(int x1, int y1, int x2, int y2) {
	if (x1 > x && y1 > y && x2 < x + (int) (size * width) && y2 < y + (int) (size * height)) {
		return true;
	}
	return false;
  } // isInside

  public boolean isInsideRect(int x1, int y1, int x2, int y2) {
	if (x1 < x && y1 < y && x2 > x + (int) (size * width) && y2 > y + (int) (size * height)) {
		return true;
	}
	return false;
  } // isInsideRect

  public void setStrokeWidth(double width) {
	try {
	  if (width >= 0.0) {
		this.lineWeight = (float) width;
	  }
	  else {
		throw new Exception("Stroke width undefined or negative.");
	  }
	}
	catch (Exception e) {
	  e.printStackTrace();
	}
  } // setStrokeWidth

  /**
   * Set the color of a shape.
   * @param col Color - color of a shape.
   */
  public void setColor(Color col) {
	this.color = col;
  } // setColor

  /**
   * Set the percentage of transparency.
   * @param transparencyPercentage double - the percentage of transparency.
   */
  public void setTransparency(double transparencyPercentage) {
	this.transparency = (float) transparencyPercentage;
  } // setTransparency

  /**
   * Returns the color of the oval.
   * @return Color - color of the oval.
   */
  public Color getColor() {
	return this.color;
  } // getColor

  /**
   * Returns a boolean value representing if the shape is filled or not.
   * @return boolean - a boolean value representing if the shape is filled or not.
   */
  public boolean isFilled() {
	return this.filled;
  } // isFilled

  /**
   * Returns the stroke with of a shape.
   * @return double - stroke width of a shape.
   */
  public double getStrokeWidth() {
	return this.lineWeight;
  } // getStrokeWidth

  /**
   * Returns the transparency of the shape.
   * @return double - the transparency of the shape.
   */
  public double getTransparency() {
	return this.transparency;
  } // getTransparency

  public int getX() {
	return x;
  } // getX

  public int getY() {
	return y;
  } // getY

  int getWidth() {
	return width;
  } // getWidth

  int getHeight() {
	return height;
  } // getHeight

  /**
   * Resizes current object.
   * @param deltaW int - change of object with.
   * @param deltaH int - change of object height.
   * @param cornerClicked int - number of the clicked corner.
   */
  public void resize(int deltaW, int deltaH, int cornerClicked) {

	if (cornerClicked == 1) { // TOP-LEFT
	  if ( (this.width - deltaW) > 0 && (this.height - deltaH) > 0) {
		this.x += deltaW;
		this.y += deltaH;
		this.width -= deltaW;
		this.height -= deltaH;
	  }
	}
	else if (cornerClicked == 2) { // TOP-RIGHT
	  if ( (this.width + deltaW) > 0 && (this.height - deltaH) > 0) {
		this.y += deltaH;
		this.width += deltaW;
		this.height -= deltaH;
	  }
	}
	else if (cornerClicked == 3) { // BOTTOM-LEFT
	  if ( (this.width - deltaW) > 0 && (this.height + deltaH) > 0) {
		this.x += deltaW;
		this.width -= deltaW;
		this.height += deltaH;
	  }
	}
	else if (cornerClicked == 4) { // BOTTOM-RIGHT
	  if ( (this.width + deltaW) > 0 && (this.height + deltaH) > 0) {
		this.width += deltaW;
		this.height += deltaH;
	  }
	}
  } // resize

  /**
   * Return a specification of the shape to be written into a file in XML format.
   * @param boundingboxX - x coordinate of the bounding box.
   * @param boundingboxY - y coordinate of the bounding box.
   * @return String - specification of a shape.
   */
  public String toFile(int boundingboxX, int boundingboxY) {
	String fill = "false";

	if (filled) {
	  fill = "true";
	}
	int colorInt = 0;

	if (color != null) {
	  colorInt = color.getRGB();
	}
	return "<oval x=\"" + (x - boundingboxX) + "\" y=\""
		+ (y - boundingboxY) + "\" width=\"" + width + "\" height=\"" + height
		+ "\" colour=\"" + colorInt + "\" filled=\"" + fill + "\"/>";
  } // toFile

  public String toText() {
	String fill = "false";

	if (filled) {
	  fill = "true";
	}

	int colorInt = 0;

	if (color != null) {
	  colorInt = color.getRGB();
	}
   return "OVAL:"+x+":"+y+":"+width+":"+height+":"+colorInt+":"+fill+":"+(int)this.lineWeight+":"+(int)this.transparency;
  } // toText

  /**
   * Returns the number representing a corner the mouse was clicked in.
   * 1: top-left, 2: top-right, 3: bottom-left, 4: bottom-right.
   * Returns 0 if the click was not in the corner.
   * @param pointX int - mouse x coordinate.
   * @param pointY int - mouse y coordinate.
   * @return int - corner number the mouse was clicked in.
   */
  public int controlRectContains(int pointX, int pointY) {
	if ( (pointX >= x) && (pointY >= y)) {
	  if ( (pointX <= x + 4) && (pointY <= y + 4)) {
		return 1;
	  }
	}
	if ( (pointX >= x + (int) (size * (width)) - 4) && (pointY >= y)) {
	  if ( (pointX <= x + (int) (size * (width))) && (pointY <= y + 4)) {
		return 2;
	  }
	}
	if ( (pointX >= x) && (pointY >= y + (int) (size * (height)) - 4)) {
	  if ( (pointX <= x + 4) && (pointY <= y + (int) (size * (height)))) {
		return 3;
	  }
	}
	if ( (pointX >= x + (int) (size * (width)) - 4)
		&& (pointY >= y + (int) (size * (height)) - 4)) {
	  if ( (pointX <= x + (int) (size * (width)))
		  && (pointY <= y + (int) (size * (height)))) {
		return 4;
	  }
	}
	return 0;
  } // controlRectContains

  /**
   * Draw the selection markers if object selected.
   */
  public void drawSelection() {
	g2.setColor(Color.black);
	g2.setStroke(new BasicStroke( (float) 1.0));
	g2.fillRect(x, y, 4, 4);
	g2.fillRect(x + (int) (size * width) - 4, y, 4, 4);
	g2.fillRect(x, y + (int) (size * height) - 4, 4, 4);
	g2.fillRect(x + (int) (size * width) - 4, y + (int) (size * height) - 4,
			   4, 4);
  } // drawSelection

  public boolean isSelected() {
	return this.selected;
  } // isSelected

  public void draw(int xModifier, int yModifier, float Xsize, float Ysize,
				   Graphics g) {

	g2 = (Graphics2D) g;

	g2.setStroke(new BasicStroke(this.lineWeight));

	alpha = (float) (1 - (this.transparency / 100));

	float red = (float) color.getRed() * 100 / 256 / 100;
	float green = (float) color.getGreen() * 100 / 256 / 100;
	float blue = (float) color.getBlue() * 100 / 256 / 100;

	g2.setColor(new Color(red, green, blue, alpha));

	if (RuntimeProperties.isAntialiasingOn) {
	  g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
						  java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
	}

	if (filled) {
	  g2.fillOval(xModifier + (int) (Xsize * x),
				  yModifier + (int) (Ysize * y), (int) (Xsize * width),
				  (int) (Ysize * height));
	}
	else {
	  g2.drawOval(xModifier + (int) (Xsize * x),
				  yModifier + (int) (Ysize * y), (int) (Xsize * width),
				  (int) (Ysize * height));
	}
	if (selected) {
	  drawSelection();
	}

  } // draw

}
