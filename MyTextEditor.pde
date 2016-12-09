int alpha = 255;
color alizarin;
color pomegranate;
color pumpkin;
color sunFlower;
color carrot;
color orange;
color turquoise;
color emerald;
color greenSea;
color nephritis;
color peterRiver;
color belizeHole;
color amethyst;
color silver;
color concrete;
color clouds;
color wetAsphalt, schaussPink;
ArrayList<Tile> blocks = new ArrayList<Tile>();

boolean isColorLight = true;
class MyTextEditor {
    char EQUAL       = '=';
    char SEMI        = ';';
    char SPACE       = ' ';
    char LBRACE      = '(';
    char RBRACE      = ')';
    char LCBRACE     = '{';
    char RCBRACE     = '}';
    char LBRACKET    = '[';
    char RBRACKET    = ']';
    char LANGLE      = '<';
    char RANGLE      = '>';
    char LF          = '\n';
    char PLUS        = '+';
    char MINUS       = '-';
    char MULT        = '*';
    char DIV         = '/';
    char DQUART      = '"';
    char COMMA       = ',';
    char AMPERSAND   = '&';
    char PIPE        = '|';
    char EXCLAMATION = '!';


    MyToken[] myTokens;

    ArrayList<StringBuilder> texts = new ArrayList<StringBuilder>();
    private StringBuilder text = new StringBuilder();
    File file;
    boolean isTextRepresentation = true;
    boolean isAnimated           = false;
    boolean isTextToVisual       = false;
    boolean isFadeOut            = true;
    private boolean isFocus      = true;


    int currentPosition = 0;
    int stringLength = 0;

    private int x               = 0;
    private int y               = 0;
    private int merginLeft      = 7;
    private int merginTop       = 7;
    private int cursorPositionY = merginTop;
    private int cursorWidth     = 2;
    private int cursorHeight    = 0;   //文字サイズによる
    private int row             = 0;    //実際の行数
    private int col             = 0;    //実際の列数
    private int nowRow          = 0; //画面上での行数
    private int topRowNumber    = 0;   //画面上での一番上の行数が全体の行数の何番目か
    private int selectStartRow  = -1;
    private int selectStartCol  = -1;
    private int selectEndRow    = -1;
    private int selectEndCol    = -1;
    private float charWidth     = 0;    //文字サイズによる
    private boolean isLoad      = false;
    private int maxRowperPage;  //1画面に表示できる最大の行数
    private float charHeight;
    private int editorWidth;
    private int editorHeight;
    private int textSize;

    private color backgroundColorDark  = color(10);
    private color backgroundColorLight = color(245);

    int elapsedTimeFromKeyPressed = -1;
    int duration = 100;

