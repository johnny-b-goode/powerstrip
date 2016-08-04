package net.scientifichooliganism.javaplug.query;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

public class Query {
	private final Pattern selectPattern = Pattern.compile("^([\\w\\s,]+?)??(?:FROM|WHERE|$)", Pattern.CASE_INSENSITIVE);
	private final Pattern fromPattern = Pattern.compile("FROM ([\\w\\s,]+?)(?:WHERE|$)", Pattern.CASE_INSENSITIVE);
	private final Pattern wherePattern = Pattern.compile("WHERE (.*?)(?:FROM|$)", Pattern.CASE_INSENSITIVE);
	private final Pattern tokenizePattern = Pattern.compile("([\"']).*?\\1|[\\w.]+|&&|\\|\\||\\(|\\)|==|!=|>=|<=|<|>|!");

	private String queryString;
	private String[] selectValues;
	private String[] fromValues;
	private String whereString;
	private String[] wherePrefix = null;
	

	public Query () {
		this("Configuration");
	}

	public Query (String queryString) {
		setQueryString(queryString);
		parseQuery();
	}

	public Query (Query query) {
		copy(query);
	}

	public void copy (Query query) {
		setQueryString(query.getQueryString());
		setSelectValues(query.getSelectValues());
		setFromValues(query.getFromValues());
		setWhereString(query.getWhereString());
		setWherePrefix(query.getWherePrefix());
	}

	public QueryNode buildTree () {
		QueryNode ret = null;

		if(getWherePrefix() != null) {
			Stack<String> prefixStack = new Stack<>();

			for (int i = wherePrefix.length - 1; i >= 0; i--) {
				prefixStack.push(wherePrefix[i]);
			}

			ret = new QueryNode();
			buildTree(ret, prefixStack);
		}

		return ret;
	}

	private void buildTree (QueryNode current, Stack<String> prefix) {
		String value = prefix.pop();
		current.setValue(value);

		if(current.isOperator()){
			if(current.getOperator() == QueryOperator.NOT){
				QueryNode left = new QueryNode();
				buildTree(left, prefix);
				left.setParent(current);
				current.setLeftChild(left);
				current.setRightChild(null);
			}
			else {
				QueryNode left = new QueryNode();
				QueryNode right = new QueryNode();
				buildTree(left, prefix);
				buildTree(right, prefix);
				left.setParent(current);
				right.setParent(current);
				current.setLeftChild(left);
				current.setRightChild(right);
			}
		}
	}

	public static void validateQuery (String query) throws IllegalArgumentException {
		if (query == null) {
			throw new IllegalArgumentException("validateQuery(String) String is null");
		}

		if (query.trim().length() <= 0) {
			throw new IllegalArgumentException("validateQuery(String) String is empty");
		}
	}

	public void parseQuery () {
		parseQuery(getQueryString());
	}

	public void parseQuery (String queryStr) {
		validateQuery(queryStr);
		String selectString = null;
		String fromString = null;
		String whereString = null;

		// Divide query into its 3 possible parts
		if (queryStr != null && !queryStr.isEmpty()) {
			Matcher selectMatcher = selectPattern.matcher(queryStr);

			if (selectMatcher.find() && selectMatcher.group(1) != null) {
				selectString = selectMatcher.group(1).trim();
				queryStr = queryStr.replaceFirst(selectString, "").trim();
			}
		}

		if (queryStr != null && !queryStr.isEmpty()) {
			Matcher fromMatcher = fromPattern.matcher(queryStr);

			if (fromMatcher.find() && fromMatcher.group(1) != null) {
				fromString = fromMatcher.group(1).trim();
				queryStr = queryStr.replaceFirst("FROM " + fromString, "").trim();
			}
		}

		if (queryStr != null && !queryStr.isEmpty()) {
			Matcher whereMatcher = wherePattern.matcher(queryStr);

			if (whereMatcher.find() && whereMatcher.group(1) != null) {
				whereString = whereMatcher.group(1).trim();
				queryStr = queryStr.replace("WHERE " + whereString, "").trim();
			}
		}

		if (queryStr == null || !queryStr.equals("")) {
			throw new RuntimeException("Query.parse(String) did not consume the entire query!");
		}

		// populate query with parsed parts
		if (selectString != null) {
			String selectParts[] = selectString.split("[, ]+");

			for (int i = 0; i < selectParts.length; i++){
				selectParts[i] = selectParts[i].trim();
			}

			setSelectValues(selectParts);
		}

		if (fromString != null){
			String fromParts[] = fromString.split("[, ]+");

			for (int i = 0; i < fromParts.length; i++) {
				fromParts[i] = fromParts[i].trim();
			}

			setFromValues(fromParts);
		}

		if (whereString != null) {
			setWhereString(whereString);
			String expressionParts[] = tokenizeExpression(whereString);
			System.out.println();
			System.out.println("Where String: " + whereString);
			String prefix[] = infixToPrefix(expressionParts);
			setWherePrefix(prefix);
		}
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
			}
			else if(expression[i].equals(")")){
				expressionStack.push("(");
			}
			else {
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
				}
				else if(nextOperator == QueryOperator.RIGHT_PAREN) { // On right paren clear till left paren
					while(operatorStack.peek() != QueryOperator.LEFT_PAREN){
						result.add(operatorStack.pop().toString());
					}

					operatorStack.pop();
				}
				else {
					// Here we ensure the new operator has a higher precedence then anything else on the stack
					// This will result in the proper pre-fix notation
					while(!operatorStack.isEmpty() &&
							!(nextOperator.getPrecedence() > operatorStack.peek().getPrecedence()) &&
							operatorStack.peek() != QueryOperator.LEFT_PAREN){
						result.add(operatorStack.pop().toString());
					}

					operatorStack.push(nextOperator);
				}
			}
			else {
				// If next element is property, just push to result
				result.add(nextElement);
			}
		}

		// Once expression stack is empty, then empty the operator stack
		// adding each operation to the end of the result
		while(! operatorStack.isEmpty()) {
			result.add(operatorStack.pop().toString());
		}

		String ret[] = new String[result.size()];

		for(int i = 0; i < ret.length; i++){
			ret[i] = result.pollLast();
		}

		return ret;
	}

	public String toString(){
		return String.valueOf(getQueryString());
	}

	public void setWherePrefix(String in[]){
		wherePrefix = in;
	}

	public String[] getWherePrefix(){
		return wherePrefix;
	}

	public void setQueryString(String in){
		queryString = in;
	}

	public String getQueryString(){
		return queryString;
	}

	public void setWhereString(String in){
		whereString = in;
	}

	public String getWhereString(){
		return whereString;
	}

	public void setSelectValues(String values[]){
		selectValues = values;
	}

	public String[] getSelectValues(){
		return selectValues;
	}

	public void setFromValues(String values[]){
		fromValues = values;
	}

	public String[] getFromValues(){
		return fromValues;
	}
}
