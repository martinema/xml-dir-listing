/* 
 * TODO:
 * 
 * Error checking everywhere!
 * Logging.
 * Sorting
 * Reverse sorting
 * Depth
 * Includes 
 * Excludes
 * Seperate Main into another class
 * Ant Task
 * 
 */

// package net.matthaynes.xml.dirlist;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

//JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

public final class FileListing {
	
	public static StreamResult streamResult;
	public static SAXTransformerFactory tf;
	public static AttributesImpl atts;
	public static TransformerHandler hd;
	public static Transformer serializer;
	
	public static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	
	// Main function
	public static void main(String aArguments[]) throws FileNotFoundException, IOException {
	
		// Get specified directory
		File dir = new File(aArguments[1]); 
		
		// Set output stream for generated file
		FileOutputStream out = new FileOutputStream("test.xml");
		
		// Begin listing
		generateXmlListing(dir, out);
		
		// Close output stream
		out.close();
	}
	
	/**
	 * Starts generation of XML directory listing.
	 * @param dir The directory to begin listing.
	 */
	public static void generateXmlListing(File dir, FileOutputStream out) {
		
				
		if (dir.isDirectory()) {
			
			System.out.print("Generating listing for " + dir.getAbsolutePath());
			
			try {
				
				// Acts as an holder for a transformation result, which may be XML, plain Text, 
				// HTML, or some other form of markup.
				streamResult = new StreamResult(out);

				// This class extends TransformerFactory to provide SAX-specific factory methods. It 
				// provides two types of ContentHandlers, one for creating Transformers, the other 
				// for creating Templates objects.
				tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
				
				// This class provides a default implementation of the SAX2 Attributes interface,
				// with the addition of manipulators so that the list can be modified or reused.
				// SAX Helper
				atts = new AttributesImpl();
				
				// A TransformerHandler listens for SAX ContentHandler parse events and 
				// transforms them to a Result.
				hd = tf.newTransformerHandler();
				
				// An instance of this abstract class can transform a source tree into a
				// result tree.
				serializer = hd.getTransformer();
				
				// Set an output property that will be in effect for the transformation.
				serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
				// serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
				serializer.setOutputProperty(OutputKeys.INDENT,"yes");
	
				// Enables the user of the TransformerHandler to set the to set the Result 
				// for the transformation.
				hd.setResult(streamResult);
				
				// SAX ContentHandler parse event
				hd.startDocument();
				
				createElement(dir);
							
				// SAX ContentHandler parse event
				hd.endDocument();
							
			} catch (Exception e) {
				
			}
			
		} else {
			// ERROR DIRECTORY IS NOT VALID
			System.out.print(dir.getAbsolutePath() + " is not a valid directory.");
		}
		
	}
	
	/**
	 * Gets the absolute path of the file.
	 * @param file The file to get the path for.
	 */
	public static String getAbsolutePath(File file) {

		return file.getAbsolutePath();	

	}
	
	/**
	 * When passed a valid file, sets the lastModified attribute.
	 * @param file The file to get the lastModified for.
	 */
	public static String getDate(File file) {
		
		// Get the last modified date
		long lastModified = file.lastModified();
		
		// Get as date
		Date d =  new Date(lastModified);
		
		// Convert to string with formatting.
        String s = dateFormat.format(d);
        									
		return s;
		
	}
	
	/**
	 * When passed a valid file, sets the fileSize attribute to the size of the file in bytes.
	 * @param file The file to get the size for.
	 */
	public static String getSize(File file) {
		
		// Get the number of bytes in the file
		long length = file.length();
		
		// Convert size to string
		String s = new String();
		s = String.valueOf(length);
		
		return s;

	}

	/**
	 * When passed a valid file, sets the lastModified attribute.
	 * @param file The file to get the lastModified for.
	 */
	public static String getLastModified(File file) {
					
		// Get the last modified date
		long lastModified = file.lastModified();
		
		// Convert last modified to string
		String s = new String();
		s = String.valueOf(lastModified);
        									
		return s;

	}
	
	/**
	 * Sets the name attribute.
	 * @param file The file to get the name for.
	 */
	public static String getName(File file) {
		
		return file.getName();	
	}	
	
	/**
	 * Gets the type of the File object, returns directort or file.
	 * @param file The file to get the type for.
	 */
	public static String getType(File file) {
		
		if (file.isDirectory()) {
			return "directory";
		} else {
			return "file";
		}
			
	}	
	
	/**
	 * Sets all attributes for the file 
	 * @param file The file to set attributes for
	 */
	public static void setAttributes(File file) {

		// Clear current attributes
		atts.clear();
		
		atts.addAttribute("","","name","CDATA",getName(file));
		atts.addAttribute("","","size","CDATA",getSize(file));
		atts.addAttribute("","","lastModified","CDATA",getLastModified(file));
		atts.addAttribute("","","date","CDATA",getDate(file));
		atts.addAttribute("","","absolutePath","CDATA",getAbsolutePath(file));
		
	}
	
	/**
	 * Sets the date format for the class, must be valid SimpleDateFormat
	 * @param format The Java SimpleDateFormat string.
	 */
	public static void setDateFormat(String format) {
		
		// Set date format
		try {
			dateFormat = new SimpleDateFormat(format);
		} catch (Exception e) {
			
		}
		
	}

	/**
	 * Creates a new element based on the file based through.
	 * @param file The file on which the element is based.
	 */
	public static void createElement(File file) {
		
		setAttributes(file);
		
		String fileType = getType(file);
		
		try{
			// Output details of the file
			hd.startElement("","",fileType,atts);
			
			if (fileType == "directory") {
				visitAllDirsAndFiles(file);
			}
			
			hd.endElement("","",fileType);
		
		} catch (Exception e) {
		
		}
	}
	
	// Recursively search directory and output details as xml in a string
	public static void visitAllDirsAndFiles(File dir) {
	
		// Store all files and folder names in an array and sort alphabetically.
		String[] children = dir.list();
		Arrays.sort(children);
		
		for (int i=0;i<children.length;i++) {

			// Get file or directory
			File file = new File(dir, children[i]);
					
			createElement(file);

		}
		
	}
	
}
