package ee.ioc.cs.vsle.layout;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.ConnectionList;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.Port;
import ee.joonasvali.graps.graph.Graph;
import ee.joonasvali.graps.graph.Node;


public class GraphAdapter extends Graph {
	Map<GObj, Node> collection;
	
	public GraphAdapter() {
		super(Collections.<Node> emptyList());		
  }

	public void apply(ObjectList objects, ConnectionList connections){				
		Map<GObj, Node> collection = getNodes(objects, connections);
		setNodes(collection.values());				
	}
	
	public Node getNode(GObj obj){
		return collection.get(obj);
	}
	
	private Map<GObj, Node> getNodes(ObjectList objects, ConnectionList connections) {
		collection = new HashMap<GObj, Node>();		
		for(Connection con: connections){
			ee.joonasvali.graps.graph.Port nodePort = null, nodePort2 = null;
			Port begin =con.getBeginPort();
  		Port end = con.getEndPort();
  		GObj beginObj = begin.getObject();
  		Node beginNode;  		
  		if(!collection.containsKey(beginObj)){
  			beginNode = gObjToNode(beginObj);
  			collection.put(beginObj, gObjToNode(beginObj));
  		} else {
  			beginNode = collection.get(beginObj);
  		}
  		nodePort = new ee.joonasvali.graps.graph.Port(new Point(begin.getAbsoluteX(), begin.getAbsoluteY())); 		
  		nodePort.setNode(beginNode);
  		beginNode.addPort(nodePort);
  		
  		Node endNode;
  		GObj endObj = end.getObject();
  		if(!collection.containsKey(endObj)){
  			endNode = gObjToNode(endObj);
  			collection.put(endObj, gObjToNode(endObj));
  		} else {
  			endNode = collection.get(endObj);
  		}
  		  		
  		nodePort2 = new ee.joonasvali.graps.graph.Port(new Point(end.getAbsoluteX(), end.getAbsoluteY()));
  		nodePort2.setNode(endNode);
  		nodePort2.setPort(nodePort);
  		nodePort.setPort(nodePort2);
  		
  		endNode.addPort(nodePort2);  				
  	}
		
		for(GObj obj : objects){	  	
			if(!collection.containsKey(obj)){
				collection.put(obj, gObjToNode(obj));
			}
	  }	  		
	  return collection;
  }
	
	private static Node gObjToNode(GObj obj){		
		Point size = new Point(obj.getWidth(), obj.getHeight());		
  	Point location = new Point(obj.getX(), obj.getY());
  	Node node = new Node(location, size);	  	
  	return node;
	}

}

