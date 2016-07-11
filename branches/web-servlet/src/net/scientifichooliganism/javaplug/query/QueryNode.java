package net.scientifichooliganism.javaplug.query;

public class QueryNode {
    private QueryNode leftChild = null;
    private QueryNode rightChild = null;
    private QueryNode parent = null;
    private String value = null;

    public QueryNode getRightChild() {
        return rightChild;
    }

    public QueryNode getLeftChild() {
        return leftChild;
    }

    public QueryNode getParent(){
        return parent;
    }

    public String getValue(){
        return value;
    }

    public QueryOperator getOperator(){
        if(isOperator()){
            return QueryOperator.fromString(value);
        } else {
            return null;
        }
    }

    public int getNumChildren(){
        if(leftChild != null && rightChild != null){
            return 2;
        } else if(leftChild != null || rightChild != null) {
            return 1;
        } else {
            return 0;
        }
    }

    void setLeftChild(QueryNode leftChild) {
        this.leftChild = leftChild;
    }

    void setRightChild(QueryNode rightChild) {
        this.rightChild = rightChild;
    }

    void setParent(QueryNode parent){
        this.parent = parent;
    }

    void setValue(String value){
        this.value = value;
    }

    public boolean isOperator(){
        return QueryOperator.isOperator(value);
    }

    public void consolePrint(){
        consolePrint(0);
    }

    private void consolePrint(int level){
        for(int i = 0; i < level; i++){
            System.out.print("  ");
        }
        System.out.println(value);
        if(getLeftChild() != null) {
            getLeftChild().consolePrint(level + 1);
        }
        if(getRightChild() != null) {
            getRightChild().consolePrint(level + 1);
        }
    }
}
