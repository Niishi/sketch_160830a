//自作のGUIボタン
//つくりかけ、もっとしっかりつくる

MyGUI selectedGUI;

int kindCursor;
boolean isRenew;
int txfWidth = 60;  //テキストフィールドの幅
int txfHeight = 20; //テキストフィールドの高さ

ArrayList<MyGUI> guiList = new ArrayList<MyGUI>();
public abstract class MyGUI {
    public abstract void draw();
    public abstract boolean isMouseOver();
    int textSize = 12;
    int x, y, w, h;
    boolean isTextField = false;

    Balloon balloon;    //非常に汚いかきかた！！！！
}

class MyButton extends MyGUI {

    String label;
    PImage image;
    color bColor        = color(255);
    color bHighlight    = color(255);
    color bNotPushColor = color(120);
    boolean isOver      = false;
    boolean isPush      = true;

    MyButton (String label, int x, int y) {
        this.x = x; this.y = y;
        h = 30; w = 100;
        this.label = label;
        guiList.add(this);
    }
    MyButton (String label, int x, int y, int w, int h) {
        this(label, x, y);
        this.w = w; this.h = h;
    }
    MyButton (PImage image,String label, int x, int y) {
        this(label, x, y);
        this.image = image;
        h = 30;
    }
    int mousePressTime = 0;
    void draw () {
        noStroke();
        update();
        color c = bColor;
        if (image != null) {
            image(image, x, y, w, h);
            if(isOver){
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h+10);
            }
        } else {
            // drawShadowSide();
            textSize(16);
            if(mousePressTime > 0){
                fill(bNotPushColor);
                mousePressTime--;
            }
            else if (isOver) {
                if(mousePressed){
                    mousePressTime = 30;
                }
                c = bHighlight;
            }
            drawButton(c);
            if(mousePressTime > 0){
                stroke(0);
                strokeWeight(2);
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h / 2+h*(1.0/15));
            }
            else if(isOver){
                stroke(0);
                strokeWeight(2);
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h / 2-h*(1.0/6));
            }else{
                stroke(0);
                strokeWeight(2);
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h / 2);
            }
        }
    }
    void drawButton(color c){
        int a = h/5;
        int groundColor = 250;
        for(int i = a; i > 0; i--){
            fill(groundColor/a*i + red(c)/a*(a-i), groundColor/a*i + green(c)/a*(a-i), groundColor/a*i + blue(c)/a*(a-i));
            rect(x-i,y-i,w+i*2,h+i*2);
        }
        fill(c);
        rect(x,y,w,h);
    }
    int shadowX = 2;
    void drawShadowSide(){
        for(int i = 1; i <= shadowX; i++){
            fill(color(120),100-10*i);
            rect(x-i,y-i,w+2*i,h+int(2*1.5)*i);
        }
    }
    void update() {
        if ( isMouseOver() ) {
            kindCursor = HAND;
            isOver = true;
        } else {
            isOver = false;
        }
    }
    void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    boolean isMouseOver () {
        if (mouseX >= x && mouseX <= x+w &&
            mouseY >= y && mouseY <= y+h) {
            return true;
        } else {
            return false;
        }
    }
    void setColor(color bColor, color highlight, color pushColor) {
        this.bColor = bColor;
        bHighlight = highlight;
        bNotPushColor = pushColor;
    }
    int getWidth(){
        return w;
    }
}
//自作コンボボックス
class MyComboBox extends MyGUI {
    String[] items;  //コンボボックスに表示するもの
    Boolean isSelected = false;  //コンボボックスを選択しているかどうか（選択されていると中身全体が見える）
    private boolean isChanged;
    private final int MARGIN = 10;
    //幅と高さを設定しないと勝手に決める
    MyComboBox(String[] items, int x, int y) {
        this.items = items;
        this.x = x;
        this.y = y;
        w = 150;
        h = 20;
        setItem(items[0]);
        guiList.add(this);
    }
    MyComboBox(String[]items, int x, int y, int w, int h) {
        this(items, x, y);
        this.w = w;
        this.h = h;
    }
    MyComboBox(String[]items, int x, int y, int w, int h, int textSize) {
        this(items, x, y, w, h);
        this.textSize = textSize;
    }
    void draw() {
        textFont(font);
        stroke(0);
        strokeWeight(2);
        textAlign(LEFT, TOP);
        drawContent();
        actionByMouseEvent();
    }
    private void drawContent(){
        int itemMaxWidth = getMaxWidth() + MARGIN;
        if (!isSelected) {  //選択されていなければ
            fill(255);      //一行分しか表示しない
            rect(x, y, w, h);
            fill(0);
            text(items[0], x+ MARGIN/2, y);
        } else {                                    //選択されていれば
            for (int i = 0; i < items.length; i++) {  //中身全部表示する
                if (isMouseIn(x, y+h*i, itemMaxWidth)) {
                    fill(155);
                } else {
                    fill(255);
                }
                rect(x, y+h*i, itemMaxWidth, h);
                fill(0);
                text(items[i], x + MARGIN/2, y + h*i);
            }
        }
    }
    private void actionByMouseEvent(){
        int itemMaxWidth = getMaxWidth() + MARGIN;
        if (mousePressed && mousePressedTime < GUI_SELECTED_MAX_TIME && isSelected) {
            if (x < mouseX && mouseX <= x + itemMaxWidth && y < mouseY && mouseY < y + h * items.length) {
                String a = getSelectedItem();  //選択された項目を取り出してきて
                int selectIndex = getSelectedIndex();  //そのインデックスも取り出す
                if(selectIndex > 0){
                    items[selectIndex] = items[0];  //現時点で先頭にある項目と選択した項目を入れ替える
                    items[0] = a;
                    this.w = int(textWidth(items[0]))+MARGIN;
                    isRenew =  true;
                    isChanged = true;
                }
            }
            isSelected = false;  //選択を解除
            mousePressed =false;
        }
        //コンボボックスがそもそも選択されていなかったら
        if (mousePressed  && mousePressedTime < GUI_SELECTED_MAX_TIME && !isSelected && isMouseOver()) {
            isSelected = true;  //コンボボックスを選択状態にする
            mousePressed = false;
            selectedGUI = this;
        }
    }

