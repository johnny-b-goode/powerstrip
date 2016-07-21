package net.scientifichooliganism.javaplug.query;

import java.util.Stack;

public class Query {
    private String queryString;
    private String[] selectValues;
    private String[] fromValues;
    private String[] wherePrefix = null;

    public Query(){
        this("");
    }

    public Query(String queryString){
        this.queryString = queryString;
        selectValues = new String[]{};
        fromValues = new String[]{};
    }

    public Query(Query query){
        this.queryString = query.queryString;
        this.selectValues = query.selectValues;
        this.fromValues = query.fromValues;
        this.wherePrefix = query.wherePrefix;
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

    public QueryNode buildTree(){
        if(getWherePrefix() != null) {
            Stack<String> prefixStack = new Stack<>();
            for (int i = wherePrefix.length - 1; i >= 0; i--) {
                prefixStack.push(wherePrefix[i]);
            }
            QueryNode root = new QueryNode();
            buildTree(root, prefixStack);
            return root;
        } else {
            return null;
        }
    }

    private void buildTree(QueryNode current, Stack<String> prefix) {
        String value = prefix.pop();
        current.setValue(value);

        if(current.isOperator()){
            if(current.getOperator() == QueryOperator.NOT){
                QueryNode left = new QueryNode();
                buildTree(left, prefix);
                left.setParent(current);
                current.setLeftChild(left);
                current.setRightChild(null);
            } else {
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

    @Override
    public String toString(){
        return getQueryString();
    }
}
