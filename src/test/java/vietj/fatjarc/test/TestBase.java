package vietj.fatjarc.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class TestBase {

  static Compiler compiler(File dst) {
    return new Compiler(dst);
  }

  static void jar(File src, File dst) {
    if (dst.exists()) {
      throw new AssertionError("Dst file already exists " + dst.getAbsolutePath());
    }
    if (!src.exists()) {
      throw new AssertionError("Src does not exists " + src.getAbsolutePath());
    }
    assertCreateFile(dst);
    int srcNameCount = src.toPath().getNameCount();
    try {
      try(JarOutputStream jar = new JarOutputStream(new FileOutputStream(dst))) {
        Files.walkFileTree(src.toPath(), new FileVisitor<Path>() {
          @Override
          public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (dir.getNameCount() > srcNameCount) {
              jar.putNextEntry(new JarEntry(dir.subpath(srcNameCount, dir.getNameCount()).toString() + '/'));
              jar.closeEntry();
            }
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            jar.putNextEntry(new JarEntry(file.subpath(srcNameCount, file.getNameCount()).toString()));
            Files.copy(file, jar);
            jar.closeEntry();
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            throw new AssertionError();
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
          }
        });
      }
    } catch (IOException e) {
      throw new AssertionError("Could not create jar", e);
    }
  }

  static void assertFile(File f) {
    assertTrue("Was expecting " + f.getAbsolutePath() + " to exist", f.exists());
    assertTrue("Was expecting " + f.getAbsolutePath() + " to be a file", f.isFile());
  }

  static void assertDir(File dir) {
    assertTrue("Was expecting " + dir.getAbsolutePath() + " to exist", dir.exists());
    assertTrue("Was expecting " + dir.getAbsolutePath() + " to be a dir", dir.isDirectory());
  }

  static void assertCreateFile(File f) {
    assertCreateDir(f.getParentFile());
    try {
      assertTrue(f.createNewFile());
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  static void assertCreateDir(File dir) {
    if (!dir.exists()) {
      assertTrue("Could not create dir " + dir, dir.mkdirs());
    }
  }
}
