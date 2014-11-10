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
  private static File basicInnerClassJar;
  private static File basicGenericOuterClassJar;
  private static File basicGenericInnerClassJar;

  @BeforeClass
  public static void compileBasic() throws Exception {
    basicClassJar = buildJar("target/basicclass", "target/basicclass.jar", "basicclass/Foo.java");
    basicInterfaceJar = buildJar("target/basicinterface", "target/basicinterface.jar", "basicinterface/Juu.java");
    basicInnerClassJar = buildJar("target/basicinnerclass", "target/basicinnerclass.jar", "basicinnerclass/Daa.java");
    basicGenericOuterClassJar = buildJar("target/basicgenericouterclass", "target/basicgenericouterclass.jar", "basicgenericouterclass/Daa.java");
    basicGenericInnerClassJar = buildJar("target/basicgenericinnerclass", "target/basicgenericinnerclass.jar", "basicgenericinnerclass/Daa.java");
  }

  private static File buildJar(String src, String dst, String... classes) throws Exception {
    File basicClasses = new File(src);
    compiler(basicClasses).assertCompile(classes);
    File jar = new File(dst);
    jar(basicClasses.getAbsoluteFile(), jar);
    return jar;
  }

  @Test
  public void testDescriptorClassLiteral() throws Exception {
    doTest("descriptor.classliteral", "basicclass.Foo");
  }

  @Test
  public void testDescriptorBaseField() throws Exception {
    doTest("descriptor.basefield");
  }

  @Test
  public void testDescriptorQualifiedField() throws Exception {
    doTest("descriptor.field.qualified", "basicclass.Foo");
    doTest("descriptor.field.unqualified", "basicclass.Foo");
  }

  @Test
  public void testDescriptorMethodParameter() throws Exception {
    doTest("descriptor.methodparameter.qualified", "basicclass.Foo");
    doTest("descriptor.methodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testDescriptorMethodReturn() throws Exception {
    doTest("descriptor.methodreturn.qualified", "basicclass.Foo");
    doTest("descriptor.methodreturn.unqualified", "basicclass.Foo");
  }

  @Test
  public void testDescriptorConstructorParameter() throws Exception {
    doTest("descriptor.constructorparameter.qualified", "basicclass.Foo");
    doTest("descriptor.constructorparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureBaseMethodParameter() throws Exception {
    doTest("signature.basemethodparameter");
  }

  @Test
  public void testSignatureBaseComponentOfArrayMethodParameter() throws Exception {
    doTest("signature.basecomponentofarraymethodparameter");
  }

  @Test
  public void testSignatureComponentOfArrayMethodParameter() throws Exception {
    doTest("signature.componentofarraymethodparameter.qualified", "basicclass.Foo");
    doTest("signature.componentofarraymethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureArgumentOfParameterizedMethodParameter() throws Exception {
    doTest("signature.argumentofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("signature.argumentofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureArgumentOfParameterizedField() throws Exception {
    doTest("signature.argumentofparameterizedfield.qualified", "basicclass.Foo");
    doTest("signature.argumentofparameterizedfield.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureUnboundedParameterizedMethodParameter() throws Exception {
    doTest("signature.unboundedparameterizedmethodparameter");
  }

  @Test
  public void testSignatureUpperBoundOfParameterizedMethodParameter() throws Exception {
    doTest("signature.upperboundofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("signature.upperboundofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureLowerBoundOfParameterizedMethodParameter() throws Exception {
    doTest("signature.lowerboundofparameterizedmethodparameter.qualified", "basicclass.Foo");
    doTest("signature.lowerboundofparameterizedmethodparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureClassUpperBoundOfMethodTypeParameter() throws Exception {
    doTest("signature.classupperboundofmethodtypeparameter.qualified", "basicclass.Foo");
    doTest("signature.classupperboundofmethodtypeparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testSignatureInterfaceUpperBoundOfMethodTypeParameter() throws Exception {
    doTest("signature.interfaceupperboundofmethodtypeparameter.qualified", "basicinterface.Juu");
    doTest("signature.interfaceupperboundofmethodtypeparameter.unqualified", "basicinterface.Juu");
  }

  @Test
  public void testSignatureClassAndInterfaceUpperBoundOfMethodTypeParameter() throws Exception {
    doTest("signature.classandinterfaceupperboundofmethodtypeparameter.qualified", "basicclass.Foo", "basicinterface.Juu");
    doTest("signature.classandinterfaceupperboundofmethodtypeparameter.unqualified", "basicclass.Foo", "basicinterface.Juu");
  }

  @Test
  public void testClassUpperBoundOfClassTypeParameter() throws Exception {
    doTest("signature.classupperboundofclasstypeparameter.qualified", "basicclass.Foo");
    doTest("signature.classupperboundofclasstypeparameter.unqualified", "basicclass.Foo");
  }

  @Test
  public void testInterfaceUpperBoundOfClassTypeParameter() throws Exception {
    doTest("signature.interfaceupperboundofclasstypeparameter.qualified", "basicinterface.Juu");
    doTest("signature.interfaceupperboundofclasstypeparameter.unqualified", "basicinterface.Juu");
  }

  @Test
  public void testClassAndInterfaceUpperBoundOfClassTypeParameter() throws Exception {
    doTest("signature.classandinterfaceupperboundofclasstypeparameter.qualified", "basicclass.Foo", "basicinterface.Juu");
    doTest("signature.classandinterfaceupperboundofclasstypeparameter.unqualified", "basicclass.Foo", "basicinterface.Juu");
  }

  @Test
  public void testClassExtends() throws Exception {
    doTest("signature.extendsclass.qualified", "basicclass.Foo");
    doTest("signature.extendsclass.unqualified", "basicclass.Foo");
  }

  @Test
  public void testInterfaceImplements() throws Exception {
    doTest("signature.implementsinterface.qualified", "basicinterface.Juu");
    doTest("signature.implementsinterface.unqualified", "basicinterface.Juu");
  }

  @Test
  public void testClassExtendsAndInterfaceImplements() throws Exception {
    doTest("signature.extendsclassanimplementsdinterface.qualified", "basicclass.Foo", "basicinterface.Juu");
    doTest("signature.extendsclassanimplementsdinterface.unqualified", "basicclass.Foo", "basicinterface.Juu");
  }

  @Test
  public void testInterfaceExtends() throws Exception {
    doTest("signature.extendsinterface.qualified", "basicinterface.Juu");
    doTest("signature.extendsinterface.unqualified", "basicinterface.Juu");
  }

  @Test
  public void testInnerClass() throws Exception {
    doTest("signature.innerclass.qualified", "basicinnerclass.Daa", "basicinnerclass.Daa$Inner");
    doTest("signature.innerclass.unqualified", "basicinnerclass.Daa", "basicinnerclass.Daa$Inner");
  }

  @Test
  public void testGenericOuterClass() throws Exception {
    doTest("signature.genericouterclass.qualified", "basicgenericouterclass/Daa", "basicgenericouterclass/Daa$Inner");
    doTest("signature.genericouterclass.unqualified", "basicgenericouterclass/Daa", "basicgenericouterclass/Daa$Inner");
  }

  @Test
  public void testGenericInnerClass() throws Exception {
    doTest("signature.genericinnerclass.qualified", "basicgenericinnerclass/Daa", "basicgenericinnerclass/Daa$InnerOuter", "basicgenericinnerclass/Daa$InnerOuter$Inner");
    doTest("signature.genericinnerclass.unqualified", "basicgenericinnerclass/Daa", "basicgenericinnerclass/Daa$InnerOuter", "basicgenericinnerclass/Daa$InnerOuter$Inner");
  }

  private void doTest(String pkg, String... expected) throws IOException {
    String relativePath = pkg.replace('.', '/');
    File classes = new File("target/" + relativePath + "/classes");
    compiler(classes).
        addToClassPath(basicClassJar).
        addToClassPath(basicInterfaceJar).
        addToClassPath(basicInnerClassJar).
        addToClassPath(basicGenericOuterClassJar).
        addToClassPath(basicGenericInnerClassJar).
        addProcessor(new FatJarProcessor()).assertCompile(relativePath + "/Bar.java");
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
