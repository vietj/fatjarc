package signature.lowerboundofparameterizedmethodparameter.unqualified;

import java.util.List;
import basicclass.Foo;

public class Bar {

  public void m(List<? super Foo> foo) {
  }
}