    //マウスがコンボボックスのアイテム内にあるかどうか
    private boolean isMouseIn(int x, int y, int w) {
        if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y+h) {
            return true;
        } else {
            return false;
        }
    }
    boolean isMouseOver(){
        return mouseX >= x && mouseX < x + w && mouseY > y && mouseY <= y + h;
    }

    boolean checkChanged(){
        if(isChanged){
            isChanged = false;
            return true;
        }else{
            return false;
        }
    }

    int getWidth(){
        return w;
    }

    //表示されているitemを返す
    String getItem() {
        return items[0];
    }

    //選択したアイテムを返す
    String getSelectedItem() {
        for (int i = 0; i < items.length; i++) {
            if (y + h * i < mouseY && mouseY <= y + h* (i+1)) {
                return items[i];
            }
        }
        return null;
    }
    int getSelectedIndex() {
        for (int i = 0; i < items.length; i++) {
            if (y + h * i < mouseY && mouseY <= y + h* (i+1)) {
                return i;
            }
        }
        return -1;
    }

    void addItem(String item) {
        String[] newItems = new String[items.length+1];
        for (int i = 0; i < items.length; i++) {
            newItems[i] = items[i];
        }
        newItems[items.length] = item;
        items = newItems;
    }
    void setItem(String name) {
        int i = 0;
        for (String item : items) {
            if (item.equals(name)) {
                items[i] = items[0];
                items[0] = name;
                textFont(font);
                this.w = int(textWidth(items[0]))+MARGIN;
            }
            i++;
        }
    }
    private int getMaxWidth(){
        int max = 0;
        for(String item : items){
            int itemWidth = int(textWidth(item));
            if(itemWidth > max) max = itemWidth;
        }
        return max;
    }

    void setItems(String[] newItems) {
        items = newItems;
    }

    void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void shiftPosition(int addX, int addY){
        this.x += addX;
        this.y += addY;
    }
}

