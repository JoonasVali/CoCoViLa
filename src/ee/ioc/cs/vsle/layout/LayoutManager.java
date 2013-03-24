package ee.ioc.cs.vsle.layout;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ee.ioc.cs.vsle.editor.Canvas;
import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.Point;
import ee.joonasvali.graps.graph.Graph;
import ee.joonasvali.graps.graph.Node;
import ee.joonasvali.graps.graph.Port;
import ee.joonasvali.graps.layout.Layout;
import ee.joonasvali.graps.layout.forcelayout.ForceLayout;
import ee.joonasvali.graps.layout.forcelayout.ForceLayoutConfiguration;
import ee.joonasvali.graps.layout.forcelayout.UpdateListener;
import ee.joonasvali.graps.simulator.Simulator;

public class LayoutManager {	
	private ObjectList objects;
	private ConnectionList connections;
	private Layout layout;
	private ForceLayoutConfiguration configuration;	
	private GraphAdapter graph;
	private Canvas canvas;
	private Map<Port, Connection> connectionReference;

	public LayoutManager(ObjectList objects, ConnectionList connections,
	    Canvas canvas) {		
		this(new ForceLayout(), objects, connections, canvas);
		this.configuration = ((ForceLayout)layout).getConfiguration();
	}

	public LayoutManager(Layout layout, ObjectList objects,
	    ConnectionList connections, Canvas canvas) {
		this.canvas = canvas;
		this.connections = connections;
		this.objects = objects;
		graph = new GraphAdapter();
		connectionReference = graph.getConnectionReferences();
		
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
	
	public void applyBreakpoints(){
		for (Connection con : connections) {
			con.removeAllBreakPoints();
		}
		HashSet<Port> done = new HashSet<Port>();
		for (Node n : graph.getNodes()) {
			for(Port p : n.getPorts()){
				if(done.add(p)){
					if(p.getPort() != null){
						done.add(p.getPort());
						Connection con = connectionReference.get(p);
						if(con == null){
							con = connectionReference.get(p.getPort());
						}
						if(con != null){
							List<java.awt.Point> breakpoints = p.getBreakpoints();							
							if(breakpoints.isEmpty()){
								breakpoints = p.getPort().getBreakpoints();								
							}
							for(java.awt.Point point : breakpoints){
								Point adapter = new Point(point.x, point.y);								
								con.addBreakPoint(adapter);								
							}
						}
					}
				}
			}
		}
	}

	public void stop() {
		layout.stop();
	}

	public void setObjects(ObjectList objectList) {
		this.objects = objectList;
	}
	
	public ForceLayoutConfiguration getConfiguration(){
		return configuration;
	}
	
	public Graph getGraph(){
		return graph;
	}
	
	public ForceLayout getLayout(){
		return (ForceLayout)layout;
	}

	public void setConnections(ConnectionList connectionList) {
		this.connections = connectionList;
	}

}
