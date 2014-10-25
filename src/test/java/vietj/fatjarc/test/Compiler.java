package vietj.fatjarc.test;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class Compiler {

  final File dest;
  final ArrayList<File> classPath = new ArrayList<>();
  final ArrayList<Processor> processors = new ArrayList<>();

  public Compiler(File dest) {
    this.dest = dest;
  }

  Compiler addToClassPath(File f) {
    classPath.add(f);
    return this;
  }

  Compiler addProcessor(Processor processor) {
    processors.add(processor);
    return this;
  }

  void assertCompile(String... sources) throws IOException {
    File tmp = Files.createTempDirectory("fatjarc").toFile();
    tmp.deleteOnExit();
    File[] copies = new File[sources.length];
    for (int i = 0;i < sources.length;i++) {
      String source = sources[i];
      File file = copies[i] = new File(tmp, source);
      TestBase.assertCreateDir(file.getParentFile());
      Files.copy(new File(new File("src/test/support").getAbsoluteFile(), source).toPath(), file.toPath());
    }
    assertCompile(copies);
  }

  void assertCompile(File... sources) {
    DiagnosticCollector<? super JavaFileObject> diagnostics = new DiagnosticCollector<>();
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.ENGLISH, Charset.forName("UTF-8"));
    if (!dest.mkdirs()) {
      assertTrue(dest.exists());
    }
    assertTrue(dest.isDirectory());
    try {
      fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(dest));
      fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
    } catch (IOException e) {
      throw new AssertionError("Could not set location", e);
    }
    Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjects(sources);
    JavaCompiler.CompilationTask task = compiler.getTask(new OutputStreamWriter(System.out), fileManager, diagnostics, Collections.<String>emptyList(), Collections.<String>emptyList(), files);
    task.setLocale(Locale.ENGLISH);
    task.setProcessors(processors);

    if (!task.call()) {
      StringWriter buffer = new StringWriter();
      buffer.append("Could not compile").append(":\n");
      for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
        buffer.append(diagnostic.toString()).append("\n");
      }
      throw new AssertionError(buffer.toString());
    }
  }

}
