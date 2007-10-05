import java.text.SimpleDateFormat;
import junit.framework.TestCase;
import net.matthaynes.xml.dirlist.*;


public class TestXmlDirectoryListing extends TestCase {

	/** XmlDirectoryListing object */
	private static XmlDirectoryListing lister = new XmlDirectoryListing();
	
	/** Test params */
	private static int depth = 4; 
	private static String dateFormat = "dd M yy'T'HHmmss"; 
	private static String [] sorts = {"directory","name","size","lastmodified"};
	private static String sortError = "gobbldygook";

    /**
     * setUp() method that initializes common objects
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * tearDown() method that cleanup the common objects
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TestXmlDirectoryListing
     * @param name
     */
    public TestXmlDirectoryListing(String name) {
        super(name);
    }
    
    /**
     * Test setDepth() method
     */
    public void testSetDepth() {
    	lister.setDepth(this.depth);
    	assertEquals(this.depth,lister.depth);
    }
    
    /**
     * Test setDateFormat() method
     */
    public void testSetDateFormat () {
    	lister.setDateFormat(this.dateFormat);
    	assertEquals(new SimpleDateFormat(this.dateFormat), lister.dateFormat);
    }
    
    /**
     * Test setSort() method
     */
    public void testSetSort() {
    	    	  	
    	for (int i=0;i<this.sorts.length;i++) {
    		lister.setSort(this.sorts[i]);
        	assertEquals(this.sorts[i],lister.sort);
    	}    	
    	
    }
        
    /**
     * Test setSort() method with erroneous value
     */
    public void testSetSortError () {
    	lister.setSort(this.sortError);
    	assertNotSame(this.sortError,lister.sort);
    }
    
    /**
     * Test setSortReverse() method on / off
     */
    public void testSetSortReverse () {
    	
    	lister.setSortReverse(true);
    	assertTrue(lister.reverse);
    	
    	lister.setSortReverse(false);
    	assertFalse(lister.reverse);
    }
    
}
