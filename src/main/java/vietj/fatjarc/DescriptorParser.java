package vietj.fatjarc;

import java.util.List;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class DescriptorParser {

  static int parseFieldDescriptor(String s, List<String> collector) throws ParseException {
    return parseFieldType(0, s, collector);
  }

  static int parseMethodDescriptor(String s, List<String> collector) throws ParseException {
    if (s.charAt(0) != '(') {
      throw new AssertionError();
    }
    int index = 1;
    while (s.charAt(index) != ')') {
      index = parseParameterDescriptor(index, s, collector);
    }
    return parseReturnDescriptor(index + 1, s, collector);
  }

  static int parseParameterDescriptor(int index, String s, List<String> collector) throws ParseException {
    return parseFieldType(index, s, collector);
  }

  static int parseReturnDescriptor(int index, String s, List<String> collector) throws ParseException {
    try {
      return parseFieldType(index, s, collector);
    } catch (ParseException e) {
      // Alternative
    }
    if (s.charAt(index) == 'V') {
      return index + 1;
    }
    throw new ParseException();
  }

  static int parseFieldType(int index, String s, List<String> collector) throws ParseException {
    try {
      return parseBaseType(index, s);
    } catch (ParseException e) {
      // Alternative
    }
    try {
      return parseObjectType(index, s, collector);
    } catch (ParseException e) {
      // Alternative
    }
    return parseArrayType(index, s, collector);
  }

  static int parseBaseType(int index, String s) throws ParseException {
    return SignatureParser.parseBaseType(index, s);
  }

  static int parseObjectType(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == 'L') {
      int pos = s.indexOf(';', index + 1);
      collector.add(s.substring(index + 1, pos).replace('/', '.'));
      return pos + 1;
    }
    throw new ParseException();
  }

  static int parseArrayType(int index, String s, List<String> collector) throws ParseException {
    if (s.charAt(index) == '[') {
      return parseComponentType(index + 1, s, collector);
    }
    throw new ParseException();
  }

  static int parseComponentType(int index, String s, List<String> collector) throws ParseException {
    return parseFieldType(index, s, collector);
  }
}
