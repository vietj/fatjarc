package methodreturn.unqualified;

import basicclass.Foo;

import java.lang.UnsupportedOperationException;

public class Bar {

  public Foo m() {
    throw new UnsupportedOperationException();
  }
}