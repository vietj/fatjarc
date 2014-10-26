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

  @Test
  public void testQualifiedMethodReturn() throws Exception {
    File classes = new File("target/qualifiedmethodreturn/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("qualifiedmethodreturn/Bar.java");
    assertFile(new File(classes, "qualifiedmethodreturn/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testUnqualifiedConstructorParam() throws Exception {
    File classes = new File("target/unqualifiedconstructorparameter/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("unqualifiedconstructorparameter/Bar.java");
    assertFile(new File(classes, "unqualifiedconstructorparameter/Bar.class"));
    assertFile(new File(classes, "basic/Foo.class"));
  }

  @Test
  public void testQualifiedConstructorParam() throws Exception {
    File classes = new File("target/qualifiedconstructorparameter/classes");
    compiler(classes).addToClassPath(basicJar).addProcessor(new FatJarProcessor()).assertCompile("qualifiedconstructorparameter/Bar.java");
    assertFile(new File(classes, "qualifiedconstructorparameter/Bar.class"));
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
