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
    File classes = new File("target/field/unqualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("field/unqualified/Bar.java");
    assertFile(new File(classes, "field/unqualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedField() throws Exception {
    File classes = new File("target/field/qualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("field/qualified/Bar.java");
    assertFile(new File(classes, "field/qualified/Bar.class"));
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
    File classes = new File("target/methodparameter/unqualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("methodparameter/unqualified/Bar.java");
    assertFile(new File(classes, "methodparameter/unqualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedMethodParameter() throws Exception {
    File classes = new File("target/methodparameter/qualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("methodparameter/qualified/Bar.java");
    assertFile(new File(classes, "methodparameter/qualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedMethodReturn() throws Exception {
    File classes = new File("target/methodreturn/unqualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("methodreturn/unqualified/Bar.java");
    assertFile(new File(classes, "methodreturn/unqualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedMethodReturn() throws Exception {
    File classes = new File("target/methodreturn/qualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("methodreturn/qualified/Bar.java");
    assertFile(new File(classes, "methodreturn/qualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedConstructorParameter() throws Exception {
    File classes = new File("target/constructorparameter/unqualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("constructorparameter/unqualified/Bar.java");
    assertFile(new File(classes, "constructorparameter/unqualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedConstructorParameter() throws Exception {
    File classes = new File("target/constructorparameter/qualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("constructorparameter/qualified/Bar.java");
    assertFile(new File(classes, "constructorparameter/qualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedArgumentOfParameterizedMethodParameter() throws Exception {
    File classes = new File("target/argumentofparameterizedmethodparameter/qualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("argumentofparameterizedmethodparameter/qualified/Bar.java");
    assertFile(new File(classes, "argumentofparameterizedmethodparameter/qualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedArgumentOfParameterizedMethodParameter() throws Exception {
    File classes = new File("target/argumentofparameterizedmethodparameter/unqualified/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("argumentofparameterizedmethodparameter/unqualified/Bar.java");
    assertFile(new File(classes, "argumentofparameterizedmethodparameter/unqualified/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }
}
