package net.matthaynes.xml.dirlist;

import java.io.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
* Ant task interface for XML Directory Listing Class
**/


public final class AntFileListing extends Task {
	
		public String sort;
		public boolean reverse;
		public String output;
		public boolean verbose;
		public String dateFormat;
		public int depth;
		public String includesRegEx;
		public String excludesRegEx;
		public String directory;
		
		/** XmlDirectoryListing object */
		private static XmlDirectoryListing lister = new XmlDirectoryListing();
		
		/** Outputstream for XmlDirectoryListing object */
		private static FileOutputStream out;
		
		/** The directory to list */
		private static File dir;

		/**
		 * Gets the sort attribute from the ant task.
		 * @param sort
		 */
	    public void setSort(String sort) {
	    	this.sort = sort;
	    }		
		
	    /**
	     * Gets the revrse attribute from the ant task.
	     * @param reverse
	     */
	    public void setReverse(boolean reverse) {
	    	this.reverse= reverse;
	    }		
	    
	    /**
	     * Gets the srcdir attribute from the ant task
	     * @param output
	     */
	    public void setDestFile(String output) {
	    	this.output = output;
	    }

	    /**
	     * Gets the verbose attribute from the ant task
	     * @param verbose
	     */
	    public void setVerbose(boolean verbose) {
		       this.verbose = verbose;
		}
	    
	    /**
	     * Gets the dateformat attribute from the ant task
	     * @param dateFormat
	     */
	    public void setDateFormat(String dateFormat) {
		       this.dateFormat = dateFormat;
		}	
	    
	    /**
	     * Gets the depth attribute from the ant task
	     * @param depth
	     */
	    public void setDepth (int depth) {
		       this.depth = depth;
		}
	    
	    /**
	     * Gets the includesRegEx attribute from the ant task
	     * @param includesRegEx
	     */
	    public void setIncludesRegEx(String includesRegEx) {
		       this.includesRegEx = includesRegEx;
		}
	    
	    /**
	     * Gets the excludesRegEx attribute from the ant task
	     * @param excludesRegEx
	     */
	    public void setExcludesRegEx(String excludesRegEx) {
		       this.excludesRegEx = excludesRegEx;
		}	    
	    	    
	    /**
	     * Gets the srcdir attribute from the ant task
	     * @param directory
	     */
	    public void setSrcDir(String directory) {
	    	this.directory = directory;
	    }

	    // Main function
	    public void execute() {

	        try {
				
				// Check for output option and and apply it to XmlDirectoryListing class, otherwise fail.
				if (this.output != null) {
					
					// Set output stream for generated file
					out = new FileOutputStream(this.output);
					
				} else {
					throw new BuildException("Please specify an output file.");
				}
				
				// Check for sort option and apply it to XmlDirectoryListing class
				if (this.sort != null) {
					lister.setSort(this.sort);
				}
				
				// Check for reverse option and apply it to XmlDirectoryListing class
				if (this.reverse) {
					lister.setSortReverse(this.reverse);
				}
				
				// Check for dateformat option and apply it to XmlDirectoryListing class
				if (this.dateFormat != null) {
					lister.setDateFormat(this.dateFormat);
				}
				
				// Check for dateformat option and apply it to XmlDirectoryListing class
				if (this.depth > 0) {
					lister.setDepth(this.depth);
				}	
				
				// Check for includes option and apply it to XmlDirectoryListing class
				if (this.includesRegEx != null) {
					lister.setIncluded(this.includesRegEx);
				}	
				
				// Check for excludes option and apply it to XmlDirectoryListing class
				if (this.excludesRegEx != null) {
					lister.setExcluded(this.excludesRegEx);
				}	
				
				// Check for verbose flag. Set logger accordingly.
				if (this.verbose) {
					lister.log.setLevel(org.apache.log4j.Level.DEBUG);
				} else {
					lister.log.setLevel(org.apache.log4j.Level.INFO);
				}
				
				
			  	// Check we have a directory!!
		    	if(this.directory == null){
		            throw new BuildException("No Directory set.");
		    	} else {
		    		dir = new File(this.directory);
		    	}
		    	
				
				// Run Class ========================================================
				
				// Begin listing			
				lister.generateXmlDirectoryListing(dir, out);

				// Close output stream
				out.close();
				
				// ==================================================================
							
				
			} catch (Exception e) {
				throw new BuildException(e);
			}
	        

    }


}

