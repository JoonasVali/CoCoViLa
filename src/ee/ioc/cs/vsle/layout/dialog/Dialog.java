package ee.ioc.cs.vsle.layout.dialog;


import java.util.IdentityHashMap;

import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.layout.LayoutManager;
import ee.ioc.cs.vsle.vclass.Scheme;

public class Dialog {
	private static IdentityHashMap<Canvas, Dialog> dialogs = new IdentityHashMap<Canvas, Dialog>();
	private Canvas canvas;
	private boolean run = false;
	private LayoutManager manager;
	
	public Dialog(Canvas canvas) {
	  this.canvas = canvas;
	  Scheme scheme = canvas.getScheme();
	  manager = new LayoutManager(scheme.getObjectList(), scheme.getConnectionList());
  }

	public static Dialog getInstance(Canvas canvas){
		Dialog dialog = dialogs.get(canvas);		
		if(dialog == null){
			dialog = new Dialog(canvas);
			dialogs.put(canvas, dialog);
		}
		return dialog;		
	}
	
	public void launch(){
		run = !run;		
		if(run){			
			manager.execute();
		} else {
			manager.stop();
		}
	}
}
