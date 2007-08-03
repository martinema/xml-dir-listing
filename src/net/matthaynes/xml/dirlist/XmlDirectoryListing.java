/* 
 * TODO:
 * 
 * Error checking everywhere!
 * Logging.
 * Specify imports more exactly.
 * DONE: Sorting
 * DONE: Reverse sorting
 * DONE: Depth
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

public final class XmlDirectoryListing {
	
	// SAX stuff
	protected static StreamResult streamResult;
	protected static SAXTransformerFactory tf;
	protected static TransformerHandler hd;
	protected static Transformer serializer;
	protected static AttributesImpl atts;
	
	protected String sort = "directory"; 
	protected boolean reverse = false;
	protected static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	protected boolean depthControl = false;
	protected int depth = 0;
	
	// Main function
	public static void main(String aArguments[]) throws FileNotFoundException, IOException {
	
		// Get specified directory
		File dir = new File(aArguments[1]); 
		
		// Set output stream for generated file
		FileOutputStream out = new FileOutputStream("test.xml");
		
		// Begin listing
		FileListing lister = new FileListing();
		lister.generateXmlDirectoryListing(dir, out);
		
		// Close output stream
		out.close();
	}
	
	/**
	 * Starts generation of XML directory listing.
	 * @param dir The directory to begin listing.
	 * @param out The output stream to write XML file to.
	 */
	public void generateXmlDirectoryListing (final File dir, final OutputStream out) {
		
		if (dir.isDirectory()) {
			
			System.out.println("Generating listing for " + dir.getAbsolutePath());
			
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
				
				createElement(dir, depth);
							
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
	 * Creates a new element based on the file based through.
	 * @param file The file on which the element is based.
	 */
	public void createElement(final File file, final int depth) {
		
		setAttributes(file);
		
		String fileType = getType(file);
		
		try{
			// Output details of the file
			hd.startElement("","",fileType,atts);
			
			if (fileType == "directory") {
				parseDirectory(file, depth);
			}
			
			hd.endElement("","",fileType);
		
		} catch (SAXException e) {
		
		}
	}
		
	/**
	 * Gets the absolute path of the file.
	 * @param file The file to get the path for.
	 */
	public static String getAbsolutePath(final File file) {

		return file.getAbsolutePath();	

	}
	
	/**
	 * When passed a valid file, sets the lastModified attribute.
	 * @param file The file to get the lastModified for.
	 */
	public static String getDate(final File file) {
		
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
	public static String getSize(final File file) {
		
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
	public static String getLastModified(final File file) {
					
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
	public static String getName(final File file) {
		
		return file.getName();	
	}	
	
	/**
	 * Gets the type of the File object, returns directort or file.
	 * @param file The file to get the type for.
	 */
	public static String getType(final File file) {
		
		if (file.isDirectory()) {
			return "directory";
		} else {
			return "file";
		}
			
	}	
	
	/**
	 * Sort all files in directory, create an element for each.
	 * @param dir The directory to parse. 
	 */
	public void parseDirectory(final File dir, final int depth) {
		
		if (!depthControl || depth > 0) {
			
			// Store all files in an array and sort.
			File[] files = sortFiles(dir.listFiles());
			
			// Loop through array and create element for each file.
			for (int i=0;i<files.length;i++) {

				createElement(files[i], depth -1);

			}
		}
		
	}
	
	/**
	 * Sets all attributes for the file 
	 * @param file The file to set attributes for
	 */
	public void setAttributes(final File file) {

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
	public void setDateFormat(final String format) {
		
		// Set date format
		try {
			dateFormat = new SimpleDateFormat(format);
		} catch (Exception e) {
			
		}
		
	}

	/**
	 * Sets the depth of the directory listing.
	 * @param newDepth How deep into the directory structure to list.
	 */
	public void setDepth(final int newDepth) {
		depthControl = true;
		depth = newDepth;
	}
	
	/**
	 * Sets the sort type, returns error if bad type specified.
	 * @param newSort The type of sort.
	 */
	public void setSort(String newSort) {
		
		if (!newSort.equals("directory")||
				!newSort.equals("name")||
				!newSort.equals("size")||
				!newSort.equals("lastmodified")) {
			
			// ERROR !!!
			// BAD SORT SPECIFIED!
	
		} else {
			sort = newSort;
		}
		
	}
	
	/**
	 * Sets the sort reverse attribute.
	 * @param newReverse The value of new reverse, true / false
	 */
	public void setSortReverse(boolean newReverse) {
		reverse = newReverse;		
	}
	
	/**
	 * Sorts the array of files based upon the specified sort order. Defaults to directory.
	 * Most of this method ripped from Cocoon's DirectoryGenerator class, the justification for this being
	 * that it seems the only logical way of coding the method!! 
	 * @param files The array of files to sort 
	 */
	public File[] sortFiles(File[] files) {
		
        if (sort.equals("name")) {
        	System.out.println("SORTING BY NAME");
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return ((File)o2).getName().compareToIgnoreCase(((File)o1).getName());
                    }
                    return ((File)o1).getName().compareToIgnoreCase(((File)o2).getName());
                }
            });
        } else if (sort.equals("size")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return new Long(((File)o2).length()).compareTo(
                            new Long(((File)o1).length()));
                    }
                    return new Long(((File)o1).length()).compareTo(
                        new Long(((File)o2).length()));
                }
            });
        } else if (sort.equals("lastmodified")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return new Long(((File)o2).lastModified()).compareTo(
                            new Long(((File)o1).lastModified()));
                    }
                    return new Long(((File)o1).lastModified()).compareTo(
                        new Long(((File)o2).lastModified()));
                }
            });
        } else if (sort.equals("directory")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    File f1 = (File)o1;
                    File f2 = (File)o2;

                    if (reverse) {
                        if (f2.isDirectory() && f1.isFile())
                            return -1;
                        if (f2.isFile() && f1.isDirectory())
                            return 1;
                        return f2.getName().compareToIgnoreCase(f1.getName());
                    }
                    if (f2.isDirectory() && f1.isFile())
                        return 1;
                    if (f2.isFile() && f1.isDirectory())
                        return -1;
                    return f1.getName().compareToIgnoreCase(f2.getName());
                }
            });
        }
		
		return files;
	}
		
}