    boolean isLiveProgramming = false;
    MyTextEditor(int x, int y, int w, int h, int textSize){
        this.x = x;
        this.y = y;
        this.textSize = textSize;
        //フォント設定（等幅フォントのみ)
        textSize(textSize);
        textAlign(LEFT, TOP);
        textFont(createFont("Ricty Diminished", textSize));
        texts.add(new StringBuilder(""));
        initColor();
        loadTokenByJSONFile();
        cursorHeight = textSize;
        charWidth = textWidth('a'); //一文字分の幅を取得、aの部分は一文字なら何でもよい
        editorWidth = w - merginLeft * 2;
        editorHeight = h - merginTop * 2;
        maxRowperPage = int(editorHeight / charHeight);
        charHeight = textSize;
        background(0);
    }
    void display() {
        //フォント設定
        textSize(textSize);
        textAlign(LEFT, TOP);
        textFont(createFont("Ricty Diminished", textSize));
        //背景色の設定
        if(isColorLight){
            fill(backgroundColorLight);
        }else{
            fill(backgroundColorDark);
        }
        rect(x,y,editorWidth+merginLeft*2, editorHeight+merginTop*2);
        if(isFocus){
            fill(#3498db);
        }else{
            fill(100);
        }
        noStroke();
        rect(x, y, editorWidth + merginLeft * 2, merginTop);    //上
        rect(x, y, merginLeft, editorHeight + merginTop * 2);  //左
        rect(x + editorWidth + merginLeft, y, merginLeft, editorHeight+merginTop*2);  //右
        rect(x, y + editorHeight + merginTop, editorWidth+merginLeft*2, merginTop);  //下

        // fill(0, 20);
        // rect(0, 0, width, height);
        if(!isLoad){
            if(selectEndRow != -1){
                // drawSelectArea();
            }
            if(isFocus){
                drawCursor();
            }
            if((isAnimated && (isTextToVisual && isFadeOut)) || isTextRepresentation){
                drawText();
            }else{
                drawBlocks();
            }
        }

        if(isAnimated){
            if(isFadeOut){
                alpha-=5;
                if(alpha == 0){
                    isFadeOut = false;
                    isTextRepresentation = !isTextRepresentation;
                    if(isTextToVisual){
                        initBlock();
                    }else{
                        initText();
                    }
                }
            }else{
                alpha += 5;
                if(alpha == 255){
                    isAnimated = false;
                }
            }
        }
        if(isLiveProgramming && elapsedTimeFromKeyPressed != -1 &&  millis() - elapsedTimeFromKeyPressed > duration){
            new LoadTextEditorThread(this).run();
            elapsedTimeFromKeyPressed = -1;
        }
    }
    void drawBlocks(){
        for(int i = 0; i < blocks.size(); i++){
            Tile block = blocks.get(i);
            block.display();
        }
    }
    void drawCursor(){
        noStroke();
        if(isColorLight){
            fill(0);
        }else{
            fill(255);
        }
        rect(x + merginLeft + charWidth * col, y + merginTop + charHeight * nowRow, cursorWidth, cursorHeight);
    }
    void drawText() {
        int count = 0;
        for(int i = topRowNumber; i < topRowNumber + maxRowperPage; i++){
            try{
                StringBuilder text = texts.get(i);
                analyze(new String(text), count);
                count++;
            }catch(IndexOutOfBoundsException e){
                break;
            }
        }
    }
    void analyze(String text, int rowNumber){
        stringLength = text.length();
        currentPosition = 0;
        ArrayList<Token> tokens = new ArrayList<Token>();
        float textX = x + merginLeft;
        float textY = y + merginTop + rowNumber * charHeight;
        while (currentPosition < stringLength) {
            String word = getWord(text);
            int kind = getToken(word);
            tokens.add(new Token(word, kind));
            int count = tobasu(text);
            color fillColor = getColorByToken(kind);
            if(isAnimated){
                fill(fillColor, alpha);
            }else{
                fill(fillColor,alpha);
            }
            if(isAnimated || isTextRepresentation){
                text(word, textX, textY);
            }
            textX += textWidth(word) + charWidth * count;
        }
    }
    private void drawSelectArea(){
        float startX = merginLeft + charWidth * selectStartCol;
        float startY = merginTop + charHeight * selectStartRow;
        float endX =  merginLeft + charWidth * selectEndCol;
        float endY = merginTop + charHeight * selectEndRow;
        fill(200);
        rect(x + startX,y + startY, (endX - startX), endY-startY + charHeight);
    }
    int getToken(String word){
        for(int i = 0; i < myTokens.length; i++){
            if(word.equals(myTokens[i].word)){
                return myTokens[i].kind;
            }
        }
        if(isNum(word)){
            return Enum.NUM;
        }
        if(isString(word)){
            return Enum.MOJIRETSU;
        }else if(isMissString(word)){
            return Enum.MISS_STRING;
        }
        if(isComment(word)){
            return Enum.COMMENT;
        }
        return Enum.OTHER;
    }
    private boolean isNum(String x){
        float result = float(x);
        if (!Float.isNaN(result)) {
            return true;
        } else {
            return false;
        }
    }
    private boolean isString(String x){
        try{
            if(x.charAt(0) == DQUART && x.charAt(x.length()-1) == DQUART){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
    private boolean isMissString(String x){
        try{
            if(x.charAt(0) == DQUART){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
    private boolean isComment(String x){
        try{
            if(x.charAt(0) == DIV && x.charAt(1) == DIV){
                return true;
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }
    //ここは頑張ればもっと短くかける
    private String getWord(String s){
        String result = "";
        boolean isEnd = false;
        boolean isSentou = true;
        do{
            char currentChar = s.charAt(currentPosition);
            if(currentChar == EQUAL){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == EQUAL){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == SEMI){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == COMMA){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == LBRACE || currentChar == RBRACE){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if (currentChar == LCBRACE || currentChar == RCBRACE){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == LBRACKET || currentChar == RBRACKET){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == EXCLAMATION){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == EQUAL){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == LANGLE || currentChar == RANGLE){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == EQUAL){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == PLUS ||currentChar == MULT){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == EQUAL){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == MINUS){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == DIV){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == DIV){
                            result += nextChar;
                            currentPosition++;
                            while(currentPosition < stringLength){
                                currentChar = s.charAt(currentPosition);
                                currentPosition++;
                                if(currentChar == LF){
                                    break;
                                }
                                result += currentChar;
                            }
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == AMPERSAND){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == AMPERSAND){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }else if(currentChar == PIPE){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    if(currentPosition < stringLength){
                        char nextChar = s.charAt(currentPosition);
                        if(nextChar == PIPE){
                            result += nextChar;
                            currentPosition++;
                        }
                    }
                }
                isEnd = true;
            }
            else if(currentChar == SPACE){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                }
                isEnd = true;
            }else if(currentChar == LF){
                isEnd = true;
                currentPosition++;
            }else if(currentChar == DQUART){
                if(isSentou){
                    result += currentChar;
                    currentPosition++;
                    while(currentPosition < stringLength){
                        currentChar = s.charAt(currentPosition);
                        currentPosition++;
                        result += currentChar;
                        if(currentChar == DQUART){
                            break;
                        }

                    }
                }
                isEnd = true;
            }
            else{
                result += currentChar;
                isSentou = false;
                currentPosition++;
            }
        }while(!isEnd && currentPosition < stringLength);
        return result;
    }
    private int tobasu(String s){
        int count = 0;
        while(true){
            if(currentPosition >= stringLength || s.charAt(currentPosition) != SPACE){
                break;
            }
            count++;
            currentPosition++;
        }
        return count;
    }
    void keyPressed(KeyEvent e) {
        if (!isFocus) {
            return;
        }
        if (keyCode == BACKSPACE) {
            if (text != null && text.length() > 0) {
                if(col > 0){
                    col--;
                    text = text.deleteCharAt(col);
                } else {
                    if(row > 0){
                        StringBuilder nowText = text;
                        deleteLine();
                        text.append(nowText);
                        upCursor();
                    }
                }
            }else{
                deleteLine();
                upCursor();
            }
        }else if(keyCode == ENTER) {
            String nokori = text.substring(col);
            text.delete(col, text.length());
            moveCursorDown();
            createNewLine();
            text.append(nokori);
            downCursor();
            if(isLiveProgramming){
                new LoadTextEditorThread(this).run();
            }
        }else if(keyCode == UP) {
            if (row > 0) {
                upLine();
            }
            upCursor();
        }else if(keyCode == DOWN) {
            if(row < texts.size()-1){
                downLine();
                downCursor();
            }
        }else if(keyCode == LEFT) {
            moveCursorLeft();
        }else if(keyCode == RIGHT) {
            moveCursorRight();
        }else if(keyCode == TAB){
            text.insert(col,"    ");
            col += 4;
        }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {    //ctrl+s セーブ
            saveScript();
        }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {    //ctrl+l ロード
            loadScript();
        }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_R) {    //ctrl+r
            isTextToVisual = !isTextToVisual;
            isAnimated = true;
            isFadeOut = true;
        }else {
            if(key == '{'){
                text.insert(col,key);
                col++;
                text.insert(col,'}');
            }else if(key == '}'){
                determineSameChar(key);
            }else if(key == '('){
                text.insert(col,key);
                col++;
                text.insert(col,')');
            }else if(key == ')'){
                determineSameChar(key);
            }else if(key == '['){
                // complementChar(key);
                text.insert(col,key);
                col++;
                text.insert(col,']');
            }else if(key == ']'){
                determineSameChar(key);
            }else if(key == '"'){
                try{
                    if(text.charAt(col) == '"'){
                        col++;
                    }else{
                        text.insert(col,key);
                        col++;
                        text.insert(col,'"');
                    }
                }catch(IndexOutOfBoundsException ex){
                    text.insert(col,key);
                    col++;
                    text.insert(col,'"');
                }
            }else if(26 < key && key < 127){  //altキーやshiftキーを押した時に空白文字が挿入されるのを防ぐため
                text.insert(col,key);        //詳しくはASCIIコード見てね
                col++;
            }
        }

        try{
            texts.set(row, text);
        }catch(IndexOutOfBoundsException ex){
            println(ex.getMessage());
        }
        elapsedTimeFromKeyPressed = millis();
    }
    void complementChar(char c){
        text.insert(col,c);
        col++;
        text.insert(col,char(c+1));
    }
    void determineSameChar(char c){
        try{
            if(text.charAt(col) == c){
                col++;
            }else{
                text.insert(col,c);
                col++;
            }
        }catch(IndexOutOfBoundsException ex){
            text.insert(col,c);
            col++;
        }
    }
    void initBlock() {
        blocks = new ArrayList<Tile>();
        int count = 0;
        for(int i = topRowNumber; i < topRowNumber + maxRowperPage; i++){
            try{
                StringBuilder text = texts.get(i);
                createBlock(new String(text), count);
                count++;
            }catch(IndexOutOfBoundsException e){
                break;
            }
        }
    }
    void initText(){
        println("variables");
        Tile[] arrayBlocks = new Tile[blocks.size()];
        blocks.toArray(arrayBlocks);
        sortByBlockY(arrayBlocks);
        texts = new ArrayList<StringBuilder>();
        int count = 0;
        ArrayList<Tile> rowBlocks = new ArrayList<Tile>();
        for(int i = 0; i < arrayBlocks.length; i++){
            Tile block = arrayBlocks[i];
            int blockRow = round((block.y- merginTop)/charHeight);
            while(count < blockRow){
                Tile[] bs = new Tile[rowBlocks.size()];
                rowBlocks.toArray(bs);
                sortByBlockX(bs);
                texts.add(new StringBuilder(getRowTextByBlock(bs)));
                count++;
                rowBlocks = new ArrayList<Tile>();
            }
            rowBlocks.add(block);
        }
        Tile[] bs = new Tile[rowBlocks.size()];
        rowBlocks.toArray(bs);
        sortByBlockX(bs);
        texts.add(new StringBuilder(getRowTextByBlock(bs)));
        text = texts.get(row);
    }
    // TODO: もっといいソートを使うべき
    void sortByBlockY(Tile[] a){
        for(int i = 0; i < a.length-2; i++){
            for(int j = 1; j < a.length-i; j++){
                if(a[j].y < a[j-1].y){
                    Tile tmp = a[j];
                    a[j] = a[j-1];
                    a[j-1] = tmp;
                }
            }
        }
    }
    void loadTokenByJSONFile() {
        JSONArray values = loadJSONArray("token.json");
        myTokens = new MyToken[values.size()];
        for(int i = 0; i < values.size(); i++){
            JSONObject value = values.getJSONObject(i);
            myTokens[i] = new MyToken(value.getString("word"), value.getInt("token"));
        }
    }
    void sortByBlockX(Tile[] a){
        for(int i = 0; i < a.length-2; i++){
            for(int j = 1; j < a.length-i; j++){
                if(a[j].x < a[j-1].x){
                    Tile tmp = a[j];
                    a[j] = a[j-1];
                    a[j-1] = tmp;
                }
            }
        }
    }
    String getRowTextByBlock(Tile[] bs){
        String result = "";
        for(int i = 0; i < bs.length; i++){
            if(i == 0){
                int count = round((bs[i].x - merginLeft) / charWidth);
                for(int j = 0; j < count; j++){
                    result += " ";
                }
            }else{
                int count = round((bs[i].x - bs[i-1].x - bs[i-1].w) / charWidth);
                for(int j = 0; j < count; j++){
                    result += " ";
                }
            }
            result += bs[i].word;
        }
        return result;
    }

    void createBlock(String text, int rowNumber){
        stringLength = text.length();
        currentPosition = 0;
        float textX = x + merginLeft;
        float textY = y + merginTop + rowNumber * charHeight;
        while (currentPosition < stringLength) {
            String word = getWord(text);
            int kind = getToken(word);
            int count = tobasu(text);
            blocks.add(new Tile(textX, textY, charHeight, word, kind));
            textX += textWidth(word) + charWidth * count;
        }
    }

    void createNewLine(){
        if(row >= texts.size()){
            text = new StringBuilder();
            texts.add(text);
        }else{
            text = new StringBuilder();
            texts.add(row,text);
        }
    }

    void deleteLine(){
        if(row > 0){
            texts.remove(row);
            row--;
            text= texts.get(row);
            col = text.length();
        }
    }

    void upLine (){
        row--;
        text = texts.get(row);
        if(col >= text.length()){
            col = text.length();
        }
    }
    void upCursor(){
        if(nowRow > 0){
            nowRow--;
        }else{
            if(topRowNumber > 0) {
                topRowNumber--;
            }
        }
    }

    void downLine(){
        row++;
        text = texts.get(row);
        if(col >= text.length()){
            col = text.length();
        }
    }
    void downCursor(){
        if(nowRow < maxRowperPage-1){
            nowRow++;
        }else{
            topRowNumber++;
        }
    }

    void moveCursorRight() {
        if(col < text.length()){
            col++;
        }else{
            if(row < texts.size()-1){
                row++;
                text = texts.get(row);
                col = 0;
                downCursor();
            }
        }
    }
    void moveCursorLeft() {
        if(col > 0){
            col--;
        }else{
            if(row > 0){
                row--;
                text = texts.get(row);
                col = text.length();
                upCursor();
            }
        }
    }
    void moveCursorDown() {
        col = 0;
        row++;
    }

    void mousePressed(){
        if(isCursorOnEditor() && selectedPlate == null){
            isFocus = true;
        }else{
            isFocus = false;
        }
        if(isFocus){
            setRowAndColByCursor();
            selectStartRow = row;
            selectStartCol = col;
            if(selectEndRow != -1){
                selectEndRow = -1;
                selectEndCol = -1;
            }
        }
    }
    void mouseDragged(){
        setRowAndColByCursor();
        selectEndRow = row;
        selectEndCol = col;
    }
    void mouseWheel(MouseEvent e ){
        addRow(int(e.getAmount()));
    }
    private boolean isCursorOnEditor(){
        if(x < mouseX && mouseX < x + editorWidth && y < mouseY && mouseY < y + editorHeight){
            return true;
        }else{
            return false;
        }
    }
    void setRowAndColByCursor() {
        int newCol = round((mouseX - x - merginLeft)/charWidth);
        int newRow = int((mouseY - y - merginTop)/charHeight);  //画面上での見かけ上の行数
        if(0 <= newRow && newRow < texts.size()){
            nowRow = newRow;
            row = topRowNumber + nowRow;
        } else {
            if(newRow < 0){
                nowRow = 0;
                row = topRowNumber;
            }else{
                nowRow = texts.size()-1;
                row = topRowNumber + texts.size()-1;
            }
        }
        text = texts.get(row);
        if(0 <= newCol && newCol <= text.length()){
            col = newCol;
        }else{
            if(newCol < 0){
                col = 0;
            }else{
                col = text.length();
            }
        }
    }
    void addRow(int add){    //追加分
        if((add < 0 &&  0 < topRowNumber) || (add > 0 && topRowNumber + maxRowperPage < texts.size() - 1)){
            row += add;    //実際の行数
            col = 0;    //実際の列数
            // int nowRow = 0; //画面上での行数
            topRowNumber += add;   //画面上での一番上の行数が全体の行数の何番目か
            text = texts.get(row);
        }
    }
    void saveScript() {
        if(file == null){
            selectOutput("Choose WriteFile", "writeScript");
        }else{
            writeScript(file);
        }
    }
    void loadScript() {
        isLoad = true;
        allclear();
        selectInput("Choose ReadFile", "readScript");
        isLoad = false;
    }
    void allclear(){
        cursorPositionY = merginTop;
        row = 0;
        col = 0;
        selectStartRow = -1;
        selectStartCol = -1;
        selectEndRow = -1;
        selectEndCol = -1;
        topRowNumber = 0;
        nowRow = 0;

        texts = new ArrayList<StringBuilder>();
        text = new StringBuilder();
    }

    void writeScript(File selection){
        if (selection != null) {
            PrintWriter writer = createWriter(selection.getAbsolutePath());
            String script = "";
            for(int i = 0; i < texts.size(); i++){
                String line = texts.get(i).toString();
                writer.println(line);
            }
            writer.flush();
            writer.close();
        }
    }
    void readScript(File selection) {
        BufferedReader reader = createReader(selection.getAbsolutePath());
        String line;
        file = selection;
        try {
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                texts.add(new StringBuilder(line));
            }
            text = texts.get(row);
        }catch(IOException  e) {
            println(e);
            while(!mousePressed){
                fill(255,0,0);
                textAlign(CENTER);
                text(e.getMessage(),width/2,height/2);
            }
        }
    }

    ArrayList<Token> getTokens(){
        return getTokens(texts.size());
    }
    ArrayList<Token> getTokens(String text){
        ArrayList<Token> result = new ArrayList<Token>();
        analyze(text, result);
        result.add(new Token("", Enum.EOF));
        return result;
    }
    ArrayList<Token> getTokens(int index) {
        ArrayList<Token> result = new ArrayList<Token>();
        for(int i = 0; i < index; i++){
            try {
                StringBuilder text = texts.get(i);
                analyze(new String(text), result);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        result.add(new Token("", Enum.EOF));
        return result;
    }

    void analyze(String text, ArrayList<Token> allToken){
        stringLength = text.length();
        currentPosition = 0;
        tobasu(text);
        while (currentPosition < stringLength) {
            String word = getWord(text);
            int kind = getToken(word);
            allToken.add(new Token(word, kind));
            tobasu(text);
        }
    }

    boolean getFocus(){
        return isFocus;
    }
    void setTexts(String[] textArray){
        texts = new ArrayList<StringBuilder>();
        for(int i = 0; i < textArray.length; i++){
            StringBuilder text = new StringBuilder(textArray[i]);
            texts.add(text);
        }
        text = texts.get(0);    //要検討
    }
}
// ファイルにスクリプトを書き出す
void writeScript(File selection) {
    editor.writeScript(selection);
}

//ファイルからスクリプトを読み込んでくる
void readScript(File selection) {
    editor.readScript(selection);
}

class Tile {
    float x, y;
    float w, h;
    String word;
    int kind;
    Tile(float x, float y, float h, String word, int kind) {
        this.x = x;
        this.y = y;
        this.word = word;
        this.w = textWidth(word);
        this.h = h;
        this.kind = kind;
    }
    void display() {
        color fillColor = getColorByToken(kind);
        fill(fillColor,alpha);
        rect(x,y,w,h);
        fill(50,alpha);
        text(word,x,y);
    }
    boolean isOnMouse(){
        if(x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h){
            return true;
        }else{
            return false;
        }
    }
    void move(int addX, int addY){
        x += addX;
        y += addY;
    }
}

class MyToken {
    String word;
    int kind;
    MyToken(String word, int kind){
        this.word = word;
        this.kind = kind;
    }
}
class Token {
    String word;
    int kind;
    Token(String word, int kind){
        this.word = word;
        this.kind = kind;
    }
}
static class Enum {
    static int OTHER        = 1;
    static int NUM          = 3;
    static int SEMI         = 4;
    static int ASSIGN       = 5;
    static int PLUS         = 6;
    static int MINUS        = 7;
    static int MULT         = 8;
    static int DIV          = 9;
    static int SPACE        = 10;
    static int FOR          = 11;
    static int IF           = 12;
    static int LCBRACE      = 13;
    static int RCBRACE      = 14;
    static int LBRACE       = 15;
    static int RBRACE       = 16;
    static int COMMA        = 17;
    static int VOID         = 18;
    static int MOJIRETSU    = 19;
    static int FOR_START    = 20;
    static int COMMENT      = 21;
    static int MISS_STRING  = 22;
    static int AND          = 23;
    static int OR           = 24;
    static int TRUE         = 25;
    static int FALSE        = 26;
    static int LESS         = 27;
    static int LESS_THAN    = 28;
    static int GRATER_THAN  = 29;
    static int GRATER       = 30;
    static int EQUAL        = 31;
    static int NOT_EQUAL    = 32;
    static int IF_START     = 33;
    static int LBRACKET     = 34;
    static int RBRACKET     = 35;
    static int NEW          = 36;
    static int WHILE        = 37;
    static int WHILE_START  = 38;

    //DATA
    //Primitive
    static int INT     = 1000;
    static int STRING  = 1001;
    static int FLOAT   = 1002;
    static int BOOLEAN = 1003;
    static int COLOR   = 1004;


    static int RECT         = 100;
    static int ELLIPSE      = 101;
    static int DRAW         = 102;
    static int SETUP        = 103;
    static int SETUP_END    = 104;
    static int METHOD_START = 105;
    static int METHOD       = 106;
    static int LINE         = 107;

    //Color
    static int BACKGROUND   = 2000;
    static int FILL         = 2003;
    static int NO_STROKE    = 2005;
    static int STROKE       = 2006;

    //Output
    static int PRINTLN = 3003;

    //Typography
    static int TEXT = 4102;
    static int TEXT_SIZE = 4203;

    //Input
    //Mouse
    static int MOUSE_PRESSED_METHOD = 5004;
    static int MOUSE_X              = 5008;
    static int MOUSE_Y              = 5009;

    //Environment
    static int HEIGHT   = 6010;
    static int WIDTH    = 6018;

    //実体のないやつら
    static int INT_ARRAY                    = 10000;
    static int STRING_ARRAY                 = 10001;
    static int BOOLEAN_ARRAY                = 10002;
    static int INT_ARRAY_SYNTAX_SUGAR       = 10003;
    static int STRING_ARRAY_SYNTAX_SUGAR    = 10004;
    static int BOOLEAN_ARRAY_SYNTAX_SUGAR   = 10005;
    static int ASSIGN_ARRAY                 = 10006;
    static int INDEX                        = 10007;
    static int METHOD_CALL                  = 10008;
    static int MOUSE_PRESSED_METHOD_START   = 10009;
    static int DRAW_METHOD_START            = 10010;
    static int DECL                         = 10011;


    static int EOF = Integer.MAX_VALUE;

    static int YUYA = 831;
}

color getColorByToken(int kind){
    if(kind == Enum.INT || kind == Enum.INT_ARRAY || kind == Enum.DRAW || kind == Enum.SETUP || kind == Enum.FLOAT || kind == Enum.COLOR){
        return alizarin;    //#e74c3c
    }else if(kind == Enum.FOR){
        return nephritis;   //#27ae60
    }else if(kind == Enum.NUM || kind == Enum.NEW){
        return sunFlower;   //#f1c40f
    }else if(kind == Enum.PLUS || kind == Enum.MINUS ||kind == Enum.MULT ||kind == Enum.DIV){
        return concrete;
    }else if(kind == Enum.WHILE){
        return schaussPink;   //#FFA3B2
    }else if(kind == Enum.IF){
        return carrot;
    }else if(kind == Enum.VOID){
        return color(78,205,196);
    }else if(kind == Enum.AND || kind == Enum.OR){
        return wetAsphalt;  //#34495e
    }else if(kind == Enum.STRING || kind == Enum.STRING_ARRAY){
        return emerald;
    }else if(kind == Enum.MOJIRETSU || kind == Enum.MISS_STRING){
        return greenSea;
    }else if(kind == Enum.RECT || kind == Enum.ELLIPSE || kind == Enum.FILL || kind == Enum.BACKGROUND || kind == Enum.PRINTLN || kind == Enum.TEXT || kind == Enum.TEXT_SIZE || kind == Enum.LINE || kind == Enum.STROKE || kind == Enum.NO_STROKE){
        return peterRiver;  //#3498db
    }else if(kind == Enum.COMMENT){
        return silver;
    }else if(kind == Enum.BOOLEAN || kind == Enum.BOOLEAN_ARRAY){
        return belizeHole;  //#2980b9
    }else if(kind == Enum.MOUSE_X || kind == Enum.MOUSE_Y || kind == Enum.HEIGHT || kind == Enum.WIDTH){
        return amethyst;    //#9b59b6
    }else if(kind == Enum.MOUSE_PRESSED_METHOD){
        return color(#A092E5);
    }else if(kind == Enum.TRUE){
        return color(#0000FF);
    }
    else if(kind == Enum.YUYA || kind == Enum.FALSE){
        return color(#ff0000);
    }else{
        if(isColorLight){
            return color(0);
        }
        return color(255);
    }
}
void initColor(){
    pomegranate   = color(#c0392b);
    alizarin      = color(#e74c3c);
    schaussPink   = color(#FFA3B2);
    pumpkin       = color(#d35400);
    carrot        = color(#e67e22);
    orange        = color(#f39c12);
    sunFlower     = color(#f1c40f);
    turquoise     = color(#1abc9c);
    emerald       = color(#2ecc71);
    greenSea      = color(#16a085);
    nephritis     = color(#27ae60);
    peterRiver    = color(#3498db);
    belizeHole    = color(#2980b9);
    wetAsphalt    = color(#34495e);
    amethyst      = color(#9b59b6);
    silver        = color(#bdc3c7);
    concrete      = color(#95a5a6);
    clouds        = color(#ecf0f1);
}
