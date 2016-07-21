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

    // TODO: Switch highest precedence
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

    public <T> boolean evaluate(T object1, T object2){
        switch(this){
            case NOT:
                return !((Boolean) object1);
            case AND:
                return (Boolean) object1 && (Boolean) object2;
            case OR:
                return (Boolean) object1 || (Boolean) object2;
            case EQUAL:
                return object1.equals(object2);
            case NOT_EQUAL:
                return !(object1.equals(object2));
            case GREATER_THAN:
                return ((Comparable)object1).compareTo(object2) > 0;
            case LESS_THAN:
                return ((Comparable)object1).compareTo(object2) < 0;
            case GREATER_EQUAL:
                return ((Comparable)object1).compareTo(object2) >= 0;
            case LESS_EQUAL:
                return ((Comparable)object1).compareTo(object2) <= 0;
            default:
                return false;
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
