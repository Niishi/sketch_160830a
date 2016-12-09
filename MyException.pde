class IncorrectSyntaxException extends Exception{
    Token errorToken;
    IncorrectSyntaxException(Token token){
        errorToken = token;
    }
}

class UndefinedVariableException extends Exception{
    String varName;
    UndefinedVariableException(String name){
        varName = name;
    }
}
