//実装した関数
//background(color)
//fill(color)
//rect()
//ellipse()
//println()


ArrayList<Statement> statementList;
int stmCount = -1;
boolean isExecutable = false;
boolean isTileConversion = true;

int[] initialTileArrangement = {300,300};
int[] currentTileArrangement = {100,100};

ArrayList variables;
public class Lang {
    ArrayList<Token> runTokens;
    int tokenSize;
    int index;
    StringBuilder result;
    Token next;
    int level = 0;
    Lang(ArrayList<Token> tokens){
        runTokens = tokens;
        tokenSize = runTokens.size();
        index = -1;

        statementList = new ArrayList<Statement>();
        variables = new ArrayList();
        result = new StringBuilder();
        hadPlate = null;

        stmCount=-1;

        currentTileArrangement = new int[2];
        currentTileArrangement[0] = initialTileArrangement[0];
        currentTileArrangement[1] = initialTileArrangement[1];
    }
    void run(){
        next = getNextToken();
        STMLIST();
        isExecutable = true;
        allPlateForDebugmode = new ArrayList<Plate>();
        // setAllExecutePlate(setupPlate);
        if(isTileConversion && isOK){
            plateList = new ArrayList<Plate>();
            statmentToPlate();
            executingPlate = setupPlate;
        }
    }
    void STMLIST(){
        try{
            while(index < tokenSize){
                STM();
                if(isSuperHackerMode){  //気にしないで
                    openWindowSound.play();
                    shmSetup();
                    return;
                }
            }
            correctSound.play();
        }catch(Exception e){
            println(e.getStackTrace());
            println("error");
            errorSound.play();
        }
    }
    void STM()throws Exception{
        if(next == null){
            return;
        }
        if(next.kind == Enum.RECT){
            stmRect();
        }else if(next.kind == Enum.ELLIPSE){
            stmEllipse();
        }else if(next.kind == Enum.BACKGROUND){
            stmBackground();
        }else if(next.kind == Enum.FILL){
            stmFill();
        }else if(next.kind == Enum.STROKE){
            stmStroke();
        }else if(next.kind == Enum.NO_STROKE){
            stmNoStroke();
        }else if(next.kind == Enum.PRINTLN){
            stmPrintln();
        }else if(next.kind == Enum.TEXT){
            stmText();
        }else if(next.kind == Enum.TEXT_SIZE){
            stmTextSize();
        }else if(next.kind == Enum.LINE){
            stmLine();
        }else if(next.kind == Enum.INT){
            next = getNextToken();
            if(next.kind == Enum.OTHER){
                DECL();
            }else if(next.kind == Enum.LBRACKET){
                next = getNextToken();
                DECL_ARRAY();
            }else{
                unexpectedTokenError(next);
            }
        }else if(next.kind == Enum.STRING){
            next = getNextToken();
            if(next.kind == Enum.OTHER){
                index--;
                declString();
            }else if(next.kind == Enum.LBRACKET){
                next = getNextToken();
                declStringArray();
            }else{
                unexpectedTokenError(next);
            }
        }else if(next.kind == Enum.FOR){
            stmFor();
        }else if(next.kind == Enum.IF){
            stmIf();
        }else if(next.kind == Enum.VOID){
            next = getNextToken();
            if(next.kind == Enum.SETUP){
                stmSetup();
            }else if(next.kind == Enum.OTHER){
                stmMethod();
            }
        }else if(next.kind == Enum.OTHER){
            String varName = next.word;
            next = getNextToken();
            if(next.kind == Enum.ASSIGN){
                stmAssign(varName);
            }else if(next.kind == Enum.LBRACKET){
                next = getNextToken();
                stmAccessArray(varName);
            }else if(next.kind == Enum.LBRACE){
                next = getNextToken();
                stmCallMethod(varName);
            }
        }else if(next.kind == Enum.EOF){
            index = tokenSize;
        }else if(next.kind == Enum.RCBRACE){
            //ここは読み飛ばしてはいけない
        }else if(next.kind == Enum.COMMENT){
            next = getNextToken();  //読み飛ばす
        }else if(next.kind == Enum.YUYA){
            isSuperHackerMode = true;
            next = getNextToken();
        }
        else {
            unexpectedTokenError(next);
        }
    }
    Token getNextToken(){
        index++;
        try{
            return runTokens.get(index);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    void unexpectedTokenError(Token token) throws Exception{
        println("ErrorToken : " + token.word);
        throw new Exception();
    }
    void DECL() throws Exception { // DECL -> INT OTHER SEMI
        //この時点では変数名のところまで読み込まれている
        String name = next.word;
        next = getNextToken();
        if(next.kind == Enum.COMMA){
            do{
                next = getNextToken();
                if (next.kind != Enum.OTHER) unexpectedTokenError(next);
                // table.addName(next.word, level);
                next = getNextToken();
            }while (next.kind == Enum.COMMA);
        }else if(next.kind == Enum.ASSIGN){
            next = getNextToken();
            String result = "";
            while(true){
                if(next.kind == Enum.SEMI){
                    break;
                }
                result += next.word;
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
            }
            String[] argString = new String[2];
            argString[0] = name;
            argString[1] = result;
            statementList.add(new Statement(Enum.INT, argString));
        }
        if (next.kind != Enum.SEMI)
            unexpectedTokenError(next);
        next = getNextToken();
    }
    void DECL_ARRAY()throws Exception{  //DECL_ARRAY -> INT LBRACKET RBRACKET OTHER ASSIGN
        String[] argString = new String[2];
        if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.OTHER) unexpectedTokenError(next);
        String varName = next.word;
        next = getNextToken();
        if(next.kind != Enum.ASSIGN) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind == Enum.NEW){
            next = getNextToken();
            if(next.kind != Enum.INT) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.LBRACKET) unexpectedTokenError(next);
            next = getNextToken();
            String arrayLength = E();
            if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.SEMI) unexpectedTokenError(next);
            next = getNextToken();
            argString[0] = varName;
            argString[1] = arrayLength;
            statementList.add(new Statement(Enum.INT_ARRAY, argString));
        }else if(next.kind == Enum.LCBRACE){
            next = getNextToken();
            StringList sl = new StringList();
            sl.append(varName);
            while(next.kind != Enum.RCBRACE){
                sl.append(E());
                if(next.kind == Enum.COMMA){
                    next = getNextToken();
                }else if(next.kind == Enum.RCBRACE){
                    break;
                }else{
                    unexpectedTokenError(next);
                }
            }
            next = getNextToken();
            if(next.kind != Enum.SEMI) unexpectedTokenError(next);
            next = getNextToken();
            argString = sl.array();
            statementList.add(new Statement(Enum.INT_ARRAY_SYNTAX_SUGAR, argString));
        }else{
            unexpectedTokenError(next);
        }
    }
    void declString() throws Exception{
        next = getNextToken();
        if(next.kind != Enum.OTHER)unexpectedTokenError(next);
        String name = next.word;
        next = getNextToken();
        if(next.kind == Enum.ASSIGN){
            next = getNextToken();
            String rightHand = stringE();
            String[] argString = new String[2];
            argString[0] = name;
            argString[1] = rightHand;
            statementList.add(new Statement(Enum.STRING, argString));
        }
        if(next.kind != Enum.SEMI)  unexpectedTokenError(next);
        next = getNextToken();
    }
    void declStringArray() throws Exception{
        String[] argString = new String[2];
        if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.OTHER) unexpectedTokenError(next);
        String varName = next.word;
        next = getNextToken();
        if(next.kind != Enum.ASSIGN) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind == Enum.NEW){
            next = getNextToken();
            if(next.kind != Enum.STRING) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.LBRACKET) unexpectedTokenError(next);
            next = getNextToken();
            String arrayLength = E();
            if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.SEMI) unexpectedTokenError(next);
            next = getNextToken();
            argString[0] = varName;
            argString[1] = arrayLength;
            statementList.add(new Statement(Enum.STRING_ARRAY, argString));
        }else if(next.kind == Enum.LCBRACE){
            next = getNextToken();
            StringList sl = new StringList();
            sl.append(varName);
            while(next.kind != Enum.RCBRACE){
                sl.append(stringE());
                if(next.kind == Enum.COMMA){
                    next = getNextToken();
                }else if(next.kind == Enum.RCBRACE){
                    break;
                }else{
                    unexpectedTokenError(next);
                }
            }
            next = getNextToken();
            if(next.kind != Enum.SEMI) unexpectedTokenError(next);
            next = getNextToken();
            argString = sl.array();
            statementList.add(new Statement(Enum.STRING_ARRAY_SYNTAX_SUGAR, argString));
        }else{
            unexpectedTokenError(next);
        }
    }
    void stmRect()throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        String[] argString = new String[4];
        for(int i = 0; i < 4; i++){
            String result = "";
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==3 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
        }
        if(next.kind != Enum.RBRACE)unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) {
            unexpectedTokenError(next);
            return;
        }
        next = getNextToken();

        statementList.add(new Statement(Enum.RECT, argString));
    }
    void stmTextSize()throws Exception{
        String[] argString = new String[1];
        float[]  arg = new float[1];
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        String result = "";
        int tmpIndex = index;
        while(true){
            try{
                next = getNextToken();
            }catch(Exception e){
                throw e;
            }
            if(next.kind==Enum.RBRACE){
                break;
            }
            result += next.word;
        }
        argString[0] = result;
        index = tmpIndex;
        next = getNextToken();
        E();
        arg[0] = getValueByCode();

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.TEXT_SIZE,arg, argString));
    }
    void stmText()throws Exception{
        String[] argString = new String[3];
        float[]  arg = new float[2];
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < 3; i++){
            String result = "";
            int tmpIndex = index;
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==2 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
            if(i > 0){
                index = tmpIndex;
                next = getNextToken();
                E();
                arg[i-1] = getValueByCode();
            }
            if(i == 2){
                break;
            }
            if(next.kind != Enum.COMMA) unexpectedTokenError(next);
        }
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.TEXT,arg, argString));
    }
    void stmSetup()throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.SETUP));
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        next = getNextToken();

        statementList.add(new Statement(Enum.SETUP_END));
    }
    void stmEllipse()throws Exception{
        String[] argString = new String[4];
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < 4; i++){
            String result = "";
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==3 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.ELLIPSE, argString));
    }
    void stmFill()throws Exception{
        int argSize = 3;    //ここは1or3なので柔軟に対応できるように変更しなければならない
        String[] argString = new String[argSize];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < argSize; i++){
            String result = "";
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==argSize-1 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.FILL, argString));
    }
    void stmStroke()throws Exception{
        int argSize = 3;    //ここは1or3なので柔軟に対応できるように変更しなければならない
        String[] argString = new String[argSize];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < argSize; i++){
            String result = "";
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==argSize-1 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.STROKE, argString));
    }
    void stmNoStroke()throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.NO_STROKE));
    }
    void stmBackground()throws Exception{
        int argSize = 3;
        float[] arg = new float[argSize];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        for(int i = 0; i < argSize; i++){
            if(next.kind == Enum.NUM){
                arg[i] = int(next.word);
            }else{
                unexpectedTokenError(next);
            }
            if(i == argSize-1){
                next = getNextToken();
                break;
            }else if(i == 0){
                next = getNextToken();
                if(next.kind == Enum.RBRACE){
                    float tmp = arg[0];
                    arg = new float[1];
                    arg[0] = tmp;
                    break;
                }else if(next.kind == Enum.COMMA){
                    next = getNextToken();
                }else{
                    unexpectedTokenError(next);
                }
            }else{
                next = getNextToken();
                if(next.kind != Enum.COMMA) unexpectedTokenError(next);
                next = getNextToken();
            }
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.BACKGROUND, arg));
    }
    void stmLine()throws Exception{
        String[] argString = new String[4];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < 4; i++){
            String result = "";
            while(true){
                try{
                    next = getNextToken();
                }catch(Exception e){
                    throw e;
                }
                if(next.kind == Enum.COMMA || (i==3 && next.kind==Enum.RBRACE)){
                    break;
                }
                result += next.word;
            }
            argString[i] = result;
            if(i == 3){
                break;
            }
            if(next.kind != Enum.COMMA) unexpectedTokenError(next);
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.LINE, argString));
    }
    void stmAccessArray(String varName) throws Exception{
        //この時点でOTHER LBRACEまでは読み込まれている
        String[] argString = new String[3];
        String index = E();
        argString[0] = varName;
        argString[1] = index;
        if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.ASSIGN) unexpectedTokenError(next);
        next = getNextToken();
        Variable var = variableTable.searchName(varName);
        if(var.kind == Enum.INT_ARRAY){
            argString[2] = E();
        }else{
            new Exception("undefined array " + var.kind);
        }

        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.ASSIGN_ARRAY, argString));
    }
    int getValue() throws Exception{
        next = getNextToken();
        E();
        return getValueByCode();
    }
    private int getValueByCode(){
        int[] code = new HsmAssembler(result.toString()).assemble();
        int value = new SimpleHSM().execute(code);
        result = new StringBuilder();
        return value;
    }
    void stmPrintln()throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind == Enum.STRING){
            String[] word = {next.word};
            next = getNextToken();
            if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.SEMI) unexpectedTokenError(next);
            statementList.add(new Statement(Enum.PRINTLN, word));
        } else {
            E();
            if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind != Enum.SEMI){
                unexpectedTokenError(next);
                return;
            }
            next = getNextToken();

            String[] word = {""+getValueByCode()};
            statementList.add(new Statement(Enum.PRINTLN, word));
        }
    }
    void stmFor() throws Exception{
        next = getNextToken();
        if (next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        //要検討！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        if(next.kind == Enum.NUM){
            String[] argString = new String[1];
            argString[0] = next.word;

            next = getNextToken();
            if (next.kind != Enum.RBRACE) unexpectedTokenError(next);
            next = getNextToken();
            if (next.kind != Enum.LCBRACE) unexpectedTokenError(next);
            next = getNextToken();
            statementList.add(new Statement(Enum.FOR_START));
            do{
                STM();
            }while(next.kind != Enum.RCBRACE);
            next = getNextToken();
            statementList.add(new Statement(Enum.FOR, argString));
        } else {
            statementList.add(new Statement(Enum.FOR_START));
            STM();
            booleanE();
            changeCodeToConditionPlate();
            next = getNextToken();
            STM();
            if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
            next = getNextToken();
            if (next.kind != Enum.LCBRACE) unexpectedTokenError(next);
            next = getNextToken();
            do{
                STM();
            }while(next.kind != Enum.RCBRACE);
            next = getNextToken();
            statementList.add(new Statement(Enum.FOR));
        }
    }
    void stmIf()throws Exception{
        next = getNextToken();
        if (next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.IF_START));
        booleanE();
        changeCodeToConditionPlate();
        if (next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if (next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        // statementList.add(new Statement(Enum.IF_START, cp));
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        statementList.add(new Statement(Enum.IF));
        next = getNextToken();
    }
    void stmAssign(String varName)throws Exception{
        //要修正さあああああああああああああああああああああああああああああああああああああああああああああああさあああああああああああああああ

        String result = "";
        while(true){
            try{
                next = getNextToken();
            }catch(Exception e){
                throw e;
            }
            if(next.kind == Enum.SEMI){
                break;
            }
            result += next.word;
        }
        next = getNextToken();
        String[] argString = new String[2];
        argString[0] = varName;
        argString[1] = result;
        statementList.add(new Statement(Enum.ASSIGN, argString));
        println("aaa");
    }
    void stmMethod() throws Exception{
        String[] argString = new String[1];
        argString[0] = next.word;   //メソッド名になる
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();

        //仮引数の設定
        IntList varKindList = new IntList();
        StringList varNameList = new StringList();
        while(next.kind != Enum.RBRACE){
            if(next.kind == Enum.INT || next.kind == Enum.STRING || next.kind == Enum.FLOAT || next.kind == Enum.BOOLEAN || next.kind == Enum.COLOR){
                int kind = next.kind;
                next = getNextToken();
                if(next.kind != Enum.OTHER) unexpectedTokenError(next);
                String name = next.word;
                varKindList.append(kind);
                varNameList.append(name);
            } else {
                unexpectedTokenError(next);
            }
            next = getNextToken();
            if(next.kind == Enum.COMMA){
                next = getNextToken();
            }else if(next.kind == Enum.RBRACE){
                break;
            }else{
                unexpectedTokenError(next);
            }
        }
        int[] argInt = varKindList.array();
        String[] argString2 = varNameList.array();
        String[] newArgString = new String[argString.length + argString2.length];
        newArgString[0] = argString[0];
        for(int i = 1; i < newArgString.length; i++){
            newArgString[i] = argString2[i-1];
        }

        statementList.add(new Statement(Enum.METHOD_START, newArgString, argInt));
        //関数内にある命令列を取得
        next = getNextToken();
        if(next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        next = getNextToken();

        statementList.add(new Statement(Enum.METHOD));
    }
    void stmCallMethod(String methodName) throws Exception{
        StringList varNameList = new StringList();
        varNameList.append(methodName);
        while(next.kind != Enum.RBRACE){
            if(next.kind == Enum.OTHER || next.kind == Enum.NUM || next.kind == Enum.MOJIRETSU || next.kind == Enum.BOOLEAN) varNameList.append(next.word);
            else unexpectedTokenError(next);
            next = getNextToken();
            if(next.kind == Enum.COMMA) next = getNextToken();
        }
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.METHOD_CALL, varNameList.array()));
    }
    private ArrayList<Token> stringOpes;
    String stringE() throws Exception{
        stringOpes = new ArrayList<Token>();
        StringBuilder result = new StringBuilder();
        result.append(stringF());
        while(true){
            if(next.kind == Enum.PLUS){
                result.append("+");
                stringOpes.add(next);
                next = getNextToken();
                result.append(stringF());
            }else{
                break;
            }
        }
        return result.toString();
    }
    private String stringF() throws Exception{
        StringBuilder result = new StringBuilder();
        if (next.kind == Enum.LBRACE) {
            result.append("(");
            stringOpes.add(next);
            next = getNextToken();
            result.append(stringE());
            if (next.kind == Enum.RBRACE){
                result.append(")");
                stringOpes.add(next);
                next = getNextToken();
            }else
                unexpectedTokenError(next);
        } else if (next.kind == Enum.MOJIRETSU || next.kind == Enum.NUM) {
            result.append(next.word);
            stringOpes.add(next);
            next = getNextToken();
        } else if (next.kind == Enum.OTHER) {
            result.append(next.word);
            String varName = next.word;
            Token  varToken = next;
            next = getNextToken();
            if(next.kind == Enum.LBRACKET){
                result.append(next.word);
                next = getNextToken();
                String indexWord = E();
                int index = getValueByCode();
                result.append(indexWord);
                stringOpes.add(new Token(varName + "," + index, Enum.STRING_ARRAY));
                if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
                result.append(next.word);
                next = getNextToken();
            }else{
                index--;
                stringOpes.add(varToken);
                next = getNextToken();
            }
        } else
            unexpectedTokenError(next);
        return result.toString();
    }
    public String getStringValue() throws Exception{
        next = getNextToken();
        stringE();
        return getStringValueByCode();
    }
    private String getStringValueByCode(){
        StringBuilder sb = new StringBuilder();
        for(Token token : stringOpes){
            if(token.kind == Enum.MOJIRETSU || token.kind == Enum.NUM){
                sb.append(token.word);
            }else if(token.kind == Enum.OTHER){
                sb.append(variableTable.searchName(token.word).getVarValue());
            }else if(token.kind == Enum.STRING_ARRAY){
                String[] words = token.word.split(",");
                CompositeVariable cv = (CompositeVariable)variableTable.searchName(words[0]);
                sb.append(cv.get(Integer.parseInt(words[1])));
            }
        }
        return sb.toString();
    }
    ArrayList codeList;
    void booleanE() throws Exception{
        codeList = new ArrayList();
        booleanT();
        while (true) {
            if (next.kind == Enum.AND) {
                next = getNextToken();
                booleanT();
                codeList.add(Enum.AND);
            } else if (next.kind == Enum.OR) {
                next = getNextToken();
                booleanT();
                codeList.add(Enum.OR);
            } else {
                break;
            }
        }
        codeList.add(Enum.EOF);
    }
    void booleanT() throws Exception{
        booleanF();
        if(next.kind == Enum.LESS || next.kind == Enum.LESS_THAN || next.kind == Enum.GRATER || next.kind == Enum.GRATER_THAN || next.kind == Enum.EQUAL || next.kind == Enum.NOT_EQUAL){
            int tmpKind = next.kind;
            next = getNextToken();
            booleanF();
            codeList.add(tmpKind);
        }
    }
    void booleanF() throws Exception {
        if (next.kind == Enum.LBRACE) {
            next = getNextToken();
            booleanE();
            if (next.kind == Enum.RBRACE)
                next = getNextToken();
            else
                unexpectedTokenError(next);
        } else if (next.kind == Enum.NUM) {
            codeList.add(Enum.NUM);
            println(next.word);
            codeList.add(next.word);
            next = getNextToken();
        } else if (next.kind == Enum.OTHER) {
            codeList.add(Enum.OTHER);
            codeList.add(next.word);
            next = getNextToken();
        } else if(next.kind == Enum.MOUSE_X || next.kind == Enum.MOUSE_Y){
            codeList.add(Enum.MOUSE_X);
            codeList.add(next.word);
            next = getNextToken();
        } else
            unexpectedTokenError(next);
    }
    String E() throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append(T());
        while (true) {
            if (next.kind == Enum.PLUS) {
                sb.append(next.word);
                next = getNextToken();
                T();
                addCode("AD 0 0");
            } else if (next.kind == Enum.MINUS) {
                sb.append(next.word);
                next = getNextToken();
                T();
                addCode("SUB 0 0");
            } else {
                break;
            }
        }
        addCode("HLT 0 0");
        return sb.toString();
    }
    private String T() throws Exception{ // T -> F {* F [*]}
        StringBuilder sb = new StringBuilder();
        sb.append(F());
        while (true) {
            if (next.kind== Enum.MULT) {
                sb.append(next.word);
                next = getNextToken();
                F();
                addCode("ML 0 0");
            }else if (next.kind == Enum.DIV) {
                sb.append(next.word);
                next = getNextToken();
                F();
                addCode("DIV 0 0");
            }else {
                break;
            }
        }
        return sb.toString();
    }
    private String F() throws Exception{ // F -> ( E ) |  NUM  |OTHER
        StringBuilder sb = new StringBuilder();
        if (next.kind == Enum.LBRACE) {
            sb.append(next.word);
            next = getNextToken();
            E();
            if (next.kind == Enum.RBRACE){
                sb.append(next.word);
                next = getNextToken();
            }
            else
                unexpectedTokenError(next);
        } else if (next.kind == Enum.NUM) {
            sb.append(next.word);
            addCode("LDC 0 "
                + Integer.parseInt(next.word));
            next = getNextToken();
        } else if (next.kind == Enum.OTHER) {
            sb.append(next.word);
            String varName = next.word;
            next = getNextToken();
            if(next.kind == Enum.LBRACKET){
                next = getNextToken();
                if(next.kind != Enum.NUM) unexpectedTokenError(next);
                int index = Integer.parseInt(next.word);
                next = getNextToken();
                if(next.kind != Enum.RBRACKET) unexpectedTokenError(next);
                addCode("LDA " + index + " " + getAdress(varName));
            }else{
                index--;
                addCode("LDV 0 " + getAdress(varName));
            }
            next = getNextToken();
        } else if(next.kind == Enum.MOUSE_X){
            sb.append(next.word);
            addCode("MOUSE_X 0 0");
            next = getNextToken();
        } else if(next.kind == Enum.MOUSE_Y){
            sb.append(next.word);
            addCode("MOUSE_Y 0 0");
            next = getNextToken();
        } else if (next.kind == Enum.MINUS) {
            sb.append(next.word);
            next = getNextToken();
            if (next.kind == Enum.NUM){
                addCode("LDC 0 " + -1*Integer.parseInt(next.word));
            }else if(next.kind == Enum.OTHER){
                addCode("LDV 0 " + getAdress(next.word));
            }else{
                unexpectedTokenError(next);
            }
            sb.append(next.word);
            next = getNextToken();
        } else
            unexpectedTokenError(next);
        return sb.toString();
    }
    //要修正
    int getAdress(String name) throws Exception{
        return variableTable.indexOf(name);
    }

    void addCode(String st) {
        result.append(st).append("\n");
    }

    WallPlate wallPlate = null;
    WallPlate preWallPlate = null;
    Plate prePlate = null;
    void statmentToPlate(){//大事:テキストからタイル表現に直す
        wallPlateList = new ArrayList<WallPlate>();
        for(int i = 0; i < statementList.size(); i++){
            Statement stm = statementList.get(i);
            if(stm.kind == Enum.INT){
                changeToAssignmentPlate(Enum.INT, stm.argString[0], stm.argString[1]);
            }else if(stm.kind == Enum.STRING){
                changeToAssignmentPlate(Enum.STRING, stm.argString[0], stm.argString[1]);
            }else if(stm.kind == Enum.INT_ARRAY || stm.kind == Enum.STRING_ARRAY){
                Plate p = new ArrayPlate_Original(currentTileArrangement[0], currentTileArrangement[1], stm.argString[0], stm.argString[1], stm.kind);
                updatePlateEnv(p);
            }else if(stm.kind == Enum.INT_ARRAY_SYNTAX_SUGAR || stm.kind == Enum.STRING_ARRAY_SYNTAX_SUGAR){
                String[] a = stm.argString;
                String[] contents = new String[a.length-1];
                for(int j = 0; j < contents.length; j++){
                    contents[j] = a[j+1];
                }
                Plate p = new ArrayPlate_SyntaxSugar(currentTileArrangement[0], currentTileArrangement[1], stm.kind, stm.argString[0], contents);
                updatePlateEnv(p);
            }else if(stm.kind == Enum.ASSIGN){
                Plate plate = new AssignmentPlate(Enum.INT, currentTileArrangement[0], currentTileArrangement[1],stm.argString[0], stm.argString[1]);
                plateList.add(plate);
                currentTileArrangement[1] += plate.pHeight;
                if(prePlate != null){
                    prePlate.combinePlate(plate);
                }
                if(wallPlate != null){
                    plate.combineWallPlate(wallPlate);
                }
                prePlate = plate;
            }else if(stm.kind == Enum.RECT){
                changeToStatementPlate2("rect",stm);
            }else if(stm.kind == Enum.ELLIPSE){
                changeToStatementPlate2("ellipse",stm);
            }else if(stm.kind == Enum.FILL){
                changeToStatementPlate2("fill",stm);
            }else if(stm.kind == Enum.NO_STROKE){
                changeToStatementPlate2("noStroke",stm);
            }else if(stm.kind == Enum.STROKE){
                changeToStatementPlate2("stroke",stm);
            }else if(stm.kind == Enum.PRINTLN){
                changeToStatementPlate("println",stm);
            }else if(stm.kind == Enum.BACKGROUND){
                changeToStatementPlate("background",stm);
            }else if(stm.kind == Enum.TEXT){
                changeToStatementPlate2("text", stm);
            }else if(stm.kind == Enum.TEXT_SIZE){
                changeToStatementPlate2("textSize", stm);
            }else if(stm.kind == Enum.LINE){
                changeToStatementPlate2("line", stm);
            }else if(stm.kind == Enum.FOR_START){
                Plate firstPlate    = getPlateByStatement(statementList.get(++i));
                ConditionPlate cond = getConditionPlate(statementList.get(++i));
                Plate lastPlate     = getPlateByStatement(statementList.get(++i));
                changeToForPlate(firstPlate, cond, lastPlate);

            }else if(stm.kind == Enum.FOR){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 70;
                prePlate = wallPlate;
                wallPlate = wallPlate.upperPlate;
            }else if(stm.kind == Enum.IF_START){
                changeToWallPlate(stm);
                i++;
                stm = statementList.get(i);
                ((IfCondPlate)wallPlate).setConditionPlate(getConditionPlate(stm));
            }else if(stm.kind == Enum.IF){
                wallPlate = null;
                prePlate = null;
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 100;
            }else if(stm.kind == Enum.SETUP){
                setupPlate = (SetupPlate)changeToWallPlate(stm);
            }else if(stm.kind == Enum.SETUP_END){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 100;
                wallPlate = null;
                prePlate = null;
            }else if(stm.kind == Enum.METHOD_START){
                changeToWallPlate(stm);
            }else if(stm.kind == Enum.METHOD){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 100;
                wallPlate = null;
                prePlate = null;
            }else if(stm.kind == Enum.METHOD_CALL){
                String methodName   = stm.argString[0];
                String[] methodArg  = new String[stm.argString.length-1];
                for(int j = 0; j < methodArg.length; j++){
                    methodArg[j] = stm.argString[j+1];
                }
                Plate plate = new Method(currentTileArrangement[0], currentTileArrangement[1], methodName, methodArg);
                updatePlateEnv(plate);
            }
        }
    }
    Plate getPlateByStatement(Statement stm){
        if(stm.kind == Enum.INT || stm.kind == Enum.STRING){
            return getAssignmentPlate(Enum.INT, stm.argString[0], stm.argString[1]);
        }else if(stm.kind == Enum.INT_ARRAY || stm.kind == Enum.STRING_ARRAY){
            return new ArrayPlate_Original(currentTileArrangement[0], currentTileArrangement[1], stm.argString[0], stm.argString[1], stm.kind);
        }else if(stm.kind == Enum.INT_ARRAY_SYNTAX_SUGAR || stm.kind == Enum.STRING_ARRAY_SYNTAX_SUGAR){
            String[] a = stm.argString;
            String[] contents = new String[a.length-1];
            for(int j = 0; j < contents.length; j++){
                contents[j] = a[j+1];
            }
            return new ArrayPlate_SyntaxSugar(currentTileArrangement[0], currentTileArrangement[1], stm.kind, stm.argString[0], contents);
        }else if(stm.kind == Enum.ASSIGN){
            return new AssignmentPlate(Enum.INT, currentTileArrangement[0], currentTileArrangement[1],stm.argString[0], stm.argString[1]);
        }else if(stm.kind == Enum.RECT){
            return getStatementPlate("rect",stm);
        }else if(stm.kind == Enum.ELLIPSE){
            return getStatementPlate("ellipse",stm);
        }else if(stm.kind == Enum.FILL){
            return getStatementPlate("fill",stm);
        }else if(stm.kind == Enum.NO_STROKE){
            return getStatementPlate("noStroke",stm);
        }else if(stm.kind == Enum.STROKE){
            return getStatementPlate("stroke",stm);
        }else if(stm.kind == Enum.TEXT){
            return getStatementPlate("text", stm);
        }else if(stm.kind == Enum.TEXT_SIZE){
            return getStatementPlate("textSize", stm);
        }else if(stm.kind == Enum.LINE){
            return getStatementPlate("line", stm);
        }else{
            new Exception("Error!!!!!!!!!!!!!!!getPlateByStatement() method");
            return null;
        }
    }
    Plate getAssignmentPlate(int type, String leftHand, String rightHand){
        return new AssignmentPlate(type, currentTileArrangement[0], currentTileArrangement[1],leftHand, rightHand);
    }
    Plate getStatementPlate(String statementName, Statement stm){
        return new StatementPlate(statementName, currentTileArrangement[0], currentTileArrangement[1], stm.argString);
    }


    void changeToAssignmentPlate(int type, String leftHand, String rightHand){
        Plate plate = new AssignmentPlate(type, currentTileArrangement[0], currentTileArrangement[1],leftHand, rightHand);
        updatePlateEnv(plate);
    }
    void changeToStatementPlate(String statementName, Statement stm){
        Plate plate = new StatementPlate(statementName, currentTileArrangement[0], currentTileArrangement[1], stm.getAllArgAsString());
        updatePlateEnv(plate);
    }
    void changeToStatementPlate2(String statementName, Statement stm){
        Plate plate = new StatementPlate(statementName, currentTileArrangement[0], currentTileArrangement[1], stm.argString);
        updatePlateEnv(plate);
    }

    private void updatePlateEnv(Plate plate){
        plateList.add(plate);
        currentTileArrangement[1] += plate.pHeight;
        if(prePlate != null){
            prePlate.combinePlate(plate);
        }
        if(wallPlate != null){
            plate.combineWallPlate(wallPlate);
        }
        prePlate = plate;
    }
    ConditionPlate getConditionPlate(Statement stm){
        String a = "";
        if(stm.kind == Enum.LESS) a = "<";
        else if(stm.kind == Enum.LESS_THAN) a = "<=";
        else if(stm.kind == Enum.GRATER) a = ">";
        else if(stm.kind == Enum.GRATER_THAN) a = ">=";
        else if(stm.kind == Enum.EQUAL) a = "==";
        else if(stm.kind == Enum.NOT_EQUAL) a = "!=";
        return new ConditionPlate(0,0,a,stm.argString[0],stm.argString[1]);
    }
    //要修正：複数のbooleanに対応できるようにする
    void changeCodeToConditionPlate() throws Exception{
        Stack<String> stack = new Stack<String>();
        for(int i = 0; i < codeList.size(); i++){
            int kind = (Integer)codeList.get(i);
            if(kind == Enum.NUM || kind == Enum.OTHER || kind == Enum.MOUSE_X){
                i++;
                stack.push((String)codeList.get(i));
            }else if(kind == Enum.LESS || kind == Enum.LESS_THAN || kind == Enum.GRATER || kind == Enum.GRATER_THAN || kind == Enum.EQUAL || kind == Enum.NOT_EQUAL){
                String[] argString = new String[2];
                argString[1] = stack.pop();
                argString[0] = stack.pop();
                statementList.add(new Statement(kind,argString));
            }
        }
    }
    WallPlate changeToWallPlate(Statement stm){
        //非常に書き方が汚い。あとでなおす。
        WallPlate wplate = null;
        int kind = stm.kind;
        if(kind == Enum.FOR_START){
            wplate = new Loop(currentTileArrangement[0], currentTileArrangement[1]);
        }else if(kind == Enum.SETUP){
            wplate = new SetupPlate(currentTileArrangement[0], currentTileArrangement[1]);
        }else if(kind == Enum.METHOD_START){
            if(stm.argString.length == 1){
                wplate = new MethodPlate(stm.argString[0], currentTileArrangement[0], currentTileArrangement[1]);
            }else{
                String[] argNames = new String[stm.argString.length-1];
                for(int i = 0; i < argNames.length; i++){
                    argNames[i] = stm.argString[i+1];
                }
                wplate = new MethodPlate(stm.argString[0], currentTileArrangement[0], currentTileArrangement[1], argNames, stm.argInt);
            }
        }else if(kind == Enum.IF_START){
            wplate = new IfCondPlate(currentTileArrangement[0], currentTileArrangement[1]);
        }
        currentTileArrangement[0] += wplate.wallPlateWidth;
        currentTileArrangement[1] += wplate.wallPlateHeight;
        if(prePlate != null){
            prePlate.combinePlate(wplate);
        }
        if(wallPlate != null){
            wplate.combineWallPlate(wallPlate);
        }
        plateList.add(wplate);
        wallPlateList.add(wplate);
        preWallPlate = wallPlate;
        wallPlate = wplate;
        prePlate = null;
        return wplate;
    }
    WallPlate changeToForPlate(Plate firstPlate, ConditionPlate cond, Plate lastPlate){
        WallPlate wplate = new ForPlate(currentTileArrangement[0], currentTileArrangement[1], firstPlate, lastPlate, cond);
        currentTileArrangement[0] += wplate.wallPlateWidth;
        currentTileArrangement[1] += wplate.wallPlateHeight;
        if(prePlate != null){
            prePlate.combinePlate(wplate);
        }
        if(wallPlate != null){
            wplate.combineWallPlate(wallPlate);
        }
        plateList.add(wplate);
        wallPlateList.add(wplate);
        preWallPlate = wallPlate;
        wallPlate = wplate;
        prePlate = null;
        return wplate;
    }
}

