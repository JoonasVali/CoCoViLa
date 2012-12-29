package ee.ioc.cs.vsle.layout;

import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.joonasvali.graps.graph.Node;
import ee.joonasvali.graps.layout.Layout;
import ee.joonasvali.graps.layout.forcelayout.ForceLayout;
import ee.joonasvali.graps.layout.forcelayout.UpdateListener;

public class LayoutManager {
	private ObjectList objects;
	private Layout layout;	
	private GraphAdapter graph;
	public LayoutManager(ObjectList objects, ConnectionList connections){		
		this(new ForceLayout(), objects, connections);	
	}
	
	public LayoutManager(Layout layout, ObjectList objects, ConnectionList connections){
		for(Connection con: connections){
			con.removeAllBreakPoints();
		}
		this.objects = objects;
		graph = new GraphAdapter();		
		graph.apply(objects, connections);
		this.layout = layout;	
		this.layout.addListener(makeListener());
	}
	
	private UpdateListener makeListener() {
		return new UpdateListener() {			
			@Override
			public void update() {
				for(GObj obj: objects){
					Node node = graph.getNode(obj);						
					obj.setX(node.getLocation().x);
					obj.setY(node.getLocation().y);					
				}				
				Editor.getInstance().getCurrentCanvas().repaint();
			}
		};
  }

	public void execute(){		
		
		layout.execute(graph);		
	}
	
	public void stop(){
		layout.stop();
	}
	
	
	
	
	
	
}	
