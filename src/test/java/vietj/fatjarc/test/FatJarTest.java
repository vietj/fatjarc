package vietj.fatjarc.test;

import org.junit.BeforeClass;
import org.junit.Test;
import vietj.fatjarc.FatJarProcessor;

import java.io.File;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FatJarTest extends TestBase {

  private static File basicJar;

  @BeforeClass
  public static void compileBasic() throws Exception {
    File basicClasses = new File("target/basic");
    compiler(basicClasses).assertCompile("basic/Foo.java");
    basicJar = new File("target/basic.jar");
    makeJar(basicClasses.getAbsoluteFile(), basicJar);
  }

  @Test
  public void testFqnImport() throws Exception {
    File classes = new File("target/fqnimport/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("fqnimport/Bar.java");
    assertFile(new File(classes, "fqnimport/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testFullyQualifiedVariable() throws Exception {
    File classes = new File("target/fqnvariable/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("fqnvariable/Bar.java");
    assertFile(new File(classes, "fqnvariable/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }
}
