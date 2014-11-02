package methodreturn.unqualified;

import basic.Foo;

import java.lang.UnsupportedOperationException;

public class Bar {

  public Foo m() {
    throw new UnsupportedOperationException();
  }
}