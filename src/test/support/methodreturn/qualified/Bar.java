package methodreturn.qualified;

import java.lang.UnsupportedOperationException;

public class Bar {

  public basicclass.Foo m() {
    throw new UnsupportedOperationException();
  }
}