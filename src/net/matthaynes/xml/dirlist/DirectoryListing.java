package net.matthaynes.xml.dirlist;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.cli.*;

public final class DirectoryListing {
	
	// Store options
	Options opt = new Options();
	
	// Help formatter
	HelpFormatter f = new HelpFormatter();
	
	// Main function
	public static void main(String aArguments[]) throws FileNotFoundException, IOException, ParseException {

		
		try {
		      

		      opt.addOption("h", false, "Print help for this application");
		      opt.addOption("u", true, "The username to use");
		      opt.addOption("dsn", true, "The data source to use");

		      BasicParser parser = new BasicParser();
		      CommandLine cl = parser.parse(opt, args);

		      if ( cl.hasOption(´h´) ) {
		    	  this.displayHelp();
		      }       else {
		        System.out.println(cl.getOptionValue("u"));
		        System.out.println(cl.getOptionValue("dsn"));
		      }
		    }
		    catch (ParseException e) {
		      e.printStackTrace();
		    }
		  }
		

		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		// Get specified directory
		File dir = new File(aArguments[0]);


		// Set output stream for generated file
		FileOutputStream out = new FileOutputStream("test.xml");

		// Begin listing
		XmlDirectoryListing lister = new XmlDirectoryListing();
		lister.dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'hello'");
		lister.generateXmlDirectoryListing(dir, out);

		// Close output stream
		out.close();

	}

	public void displayHelp() {
		this.f.printHelp("OptionsTip", opt);
	}
	

}