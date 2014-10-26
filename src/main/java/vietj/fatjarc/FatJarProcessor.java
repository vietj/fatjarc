package vietj.fatjarc;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreeScanner;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

    if (e.getKind() == TaskEvent.Kind.GENERATE) {

      TreeVisitor<Void, Void> visitor = new TreeScanner<Void, Void>() {
        @Override
        public Void visitClass(ClassTree node, Void v) {

          String simpleName = node.getSimpleName().toString();
          try {
            FileObject o = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT,
                e.getCompilationUnit().getPackageName().toString(),
                simpleName + ".class");
            readClassFile(o.openInputStream());
          } catch (IOException e1) {
            e1.printStackTrace();
          }

          return super.visitClass(node, v);
        }
      };

      e.getCompilationUnit().accept(visitor, null);

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
              if (entry.getName().endsWith(".class")) {
                try(InputStream in = file.getInputStream(entry)) {
                  readClassFile(in);
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

  private void readClassFile(InputStream in) throws IOException {
    List<String> ss = foo(in);
    for (String s : ss) {
      readFqn(s);
    }
  }

  static int readDescriptor(String typeDesc, int from, List<String> collector) {
    switch (typeDesc.charAt(from)) {
      case 'L': {
        int to = typeDesc.indexOf(';', from);
        String fqn = typeDesc.substring(from + 1, to).replace('/', '.');
        collector.add(fqn);
        return to + 1;
      }
      case '[': {
        return readDescriptor(typeDesc, from + 1, collector);
      }
      default:
        return from + 1;
    }
  }

  static List<String> foo(InputStream _in) throws IOException {

    List<String> names = new ArrayList<>();


    DataInputStream in = new DataInputStream(_in);
    in.readInt(); // 0xCAFEBABE
    int minor = in.readUnsignedShort();
    int major = in.readUnsignedShort();
    int constantPoolCount = in.readUnsignedShort();
    String[] strings = new String[constantPoolCount];
    List<Integer> refs = new ArrayList<>();
    for (int i = 1;i < constantPoolCount;i++) {
      int tag = in.readUnsignedByte();
      switch (tag) {
        case 1:  // String
          int len = in.readUnsignedShort();
          byte[] data = new byte[len];
          int index = 0;
          while (index < len) {
            int a = in.read(data, index, data.length - index);
            if (a == -1) {
              throw new AssertionError("bug");
            } else {
              index += a;
            }
          }
          strings[i] = new String(data);
          break;
        case 3:  // Integer
        case 4:  // Float
          in.readInt();
          break;
        case 5: //  Long
        case 6: //  Double
          in.readLong();
          i++;
          break;
        case 7:  // Class reference
          refs.add(in.readUnsignedShort());
          break;
        case 8:  // String reference
          in.readShort();;
          break;
        case 9:  // Field reference
          in.readShort();
          in.readShort();
          break;
        case 10: // Method reference
        case 11: // Interface method reference
          in.readInt();
          break;
        case 12: // Name and type descriptor
          in.readShort();
          in.readShort();
          break;
        case 0:
          break;
        default:
          throw new UnsupportedOperationException("Todo " + tag);
      }
    }
    for (int classReference : refs) {
      String ref = strings[classReference];
      if (ref.charAt(0) == '[') {
        readDescriptor(ref, 0, names);
      } else {
        names.add(ref.replace('/', '.'));
      }
    }
    in.readUnsignedShort(); // access_flags
    in.readUnsignedShort(); // this_class
    in.readUnsignedShort(); // super_class
    int interfaces_count = in.readUnsignedShort(); // interfaces_count
    for (int i = 0;i < interfaces_count;i++) {
      in.readUnsignedShort(); // interfaces[]
    }
    int fields_count = in.readUnsignedShort(); // fields_count
    for (int i = 0;i < fields_count;i++) {
      in.readUnsignedShort(); // access_flags
      in.readUnsignedShort(); // name_index
      int descriptor_index = in.readUnsignedShort(); // descriptor_index
      readDescriptor(strings[descriptor_index], 0, names);
      readAttributes(in);
    }
    int methods_count = in.readUnsignedShort(); // methods_count
    for (int i = 0;i < methods_count;i++) {
      in.readUnsignedShort(); // access_flags
      in.readUnsignedShort(); // name_index
      int descriptor_index = in.readUnsignedShort(); // descriptor_index
      String ss = strings[descriptor_index];
      int j = 1;
      while (ss.charAt(j) != ')') {
        j = readDescriptor(ss, j, names);
      }
      readDescriptor(ss, j + 1, names);
      readAttributes(in);
    }

    //
    return names;
  }

  private static void readAttributes(DataInputStream in) throws IOException {
    int attributes_count = in.readUnsignedShort(); // attributes_count
    for (int j = 0;j < attributes_count;j++) {
      in.readUnsignedShort(); // attribute_name_index
      int attribute_length = in.readInt(); // attribute_length
      while (attribute_length > 0) {
        if (in.read() == -1) {
          throw new AssertionError();
        } else {
          attribute_length--;
        }
      }
    }
  }

}
