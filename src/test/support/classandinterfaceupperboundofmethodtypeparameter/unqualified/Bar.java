package classandinterfaceupperboundofmethodtypeparameter.unqualified;

import basicclass.Foo;
import basicinterface.Juu;

public class Bar {

  public <T extends Foo & Juu> void m(T t) {
  }
}