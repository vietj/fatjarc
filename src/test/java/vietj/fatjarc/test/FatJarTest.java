package vietj.fatjarc.test;

import org.junit.Test;
import vietj.fatjarc.FatJarProcessor;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class FatJarTest {

  @Test
  public void testFoo() throws Exception {

    DiagnosticCollector<? super JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.forName("UTF-8"));

    File src = new File("src/test/resources");
    File dst = new File("target/test1");
    if (!dst.mkdirs()) {
      assertTrue(dst.exists());
    }
    assertTrue(dst.isDirectory());

    fileManager.setLocation(StandardLocation.SOURCE_PATH, Collections.singletonList(src));
    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(dst));

    Iterable<? extends JavaFileObject> sources = fileManager.getJavaFileObjects(new File(src, "test1/A.java"));

    JavaCompiler.CompilationTask task = compiler.getTask(new OutputStreamWriter(System.out), fileManager, diagnostics, Collections.<String>emptyList(), Collections.<String>emptyList(), sources);
    task.setLocale(Locale.ENGLISH);
    task.setProcessors(Collections.singletonList(new FatJarProcessor()));

    if (!task.call()) {
      for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        System.out.println("diagnostic = " + diagnostic);
      }
    }



  }

}
