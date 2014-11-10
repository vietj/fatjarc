package vietj.fatjarc;

import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class SignatureParser {

  static void parseFieldTypeSignature(String s, List<String> collector) throws ParseException {
    parseFieldTypeSignature(0, s, collector);
  }

  static void parseMethodTypeSignature(String s, List<String> collector) throws ParseException {
    parseMethodTypeSignature(0, s, collector);
  }

  static void parseClassSignature(String s, List<String> collector) throws ParseException {
    parseClassSignature(0, s, collector);
  }

  static int parseClassSignature(int index, String s, List<String> collector) throws ParseException {
    try {
      index = parseFormalTypeParameters(index, s, collector);
    } catch (ParseException e) {
      // Optional
    }
    index = parseSuperclassSignature(index, s, collector);
    while (index < s.length()) {
      index = parseSuperinterfaceSignature(index, s, collector);
    }
    return index;
  }

  static int parseSuperclassSignature(int index, String s, List<String> collector) throws ParseException {
    return parseClassTypeSignature(index, s, collector);
  }

  static int parseSuperinterfaceSignature(int index, String s, List<String> collector) throws ParseException {
    return parseClassTypeSignature(index, s, collector);
  }

  static int parseMethodTypeSignature(int index, String s, List<String> collector) throws ParseException {
    try {
      index = parseFormalTypeParameters(0, s, collector);
    } catch (ParseException e) {
      // Optional
    }
    if (s.charAt(index) != '(') {
      throw new AssertionError("parse error");
    }
    index++;
    while (s.charAt(index) != ')') {
      index = parseTypeSignature(index, s, collector);
    }
    // Todo ReturnType and ThrowsSignature
    return index;
  }

  static int parseFormalTypeParameters(int index, String s, List<String> collector) throws ParseException {
    if (index >= s.length() || s.charAt(index++) != '<') {
      throw new ParseException();
    } else {
      while (s.charAt(index) != '>') {
        index = parseFormalTypeParameter(index, s, collector);
      }
      return index + 1;
    }
  }

  private static boolean isIdentifierDelimiter(char c) {
    return c != '.' && c != ';' && c != '[' && c != '/' && c != '<' && c != '>' && c != ':';
  }

  static int parseIdentifier(int index, String s) {
    while (index < s.length() && isIdentifierDelimiter(s.charAt(index))) {
      index++;
    }
    return index;
  }

  static int parseFormalTypeParameter(int index, String s, List<String> collector) throws ParseException {
    index = parseIdentifier(index, s);
    try {
      index = parseFieldTypeSignature(index + 1, s, collector);
    } catch (ParseException ignore) {
      // Optional
      index++;
    }
    while (index< s.length() && s.charAt(index) == ':') {
      index = parseFieldTypeSignature(index + 1, s, collector);
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
      return parseArrayTypeSignature(index, s, collector);
    } catch (ParseException ignore) {
      // Alternative
    }
    return parseTypeVariableSignature(index, s);
  }

  static int parseTypeVariableSignature(int index, String s) throws ParseException {
    if (s.charAt(index) == 'T') {
      index = parseIdentifier(index + 1, s);
      if (s.charAt(index) != ';') {
        throw new AssertionError();
      }
      return index + 1;
    } else {
      throw new ParseException();
    }
  }

  static int parseArrayTypeSignature(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == '[') {
      return parseTypeSignature(index + 1, s, collector);
    }
    throw new ParseException();
  }

  static int parseClassTypeSignature(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == 'L') {
      int from = index + 1;
      try {
        index = parsePackageSpecifier(index, s);
      } catch (ParseException e) {
        // Opt
      }
      index = parseSimpleClassTypeSignature(index, s, collector);
      int to = index;
      while (s.charAt(index) != ';') {
        index = parseClassTypeSignatureSuffix(index, s, collector);
      }
      collector.add(s.substring(from, to).replace('/', '.'));
      return index + 1;
    } else {
      throw new ParseException();
    }
  }

  static int parseSimpleClassTypeSignature(int index, String s, List<String> collector) {
    index = parseIdentifier(index, s);
    try {
      index = parseTypeArguments(index, s, collector);
    } catch (ParseException e) {
      // Opt
    }
    return index;
  }

  static int parsePackageSpecifier(int index, String s) throws ParseException {
    index = parseIdentifier(index, s);
    if (s.charAt(index) != '/') {
      throw new ParseException();
    }
    index++;
    while (true) {
      try {
        index = parsePackageSpecifier(index, s);
      } catch (ParseException e) {
        // *
        return index;
      }
    }
  }

  static int parseClassTypeSignatureSuffix(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) != '.') {
      throw new ParseException();
    }
    return parseSimpleClassTypeSignature(index + 1, s, collector);
  }

  static int parseTypeArguments(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index++) != '<') {
      throw new ParseException();
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
        return index + 1;
      case 'C':
        return index + 1;
      case 'D':
        return index + 1;
      case 'F':
        return index + 1;
      case 'I':
        return index + 1;
      case 'J':
        return index + 1;
      case 'S':
        return index + 1;
      case 'Z':
        return index + 1;
      default:
        throw new ParseException();
    }
  }
}
