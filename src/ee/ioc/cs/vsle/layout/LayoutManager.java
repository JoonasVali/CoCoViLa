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
	private ConnectionList connections;
	private Layout layout;	
	private GraphAdapter graph;
	public LayoutManager(ObjectList objects, ConnectionList connections){		
		this(new ForceLayout(), objects, connections);	
	}
	
	public LayoutManager(Layout layout, ObjectList objects, ConnectionList connections){
		this.connections = connections;
		this.objects = objects;				
		graph = new GraphAdapter();		
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
		for(Connection con: connections){
			con.removeAllBreakPoints();
		}
		
		graph.apply(this.objects, this.connections);
		layout.execute(graph);		
	}
	
	public void stop(){
		layout.stop();
	}

	public void setObjects(ObjectList objectList) {
	  this.objects = objectList;	  
  }

	public void setConnections(ConnectionList connectionList) {
	  this.connections = connectionList;	  
  }
	
	
	
	
	
	
}	
