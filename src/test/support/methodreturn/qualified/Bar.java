package methodreturn.qualified;

import java.lang.UnsupportedOperationException;

public class Bar {

  public basic.Foo m() {
    throw new UnsupportedOperationException();
  }
}