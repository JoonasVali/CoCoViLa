package ee.ioc.cs.vsle.packageparse;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.graphics.*;

import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.synthesize.SpecParser;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.Font;
import java.awt.Color;
import java.util.ArrayList;
import ee.ioc.cs.vsle.editor.RuntimeProperties;


public class PackageParser {
	VPackage pack = new VPackage();

	PackageClass newClass;
	Port newPort;
	ClassField newField;
	ClassGraphics newGraphics;
	String element;
	String path;
	ArrayList classFields;
	Polygon polygon;
	ArrayList polyXs = new ArrayList();
	ArrayList polyYs = new ArrayList();
	final int CLASS = 1, PORT_OPEN = 2, PORT_CLOSED = 3, PACKAGE = 4, FIELD = 5, FIELD_KNOWN = 6;

	int status;

	/**
	 * Class constructor.
	 * @param file File - package file.
	 */
	public PackageParser(File file) {

		// Use an instance of ourselves as the SAX event handler
		DefaultHandler handler = new PackageHandler();


		// Use the validating parser
		SAXParserFactory factory = SAXParserFactory.newInstance();

		factory.setValidating(true);
		path = file.getParent();

		try {
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();

			saxParser.parse(file, handler);
		} catch (SAXException sxe) {
			// Error generated by this application (or a parser-initialization error)
			Exception x = sxe;

			if (sxe.getException() != null) {
				x = sxe.getException();
			}
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (IOException ioe) {
			// I/O error
			ioe.printStackTrace();
		}
		db.p("doneparsing");
	} // PackageParser

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	class PackageHandler extends DefaultHandler {

		public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) throws SAXException {
			InputSource is = null;
			if (systemId != null && systemId.endsWith("dtd"))
				is = new InputSource(RuntimeProperties.PACKAGE_DTD);
			return is;
		}

		public void error(SAXParseException spe) {
			// Error generated by the parser

			db.p(
				"\n** Parsing error, line " + spe.getLineNumber() + ", uri "
				+ spe.getSystemId());
			//db.p("   " + spe.getMessage());

			// Use the contained exception, if any
			Exception x = spe;

			if (spe.getException() != null) {
				x = spe.getException();
			}
			x.printStackTrace();
		}

		public void setDocumentLocator(Locator l) {// Save this to resolve relative URIs or to give diagnostics.
		}

		public void startDocument() throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		public void startElement(String namespaceURI, String lName, String qName,
								 Attributes attrs) throws SAXException {
			element = qName;
			if (element.equals("package")) {
				status = PACKAGE;
			}
			if (element.equals("class")) {
				newClass = new PackageClass();
				status = CLASS;
				String type = attrs.getValue("type");
				if (type != null && type.equals("relation")) {
					newClass.relation = true;
				}
			}
			if (element.equals("graphics")) {
				newGraphics = new ClassGraphics();
				String showFields = attrs.getValue("showFields");
				String type = attrs.getValue("type");

				if (showFields != null && showFields.equals("true")) {
					newGraphics.showFields = true;
				}
				if (type != null && type.equals("relation")) {
					newGraphics.relation = true;
				}
			}

			if (element.equals("port")) {
				status = PORT_OPEN;
				String id = attrs.getValue("id");
				String name = attrs.getValue("name");
				String type = attrs.getValue("type");
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String portConnection = attrs.getValue("portConnection");
				String strict = attrs.getValue("strict");

				newPort = new Port(name, type, Integer.parseInt(x),
					Integer.parseInt(y), portConnection, strict);
				newPort.id = id;
			}
			if (element.equals("open")) {
				status = PORT_OPEN;
			}
			if (element.equals("default")) {
				status = FIELD;
			}
			if (element.equals("known")) {
				status = FIELD_KNOWN;
			}


			if (element.equals("closed")) {
				status = PORT_CLOSED;
			}

			if (element.equals("fields")) {
				classFields = new ArrayList();

			}

			if (element.equals("polygon")) {
				//initiate them to gather all X and Y positions there.
				makeInitialPolygon(attrs);

			}

			if (element.equals("point")) {
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				polyXs.add(x);
				polyYs.add(y);
			}


			if (element.equals("field")) {
				String name = attrs.getValue("name");
				String type = attrs.getValue("type");
				String value = attrs.getValue("value");
				String desc = attrs.getValue("description");

				newField = new ClassField(name, type, value, desc);
				classFields.add(newField);
			}
			if (element.equals("text")) {
				Text newText = makeText(attrs, newGraphics);


				/*if (str.equals("*self"))
					newText.name = "self";
				else if (str.equals("*selfWithName"))
					newText.name = "selfName";*/
				newGraphics.addShape(newText);

			}

			if (element.equals("line")) {
				Line newLine = makeLine(attrs, newGraphics);

				newGraphics.addShape(newLine);
			}


			if (element.equals("rect")) {
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String width = attrs.getValue("width");
				String height = attrs.getValue("height");
				String color = attrs.getValue("colour");
				String filled = attrs.getValue("filled");
				String stroke = attrs.getValue("stroke");
				String transp = attrs.getValue("transparency");
				String lineType = attrs.getValue("linetype");
				double str = 1.0;
				if (stroke != null) {
					str = Double.parseDouble(stroke);
				}
				int tr = 255;
				if (transp != null) {
					tr = Integer.parseInt(transp);
				}
				int lt = 0;
				if (lineType != null) {
					lt = Integer.parseInt(lineType);
				}

				Rect newRect = new Rect(Integer.parseInt(x), Integer.parseInt(y),
					Integer.parseInt(width), Integer.parseInt(height),
					Integer.parseInt(color),
					Boolean.valueOf(filled).booleanValue(), str, tr, lt);

				newGraphics.addShape(newRect);
			}
			if (element.equals("oval")) {
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String width = attrs.getValue("width");
				String height = attrs.getValue("height");
				String color = attrs.getValue("colour");
				String filled = attrs.getValue("filled");
				String stroke = attrs.getValue("stroke");
				String transp = attrs.getValue("transparency");
				String lineType = attrs.getValue("linetype");
				double str = 1.0;
				if (stroke != null) {
					str = Double.parseDouble(stroke);
				}
				int tr = 255;
				if (transp != null) {
					tr = Integer.parseInt(transp);
				}
				int lt = 0;
				if (lineType != null) {
					lt = Integer.parseInt(lineType);
				}

				Oval newOval = new Oval(Integer.parseInt(x), Integer.parseInt(y),
					Integer.parseInt(width), Integer.parseInt(height),
					Integer.parseInt(color),
					Boolean.valueOf(filled).booleanValue(), str, tr, lt);

				newGraphics.addShape(newOval);
			}
			if (element.equals("arc")) {
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String width = attrs.getValue("width");
				String height = attrs.getValue("height");
				String startAngle = attrs.getValue("startAngle");
				String arcAngle = attrs.getValue("arcAngle");
				String color = attrs.getValue("colour");
				String filled = attrs.getValue("filled");
				String stroke = attrs.getValue("stroke");
				String transp = attrs.getValue("transparency");
				String lineType = attrs.getValue("lineType");
				double str = 1.0;
				if (stroke != null) {
					str = Double.parseDouble(stroke);
				}
				int tr = 255;
				if (transp != null) {
					tr = Integer.parseInt(transp);
				}
				int lt = 0;
				if (lineType != null) {
					lt = Integer.parseInt(lineType);
				}

				Arc newArc = new Arc(Integer.parseInt(x), Integer.parseInt(y),
					Integer.parseInt(width), Integer.parseInt(height),
					Integer.parseInt(startAngle), Integer.parseInt(arcAngle),
					Integer.parseInt(color),
					Boolean.valueOf(filled).booleanValue(), str, tr, lt);

				newGraphics.addShape(newArc);
			}
			if (element.equals("bounds")) {
				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String width = attrs.getValue("width");
				String height = attrs.getValue("height");

				newGraphics.setBounds(Integer.parseInt(x), Integer.parseInt(y),
					Integer.parseInt(width), Integer.parseInt(height));
			}

		}

		private Text makeText(Attributes attrs, ClassGraphics newGraphics) {
			String str = attrs.getValue("string");
			int colorInt = Integer.parseInt(attrs.getValue("colour"));
			//parse the coordinates and check if they are fixed or reverse fixed
			String val = attrs.getValue("x");
			int x, y, fixedX = 0, fixedY = 0;
			if (val.endsWith("rf")) {
				x = newGraphics.boundWidth;
				fixedX = x - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				x = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedX = -1;
			} else {
				x = Integer.parseInt(val);
			}
			val = attrs.getValue("y");
			if (val.endsWith("rf")) {
				y = newGraphics.boundWidth;
				fixedY = y - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				y = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedY = -1;
			} else {
				y = Integer.parseInt(val);
			}

			String fontName = attrs.getValue("fontname");
			int fontStyle = Integer.parseInt(attrs.getValue("fontstyle"));
			int fontSize = Integer.parseInt(attrs.getValue("fontsize"));


			Font font = new Font(fontName, fontStyle, fontSize);
			String s = attrs.getValue("fixedsize");
			boolean fixed = false;
			fixed = Boolean.valueOf(s).booleanValue();

			Text newText = new Text(x, y, font, new Color(colorInt), 255,
				str, fixed);
			newText.fixedX = fixedX;

			newText.fixedY = fixedY;
			return newText;
		}

		private void makeInitialPolygon(Attributes attrs) {
			polyXs = new ArrayList();
			polyYs = new ArrayList();
			String colour = attrs.getValue("colour");
			String filled = attrs.getValue("filled");
			String stroke = attrs.getValue("stroke");
			String transp = attrs.getValue("transparency");
			String lineType = attrs.getValue("linetype");
			double str = 1.0;
			if (stroke != null) {
				str = Double.parseDouble(stroke);
			}
			int tr = 255;
			if (transp != null) {
				tr = Integer.parseInt(transp);
			}
			int lt = 0;
			if (lineType != null) {
				lt = Integer.parseInt(lineType);
			}
			polygon = new Polygon(Integer.parseInt(colour), Boolean.valueOf(filled).booleanValue(), str, tr, lt);
		}

		private Line makeLine(Attributes attrs, ClassGraphics newGraphics) {
			int x1, x2, y1, y2;
			int fixedX1 = 0, fixedX2 = 0, fixedY1 = 0, fixedY2 = 0;
			//parse the coordinates and check if they are fixed or reverse fixed
			String val = attrs.getValue("x1");
			if (val.endsWith("rf")) {
				x1 = newGraphics.boundWidth;
				fixedX1 = x1 - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				x1 = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedX1 = -1;
			} else {
				x1 = Integer.parseInt(val);
			}
			val = attrs.getValue("x2");
			if (val.endsWith("rf")) {
				x2 = newGraphics.boundWidth;
				fixedX2 = x2 - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				x2 = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedX2 = -1;
			} else {
				x2 = Integer.parseInt(val);
			}
			val = attrs.getValue("y1");
			if (val.endsWith("rf")) {
				y1 = newGraphics.boundHeight;
				fixedY1 = y1 - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				y1 = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedY1 = -1;
			} else {
				y1 = Integer.parseInt(val);
			}
			val = attrs.getValue("y2");
			if (val.endsWith("rf")) {
				y2 = newGraphics.boundHeight;
				fixedY2 = y2 - Integer.parseInt(val.substring(0, val.length() - 2));
			} else if (val.endsWith("f")) {
				y2 = Integer.parseInt(val.substring(0, val.length() - 1));
				fixedY2 = -1;
			} else {
				y2 = Integer.parseInt(val);
			}
			String color = attrs.getValue("colour");
			String stroke = attrs.getValue("stroke");
			String transp = attrs.getValue("transparency");
			String lineType = attrs.getValue("linetype");
			double str = 1.0;
			if (stroke != null) {
				str = Double.parseDouble(stroke);
			}
			double tr = 255;
			if (transp != null) {
				tr = Double.parseDouble(transp);
			}
			int lt = 0;
			if (lineType != null) {
				lt = Integer.parseInt(lineType);
			}

			Line newLine = new Line(x1,
				y1, x2, y2, Integer.parseInt(color), str, tr, lt);
			newLine.fixedX1 = fixedX1;
			newLine.fixedX2 = fixedX2;
			newLine.fixedY1 = fixedY1;
			newLine.fixedY2 = fixedY2;
			return newLine;
		}

		public void endElement(String namespaceURI, String sName, String qName) throws
			SAXException {
			if (qName.equals("class")) {
				pack.classes.add(newClass);
			}
			if (qName.equals("port")) {
				if (newPort.openGraphics == null) {
					newGraphics = new ClassGraphics();
					newGraphics.addShape(new Oval(-4, -4, 8, 8, 12632256, true, 1.0, 255, 0));
					newGraphics.addShape(new Oval(-4, -4, 8, 8, 0, false, 1.0, 255, 0));

					newGraphics.setBounds(-4, -4, 8, 8);
					newPort.openGraphics = newGraphics;
				}
				if (newPort.closedGraphics == null) {
					newGraphics = new ClassGraphics();
					newGraphics.addShape(new Oval(-4, -4, 8, 8, 0, true, 1.0, 255, 0));
					newGraphics.setBounds(-4, -4, 8, 8);
					newPort.closedGraphics = newGraphics;
				}

				newClass.addPort(newPort);
				status = CLASS;
			}
			if (qName.equals("fields")) {
				newClass.fields = classFields;
				SpecParser sp = new SpecParser();
				ArrayList a = new ArrayList();
				try {
					a = sp.getFields(path + File.separator + newClass.name + ".java");
				} catch (IOException e) {
					db.p("Warning: class " + newClass.name + " specified in package does not exist.");
				}
				if (classFields.size() == 0) {
					newClass.fields = a;
				}

				ClassField cf;
				for (int i = 0; i < classFields.size(); i++) {
					cf = ((ClassField) classFields.get(i));

					if (cf.type == null) {
						for (int j = 0; j < a.size(); j++) {
							if (((ClassField) a.get(j)).name == cf.name) {
								cf.type = ((ClassField) a.get(j)).type;
								cf.value = ((ClassField) a.get(j)).value;
							}
						}
					}
				}
				status = CLASS;
			}

			if (qName.equals("polygon")) {
				//arrays of polygon points
				int[] xs = new int[polyXs.size()];
				int[] ys = new int[polyYs.size()];
				//arrays of FIXED information about polygon points
				int[] fxs = new int[polyXs.size()];
				int[] fys = new int[polyYs.size()];


				for (int i = 0; i < polyXs.size(); i++) {
					String s = (String) polyXs.get(i);
					//parse the coordinates and check if they are fixed or reverse fixed
					if (s.endsWith("rf")) {
						xs[i] = newGraphics.boundWidth;
						fxs[i] = newGraphics.boundWidth - Integer.parseInt(s.substring(0, s.length() - 2));
					} else if (s.endsWith("f")) {
						xs[i] = Integer.parseInt(s.substring(0, s.length() - 1));
						fxs[i] = -1;
					} else {
						xs[i] = Integer.parseInt(s);
						fxs[i] = 0;
					}
					s = (String) polyYs.get(i);
					if (s.endsWith("rf")) {
						ys[i] = newGraphics.boundHeight;
						fys[i] = newGraphics.boundHeight - Integer.parseInt(s.substring(0, s.length() - 2));
					} else if (s.endsWith("f")) {
						ys[i] = Integer.parseInt(s.substring(0, s.length() - 1));
						fys[i] = -1;
					} else {
						ys[i] = Integer.parseInt(s);
						fys[i] = 0;
					}
				}
				polygon.setPoints(xs, ys, fxs, fys);
				newGraphics.addShape(polygon);
			}

			if (qName.equals("graphics")) {

				if (status == FIELD) {
					newField.defaultGraphics = newGraphics;
				} else if (status == FIELD_KNOWN) {
					newField.knownGraphics = newGraphics;
				} else if (status == PORT_OPEN) {
					newPort.openGraphics = newGraphics;
				} else if (status == PORT_CLOSED) {
					newPort.closedGraphics = newGraphics;
				} else {
					//newGraphics.packageClass = newClass;
					newClass.addGraphics(newGraphics);


				}
			}

		}

		public void characters(char buf[], int offset, int len) throws SAXException {
			String s = new String(buf, offset, len);

			if (element.equals("name")) {
				// if we are reading a package field
				if (status == PACKAGE) {
					pack.name = s;
				} else { // else we a reading a class field
					newClass.name = s;
					// classNames.add(s);
				}
			}
			if (element.equals("description") && status == PACKAGE) {
				pack.description = s;
			}

			if (element.equals("description") && status != PACKAGE) {
				newClass.description = s;
			}

			if (element.equals("icon")) {
				newClass.icon = s;
			}
		}

		public void ignorableWhitespace(char buf[], int offset, int len) throws SAXException {// Purposely ignore it.
		}

		public void processingInstruction(String target, String data) throws SAXException {// Purposely ignore it.
		}

	}

	/**
	 * Treat validation errors as fatal.
	 * SAX ErrorHandler method.
	 * @param e - SAXParseException.
	 * @throws SAXParseException -
	 */
	public void error(SAXParseException e) throws SAXParseException {
		db.p(e);
	} // error

	/**
	 * Output warnings via the debug output module.
	 * SAX ErrorHandler method.
	 * @param e - SAXParseException.
	 * @throws SAXParseException -
	 */
	public void warning(SAXParseException e) throws SAXParseException {
		db.p(
			"** Warning, line " + e.getLineNumber() + ", uri "
			+ e.getSystemId());
		db.p("   " + e.getMessage());
	} // warning

	/**
	 * Parse the Java file, read the specification and make the relations accordingly.
	 * @return VPackage -
	 */
	public VPackage getPackage() {
		return pack;
	} // getPackage

}

