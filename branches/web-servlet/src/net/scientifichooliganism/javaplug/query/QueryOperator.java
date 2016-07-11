package net.scientifichooliganism.javaplug.query;

public enum QueryOperator {
    NOT ("!"),
    AND ("&&"),
    OR ("||"),
    LEFT_PAREN ("("),
    RIGHT_PAREN (")"),
    EQUAL ("=="),
    NOT_EQUAL ("!="),
    GREATER_THAN (">"),
    LESS_THAN ("<"),
    GREATER_EQUAL (">="),
    LESS_EQUAL ("<="),
    NO_OPERATOR;


    private final String name;
    private final int precedence;

    private QueryOperator(){
        this("No operator");
    }

    private QueryOperator(String value){
        name = value;
        switch(name){
            case "!":
                precedence = 3;
                break;
            case "&&":
                precedence = 1;
                break;
            case "||":
                precedence = 0;
                break;
            case "(":
                precedence = 4;
                break;
            case ")":
                precedence = -1;
                break;
            case "==":
            case "!=":
            case ">":
            case "<":
            case ">=":
            case "<=":
                precedence = 2;
                break;
            default:
                precedence = 0;
                break;
        }
    }

    public static QueryOperator fromString(String value){
        if(value != null){
            for(QueryOperator op : QueryOperator.values()){
                if(op.name.equalsIgnoreCase(value)){
                    return op;
                }
            }
        }

        return null;
    }

    public String toString(){
        return name;
    }

    public int getPrecedence(){
        return precedence;
    }

    public static boolean isOperator(String in){
        for(int i = 0; i < values().length; i++){
            if(values()[i].toString().equals(in) && !in.equals(NO_OPERATOR.toString())){
                return true;
            }
        }
        return false;
    }
}
