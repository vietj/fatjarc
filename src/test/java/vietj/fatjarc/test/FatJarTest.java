package vietj.fatjarc.test;

import org.junit.BeforeClass;
import org.junit.Test;
import vietj.fatjarc.FatJarProcessor;

import java.io.File;
import java.io.IOException;

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
  public void testClassLiteral() throws Exception {
    doTest("classliteral");
  }

  @Test
  public void testQualifiedField() throws Exception {
    doTest("field/qualified");
    doTest("field/unqualified");
  }

  @Test
  public void testMethodParameter() throws Exception {
    doTest("methodparameter/qualified");
    doTest("methodparameter/unqualified");
  }

  @Test
  public void testMethodReturn() throws Exception {
    doTest("methodreturn/qualified");
    doTest("methodreturn/unqualified");
  }

  @Test
  public void testConstructorParameter() throws Exception {
    doTest("constructorparameter/qualified");
    doTest("constructorparameter/unqualified");
  }

  @Test
  public void testArgumentOfParameterizedMethodParameter() throws Exception {
    doTest("argumentofparameterizedmethodparameter/qualified");
    doTest("argumentofparameterizedmethodparameter/unqualified");
  }

  private void doTest(String relativePath) throws IOException {
    File classes = new File("target/" + relativePath + "/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile(relativePath + "/Bar.java");
    assertFile(new File(classes, relativePath + "/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }
}
