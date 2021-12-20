
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ndpproje.SingleTransposition;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleTranspositionTest {
    
    public SingleTranspositionTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    /*
    @Before
    public void setUp() throws IOException {
        if(System.getProperty("os.name").startsWith("Window"))
            Runtime.getRuntime().exec("cls");
        else
            try{
                Runtime.getRuntime().exec("clear");
        } catch (IOException ex) {
            Logger.getLogger(SingleTransposition.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }*/
        //Bir sebepten oturu yukaridaki kod netbeans ortaminda calismamakta
    
   
    @After
    public void tearDown() {
        Runtime r = Runtime.getRuntime();
        System.out.println("Test worked fine. Calling garbage collector!");
        r.gc();
    }

    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    //yapildi.
    
    @Test
    public void testClassSingleTransposition(){
        SingleTransposition st = new SingleTransposition();
    }
    
    @Test
    public void testSingleTransposition(){
        SingleTransposition st = new SingleTransposition();
        boolean error = st.singleTransposition("object", "objetc");
        Assert.assertEquals(error, true);
        boolean error2 = st.singleTransposition("donurmad", "dondurma");
        Assert.assertEquals(error2, false);
    }

    @Test
    public void testControlWords() throws FileNotFoundException{
        SingleTransposition st = new SingleTransposition();
        //boolean wordExists = st.controlWords("Listede olmayan herhangi bir kelime");
        //Assert.assertEquals(wordExists, false);
        //yukaridaki ifadenin dogru olmasi gerekir 
        boolean wordExists = st.controlWords("boobs");
        Assert.assertEquals(wordExists, true);
        boolean wordExists2 = st.controlWords("dondurma");
        Assert.assertEquals(wordExists2, false);
    }
    
    
}
