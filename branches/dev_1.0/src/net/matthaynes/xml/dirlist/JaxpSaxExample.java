/**
 * JAXP and SAX XML generation example 
 * 
 * Based on the tutorial at: http://www.javazoom.net/services/newsletter/xmlgeneration.html
 * 
 * What is JAXP and why are we using it ?
 * 
 * JAXP (Java Api for XML Processing) provides the capability of validating and 
 * parsing XML documents. he two basic parsing interfaces are:
 *
 * 		- The Document Object Model (DOM) parsing interface or DOM interface
 * 		- The Simple API for XML (SAX) parsing interface or SAX interface
 * 
 * The main advantage of JAXP is that it enables applications to parse and transform XML
 * documents independent of a particular XML processing implementation. That is it interfaces 
 * with the available XML processor for you, without tying you down any one in particular. This
 * improves portabilty of code.
 * 
 * What is SAX and why are we using it ?
 * 
 * SAX (Simple API for XML) is a serial access parser API for XML. SAX provides a mechanism for
 * reading and writing data to an XML document. Its main advantage over the DOM is that it has 
 * a much lower memory footprint and is often faster to execute. A parser which implements SAX 
 * functions as a stream parser, with an event-driven API.
 * 
 * 
 */

package net.matthaynes.xml.dirlist;

import java.io.*;

// SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

public class JaxpSaxExample {
	

	public static void main(String args[]) throws FileNotFoundException {

		// Set output stream for generated file
		FileOutputStream out = new FileOutputStream("test.xml");
		
		// Acts as an holder for a transformation result, which may be XML, plain Text, 
		// HTML, or some other form of markup.
		StreamResult streamResult = new StreamResult(out);

		// This class extends TransformerFactory to provide SAX-specific factory methods. It 
		// provides two types of ContentHandlers, one for creating Transformers, the other 
		// for creating Templates objects.
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
		
		try {
			
			// A TransformerHandler listens for SAX ContentHandler parse events and 
			// transforms them to a Result.
			TransformerHandler hd = tf.newTransformerHandler();
			
			// An instance of this abstract class can transform a source tree into a
			// result tree.
			Transformer serializer = hd.getTransformer();
			
			// Set an output property that will be in effect for the transformation.
			serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
			serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
			serializer.setOutputProperty(OutputKeys.INDENT,"yes");

			// Enables the user of the TransformerHandler to set the to set the Result 
			// for the transformation.
			hd.setResult(streamResult);
			
			// SAX ContentHandler parse event
			hd.startDocument();
			
			// This class provides a default implementation of the SAX2 Attributes interface,
			// with the addition of manipulators so that the list can be modified or reused.
			// SAX Helper
			AttributesImpl atts = new AttributesImpl();
			
			// SAX ContentHandler parse event
			hd.startElement("","","USERS",atts);
			
			//USER tags.
			String[] id = {"PWD122","MX787","A4Q45"};
			String[] type = {"customer","manager","employee"};
			String[] desc = {"Tim@Home","Jack&Moud","John D'oé"};
			
			for (int i=0;i<id.length;i++) {

				// Add attributes
				atts.clear();
				atts.addAttribute("","","ID","CDATA",id[i]);
				atts.addAttribute("","","TYPE","CDATA",type[i]);
				
				// SAX ContentHandler parse event
				hd.startElement("","","USER",atts);
				hd.characters(desc[i].toCharArray(),0,desc[i].length());
				hd.endElement("","","USER");
			}
			
			// SAX ContentHandler parse event
			hd.endElement("","","USERS");
			
			// SAX ContentHandler parse event
			hd.endDocument();
			
			// Close output stream
			out.close();
			
		} catch (Exception e) {
			
		}
		
	}
}

