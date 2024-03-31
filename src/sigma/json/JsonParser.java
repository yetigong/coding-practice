package sigma.json;

import java.sql.Array;
import java.util.*;

public class JsonParser {

    static class Token {
        String type;
        Object value;

        public Token(String type, Object value) {
            this.type = type;
            this.value = value;
        }
    }
    static class Parser {

        Object parse(List<Token> tokens) {
            // using it as a stack to keep track of all our items
            Deque<List<Token>> deque = new LinkedList<>();
            List<Token> curr = new ArrayList<>();
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token.type.equals("start-object")) {
                    curr = new ArrayList<>();
                    deque.addLast(curr);
                    curr.add(token);
                } else if (token.type.equals("end-object")) {
                    curr.add(token);
                    deque.removeLast();
                    // remove curr from deque
                    Map<String, Object> jsonObj = createJsonObject(curr);
                    if (deque.isEmpty()) {
                        return jsonObj;
                    }
                    curr = deque.getLast();
                    curr.add(new Token("object", jsonObj));
                } else if (token.type.equals("start-array")) {
                    curr = new ArrayList<>();
                    deque.addLast(curr);
                    curr.add(token);
                } else if (token.type.equals("end-array")) {
                    curr.add(token);
                    deque.removeLast();
                    List<Object> jsonArray = createJsonArray(curr);
                    if (deque.isEmpty()) {
                        return jsonArray;
                    }
                    curr = deque.getLast();
                    curr.add(new Token("array", jsonArray));
                } else {
                    curr.add(token);
                }
            }
            return null;
        }

        Map<String, Object> createJsonObject(List<Token> curr) {
            Map<String, Object> obj = new HashMap<>();
            String key = null;
            for (int i = 0; i < curr.size(); i++) {
                Token token = curr.get(i);
                if (token.type.equals("start-object") || token.type.equals("end-object")) {
                    // no op
                }
                else if (token.type.equals("field-name")) {
                    key = (String) token.value;
                } else if (token.type.equals("string")) {
                    String value = (String) token.value;
                    obj.put(key, value);
                    key = null;
                } else if (token.type.equals("number")) {
                    int value = (int) token.value;
                    obj.put(key, value);
                    key = null;
                } else if (token.type.equals("boolean")) {
                    boolean value = (boolean) token.value;
                    obj.put(key, value);
                    key = null;
                } else if (token.type.equals("null")) {
                    obj.put(key, null);
                    key = null;
                } else if (token.type.equals("object")) {
                    obj.put(key, token.value);
                    key = null;
                } else if (token.type.equals("array")){ // array not handled yet
                    obj.put(key, token.value);
                    key = null;
                }
            }
            return obj;
        }

        List<Object> createJsonArray(List<Token> curr) {
            List<Object> array = new ArrayList<>();
            for (int i = 0; i < curr.size(); i++) {
                Token token = curr.get(i);
                if (token.type.equals("start-array") || token.type.equals("end-array")) {
                    // no op
                } else if (token.type.equals("string")) {
                    String value = (String) token.value;
                    array.add(value);
                } else if (token.type.equals("number")) {
                    int value = (int) token.value;
                    array.add(value);
                } else if (token.type.equals("boolean")) {
                    boolean value = (boolean) token.value;
                    array.add(value);
                } else if (token.type.equals("null")) {
                    array.add(null);
                } else if (token.type.equals("object")) {
                    array.add(token.value);
                } else if (token.type.equals("array")){
                    array.add(token.value);
                }
            }
            return array;
        }
    }

    public static void main(String[] args) {
        testArray();
    }

    public static void testBasic() {
        List<Token> tokens = List.of(
                new Token("start-object", null),
                new Token("field-name", "a"),
                new Token("number", 10),
                new Token("field-name", "b"),
                new Token("string", "foo"),
                new Token("end-object", null)
        );
        Parser parser = new Parser();
        System.out.println(parser.parse(tokens));
    }


    public static void testNested() {
        List<Token> tokens = List.of(
                new Token("start-object", null),
                new Token("field-name", "a"),
                new Token("number", 10),
                new Token("field-name", "b"),
                new Token("string", "foo"),
                new Token("field-name", "c"),
                new Token("start-array", null),
                new Token("null", null),
                new Token("number", 1),
                new Token("string", "array-element1"),
                new Token("start-object", null),
                new Token("field-name", "array-element"),
                new Token("string", "element2"),
                new Token("end-object", null),
                new Token("end-array", null),
                new Token("field-name", "d"),
                new Token("start-object", null),
                new Token("field-name", "e"),
                new Token("start-object", null),
                new Token("field-name", "f"),
                new Token("string", "nested"),
                new Token("end-object", null),
                new Token("end-object", null),
                new Token("end-object", null)
        );
        Parser parser = new Parser();
        System.out.println(parser.parse(tokens));
    }

    public static void testArray() {
        List<Token> tokens = List.of(

                new Token("start-array", null),
                new Token("null", null),
                new Token("number", 1),
                new Token("string", "array-element1"),
                new Token("start-object", null),
                new Token("field-name", "array-element"),
                new Token("string", "element2"),
                new Token("end-object", null),
                new Token("end-array", null)
        );
        Parser parser = new Parser();
        System.out.println(parser.parse(tokens));
    }
}
