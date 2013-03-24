package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;

public class Point extends java.awt.Point 
	implements Serializable {
	
  private static final long serialVersionUID = 1L;	

	public Point(int x, int y) {
		super(x, y);		
	}

  @Override
  public String toString() {
    return "(" + x + "," + y + ")";
  }
}
