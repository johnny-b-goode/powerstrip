package net.scientifichooliganism.javaplug.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryResolver {
    private static QueryResolver instance;
    private final Pattern selectPattern = Pattern.compile("^([\\w\\s,]+?)??(?:FROM|WHERE|$)", Pattern.CASE_INSENSITIVE);
    private final Pattern fromPattern = Pattern.compile("FROM ([\\w\\s,]+?)(?:WHERE|$)", Pattern.CASE_INSENSITIVE);
    private final Pattern wherePattern = Pattern.compile("WHERE (.*?)(?:FROM|$)", Pattern.CASE_INSENSITIVE);
    private final Pattern tokenizePattern = Pattern.compile("([\"']).*?\\1|[\\w.]+|&&|\\|\\||\\(|\\)|==|!=|>=|<=|<|>|!");

    private QueryResolver(){

    }

    public static QueryResolver getInstance(){
        if(instance == null){
            instance = new QueryResolver();
        }
        return instance;
    }

    public static void main(String args[]){
        String testStrings[] = new String[]{
                "Action",
                "Configuration",
                "Action WHERE Action.name == \"query\"",
                "Configuration WHERE Configuration.module == \"XMLDataStore\"",
                "Actions FROM XMLDataStore",
                "Configuration FROM XMLDataStore WHERE Configuration.key == \"depends\"",
                "Configuration FROM XMLDataStore, JSONDataStore WHERE Configuration.key == \"depends\"",
                "Environment, Release WHERE Environment.name == Release.name",
                "Environment, Release WHERE Environment.name == Release.name && Release.name == \"zebra\"",
                "Environment, Task WHERE Task.data.key == \"Environment.id\" && task.data.value == \"2\"",
                "Environment, Task WHERE Task.data.key == \"Environment.name\" && task.data.value == \"production\"",
                "WHERE Action.name == \"query\"",
                "WHERE Configuration.module == \"XMLDataStore\"",
                "FROM XMLDataStore",
                "FROM XMLDataStore WHERE Configuration.key == \"depends\"",
                "WHERE Configuration.key == \"depends\" FROM XMLDataStore",
                "WHERE Environment.name == Release.name",
                "WHERE Environment.name == Release.name && Release.name == \"zebra\"",
                "WHERE Task.data.key == \"Environment.id\" && task.data.value == \"2\"",
                "WHERE Task.data.key == \"Environment.name\" && task.data.value == \"production\"",
                "WHERE Task.data.key != \"Environment.name\" && !(task.data.value == !'production startup')",
                "WHERE Task.data.key != \"Environment.name\" && !(task.data.value == 'production startup' || x != 5 && test == 6)"
        };

        for(int i = 0; i < testStrings.length; i++){
            QueryResolver resolver = QueryResolver.getInstance();
            Query temp = resolver.resolve(testStrings[i]);

            System.out.println("Prefix: ");
            for(int j = 0; temp.getWherePrefix() != null && j < temp.getWherePrefix().length; j++){
                System.out.print(temp.getWherePrefix()[j] + " ");
            }
            System.out.println();

            QueryNode tree = temp.buildTree();
            if(tree != null) {
                tree.consolePrint();
            }
        }
    }

    public Query resolve(String queryStr){
        String selectString = null;
        String fromString = null;
        String whereString = null;

        Query ret = new Query(queryStr);

        // Divide query into its 3 possible parts
        if(queryStr != null && !queryStr.isEmpty()) {
            Matcher selectMatcher = selectPattern.matcher(queryStr);
            if (selectMatcher.find() && selectMatcher.group(1) != null){
                selectString = selectMatcher.group(1).trim();
                queryStr = queryStr.replaceFirst(selectString, "").trim();
            }
        }

        if(queryStr != null && !queryStr.isEmpty()) {
            Matcher fromMatcher = fromPattern.matcher(queryStr);
            if (fromMatcher.find() && fromMatcher.group(1) != null){
                fromString = fromMatcher.group(1).trim();
                queryStr = queryStr.replaceFirst("FROM " + fromString, "").trim();
            }
        }

        if(queryStr != null && !queryStr.isEmpty()) {
            Matcher whereMatcher = wherePattern.matcher(queryStr);
            if(whereMatcher.find() && whereMatcher.group(1) != null){
                whereString = whereMatcher.group(1).trim();
                queryStr = queryStr.replace("WHERE " + whereString, "").trim();
            }
        }

        if(queryStr == null || !queryStr.equals("")){
            throw new RuntimeException("QueryResolver.resolve(String) did not consume the entire query!");
        }

        // populate query with parsed parts
        if(selectString != null) {
            String selectParts[] = selectString.split("[, ]+");
            for(int i = 0; i < selectParts.length; i++){
                selectParts[i] = selectParts[i].trim();
            }
            ret.setSelectValues(selectParts);
        }

        if(fromString != null){
            String fromParts[] = fromString.split("[, ]+");
            for(int i = 0; i < fromParts.length; i++){
                fromParts[i] = fromParts[i].trim();
            }
            ret.setFromValues(fromParts);
        }

        if(whereString != null) {
            String expressionParts[] = tokenizeExpression(whereString);
            System.out.println();
            System.out.println("Where String: " + whereString);
            String prefix[] = infixToPrefix(expressionParts);
            ret.setWherePrefix(prefix);
        }

        return ret;
    }

    public String[] tokenizeExpression(String expression){
        Matcher tokenizeMatcher = tokenizePattern.matcher(expression);
        List<String> tokenList = new ArrayList<>();

        while(tokenizeMatcher.find()){
            tokenList.add(tokenizeMatcher.group(0));
        }

        String[] ret = new String[tokenList.size()];
        tokenList.toArray(ret);

        return ret;
    }

    private String[] infixToPrefix(String[] expression){
        int length = expression.length;
        Stack<String> expressionStack = new Stack<>();
        Stack<QueryOperator> operatorStack = new Stack<>();
        LinkedList<String> result = new LinkedList<>();

        for(int i = 0; i < length; i++){
            // Parenthesis must be flipped as well
            if(expression[i].equals("(")){
                expressionStack.push(")");
            } else if(expression[i].equals(")")){
                expressionStack.push("(");
            } else {
                expressionStack.push(expression[i]);
            }
        }

        while(!expressionStack.isEmpty()){
            String nextElement = expressionStack.pop();

            if(QueryOperator.isOperator(nextElement)){
                QueryOperator nextOperator = QueryOperator.fromString(nextElement);
                // Here we implement logic to arrange operators in proper order for pre-fix notation

                // Always push if operator stack is empty, and treat left parenthesis as empty
                if(operatorStack.isEmpty() || operatorStack.peek() == QueryOperator.LEFT_PAREN){
                    operatorStack.push(nextOperator);
                } else if(nextOperator == QueryOperator.RIGHT_PAREN) { // On right paren clear till left paren
                    while(operatorStack.peek() != QueryOperator.LEFT_PAREN){
                        result.add(operatorStack.pop().toString());
                    }
                    operatorStack.pop();
                } else {
                    // Here we ensure the new operator has a higher precedence then anything else on the stack
                    // This will result in the proper pre-fix notation
                    while(!operatorStack.isEmpty() &&
                            !(nextOperator.getPrecedence() > operatorStack.peek().getPrecedence()) &&
                            operatorStack.peek() != QueryOperator.LEFT_PAREN){
                        result.add(operatorStack.pop().toString());
                    }
                    operatorStack.push(nextOperator);
                }
            } else {
                // If next element is property, just push to result
                result.add(nextElement);
            }
        }

        // Once expression stack is empty, then empty the operator stack
        // adding each operation to the end of the result
        while(!operatorStack.isEmpty()) {
            result.add(operatorStack.pop().toString());
        }

        String ret[] = new String[result.size()];
        for(int i = 0; i < ret.length; i++){
            ret[i] = result.pollLast();
        }
        return ret;
    }

//    private void infixToPrefixRec(Stack<String> expression, List<String> result){
//        infixToPrefixRec(expression, new Stack<QueryOperator>(), result);
//    }
//
//    private void infixToPrefixRec(Stack<String> expression, Stack<QueryOperator> operatorStack, List<String> result){
//        if(expression.isEmpty()){
//            while(!operatorStack.isEmpty()){
//                result.add(operatorStack.pop().toString());
//            }
//            return;
//        }
//        else {
//            String next = expression.pop();
//            if(QueryOperator.isOperator(next)){
//                QueryOperator nextOperator = QueryOperator.valueOf(next);
//                if(operatorStack.isEmpty()){
//                    operatorStack.push(nextOperator);
//                } else if (nextOperator == QueryOperator.RIGHT_PAREN) {
//                    while(operatorStack.peek() != QueryOperator.LEFT_PAREN){
//                        result.add(operatorStack.pop().toString());
//                    }
//                    // Pop the left paren
//                    operatorStack.pop();
//                } else if (operatorStack.peek().getPrecedence() < nextOperator.getPrecedence()
//                        || operatorStack.peek() == QueryOperator.LEFT_PAREN){
//                    operatorStack.push(nextOperator);
//                } else {
////                    while(operatorStack.peek().getPrecedence())
//                }
//            } else {
//                result.add(next);
//            }
//        }
//    }

//    private void parseQuery(){
//        // Regex separates the selections, stores and conditions into groups
//        Pattern queryPattern = Pattern.compile("^(?:([\\w\\s,]*?))?(?i: FROM ([\\w\\s,]*))?(?i: WHERE (.*))?$");
//        Matcher matcher = queryPattern.matcher(queryString);
//        if(matcher.group(1) != null){
//            selectString = matcher.group(1).trim();
//            selectObjects = selectString.split("(?:,\\s*|\\s+)");
//        }
//        if(matcher.group(2) != null){
//            fromString = matcher.group(2).trim();
//            dataStores = fromString.split("(?:,\\s*|\\s+)");
//        }
//        if(matcher.group(3) != null){
//            whereString = matcher.group(3).trim();
//        }
//
//    }

//    public String infixToPostfix(String in){
//        String input = in.replaceAll(" ", "").trim();
//        Map<String, Integer> precidence = new TreeMap<>();
//        precidence.put("!", 0);
//        precidence.put("||", 1);
//        precidence.put("&&", 2);
//        precidence.put("(", 3);
//
//        precidence.put("==", 4);
//        precidence.put("!=", 4);
//        precidence.put("<", 4);
//        precidence.put(">", 4);
//        precidence.put("<=", 4);
//        precidence.put(">=", 4);
//
//        try(StringReader reader = new StringReader(input);
//            StringWriter writer = new StringWriter()){
//            Stack<String> operators = new Stack<>();
//            Stack<String> operands = new Stack<>();
//
//            SortedSet<Character> operatorChars =  new TreeSet<Character>(Arrays.asList('!', '=', '<', '>', '&', '|', '(', ')'));
//
//            String currentOperand = "";
//            String currentOperator = "";
//            char inChar = (char)reader.read();
//            while((int)inChar != -1){
//                if((currentOperand.isEmpty() && Character.isJavaIdentifierStart(inChar))
//                        || Character.isJavaIdentifierPart(inChar)
//                        || Character.isDigit(inChar)
//                        || (Character.compare(inChar, '.') == 1)
//                        || (Character.compare(inChar, '\"') == 1)
//                        || (Character.compare(inChar, '\'') == 1)){
//                    currentOperand += inChar;
//                } else if (operatorChars.contains(inChar)) {
//                    currentOperator += inChar;
//                }
//
//                inChar = (char)reader.read();
//            }
//
//        } catch(Exception exc){
//            exc.printStackTrace();
//        }
//
//        return "";
//    }

}
