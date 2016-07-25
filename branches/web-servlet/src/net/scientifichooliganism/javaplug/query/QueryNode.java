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

    public boolean isProperty(){
        return value.matches("[\\w.]+");
    }

    public boolean isLiteral(){
        return value.startsWith("\"") && value.endsWith("\"")
            || value.startsWith("\'") && value.endsWith("\'");
    }

    public void removeNots(){
        if(isOperator()){
            QueryOperator operator = getOperator();
            if(operator == QueryOperator.NOT){
                QueryNode parent = getParent();
                QueryNode child = null;

                if(getRightChild() != null){
                    child = getRightChild();
                } else {
                    child = getLeftChild();
                }

                if(parent == null){
                    // Root node case
                    setValue(child.getValue());
                    setLeftChild(child.getLeftChild());
                    setRightChild(child.getRightChild());
                    flipLogicBelow();
                } else {
                    if(parent.getLeftChild() == this){
                        parent.setLeftChild(child);
                    } else if(parent.getRightChild() == this){
                        parent.setRightChild(child);
                    }
                    child.flipLogicBelow();
                }
            }

            // Recursively check down the tree
            if(getLeftChild() != null){
                getLeftChild().removeNots();
            }

            if(getRightChild() != null){
                getRightChild().removeNots();
            }
        }
    }

    private void flipLogicBelow(){
        if(isOperator()){
            QueryOperator operator = getOperator();
            if(operator == QueryOperator.NOT) {
                QueryNode parent = getParent();
                QueryNode child = null;

                if(getRightChild() != null){
                    child = getRightChild();
                } else {
                    child = getLeftChild();
                }

                if(parent.getLeftChild() == this){
                    parent.setLeftChild(child);
                } else if(parent.getRightChild() == this){
                    parent.setRightChild(child);
                }
            } else {
                setValue(getOperator().getOpposite().toString());
                if(getLeftChild() != null){
                    getLeftChild().flipLogicBelow();
                }
                if(getRightChild() != null){
                    getRightChild().flipLogicBelow();
                }
            }
        }
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
