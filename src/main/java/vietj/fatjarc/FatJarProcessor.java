package vietj.fatjarc;

import com.sun.source.tree.ImportTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@SupportedAnnotationTypes("*")
public class FatJarProcessor extends AbstractProcessor implements TaskListener {

  private final Set<URL> pendingJars = new HashSet<>();
  private final Set<URL> processedJars = new HashSet<>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    JavacTask task = JavacTask.instance(processingEnv);
    task.addTaskListener(this);
  }

  @Override
  public void started(TaskEvent e) {
  }

  @Override
  public void finished(TaskEvent e) {

    if (e.getKind() == TaskEvent.Kind.ANALYZE) {
      TreeVisitor<Void, Void> visitor = new TreeScanner<Void, Void>() {

        @Override
        public Void visitImport(ImportTree node, Void v) {
          String fqn = node.getQualifiedIdentifier().toString();
          readFqn(fqn);
          return v;
        }

        @Override
        public Void visitVariable(VariableTree node, Void v) {
          Tree type = node.getType();
          if (type instanceof MemberSelectTree) {
            String fqn = type.toString();
            readFqn(fqn);
          }
          return super.visitVariable(node, v);
        }
      };

      e.getCompilationUnit().accept(visitor, null);
    }

    if (e.getKind() == TaskEvent.Kind.GENERATE) {
      writePendingJars();
    }
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
/*
    if (roundEnv.processingOver()) {
      writePendingJars();
    } else {
      for (Element element : roundEnv.getRootElements()) {
        TreePath treePath = trees.getPath(element);
        if (treePath != null) {
          CompilationUnitTree unit = treePath.getCompilationUnit();
          for (ImportTree importTree : unit.getImports()) {
            String fqn = importTree.getQualifiedIdentifier().toString();
            readFqn(fqn);
          }
        }
      }
    }
*/
    return true;
  }

  private static Field outField;

  private static OutputStream getMostEfficientOutputStream(OutputStream abc) {
    if (abc instanceof FilterOutputStream) {
      // FilterOutputStream is a real performance hog
      try {
        if (outField == null) {
          outField = FilterOutputStream.class.getDeclaredField("out");
          outField.setAccessible(true);
        }
        return (OutputStream) outField.get(abc);
      } catch (Exception ignore) {
      }
    }
    return abc;
  }

  private void writePendingJars() {
    byte[] buffer = new byte[4096];
    HashMap<String, FileObject> using = new HashMap<>();
    while (pendingJars.size() > 0) {
      Iterator<URL> i = pendingJars.iterator();
      URL jarURL = i.next();
      i.remove();
      try {
        System.out.println("copying = " + jarURL);
        JarFile file = new JarFile(jarURL.getFile());
        for (JarEntry entry : Collections.list(file.entries())) {
          if (!entry.isDirectory()) {
            try(InputStream in = new BufferedInputStream(file.getInputStream(entry))) {
              String resourceName = entry.getName();
              if (!using.containsKey(resourceName)) {
                FileObject resource = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourceName);
                using.put(resourceName, resource);
                try (OutputStream out = new BufferedOutputStream(getMostEfficientOutputStream(resource.openOutputStream()))) {
                  while (true) {
                    int read = in.read(buffer);
                    if (read == -1) {
                      break;
                    }
                    out.write(buffer, 0, read);
                  }
                }
              } else {
                System.out.println("Clash " + resourceName);
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void readFqn(String fqn) {
    if (fqn.startsWith("java.") || fqn.startsWith("javax.") || fqn.startsWith("com.sun.") || fqn.startsWith("org.w3c.")) {
      return;
    }
    int pos = fqn.lastIndexOf('.');
    FileObject obj;
    try {
      obj = processingEnv.getFiler().getResource(StandardLocation.CLASS_PATH, fqn.substring(0, pos), fqn.substring(pos + 1) + ".class");
    } catch (IOException e) {
      return;
    }
    try {
      URL url = obj.toUri().toURL();
      if (url.getProtocol().equals("jar")) {
        JarURLConnection conn = (JarURLConnection) url.openConnection();
        URL jarURL = conn.getJarFileURL();
        if (!processedJars.contains(jarURL)) {
          pendingJars.add(jarURL);
          processedJars.add(jarURL);
//          System.out.println("Copying " + jarURL);
          JarFile file = conn.getJarFile();
          for (JarEntry entry : Collections.list(file.entries())) {
            if (!entry.isDirectory()) {

              //
              if (entry.getName().endsWith(".class")) {
                try(InputStream in = file.getInputStream(entry)) {
                  ClassReader reader = new ClassReader(in);
                  reader.accept(new ClassVisitor(Opcodes.ASM5) {

                    int onType(String typeDesc, int from) {
                      switch (typeDesc.charAt(from)) {
                        case 'L': {
                          int to = typeDesc.indexOf(';', from);
                          String fqn = typeDesc.substring(from + 1, to).replace('/', '.');
                          readFqn(fqn);
                          return to + 1;
                        }
                        case '[': {
                          return onType(typeDesc, from + 1);
                        }
                        default:
                          return from + 1;
                      }
                    }

                    @Override
                    public void visit(int i, int i2, String s, String s2, String s3, String[] strings) {
                    }
                    @Override
                    public void visitSource(String s, String s2) {
                    }
                    @Override
                    public void visitOuterClass(String s, String s2, String s3) {
                    }
                    @Override
                    public AnnotationVisitor visitAnnotation(String s, boolean b) {
                      onType(s, 0);
                      return new AnnotationVisitor(Opcodes.ASM5) {
                        @Override
                        public void visit(String name, Object value) {
                          if (value instanceof Type) {
                            Type type = (Type) value;
                            // ? Handler this
                          }
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String name, String desc) {
                          onType(s, 0);
                          return this;
                        }
                      };
                    }
                    @Override
                    public void visitAttribute(Attribute attribute) {
                    }
                    @Override
                    public void visitInnerClass(String s, String s2, String s3, int i) {
                    }
                    @Override
                    public FieldVisitor visitField(int i, String s, String s2, String s3, Object o) {
                      onType(s2, 0);
                      return null;
                    }
                    @Override
                    public MethodVisitor visitMethod(int i, String s, String s2, String s3, String[] strings) {
                      int index = 1;
                      while (s2.charAt(index) != ')') {
                        index = onType(s2, index);
                      }
                      onType(s2, index + 1);
                      return null;
                    }
                    @Override
                    public void visitEnd() {
                    }
                  }, 0);
                }
              }
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
