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
  public void testUnqualifiedField() throws Exception {
    File classes = new File("target/unqualifiedfield/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("unqualifiedfield/Bar.java");
    assertFile(new File(classes, "unqualifiedfield/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedField() throws Exception {
    File classes = new File("target/qualifiedfield/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("qualifiedfield/Bar.java");
    assertFile(new File(classes, "qualifiedfield/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testClassLiteral() throws Exception {
    File classes = new File("target/classliteral/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("classliteral/Bar.java");
    assertFile(new File(classes, "classliteral/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedMethodParameter() throws Exception {
    File classes = new File("target/unqualifiedmethodparameter/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("unqualifiedmethodparameter/Bar.java");
    assertFile(new File(classes, "unqualifiedmethodparameter/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedMethodParameter() throws Exception {
    File classes = new File("target/qualifiedmethodparameter/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("qualifiedmethodparameter/Bar.java");
    assertFile(new File(classes, "qualifiedmethodparameter/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedMethodReturn() throws Exception {
    File classes = new File("target/unqualifiedmethodreturn/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("unqualifiedmethodreturn/Bar.java");
    assertFile(new File(classes, "unqualifiedmethodreturn/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }
}
