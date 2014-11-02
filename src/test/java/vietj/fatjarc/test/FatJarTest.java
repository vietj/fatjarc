package vietj.fatjarc.test;

import org.junit.BeforeClass;
import org.junit.Test;
import vietj.fatjarc.FatJarProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FatJarTest extends TestBase {

  private static File basicClassJar;
  private static File basicInterfaceJar;

  @BeforeClass
  public static void compileBasic() throws Exception {
    basicClassJar = buildJar("target/basicclass", "target/basicclass.jar", "basicclass/Foo.java");
    basicInterfaceJar = buildJar("target/basicinterface", "target/basicinterface.jar", "basicinterface/Juu.java");
  }

  private static File buildJar(String src, String dst, String... classes) throws Exception {
    File basicClasses = new File(src);
    compiler(basicClasses).assertCompile(classes);
    File jar = new File(dst);
    jar(basicClasses.getAbsoluteFile(), jar);
    return jar;
  }

  @Test
  public void testClassLiteral() throws Exception {
    doTest("classliteral", "basicclass.Foo");
  }

  @Test
  public void testQualifiedField() throws Exception {
    doTest("field.qualified", "basicclass.Foo");
    doTest("field.unqualified", "basicclass.Foo");
  }

  @Test
  public void testMethodParameter() throws Exception {
    doTest("methodparameter.qualified", "basicclass.Foo");
    doTest("methodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testMethodReturn() throws Exception {
    doTest("methodreturn.qualified", "basicclass.Foo");
    doTest("methodreturn.unqualified", "basicclass.Foo");
  }

  @Test
  public void testConstructorParameter() throws Exception {
    doTest("constructorparameter.qualified", "basicclass.Foo");
    doTest("constructorparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testArgumentOfParameterizedMethodParameter() throws Exception {
    doTest("argumentofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("argumentofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testUpperBoundOfParameterizedMethodParameter() throws Exception {
    doTest("upperboundofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("upperboundofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testLowerBoundOfParameterizedMethodParameter() throws Exception {
    doTest("lowerboundofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("lowerboundofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testClassUpperBoundOfMethodTypeParameter() throws Exception {
    doTest("classupperboundofmethodtypeparameter.qualified", "basicclass.Foo");
    doTest("classupperboundofmethodtypeparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testInterfaceUpperBoundOfMethodTypeParameter() throws Exception {
    doTest("interfaceupperboundofmethodtypeparameter.qualified", "basicinterface.Juu");
    doTest("interfaceupperboundofmethodtypeparameter.unqualified", "basicinterface.Juu");
  }

  private void doTest(String pkg, String... expected) throws IOException {
    String relativePath = pkg.replace('.', '/');
    File classes = new File("target/" + relativePath + "/classes");
    compiler(classes).addToClassPath(basicClassJar).addToClassPath(basicInterfaceJar).addProcessor(new FatJarProcessor()).assertCompile(relativePath + "/Bar.java");
    HashSet<File> expectedFiles = new HashSet<>();
    expectedFiles.add(new File(classes, relativePath + "/Bar.class"));
    for (String exp : expected) {
      expectedFiles.add(new File(classes, exp.replace('.', '/') + ".class"));
    }
    Files.walkFileTree(classes.toPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        assertTrue("Was not expecting file " + file, expectedFiles.remove(file.toFile()));
        return super.visitFile(file, attrs);
      }
    });
    assertTrue("Was not expecting these files " + expectedFiles, expectedFiles.isEmpty());
  }
}
