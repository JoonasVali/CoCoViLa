package ee.ioc.cs.vsle.layout.dialog;


import java.util.IdentityHashMap;

import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.layout.LayoutManager;
import ee.ioc.cs.vsle.vclass.Scheme;

public class DialogManager {
	private static IdentityHashMap<Canvas, DialogManager> dialogs = new IdentityHashMap<Canvas, DialogManager>();	
	private LayoutManager manager;
	
	public DialogManager(Canvas canvas) {	  
	  Scheme scheme = canvas.getScheme();	  
	  manager = new LayoutManager(scheme.getObjectList(), scheme.getConnectionList(), canvas);
  }

	public static DialogManager getInstance(Canvas canvas){
		DialogManager dialog = dialogs.get(canvas);		
		if(dialog == null){
			dialog = new DialogManager(canvas);
			dialogs.put(canvas, dialog);
		}
		return dialog;		
	}
	
	public void launch(Scheme scheme){		
		new LayoutDialog(scheme, manager);		
	}
}
