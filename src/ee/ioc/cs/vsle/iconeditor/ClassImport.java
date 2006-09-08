package ee.ioc.cs.vsle.iconeditor;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

public class ClassImport {
	ArrayList <String> pc;
	ArrayList <IconClass>icons;
	ShapeGroup shapeList;
	IconClass classIcon;
	ArrayList <IconPort> ports;
	ArrayList <ClassField> fields;
	
/*
 * Takes the package file and gives a list of class names and a list of classes information
 */
	public ClassImport(File file,  ArrayList <String> packageClasses, ArrayList <IconClass> icons){
		
		this.pc = packageClasses;
		this.icons = icons;
		DefaultHandler handler = new ClassHandler();
		icons.clear();
		packageClasses.clear();
		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setValidating(true);
		
		try {
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(file, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	class ClassHandler extends DefaultHandler {
		boolean inClass = false;
		boolean inName = false;
		boolean inGraphics = false;
		boolean inPort = false;
		boolean inDesc = false;
		boolean inIcon = false;
		
		
		public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) throws SAXException {
			InputSource is = null;
			if (systemId != null && systemId.endsWith("dtd")) {
				is = new InputSource(System.getProperty("user.dir") 
                		+ System.getProperty("file.separator")
                		+ RuntimeProperties.PACKAGE_DTD);
			}
			return is;
		}
	
		public void startElement(String namespaceURI, String lName, String qName,
			 Attributes attrs) throws SAXException {
			String element = qName;
			
			int x, y, w, h, col; // x, y, width, height, colour 
			int x2, y2;
			int st = 1; // stroke
			int lt = 0; // linetype
			int tr = 255; //transparency
			String strVal;
			int startAngle, arcAngle;
			boolean filled, fixed, strict;
			String name = null;
			String type = null;
			String value = null;
			String fontName = null;
			String fontStyle = null;
			int fontSize = 10;
							
			if(element.equals("class")){
				inClass = true;
				classIcon = new IconClass();
				ports = new ArrayList<IconPort>();
				fields = new ArrayList<ClassField>();
				shapeList = new ShapeGroup(new ArrayList<Shape>());
			}
			if(element.equals("name") && inClass){
				inName = true;
			}
			if(element.equals("description") && inClass){
				inDesc = true;
			}
			if(element.equals("graphics")){
				inGraphics = true;
			}
			if(element.equals("icon")){
				inIcon = true;
			}
			if (element.equals("rect")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Integer.parseInt(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Integer.parseInt(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Rect rect = new Rect(x, y, w, h, col, filled, st, tr, lt);
				rect.setFixed(fixed);
				shapeList.add(rect);
			}else if(element.equals("oval")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Integer.parseInt(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Integer.parseInt(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Oval oval = new Oval(x, y, w, h, col, filled, st, tr, lt);
				oval.setFixed(fixed);
				shapeList.add(oval);
			}else if(element.equals("line")){
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				
				x = getCoordinate(attrs.getValue("x1"));
				y = getCoordinate(attrs.getValue("y1"));
				x2 = getCoordinate(attrs.getValue("x2"));
				y2 = getCoordinate(attrs.getValue("y2"));
				
				col = Integer.parseInt(attrs.getValue("colour"));
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Integer.parseInt(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Integer.parseInt(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				
				Line line = new Line(x, y, x2, y2, col, st, tr, lt);
				line.setFixed(fixed);
				shapeList.add(line);
			}else if(element.equals("arc")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				col = Integer.parseInt(attrs.getValue("colour"));
				filled = Boolean.parseBoolean(attrs.getValue("filled"));
				
				strVal = attrs.getValue("stroke");
				if (strVal != null)
					st = Integer.parseInt(strVal);
				strVal = attrs.getValue("lineType");
				if (strVal != null)
					lt = Integer.parseInt(strVal);
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				
				
				startAngle = Integer.parseInt(attrs.getValue("startAngle"));
				arcAngle = Integer.parseInt(attrs.getValue("arcAngle"));
				
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Arc arc = new Arc(x, y, w, h, startAngle, arcAngle, col, filled, st, tr, lt);
				arc.setFixed(fixed);
				shapeList.add(arc);
			}else if (element.equals("bounds")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				w = Integer.parseInt(attrs.getValue("width"));
				h = Integer.parseInt(attrs.getValue("height"));
				classIcon.setMax(w,h);
				BoundingBox b = new BoundingBox(x, y, w, h);
				classIcon.boundingbox = b;
				shapeList.add(b);
			}else if(element.equals("dot")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				col = Integer.parseInt(attrs.getValue("colour"));
				st = Integer.parseInt(attrs.getValue("stroke"));
				tr = Integer.parseInt(attrs.getValue("transparency"));
				fixed = Boolean.parseBoolean(attrs.getValue("fixed"));
				Dot dot = new Dot(x, y, col, st, tr);
				dot.setFixed(fixed);
				shapeList.add(dot);
			}else if(element.equals("text")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				col = Integer.parseInt(attrs.getValue("colour"));
				fontSize = Integer.parseInt(attrs.getValue("fontsize"));
				fontName = attrs.getValue("fontname");
				fontStyle = attrs.getValue("fontstyle");
				
				strVal = attrs.getValue("transparency");
				if (strVal != null)
					tr = Integer.parseInt(strVal);
				String textStr = attrs.getValue("string");
				
				Font font = null;

				if (fontStyle.equalsIgnoreCase("0"))
					font = new Font(fontName, Font.PLAIN, fontSize);
				else if (fontStyle.equalsIgnoreCase("1"))
					font = new Font(fontName, Font.BOLD, fontSize);
				else if (fontStyle.equalsIgnoreCase("2")) font = new Font(fontName, Font.ITALIC, fontSize);
				if (font != null) {
					Text text = new Text(x, y, font, new Color(col), tr, textStr);
					shapeList.add(text);
				}
				
			}else if(element.equals("port")){
				x = getCoordinate(attrs.getValue("x"));
				y = getCoordinate(attrs.getValue("y"));
				name = attrs.getValue("name");
				boolean isAreaConn = Boolean.parseBoolean(attrs.getValue("isAreaConn"));
				strict= Boolean.parseBoolean(attrs.getValue("strict"));
				
				IconPort port = new IconPort(name, x, y, isAreaConn, strict);
				ports.add(port);
			}else if (element.equals("field")){
				
				name = attrs.getValue("name");
				type = attrs.getValue("type");
				value = attrs.getValue("value");
				ClassField field = new ClassField (name, type, value);
				fields.add(field);
			}
			
		}
		public void endElement(String namespaceURI, String sName, String qName) throws
		SAXException {
			String element = qName;
			if (element.equals("class")){
				inClass = false;
				classIcon.shapeList = shapeList;
				classIcon.ports = ports;
				classIcon.fields = fields;
				icons.add(classIcon);
			}
			if(element.equals("name")){
				inName = false;
			}
			if(element.equals("graphics")){
				inGraphics = false;
			}
			if(element.equals("description") && inClass){
				inDesc = false;
			}
			if(element.equals("icon")){
				inIcon = false;
			}
				
		}
		
		public void characters(char[] ch, int start, int length)
		throws SAXException {
			if (inName) {
				pc.add(new String(ch,start,length));
				classIcon.setName(new String(ch,start,length));
			}
			if (inDesc) 
				classIcon.setDescription(new String(ch,start,length));
			if (inIcon)
				classIcon.setIconName(new String(ch,start,length));
		   
		}
		/*
		 * Removes either r or f or both from the end of the
		 * coordinate
		 */
		public int getCoordinate(String c) {
			while (c.length() > 1 && (((c.charAt(c.length() - 1)) == 'f') || ((c.charAt(c.length() - 1)) == 'r'))){
				c = c.substring(0,c.length() - 1);
			}
			return Integer.parseInt(c);
		}
	
	}

}
