package net.matthaynes.xml.dirlist;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


// Log4j
import org.apache.log4j.*;

//SAX classes.
import org.xml.sax.*;
import org.xml.sax.helpers.*;

//JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;

// Jakarta Regex
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public final class XmlDirectoryListing {
	
	// SAX stuff, comment better, understand more!
	protected static StreamResult streamResult;
	protected static SAXTransformerFactory tf;
	protected static TransformerHandler hd;
	protected static Transformer serializer;
	protected static AttributesImpl atts;
	
	/** Sort method for the directory listing. Defaults to "name". */
	public String sort = "name"; 
	
	/** Reverse sort method. Defaults to false. */
	public boolean reverse = false;
	
	/** Date format for date attributes. Defaults to "yyyyMMdd'T'HHmmss" */
	public DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
	
	/** Depth control for directory recursion. Defaults to "false" */
	protected boolean depthControl = false;
	
	/** How deep to recurse into directory structure. */
	public int depth;
	
    /** The regular expression for the include pattern. */
	public RE includeRE;
    
    /** The regular expression for the exclude pattern. */
	public RE excludeRE;
	
	/** Log4j logger */ 
    public org.apache.log4j.Logger log = Logger.getLogger(XmlDirectoryListing.class);
	
	/**
	 * Starts generation of XML directory listing.
	 * @param dir The directory to begin listing.
	 * @param out The output stream to write XML file to.
	 */
	public void generateXmlDirectoryListing (final File dir, final OutputStream out) {
		
		if (dir.isDirectory()) {
			
			//System.out.println("Generating listing for " + dir.getAbsolutePath());
			log.trace("Trace");
			log.debug("Debug");
			log.info("Info");
			log.warn("Warn");
			log.error("Error");
			log.fatal("Fatal");
			
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
				
				createElement(dir, depth, true);
							
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
	public void createElement(final File file, final int depth, boolean isRoot) {
		
		if (this.isIncluded(file) && !this.isExcluded(file) || isRoot == true) {

			this.setAttributes(file);
			
			String fileType = (file.isDirectory()) ? "directory" : "file"; 
			
			try{
				// Output details of the file
				hd.startElement("","",fileType,atts);
				
				if (fileType == "directory") {
					this.parseDirectory(file, depth);
				}
				
				hd.endElement("","",fileType);
			
			} catch (SAXException e) {
			
			}			
		}
	
	}
	
    /**
     * Determines if a given File shall be visible.
     * 
     * @param path  the File to check
     * @return true if the File shall be visible or the include Pattern is <code>null</code>,
     *         false otherwise.
     */
    public boolean isIncluded(File path) {
        return (this.includeRE == null) ? true : this.includeRE.match(path.getName());
    }

    /**
     * Determines if a given File shall be excluded from viewing.
     * 
     * @param path  the File to check
     * @return false if the given File shall not be excluded or the exclude Pattern is <code>null</code>,
     *         true otherwise.
     */
    public boolean isExcluded(File path) {
        return (this.excludeRE == null) ? false : this.excludeRE.match(path.getName());
    }
	
	/**
	 * Sort all files in directory, create an element for each.
	 * 
	 * @param dir The directory to parse. 
	 * @param depth Current depth of directory structure. 
	 */
	public void parseDirectory(File dir, final int depth) {
		
		if (!this.depthControl || depth > 0) {
			
			// Store all files in an array and sort.
			File[] files = sortFiles(dir.listFiles());
			
			// Loop through array and create element for each file.
			for (int i=0;i<files.length;i++) {

				createElement(files[i], depth -1, false);

			}
		}
		
	}
	
	/**
	 * Sets all attributes for the file 
	 * @param file The file to set attributes for
	 */
	public void setAttributes(File file) {

		// Clear current attributes
		atts.clear();
		
		atts.addAttribute("","","name","CDATA",file.getName());
		atts.addAttribute("","","size","CDATA",String.valueOf(file.length()));
		atts.addAttribute("","","lastModified","CDATA",String.valueOf(file.lastModified()));
		atts.addAttribute("","","date","CDATA",this.dateFormat.format(new Date(file.lastModified())));
		atts.addAttribute("","","absolutePath","CDATA",file.getAbsolutePath());
		
	}
	
	/**
	 * Sets the date format for the class, must be valid SimpleDateFormat
	 * @param format The Java SimpleDateFormat string.
	 */
	public void setDateFormat(SimpleDateFormat format) {
		
		// Set date format
		try {
			this.dateFormat = format;
		} catch (Exception e) {
			
		}
		
	}

	/**
	 * Sets the depth of the directory listing.
	 * @param newDepth How deep into the directory structure to list.
	 */
	public void setDepth(int newDepth) {
		this.depthControl = true;
		this.depth = newDepth;
	}
	
	/**
	 * Sets the exclude regular expression
	 * @param rePattern The regular expression.
	 */
	public void setExcluded(String rePattern) {
        
		try {           
            this.excludeRE = new RE(rePattern);
        } catch (RESyntaxException rese) {
            /*throw new Exception("Syntax error in regexp pattern '"
                                          + rePattern + "'", rese);
                                          */
		   
        }
	}
	
	/**
	 * Sets the include regular expression
	 * @param rePattern The regular expression.
	 */
	public void setIncluded(String rePattern) {
        
		try {   
			this.includeRE  = (rePattern == null) ? null : new RE(rePattern);
        } catch (RESyntaxException rese) {
           // throw new Exception("Syntax error in regexp pattern '" + rePattern + "'", rese);
        	System.out.println("Syntax error in regexp pattern '" + rePattern + "'");
        }
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
			this.sort = newSort;
		}
		
	}
	
	/**
	 * Sets the sort reverse attribute.
	 * @param newReverse The value of new reverse, true / false
	 */
	public void setSortReverse(boolean newReverse) {
		this.reverse = newReverse;		
	}
	
	/**
	 * Sorts the array of files based upon the specified sort order. Defaults to directory.
	 * Most of this method ripped from Cocoon's DirectoryGenerator class, the justification for this being
	 * that it seems the only logical way of coding the method!! 
	 * @param files The array of files to sort 
	 */
	public File[] sortFiles(File[] files) {
		
        if (this.sort.equals("name")) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {
                    if (reverse) {
                        return ((File)o2).getName().compareToIgnoreCase(((File)o1).getName());
                    }
                    return ((File)o1).getName().compareToIgnoreCase(((File)o2).getName());
                }
            });
        } else if (this.sort.equals("size")) {
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
        } else if (this.sort.equals("lastmodified")) {
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
        } else if (this.sort.equals("directory")) {
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
