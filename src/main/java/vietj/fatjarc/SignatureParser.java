package vietj.fatjarc;

import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SignatureParser {

  static int parseMethodTypeSignature(String s, List<String> collector) throws ParseException {
    if (s.startsWith("<")) {
      return parseFormalTypeParameters(0, s, collector);
    } else if (s.startsWith("(")) {
      int index = 1;
      while (s.charAt(index) != ')') {
        index = parseTypeSignature(index, s, collector);
      }
      return index;
    } else {
      throw new AssertionError("parse error");
    }
  }

  static int parseFormalTypeParameters(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index++) != '<') {
      throw new AssertionError("parse error");
    } else {
      while (s.charAt(index) != '>') {
        index = parseFormalTypeParameter(index, s, collector);
      }
      return index;
    }
  }

  static int parseFormalTypeParameter(int index, String s, List<String> collector) throws ParseException {
    index = s.indexOf(':', index) + 1;
    try {
      index = parseFieldTypeSignature(index, s, collector);
    } catch (ParseException ignore) {
      // Optional
    }
    while (index + 1 < s.length() && s.charAt(index + 1) == ':') {
      throw new UnsupportedOperationException();
    }
    return index;
  }

  static int parseTypeSignature(int index, String s, List<String> collector) throws ParseException {
    try {
      return parseBaseType(index, s);
    } catch (ParseException ignore) {
      // Alternative
    }
    return parseFieldTypeSignature(index, s, collector);
  }

  static int parseFieldTypeSignature(int index, String s, List<String> collector) throws ParseException {
    try {
      return parseClassTypeSignature(index, s, collector);
    } catch (ParseException ingore) {
      // Alternative
    }
    try {
      return parseArrayTypeSignature(index, s);
    } catch (ParseException ignore) {
      // Alternative
    }
    return parseTypeVariableSignature(index, s);
  }

  static int parseTypeVariableSignature(int index, String s) throws ParseException {
    if (s.charAt(index) == 'T') {
      throw new UnsupportedOperationException();
    } else {
      throw new ParseException();
    }
  }

  static int parseArrayTypeSignature(int index, String s) throws ParseException {
    if (s.charAt(index) == '[') {
      throw new UnsupportedOperationException();
    }
    throw new ParseException();
  }

  static int parseClassTypeSignature(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == 'L') {
      int next = index;
      out:
      while (true) {
        switch (s.charAt(next)) {
          case '<':
            next = parseTypeArguments(next, s, collector);
            if (next + 1 < s.length() && s.charAt(next + 1) == '.') {
              throw new UnsupportedOperationException("ClassTypeSignatureSuffix");
            } else {
              break out;
            }
          case '.':
            throw new UnsupportedOperationException("ClassTypeSignatureSuffix");
          case ';':
            // Parse type
            break out;
          default:
            next++;
        }
      }
      collector.add(s.substring(index + 1, next).replace('/', '.'));
      return next + 1;
    } else {
      throw new ParseException();
    }
  }

  static int parseTypeArguments(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index++) != '<') {
      throw new AssertionError("parse error");
    } else {
      while (s.charAt(index) != '>') {
        index = parseTypeArgument(index, s, collector);
      }
      return index + 1;
    }
  }

  static int parseTypeArgument(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == '*') {
      return index + 1;
    } else {
      try {
        index = parseWildcardIndicator(index, s);
      } catch (ParseException ignore) {
        // Optional
      }
      return parseFieldTypeSignature(index, s, collector);
    }
  }

  static int parseWildcardIndicator(int index, String s) throws ParseException {
    switch (s.charAt(index)) {
      case '+':
      case '-':
        return index + 1;
      default:
        throw new ParseException();
    }
  }

  static int parseBaseType(int index, String s) throws ParseException {
    switch (s.charAt(index)) {
      case 'B':
      case 'C':
      case 'D':
      case 'F':
      case 'I':
      case 'J':
      case 'S':
      case 'Z':
        return index + 1;
      default:
        throw new ParseException();
    }
  }
}