class Statement {
    int kind;
    // ArrayList arg = new ArrayList();
    int[] argInt = new int[0];
    float[] arg = new float[0];
    String[] argString = new String[0];
    Statement(int kind, float[] arg){
        this.kind   = kind;
        this.arg    = arg;
    }
    Statement(int kind, String[] argString){
        this.kind       = kind;
        this.argString  = argString;
    }
    Statement(int kind, float[] arg, String[] argString){
        this.kind       = kind;
        this.arg        = arg;
        this.argString  = argString;
    }
    Statement(int kind, String[] argString, int[] argInt){
        this.kind       = kind;
        this.argString  = argString;
        this.argInt     = argInt;
    }
    Statement(int kind, int[] argInt){
        this.kind   = kind;
        this.argInt = argInt;
    }
    Statement(int kind){
        this.kind = kind;
    }
    int getArgCount(){
        return argInt.length + arg.length + argString.length;
    }

    String[] getAllArgAsString(){
        String[] allArgStr = new String[argInt.length + arg.length + argString.length];
        int index = 0;
        for(int i = 0; i < argInt.length; i++){
            allArgStr[index] = ""+argInt[i];
            index++;
        }
        for(int i = 0; i < arg.length; i++){
            allArgStr[index] = ""+int(arg[i]);
            index++;
        }
        for(int i = 0; i < argString.length; i++){
            allArgStr[index] = argString[i];
            index++;
        }
        return allArgStr;
    }
}

public class Name {
    String ident;
    int address;
    int level;
    public Name(String id, int addr, int lv) {
        // コンストラクタ
        ident = id;
        address = addr;
        level = lv;
    }
}