class MyTextField extends MyGUI {
    private String text = "";
    private PFont mono;
    boolean isFocus = false;
    private int offsetX = 10;
    private int posX = offsetX;
    private float charWidth;
    private int index = 0;    //文字列に挿入する場所
    private int myFrameCount = 0;
    private boolean isChanged;
    private color fillColor;
    private int kind;
    private boolean isNotError = true;
    MyTextField(int x, int y) {
        this.x = x;  this.y = y;
        this.fillColor = #ecf0f1;
        w = 54; h = 20;
        mono = loadFont("RictyDiminished-Regular-16.vlw");
        textFont(mono);
        charWidth = textWidth("a");
        kind = -1;
        setText("");
        guiList.add(this);
        isTextField = true;
    }
    MyTextField(int x, int y, String text){
        this(x,y);
        setText(text);
    }
    MyTextField(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.fillColor = #ecf0f1;
        w = width;
        h = height;
        mono = loadFont("RictyDiminished-Regular-16.vlw");
        textFont(mono);
        charWidth = textWidth("a");
        setText("");
        guiList.add(this);
        isTextField = true;
        kind = -1;
    }
    MyTextField(String text, int x, int y, int width, int height) {
        this(x,y,width,height);
        setText(text);
    }
    void draw() {

        if(isFocus){
            stroke(wetAsphalt);
            strokeWeight(1);
        }else
            noStroke();
        drawInputField();
        drawCursor();
        drawText();
        if (keyPressed && isFocus) {
            keyPressed();
        }
        if (mousePressed && !isFocus) {
            if(isMouseOver()){
                isFocus = true;
                setLastIndex();
                myFrameCount = 0;
            }
        }else if(mousePressed && isFocus){
            isFocus = isMouseOver();
        }
        if (isMouseOver()) {
            kindCursor = HAND;
        }
        myFrameCount++;
    }
    boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + w && y <= mouseY && mouseY <= y + h) {
            return true;
        }
        return false;
    }
    int getWidth(){
        return w;
    }
    boolean checkChanged(){
        if(isChanged){
            isChanged = false;
            return true;
        }else{
            return false;
        }
    }
    String getText() {
        return text;
    }
    int getKind(){
        return kind;
    }
    void setText(String text) {
        this.text = text;
        int tLength = text.length();
        w = int(textWidth(text)) + offsetX * 2;
    }
    void setKind(int kind){
        this.kind = kind;
    }
    void setFillColor(color fc){
        this.fillColor = fc;
    }
    void addText(String text) {
        this.text += text;
        w = int(textWidth(this.text)) + offsetX * 2;
        isChange = true;
    }
    void moveTo(int x, int y) { //目的地を引数にもつ
        this.x = x;
        this.y = y;
        // if(balloon != null) balloon.setPosition(x + w + MARGIN, y + h + MARGIN, x + w/2, y + h/2);
    }
    void shiftPos(int addX, int addY){ //現在位置からの動かす距離を引数にもつ
        this.x += addX;
        this.y += addY;
        if(balloon != null) balloon.shiftPos(addX, addY);
    }
    private void checkType() {
        String type = "";
        if(kind == Enum.INT) {
            isNotError = checkInt();
            type = "int";
        }else if(kind == Enum.STRING){
            isNotError = checkString();
            type = "String";
        }else if(kind == -1){
            isNotError = true;
        }else{
            println("type is not defined. In MyTextField class checkType() method!!!! type is \"" + type + "\"");
        }
        if(isNotError){
            balloonList.remove(balloon);
            balloon = null;
        }
    }
    private boolean checkInt(){
        try{
            Lang l = new Lang(editor.getTokens(text));
            l.next = l.getNextToken();
            l.E();
            hasError = false;
            return true;
        }catch(UndefinedVariableException e){
            balloonList.remove(balloon);
            balloon = new Balloon("UndefinedVariableException", this);
            hasError = true;
            return false;
        }catch(ArithmeticException e){
            balloonList.remove(balloon);
            balloon = new Balloon("ArithmeticException:\n" + e.getMessage(), this);
            hasError = true;
            return false;
        }catch(Exception e){
            balloonList.remove(balloon);
            balloon = new Balloon("Type is not correct.\n You must input int", this);
            hasError = true;
            return false;
        }
    }
    private boolean checkString(){
        try{
            Lang l = new Lang(editor.getTokens(text));
            l.next = l.getNextToken();
            l.stringE();
            hasError = false;
            return true;
        }catch(Exception e){
            balloonList.remove(balloon);
            balloon = new Balloon("Type is not correct.\n You must input int", this);
            hasError = true;
            return false;
        }
    }
    //private method
    private void drawInputField(){
        if (isFocus) {
            strokeWeight(2);
        } else {
            strokeWeight(1);
        }
        fill(fillColor);
        rect(x, y, w, h, 10);
    }
    private void drawCursor(){
        if (isFocus && myFrameCount % 100 < 50) {
            stroke(0);
            strokeWeight(1);
            line(x + posX, y + 3, x + posX, y + h - 3);
        }
    }
    private void drawText(){
        textAlign(LEFT, CENTER);
        textFont( mono );
        fill(0);
        text(text, offsetX + x, y+h/2);
    }
    private void keyPressed() {
        if (keyCode == LEFT) {
            moveCursorLeft();
        } else if (keyCode == RIGHT) {
            moveCursorRight();
        } else if (keyCode == SHIFT) {
            //何もしない
        } else if (key == BACKSPACE) {
            if(index > 0 && index <= text.length()){
                StringBuffer sb = new StringBuffer(text);
                sb.deleteCharAt(index-1);
                text = sb.toString();
                w -= charWidth;
                moveCursorLeft();
                isChanged = true;
            }
        } else if(26 < key && key < 127){
            if (index < text.length()) {
                StringBuffer sb = new StringBuffer(text);
                sb.insert(index, key);
                text = sb.toString();
            } else {
                text += key;
            }
            w += charWidth;
            moveCursorRight();
            isChanged = true;
        }
        keyPressed = false;
        if(isChanged) checkType();
    }
    private void moveCursorLeft(){
        if (index > 0) {
            posX -= charWidth;
            index--;
        }
        myFrameCount = 0;
    }
    private void moveCursorRight(){
        if (index < text.length()) {
            posX += charWidth;
            index++;
        }
        myFrameCount = 0;
    }
    private void setLastIndex(){
        this.index = text.length();
        this.posX =  int(this.index * charWidth) + offsetX;
    }
}
