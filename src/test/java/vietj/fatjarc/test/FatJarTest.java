package vietj.fatjarc.test;

import org.junit.Test;
import vietj.fatjarc.FatJarProcessor;

import java.io.File;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FatJarTest extends TestBase {

  @Test
  public void testSimple() throws Exception {
    File test1Classes = new File("target/simple/test1");
    compiler(test1Classes).assertCompile("test1/A.java");
    File test1Jar = new File("target/simple-test1.jar");
    makeJar(test1Classes.getAbsoluteFile(), test1Jar);
    File test2Classes = new File("target/simple/test2");
    compiler(test2Classes).addToClassPath(test1Jar).addProcessor(new FatJarProcessor()).assertCompile("test2/B.java");
    assertFile(new File(test2Classes, "test2/B.class"));
    assertFile(new File(test2Classes, "test1/A.class"));
  }

}
