package ee.ioc.cs.vsle.layout;

import java.awt.Color;

import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.joonasvali.graps.graph.Node;
import ee.joonasvali.graps.layout.Layout;
import ee.joonasvali.graps.layout.forcelayout.ForceLayout;
import ee.joonasvali.graps.layout.forcelayout.UpdateListener;
import ee.joonasvali.graps.simulator.Simulator;

public class LayoutManager {
	private ObjectList objects;
	private ConnectionList connections;
	private Layout layout;
	private GraphAdapter graph;
	private Canvas canvas;	

	public LayoutManager(ObjectList objects, ConnectionList connections,
	    Canvas canvas) {
		this(new ForceLayout(), objects, connections, canvas);
	}

	public LayoutManager(Layout layout, ObjectList objects,
	    ConnectionList connections, Canvas canvas) {
		this.canvas = canvas;
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
				for (GObj obj : objects) {
					try {
						Node node = graph.getNode(obj);
						obj.setX(node.getLocation().x);
						obj.setY(node.getLocation().y);						
						canvas.getGraphics().setColor(Color.RED);						
					}
					catch (NullPointerException e) {
						System.out.println(obj + " not available in the layout");
					}
				}
				canvas.repaint();
			}
		};
	}

	public void execute() {	
		for (Connection con : connections) {
			con.removeAllBreakPoints();
		}

		graph.apply(this.objects, this.connections);
		layout.execute(graph);		
		
		/* FOR DEBUGGING */
		//new Simulator(graph, false);
	}

	public void stop() {
		layout.stop();
	}

	public void setObjects(ObjectList objectList) {
		this.objects = objectList;
	}

	public void setConnections(ConnectionList connectionList) {
		this.connections = connectionList;
	}

}
