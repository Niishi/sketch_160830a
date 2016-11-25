Plate errPlate;
int marginX = 30;
int marginY = 20;
int indent = 0;
private int txfPosX = 10;  //操作内のテキストフィールドの位置
private int txfPosY = 5;
int loopTxfPosX = 50, loopTxfPosY = 5;
final int originalLoopHeight = 100;
final int originalLoopWidth = 180;
final int indentVolume = 4;

public abstract class Plate {

    int x, y;
    int pWidth, pHeight;
    int[] targetPos;
    WallPlate upperPlate;  //所属するループ
    Plate prePlate;  //上にある操作
    Plate nextPlate;  //下にある操作
    FuncPlate belongPlate;
    boolean isWallPlate = false;
    protected color fillColor;
    boolean isVariablePlate = false;

    public abstract void draw();
    public abstract void drawShadow();
    public abstract void drawTransparent();
    public abstract void moveTo(int addX, int addY);
    public abstract void execute();
    public abstract String getScript();

    public String getNoIndentScript(){
        return "未定義:getNoIndentScript()";
    }
    public boolean isMouseOver(){
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    boolean isPlateBelow(Plate plate) {     //引数に渡したplateが自分の下にいるかどうか
        if (abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
            return true;
        }
        // else if(plate.upperPlate != null && plate.upperPlate == this.upperPlate && y + pHeight - plate.y <= 0  && abs(x - plate.x) <= marginX){
        //     return true;
        // }
        return false;
    }
    public String changePlatetoString(){
        return  "this      : " + this + "\n" +
                "upperPlate: " + upperPlate + "\n" +
                "prePlate  : " + prePlate + "\n" +
                "nextPlate : " + nextPlate;
    }
    public void combinePlate(Plate plate){  //thisの下に引数のplateをくっつける
        this.nextPlate = plate;
        plate.prePlate = this;
    }
    public void checkPlateLink() {
        if(this.prePlate != null){
            if(this.prePlate.isPlateBelow(this)){
                this.goToUnderThePlate(this.prePlate);
            } else {
                this.prePlate.nextPlate = null;
                this.prePlate = null;
            }
        }
    }
    public void checkWallPlateLink(WallPlate wp){
        if(this.upperPlate != null) {
            if(this.upperPlate != wp){
                this.upperPlate.cancelLink(this);
            }
        }
    }
    public WallPlate getNearestWallPlate(ArrayList<WallPlate> wallPlateList){
        int minDist = 10000000;
        WallPlate nearestPlate = null;
        for(int i = 0; i < wallPlateList.size(); i++){
            WallPlate wallPlate = wallPlateList.get(i);
            if(this != wallPlate){
                if(wallPlate.isPlateIn(this)){
                    int dist = getDistBetweenPlate(wallPlate);
                    if(dist < minDist){
                        minDist = dist;
                        nearestPlate = wallPlate;
                    }
                }
            }
        }
        return nearestPlate;
    }
    public void combineWallPlate(WallPlate wallPlate){
        int lastIndex = wallPlate.loopOpes.size()-1;
        if(lastIndex >= 0){
            Plate lastPlate = wallPlate.loopOpes.get(lastIndex);
            lastPlate.nextPlate = this;
            this.prePlate = lastPlate;
        }
        wallPlate.loopOpes.add(this);
        this.upperPlate = wallPlate;
        int addX = this.pWidth + wallPlate.wallPlateWidth - wallPlate.pWidth;
        if(addX < 0) addX = 0;
        int addY = this.pHeight;
        wallPlate.resize(addX, addY);
        if(this.nextPlate != null){
            this.nextPlate.combineWallPlate(wallPlate);
        }
    }
    public void goToUnderThePlate(Plate plate){
        int differencePosX = plate.x - this.x;
        int differencePosY = plate.y + plate.pHeight - this.y;
        this.moveTo(differencePosX, differencePosY);
    }
    public void goIntoWallPlate(WallPlate wallPlate){
        int index = wallPlate.loopOpes.indexOf(this);
        int differencePosX = wallPlate.x + wallPlate.wallPlateWidth - this.x;
        int differencePosY = wallPlate.getPositionYinLoopOpes(index) - this.y;
        this.moveTo(differencePosX, differencePosY);
    }
    public void setBorder(){
        if(isDebugMode)
        {gradationR += 3;
        gradationR = gradationR % 350;
        strokeWeight(3);
        stroke(gradationR,0,0);}
    }
    private int getDistBetweenPlate(Plate plate){
        return int(dist(this.x, this.y, plate.x, plate.y));
    }

    //スクリプト関係のメソッド群
    String getIndent(){
        String result = "";
        for(int i = 0; i < indent; i++){
            result += " ";
        }
        return result;
    }
    void incrementIndent(){
        indent += indentVolume;
    }
    void decrementIndent(){
        indent -= indentVolume;
    }
    int getValue(String text, MyGUI gui){
        try{
            return new Lang(editor.getTokens(text)).getValue();
        }catch(IndexOutOfBoundsException e){
            balloonList.remove(gui.balloon);
            gui.balloon = new Balloon("undefined variable!", gui.x + gui.w, gui.y + gui.h + MARGIN, gui.x + gui.w / 2, gui.y + gui.h/2);
            return 0;
        }catch(Exception e){
            return 0;
        }
    }
    String getStringValue(String text){
        try{
            return new Lang(editor.getTokens(text)).getStringValue();
        }catch(Exception e){
            println("error occurs in getStringValue() method");
            return "";
        }
    }
    public void mouseClicked(MouseEvent e) {
        //特に何もしない。必要ならサブクラスで定義してね
    }
    public int getFillColor(){
        return fillColor;
    }
}
public abstract class WallPlate extends Plate {
    ArrayList<Plate> loopOpes = new ArrayList<Plate>();
    int wallPlateWidth  = 30;   //囲みタイルの上の部分の幅
    int wallPlateHeight = 30;   //囲みタイルの上の部分の高さ
    int wallPlateHeightBottom = 30;
    void removePlate(Plate plate) {
        loopOpes.remove(plate);
    }
    void resize(int addX, int addY) {
        pWidth += addX;
        pHeight += addY;
        if (upperPlate != null) {
            addX = this.pWidth + wallPlateWidth - upperPlate.pWidth;
            if(addX < 0) addX = 0;
            upperPlate.resize(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(0, addY);
        }
    }
    protected void updateWidth(){
        if(!loopOpes.isEmpty()){
            int rightmostX = 0;
            Plate p = loopOpes.get(0);
            do{
                int x = p.x + p.pWidth;
                if(x > rightmostX){
                    rightmostX = x;
                }
                p = p.nextPlate;
            }while(p != null);
            resize(rightmostX - (this.x + this.pWidth), 0);
        }
    }
    boolean isPlateIn(Plate stm) {
        if (x  <= stm.x && stm.x <= x+pWidth +wallPlateWidth && y  <= stm.y && stm.y <= y + pHeight - wallPlateHeightBottom) {
            return true;
        }
        return false;
    }
    boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + wallPlateWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + wallPlateHeight) {
            return true;
        }
        if (x <= mouseX && mouseX <= x + pWidth && y + pHeight - wallPlateHeightBottom <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public int getPositionYinLoopOpes(int index){
        int resultY = y + wallPlateHeight;
        for(int i = 0; i < index; i++){
            Plate p = loopOpes.get(i);
            resultY += p.pHeight;
        }
        switch(x){

        }
        return resultY;
    }
    void cancelLink(Plate plate){
        loopOpes.remove(plate);
        plate.upperPlate = null;
        int maxWidth = getMaxWidthInLoopOpes();
        int addX = 0;
        if(maxWidth != 0){
            addX = maxWidth + wallPlateWidth - this.pWidth;
        }else{
            addX = originalLoopWidth - this.pWidth;
        }
        int addY = -plate.pHeight;
        resize(addX, addY);
        if(plate.nextPlate != null){
            cancelLink(plate.nextPlate);
        }
    }
    private int getMaxWidthInLoopOpes(){
        int maxWidth = 0;
        for(int i = 0; i < loopOpes.size(); i++){
            Plate p = loopOpes.get(i);
            if(p.pWidth > maxWidth){
                maxWidth = p.pWidth;
            }
        }
        return maxWidth;
    }

}
public abstract class FuncPlate extends Plate {
    ArrayList<Plate> plates = new ArrayList<Plate>();
    MyTextField txf;
    MyButton addVarButton;
    MyButton removeVarButton;
    ArrayList<MyTextField> args = new ArrayList<MyTextField>();
}

int originalStatementWidth = 20;

HashMap<String, Integer[]> argTypeList;
void initArgTypeList(){
    argTypeList = new HashMap<String, Integer[]>();
    Integer[] a = {Enum.INT, Enum.INT, Enum.INT, Enum.INT};
    argTypeList.put("rect", a);
    argTypeList.put("ellipse", a);
    argTypeList.put("line", a);
    Integer[] b = {Enum.INT, Enum.INT, Enum.INT};
    argTypeList.put("fill", b);
    argTypeList.put("background", b);
    argTypeList.put("stroke", b);
    Integer[] c = {Enum.STRING};
    argTypeList.put("println", c);
    Integer[] d = {Enum.INT};
    argTypeList.put("textSize", d);
    Integer[] e = {Enum.STRING, Enum.INT, Enum.INT};
    argTypeList.put("text", e);
}
class StatementPlate extends Plate {
    private MyComboBox comboBox;  //命令の選択欄
    ArrayList<MyTextField> textFields = new ArrayList<MyTextField>();
    ConditionPlate condPlate;
    private int txfInterval = 10;
    private int comboBoxWidth = 0;
    private int comboBoxX = 10;
    StatementPlate(String methodName, int x, int y, String[] textFieldContents) {//追加するときは編集よろしく
        this.x = x;
        this.y = y;
        pWidth = 20;
        pHeight = 30;
        fillColor = peterRiver;
        String[] stmItems = {
            "rect", "ellipse", "fill", "background", "noStroke", "stroke", "line",  "println", "text", "textSize"
        };
        comboBox = new MyComboBox(stmItems, x + comboBoxX, y + 5, 70, 20);
        comboBox.setItem(methodName);
        comboBoxWidth = comboBox.getWidth();
        pWidth += comboBoxWidth;
        changeArraySizeByComboBox();
        setTextFieldContents(textFieldContents);
        setTextFieldPosition();
    }
    StatementPlate(int x, int y) {//追加するときはここも編集してね
        this.x = x;
        this.y = y;
        pWidth = 150;
        pHeight = 30;
        fillColor = peterRiver;
        String[] stmItems = {
            "rect", "ellipse", "line", "background", "stroke", "noStroke", "fill",  "println", "text", "textSize"
        };
        comboBox = new MyComboBox(stmItems, x + 10, y +5, 70, 20);
        changeArraySizeByComboBox();
    }
    String val = "";
    void draw() {
        textFont(font);
        checkGUIChange();
        if (val != null) {
            textAlign(CENTER, CENTER);
            fill(0);
            textSize(14);
            text(val, x+pWidth + 20, y+pHeight/2);
            textSize(12);
        }
        if(executingPlate == this){
            setBorder();
        } else if (errPlate == this) {
            stroke(255, 0, 0);
        } else {
            noStroke();
        }
        fill(fillColor);
        rect(x, y, pWidth, pHeight, 10);
        comboBox.draw();
        for(int i = 0; i < textFields.size(); i++){
            textFields.get(i).draw();
        }
        strokeWeight(2);
    }
    void execute(){//実行部はここに追加する
        String comboboxItem = comboBox.getItem();
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            try{
                if(comboboxItem.equals("rect")){
                    int[] arg = getArg(textFields.size());
                    rect(arg[0],arg[1],arg[2],arg[3]);
                }else if(comboboxItem.equals("ellipse")){
                    int[] arg = getArg(textFields.size());
                    ellipse(arg[0], arg[1], arg[2], arg[3]);
                }else if(comboboxItem.equals("fill")){
                    int[] arg = getArg(textFields.size());
                    fill(arg[0], arg[1], arg[2]);
                }else if(comboboxItem.equals("noStroke")){
                    noStroke();
                }else if(comboboxItem.equals("stroke")){
                    int[] arg = getArg(textFields.size());
                    stroke(arg[0], arg[1], arg[2]);
                }else if(comboboxItem.equals("background")){
                    int[] arg = getArg(textFields.size());
                    background(arg[0], arg[1], arg[2]);
                }else if(comboboxItem.equals("println")){
                    int[] arg = getArg(textFields.size());
                    println(arg[0]);
                }else if(comboboxItem.equals("text")){
                    String text = getStringValue(textFields.get(0).getText());
                    int[] arg = getArg(1,2);
                    text(text, arg[0], arg[1]);
                }else if(comboboxItem.equals("textSize")){
                    int[] arg = getArg(textFields.size());
                    textSize(arg[0]);
                }else if(comboboxItem.equals("line")){
                    int[] arg = getArg(textFields.size());
                    line(arg[0], arg[1], arg[2], arg[3]);
                }
            }catch(ArrayIndexOutOfBoundsException ex){
                println(ex.toString());
            }
            step++;
        }
    }
    private void setTextField(int count, Integer[] typeKind) {
        pWidth = originalStatementWidth;
        textFields = new ArrayList<MyTextField>();
        pWidth += comboBoxWidth;
        for(int i = 0; i < count; i++){
            MyTextField txf = new MyTextField(0, 0, txfWidth, txfHeight);
            int txfWidth = txf.getWidth();
            txf.moveTo(x + comboBoxX + comboBoxWidth + txfPosX + (txfWidth + txfInterval) * i, y + txfPosY);
            txf.setKind(typeKind[i]);
            txf.setFillColor(colorDict.get(typeKind[i]));
            textFields.add(txf);
            pWidth += txfWidth + txfInterval;
        }
    }
    private void setTextFieldContents(String[] textFieldContents){
        for(int i = 0; i < textFieldContents.length; i++){
            textFields.get(i).setText(textFieldContents[i]);
        }
    }
    private void setTextFieldPosition(){
        pWidth = originalStatementWidth + comboBoxWidth;
        int txfWidth = 0;
        for(int i = 0; i < textFields.size(); i++){
            MyTextField txf = textFields.get(i);
            txf.moveTo(x + comboBoxX + comboBoxWidth  + txfPosX + txfWidth , y + txfPosY);
            txfWidth += txf.getWidth() + txfInterval;
            pWidth += txf.getWidth() + txfInterval;
        }
    }
    private int getSumTextFieldWidth(){
        int sum = 0;
        for(MyTextField txf : textFields){
            sum += txf.getWidth() + txfInterval;
        }
        return sum;
    }
    private int[] getArg(int length){
        int[] arg = new int[length];
        for(int i = 0; i < length; i++){
            String a = textFields.get(i).getText();
            arg[i] = getValue(a, textFields.get(i));
        }
        return arg;
    }
    private int[] getArg(int start, int end){
        int[] arg = new int[end - start+1];
        for(int i = start; i <= end; i++){
            String a = textFields.get(i).getText();
            arg[i-start] = getValue(a, textFields.get(i));
        }
        return arg;
    }
    void changeArraySizeByComboBox(){//追加するときはここを編集してね
        String item = comboBox.getItem();

        if(item.equals("rect")){
            setTextField(4, argTypeList.get("rect"));
        }else if(item.equals("ellipse")){
            setTextField(4,argTypeList.get("ellipse"));
        }else if(item.equals("fill")){
            setTextField(3, argTypeList.get("fill"));
        }else if(item.equals("background")){
            setTextField(3, argTypeList.get("background"));
        }else if(item.equals("println")){
            setTextField(1, argTypeList.get("println"));
        }else if(item.equals("text")){
            setTextField(3, argTypeList.get("text"));
        }else if(item.equals("textSize")){
            setTextField(1, argTypeList.get("textSize"));
        }else if(item.equals("line")){
            setTextField(4, argTypeList.get("line"));
        }else if(item.equals("noStroke")){
            setTextField(0, new Integer[0]);    //new Integer[0]は適当に埋めただけ。なぜなら第1引数が0で引数がないから
        }else if(item.equals("stroke")){
            setTextField(3, argTypeList.get("stroke"));
        }
        else {
            new Exception();
        }
    }
    void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent() {
        textFont(font);
        stroke(0, alpha);
        strokeWeight(2);
        fill(244, 221, 81, alpha);
        rect(x, y, pWidth, pHeight, 10);
        comboBox.draw();
        for(int i = 0; i < textFields.size(); i++){
            textFields.get(i).draw();
        }
        strokeWeight(2);
    }
    boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY ; //命令文を移動
        comboBox.x += addX;  //操作内のコンボボックスを移動
        comboBox.y += addY;
        setTextFieldPosition();
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    String getComboBoxText() {
        return comboBox.getItem();
    }
    String getScript() {
        StringBuilder result = new StringBuilder();
        result.append(getIndent() + comboBox.getItem() + "(");
        for(int i = 0; i < textFields.size(); i++){
            MyTextField txf = textFields.get(i);
            result.append(txf.getText());
            if(i != textFields.size()-1){
                result.append(", ");
            }
        }
        result.append(");\n");
        return result.toString();
    }
    private void checkGUIChange(){
        if(comboBox.checkChanged()){
            pWidth -= comboBoxWidth;
            comboBoxWidth = comboBox.getWidth();
            pWidth += comboBoxWidth;
            changeArraySizeByComboBox();
            isChange = true;
        }
        boolean txfChange = false;
        for(MyTextField txf : textFields){
            if(txf.checkChanged()){
                txfChange = true;
                break;
            }
        }
        if(txfChange){
            isChange = true;
            setTextFieldPosition();
        }
    }
}

class AssignmentPlate extends Plate {

    int type;
    MyTextField variableNameTxf;
    MyTextField valueTxf;
    private int txfInterval = 10;
    private int equalWidth = 24;
    ArrayList<VariablePlate> variablePlates = new ArrayList<VariablePlate>();

    AssignmentPlate(int type, int x, int y){
        this.x = x;
        this.y = y;
        this.pWidth = originalStatementWidth;
        this.pHeight = 30;
        this.type = type;
        textSize(24);
        equalWidth = int(textWidth("="));
        fillColor = clouds;
    }
    AssignmentPlate(int type, int x, int y, String varName, String value){
        this(type, x, y);
        int totalTxfWidth = txfInterval;
        variableNameTxf = new MyTextField(x + totalTxfWidth, y + txfPosY, varName);
        totalTxfWidth += variableNameTxf.getWidth() + txfInterval + equalWidth + txfInterval;
        valueTxf = new MyTextField(x + totalTxfWidth, y + txfPosY, value);
        totalTxfWidth += valueTxf.getWidth();
        pWidth += totalTxfWidth;
        fillColor = getColorByToken(type);
        valueTxf.setFillColor(colorDict.get(type));
        valueTxf.setKind(type);
    }
    void execute(){
        String name     = variableNameTxf.getText();
        String content  = valueTxf.getText();
        Variable v      = variableTable.searchName(name);
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            if(v == null){
                if(type == Enum.INT){
                    int value = getValue(content, valueTxf);
                    content = "" + value;
                    variableTable.addVariable(new Variable(type, name, value, content));
                }else if(type == Enum.STRING){
                    String value = getStringValue(content);
                    content = value;
                    variableTable.addVariable(new Variable(type, name, value, content));
                }else{
                    new Exception("erro occurs in AssingPlate execute():型情報がありません => " + type);
                }
            }else{
                if(v.kind != type) new Exception(); //すでに定義されているエラーを出さないといけない。修正しろよ。
                if(type == Enum.INT){
                    int value = getValue(content, valueTxf);
                    content = "" + value;
                    variableTable.updateVariable(name, content);
                }else if(type == Enum.STRING){
                    String value = getStringValue(content);
                    content = value;
                    variableTable.updateVariable(name, content);
                }
            }
            step++;
        }
    }
    void draw(){
        if(executingPlate == this){
            setBorder();
        }else{
            noStroke();
        }
        if(variableNameTxf.checkChanged() || valueTxf.checkChanged()){
            isChange = true;
            setTextFieldPosition();
        }
        textFont(font);
        fill(fillColor);
        rect(x, y, pWidth, pHeight, 10);
        drawContents();
    }
    void drawContents(){
        textAlign(LEFT,CENTER);
        textSize(24);
        fill(0);
        text("=", x + variableNameTxf.getWidth() + txfInterval * 2, y+pHeight/2);
        variableNameTxf.draw();
        valueTxf.draw();
    }
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent(){
    }
    private void setTextFieldPosition(){
        pWidth = originalStatementWidth;
        int totalTxfWidth = txfInterval;
        variableNameTxf.moveTo(x + totalTxfWidth, y + txfPosY);
        totalTxfWidth += variableNameTxf.getWidth() + txfInterval + equalWidth + txfInterval;
        valueTxf.moveTo(x + totalTxfWidth, y + txfPosY);
        totalTxfWidth += valueTxf.getWidth();
        pWidth += totalTxfWidth;
    }
    private int getSumTextFieldWidth(){
        return valueTxf.getWidth() + txfInterval*2 + equalWidth + variableNameTxf.getWidth();
    }
    void moveTo(int addX, int addY){
        x += addX;
        y += addY ; //命令文を移動
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
        variableNameTxf.shiftPos(addX, addY);
        valueTxf.shiftPos(addX, addY);
    }
    boolean isMouseOver(){
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    String getScript(){
        StringBuilder result = new StringBuilder(getIndent());
        if(type == Enum.INT) {
            result.append("int ");
        }else if(type == Enum.STRING){
            result.append("String ");
        }
        result.append(variableNameTxf.getText() + " = " + valueTxf.getText() + ";\n");
        return result.toString();
    }
    String getNoIndentScript(){
        StringBuilder result = new StringBuilder();
        if(type == Enum.INT) {
            result.append("int ");
        }else if(type == Enum.STRING){
            result.append("String ");
        }
        result.append(variableNameTxf.getText() + " = " + valueTxf.getText() + ";");
        return result.toString();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //ダブルクリックの判定を行う
            plateList.add(new VariablePlate(x + pWidth +  MARGIN, y + pHeight + MARGIN, variableNameTxf.getText(), type));
        }
    }
}
//ForPlateにとってかわられたクラス
class Loop extends WallPlate {
    MyTextField txf;
    Loop(int x, int y) {
        this.x = x;
        this.y = y;
        this.txf = new MyTextField(x+loopTxfPosX, y + loopTxfPosY, 30, 20);
        pWidth = 180;
        pHeight = 60+40;
        isWallPlate = true;
        fillColor = color(179, 204, 87);
    }
    Loop(int count, int x, int y){
        this(x,y);
        this.txf = new MyTextField(""+count, x+loopTxfPosX, y + loopTxfPosY, 30, 20);
        isWallPlate = true;
    }
    void execute(){
        int kurikaesi = getValue(txf.getText(), txf);
        for(int i = 0; i < kurikaesi; i++){
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
        }
    }
    void draw() {
        updateWidth();
        noStroke();
        if(executingPlate == this){
            setBorder();
        }
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        if(executingPlate == this){
            noStroke();
            rect(x+2, y+2, pWidth-4, wallPlateHeight-2, 10);
            rect(x+2, y+2, wallPlateWidth-2, pHeight-4, 10);
            rect(x+2, y+pHeight-wallPlateHeight+2, pWidth-4, wallPlateHeight-2, 10);
        }
        stroke(0);
        fill(0);
        textSize(18);
        // textFont(font);
        textAlign(LEFT,TOP);
        text("for", x+10, y+5);
        txf.draw();
        if(txf.checkChanged()){
            isChange = true;
        }
    }
    void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateHeight, 10);
        rect(x+8, y+8, wallPlateWidth, pHeight, 10);
        rect(x+8, y+8+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        draw();
        for (Plate plate : loopOpes) {
            plate.drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent() {
        noStroke();
        fill(179, 204, 87, alpha);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        stroke(0, alpha);
        fill(0, alpha);
        textSize(15);
        textFont(font);
        text("for", x+20, y+10);
        txf.draw();
    }
    void setPlateInDebugmode(){
        iter = getValue(txf.getText(),txf);
        for(int i = 0; i < iter; i++){
            for(int j = 0; j < loopOpes.size(); j++){
                allPlateForDebugmode.add(loopOpes.get(j));
            }
        }
    }
    int index = 0;
    private int count=0;
    private int iter = 0;
    ArrayList<Statement> doingOpes;
    void setIter(int iter){
        this.iter = iter;
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY;
        txf.moveTo(x + 50, y + 5);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    String getScript() {
        StringBuilder result = new StringBuilder(getIndent());
        result.append("for(" + txf.getText() + "){\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append(getIndent() + "}\n");
        return result.toString();
    }
    void setLoopCount(String x){
        this.txf.setText(x);
    }
}

class ForPlate extends WallPlate{
    private Plate firstPlate;
    private Plate lastPlate;
    private ConditionPlate cond;
    ForPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+wallPlateHeight+MARGIN + 40;
        isWallPlate = true;
        wallPlateHeight = 30 * 2 + MARGIN;
        wallPlateHeightBottom = 30;
        fillColor = color(179, 204, 87);
        //命令の指定がないので文を適当に挿入する
        firstPlate  = new AssignmentPlate(Enum.INT, x + wallPlateWidth + MARGIN, y + MARGIN/2, "i", "0");
        lastPlate   = new AssignmentPlate(Enum.INT, x + wallPlateWidth + MARGIN, y + pHeight - wallPlateHeightBottom, "i", "i+1");
        cond        = new ConditionPlate(x + wallPlateWidth + MARGIN, y + 30 + MARGIN, "<", "i", "10");
    }
    ForPlate(int x, int y, Plate firstPlate, Plate lastPlate, ConditionPlate cond){
        this(x, y);
        this.firstPlate = firstPlate;
        this.lastPlate  = lastPlate;
        this.cond       = cond;
        this.firstPlate.moveTo(wallPlateWidth + MARGIN, MARGIN/2);
        this.lastPlate.moveTo(wallPlateWidth + MARGIN, pHeight - wallPlateHeightBottom);
        this.cond.moveTo(x + wallPlateWidth + MARGIN, y + 30 + MARGIN);
    }
    void draw(){
        updateWidth();
        noStroke();
        if(executingPlate == this) setBorder();
        fill(fillColor);

        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateWidth, pWidth, wallPlateWidth, 10);
        fill(0);
        textAlign(LEFT,TOP);
        textFont(font);
        text("for", x + MARGIN, y + MARGIN);
        firstPlate.draw();
        lastPlate.draw();
        cond.draw();
    }
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateWidth*2+MARGIN, 10);
        rect(x+8, y+8, wallPlateWidth, pHeight, 10);
        rect(x+8, y+8+pHeight-wallPlateWidth, pWidth, wallPlateWidth, 10);
        draw();
        for (Plate plate : loopOpes) {
            plate.drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent(){

    }
    void resize(int addX, int addY){
        super.resize(addX, addY);
        lastPlate.moveTo(0, addY);
    }
    void moveTo(int addX, int addY){
		x += addX;
        y += addY ; //命令文を移動
        firstPlate.moveTo(addX, addY);
        lastPlate.moveTo(addX, addY);
        cond.moveTo(addX, addY);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    void execute(){
        firstPlate.execute();
        while(cond.getCondition()){
            if(hasExecuteEnd)   return;
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    println("aaa");
                    if(hasExecuteEnd)   return;
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
            lastPlate.execute();
        }
    }
    String getScript(){
		StringBuilder result = new StringBuilder(getIndent() + "for(");
        result.append(firstPlate.getNoIndentScript() +  cond.getScript() + ";" + lastPlate.getNoIndentScript() +"){\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append(getIndent() + "}\n");
		return result.toString();
    }
}

String deleteLastChar(String word){
    return word.substring(0,word.length()-1);
}

MethodPlate getMethodPlateByName(String name){
    for(MethodPlate mp : methodPlateList){
        if(mp.getName().equals(name)) return mp;
    }
    return null;
}
//関数呼び出しの方
class Method extends Plate {
    MethodPlate methodPlate;
    String name;
    ArrayList<MyTextField> args = new ArrayList<MyTextField>();
    private final int MARGIN = 10;
    Balloon balloon;
    Method(int x, int y, MethodPlate methodPlate) {
        this.x = x;
        this.y = y;
        pWidth = 150;
        pHeight = 30;
        this.methodPlate = methodPlate;
        this.name = methodPlate.methodNameTxf.getText();
        setTextFieldPosition();
    }
    Method(int x, int y, String name){
        this.x = x;
        this.y = y;
        pWidth = 150;
        pHeight = 30;
        this.name = name;
    }
    Method(int x, int y, String name, ArrayList<String> actParms){
        this(x,y,name);
        for(int i = 0; i < actParms.size(); i++){
            addVar();
            args.get(i).setText(actParms.get(i));
        }
    }
    Method(int x, int y, String name, String[] actParms){
        this(x,y,name);
        for(int i = 0; i < actParms.length; i++){
            addVar();
            args.get(i).setText(actParms[i]);
        }
    }
    void execute(){
        methodPlate.execute();
    }
    void draw() {
        if(methodPlate == null){
            methodPlate = getMethodPlateByName(name);
            if(methodPlate == null) balloon = new Balloon("Method:" + name + "() is undefined.", x + pWidth + MARGIN, y + pHeight + MARGIN, x + pWidth/2, y + pHeight/2);
            else balloon = null;
        }
        noStroke();
        if(methodPlate != null){
            fill(methodPlate.getFillColor());
        }
        rect(x, y, pWidth, pHeight, 10);
        fill(0);
        textAlign(LEFT, CENTER);
        textFont(font);
        text(name, x+MARGIN, y+pHeight/2);
        for (MyTextField arg : args) {
            arg.draw();
        }
        checkVar();
        if(isMouseOver()){
            linkPlate();
        }
        if(balloon != null) balloon.draw();
    }
    void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
    }
    void drawTransparent() {
        noStroke();
        fill(78,205,196, alpha);
        rect(x, y, pWidth, pHeight, 10);
        fill(0, alpha);
        textAlign(CENTER, CENTER);
        textFont(font);
        text(methodPlate.methodNameTxf.getText(), x+70, y+pHeight/2);
        for (MyTextField arg : args) {
            arg.draw();
        }
    }
    void checkVar() {
        if(methodPlate != null){
            name = methodPlate.methodNameTxf.getText();
            if (methodPlate.args.size() > args.size()) {
                addVar();
            }
            if (methodPlate.args.size() < args.size()) {
                removeVar();
            }
            setTextFieldPosition();
        }
    }
    void addVar() {
        int argSize = args.size();
        args.add(new MyTextField(x+argSize*(argWidth+10)+135 + 10, y + 5));
        pWidth += argWidth +10;
    }
    void removeVar() {
        int argSize = args.size();
        if (argSize > 0) {
            args.remove(argSize-1);
            pWidth -= argWidth +10;
        }
    }
    private void setTextFieldPosition(){
        int textwidth = int(textWidth(name));
        pWidth = MARGIN + textwidth + MARGIN;
        int tmpx = x + pWidth;
        for(int i = 0; i < args.size(); i++){
            MyTextField arg = args.get(i);
            arg.moveTo(tmpx , y + txfPosY);
            tmpx += arg.getWidth() + MARGIN;
            pWidth += arg.getWidth() + MARGIN;
        }

    }
    String[] getArgNames() {
        String[] names = new String[args.size()];
        for (int i = 0; i < names.length; i++) {
            String txt = args.get(i).getText().trim();
            names[i] = txt;
        }
        return names;
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY;
        setTextFieldPosition();
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    boolean isPlateBelow(Plate plate) {
        if (abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
            return true;
        }
        return false;
    }
    String getScript() {
        StringBuilder result = new StringBuilder(getIndent() + name + "(");
        for(int i = 0; i < args.size(); i++){
            if(i != 0){
                result.append(", ");
            }
            result.append(args.get(i).getText());
        }
        result.append(");\n");
        return result.toString();
    }
    void linkPlate() {
        stroke(0);
        line(x+pWidth/2, y+pHeight/2, methodPlate.x+methodPlate.pWidth/2, methodPlate.y+methodPlate.pHeight/2);
    }
}
class SetupPlate extends WallPlate {
    SetupPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+40;
        isWallPlate = true;
        fillColor = alizarin;
    }
    void execute(){
        if(isDebugMode && counter == -1) executingPlate = this;
        else{
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    if(hasExecuteEnd) return;
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
        }
    }
    void draw(){
        updateWidth();
        noStroke();
        if(executingPlate == this){
            setBorder();
        }
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        if(executingPlate == this){
            noStroke();
            rect(x+2, y+2, pWidth-4, wallPlateHeight-2, 10);
            rect(x+2, y+2, wallPlateWidth-2, pHeight-4, 10);
            rect(x+2, y+pHeight-wallPlateHeight+2, pWidth-4, wallPlateHeight-2, 10);
        }
        stroke(0);
        fill(0);
        textSize(18);
        textFont(font);
        textAlign(LEFT,TOP);
        text("setup", x+10, y+5);
    }
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateHeight, 10);
        rect(x+8, y+8, wallPlateWidth, pHeight, 10);
        rect(x+8, y+8+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        draw();
        for (Plate plate : loopOpes) {
            plate.drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent(){
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY ; //命令文を移動
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    String getScript() {
        StringBuilder result = new StringBuilder("void setup() {\n");
        incrementIndent();
        if(loopOpes.size() > 0){
            Plate plate = this.loopOpes.get(0);
            while(plate != null){
                result.append(plate.getScript());
                plate = plate.nextPlate;
            }
        }else{
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append("}\n");
        return result.toString();
    }
}

final int argWidth = 50;
final int argHeight = 30;
//関数定義の方
class MethodPlate extends WallPlate {
    MyComboBox typeBox;
    MyTextField methodNameTxf;
    MyButton addVarButton;
    MyButton removeVarButton;
    ArrayList<MyTextField> args = new ArrayList<MyTextField>();
    private int txfInterval     = 10;
    private int comboBoxWidth   = 0;
    private int MARGIN          = 10;
    private int type;

    MethodPlate(int x, int y) {
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+40;
        fillColor = color(78,205,196);

        String[] typeItems = {
            "void", "int", "String", "boolean"
        };
        typeBox = new MyComboBox(typeItems, x + MARGIN, y + 5, 70, 20);
        typeBox.setItem("void");
        methodNameTxf = new MyTextField(x, y+5, 120, 20);
        addVarButton = new MyButton("+", x +135, y+20, 10, 10);
        removeVarButton = new MyButton("-", x + 135, y+ 5, 10, 10);
        setTextFieldPosition();
        isWallPlate = true;
        methodPlateList.add(this);
    }
    MethodPlate(String name, int x, int y) {
        this(x, y);
        methodNameTxf.setText(name);
        setMethod(name);
        setTextFieldPosition();
        isWallPlate = true;
    }
    MethodPlate(String name, int x, int y, String[]argNames, int[] argTypes) {
        this(name, x, y);
        for (int i = 0; i < argNames.length; i++) {
            addVar();    //引数追加
            args.get(i).setText(argNames[i]);    //引数の中身を代入
            args.get(i).setFillColor(colorDict.get(argTypes[i]));   //引数に応じて色を変更
            args.get(i).setKind(argTypes[i]);
        }
        setTextFieldPosition();
        isWallPlate = true;
    }
    void execute(){
        if(!loopOpes.isEmpty()){
            Plate p = loopOpes.get(0);
            do{
                if(hasExecuteEnd) return;
                p.execute();
                p = p.nextPlate;
            }while(p != null);
        }
    }
    void setMethod(String name){
        for(Method method : methodList){
            if(method.name.equals(name)){
                method.methodPlate = this;
            }
        }
    }
    void draw() {

        updateWidth();
        noStroke();
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);


        stroke(0);
        fill(0);
        textSize(18);
        checkGUIChange();
        typeBox.draw();
        methodNameTxf.draw();
        for (MyTextField arg : args) {
            arg.draw();
        }
        addVarButton.draw();
        removeVarButton.draw();
        strokeWeight(2);
        if (mousePressed) {
            if (addVarButton.isOver) {
                addVar();
            }
            if (removeVarButton.isOver) {
                removeVar();
            }
        }
    }
    private void setTextFieldPosition(){
        int tmp = MARGIN + typeBox.getWidth() + MARGIN;
        methodNameTxf.moveTo(x + tmp, y + txfPosY);

        tmp += methodNameTxf.getWidth() + MARGIN;
        int argWidth = 0;
        for(int i = 0; i < args.size(); i++){
            MyTextField arg = args.get(i);
            arg.moveTo(x + tmp , y + txfPosY);
            tmp += arg.getWidth() + MARGIN;
        }
        removeVarButton.moveTo(x + tmp, y + 5);
        addVarButton.moveTo(x + tmp, y + 20);
        tmp += removeVarButton.getWidth() + MARGIN;
        if(tmp < originalLoopWidth) tmp = originalLoopWidth;
        pWidth = tmp;
    }
    private void checkGUIChange(){
        boolean txfChange = false;
        for(MyTextField txf : args){
            if(txf.checkChanged()){
                txfChange = true;
                break;
            }
        }
        if(typeBox.checkChanged()){
            int type = 0;
            String item = typeBox.getItem();
            if(item.equals("void")){
                type = Enum.VOID;
            }else if(item.equals("int")){
                type = Enum.INT;
            }else if(item.equals("String")){
                type = Enum.STRING;
            }else if(item.equals("boolean")){
                type = Enum.BOOLEAN;
            }
            fillColor = getColorByToken(type);
            txfChange = true;
        }
        if(methodNameTxf.checkChanged()){
            txfChange = true;
        }
        if(txfChange){
            isChange = true;
            setTextFieldPosition();
        }
    }
    int getAllWidth(){
        int result = MARGIN + typeBox.getWidth() + MARGIN + methodNameTxf.getWidth() + MARGIN;
        for(int i = 0; i < args.size(); i++){
            result += args.get(i).getWidth() + MARGIN;
        }
        return result;
    }
    void addVar() {
        MyTextField arg = new MyTextField(x + this.getAllWidth(), y + 5);
        args.add(arg);
        removeVarButton.moveTo(x + this.getAllWidth(), y+5);
        addVarButton.moveTo(x + this.getAllWidth(), y+20);
        pWidth += arg.getWidth() + txfInterval;
    }
    void removeVar() {
        int argSize = args.size();
        if (argSize > 0) {
            MyTextField arg = args.get(argSize-1);
            args.remove(arg);

            removeVarButton.moveTo(x + this.getAllWidth(), y+5);
            addVarButton.moveTo(x + this.getAllWidth(), y+20);
            pWidth -= arg.getWidth() + txfInterval;
            if(pWidth < originalLoopWidth) pWidth = originalLoopWidth;
        }
    }
    void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateHeight, 10);
        rect(x+8, y+8, wallPlateWidth, pHeight, 10);
        rect(x+8, y+8+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        draw();
        for (Plate plate : loopOpes) {
            plate.drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent() {
        stroke(0);
        strokeWeight(2);
        fill(78,205,196, 80);
        rect(x, y, pWidth, pHeight, 10);
        methodNameTxf.draw();
        for (MyTextField arg : args) {
            arg.draw();
        }
        addVarButton.draw();
        removeVarButton.draw();
        strokeWeight(2);
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY;
        typeBox.moveTo(x + MARGIN, y + 5);
        setTextFieldPosition();
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    boolean isPlateBelow(Plate plate) {
        if (abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
            return true;
        }
        return false;
    }
    String getScript() {
        StringBuilder result = new StringBuilder();
        result.append(typeBox.getItem() + " " + methodNameTxf.getText() + "(");
        for(int i = 0; i < args.size(); i++){
            MyTextField argTxf = args.get(i);
            if(i != 0){
                result.append(", ");
            }
            int kind = argTxf.getKind();
            String name = argTxf.getText();
            if(kind == Enum.INT){
                result.append("int ");
            }else if(kind == Enum.STRING){
                result.append("String ");
            }
            result.append(name);
        }
        result.append(") {\n");

        incrementIndent();
        if(loopOpes.size() > 0){    //命令があればその命令を追加
            Plate plate = this.loopOpes.get(0);
            while(plate != null){
                result.append(plate.getScript());
                plate = plate.nextPlate;
            }
        } else {    //命令がなければ空行を1行だけソースコードブロック内に挿入する
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append("}\n");
        return result.toString();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //ダブルクリックの判定を行う
            plateList.add(new Method(x+50, y+50, this));
        }
    }
    String getName(){
        return methodNameTxf.getText();
    }
}
class ConditionPlate extends Plate {
    MyComboBox comboBox;  //＜、＝、＞の選択欄
    MyTextField txf1;       //値の記入欄
    MyTextField txf2;
    IfCondPlate plate;
    int state = -1; //-1の状況は条件式の判定前
    private boolean isBoolean       = false;
    private final int START_MARGIN  = 5;
    private final int MARGIN        = 10;
    private final int COMBOBOX_Y    = 3;
    private final int TXF_POS_Y     = 3;
    ConditionPlate(int x, int y) {
        this.x = x;
        this.y = y;
        pWidth = 150;
        pHeight = 25;
        String[] items = {
            "==", ">=", ">", "<=", "<", "boolean"
        };
        comboBox = new MyComboBox(items, x + 65, y +COMBOBOX_Y, 25, 20, 18);
        txf1 = new MyTextField(0, 0, 30, txfHeight);
        txf2 = new MyTextField(0, 0, 30, txfHeight);
        setTextFieldPosition();
    }
    ConditionPlate(int x, int y, String enzanshi, String lh, String rh){
        this(x,y);
        comboBox.setItem(enzanshi);
        txf1.setText(lh);
        txf2.setText(rh);
    }
    String leftVal;
    void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            int left  = getValue(txf1.getText(), txf1);
            int right = getValue(txf2.getText(), txf2);
            String relOpe = comboBox.getItem();
            if(relOpe.equals("<")){
                state = (left < right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals("<=")){
                state = (left <= right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals(">")){
                state = (left > right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals(">=")){
                state = (left >= right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals("==")){
                state = (left == right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals("!=")){
                state = (left <= right) ? Enum.TRUE : Enum.FALSE;
            }else if(relOpe.equals("boolean")){
                state = Enum.FALSE;    //TODO:うめる
            }
            step++;
        }
    }
    void draw() {
        noStroke();
        if(executingPlate == this) setBorder();
        if(state == -1){
            fill(#E0E4CC);
        }else if(state == Enum.FALSE){   //false
            fill(alizarin);
        }else if(state == Enum.TRUE){
            fill(peterRiver);
        }
        rect(x, y, pWidth, pHeight, 10);
        comboBox.draw();
        txf1.draw();
        if (txf2 != null) {
            txf2.draw();
        }
        checkGUIChange();
        state = -1;
    }
    void drawShadow() {
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
    }
    void drawTransparent() {
        strokeWeight(2);
        fill(200, 200, 200, alpha);
        rect(x, y, pWidth, pHeight, 10);
        textAlign(CENTER, CENTER);
        comboBox.draw();
        txf1.draw();
        if (txf2 != null) {
            txf2.draw();
        }
    }
    private void setTextFieldPosition(){
        pWidth = START_MARGIN;
        int tmpx = x + pWidth;
        txf1.moveTo(tmpx, y + TXF_POS_Y);
        tmpx += txf1.getWidth() + MARGIN;
        pWidth += txf1.getWidth() + MARGIN;
        comboBox.moveTo(tmpx, y + COMBOBOX_Y);
        tmpx += comboBox.getWidth() + MARGIN;
        pWidth += comboBox.getWidth() + MARGIN;
        if(txf2 != null){
            txf2.moveTo(tmpx, y + TXF_POS_Y);
            pWidth += txf2.getWidth() + START_MARGIN;
        }
    }
    private void checkGUIChange(){
        boolean txfChange = false;
        if(txf1.checkChanged()) txfChange = true;
        if(txf2 != null && txf2.checkChanged()) txfChange = true;
        if(comboBox.checkChanged()){
            txfChange = true;
            if(comboBox.getItem().equals("boolean")){
                setBooleanTrue();
            }else if(isBoolean){
                txf2 = new MyTextField(0, 0, 30, txfHeight);
                txf1.setFillColor(#ecf0f1);
                isBoolean = false;
            }
        }
        if(txfChange){
            isChange = true;
            setTextFieldPosition();
        }
    }
    void setBooleanTrue(){
        txf2 = null;
        txf1.setFillColor(colorDict.get(Enum.BOOLEAN));
        isBoolean = true;
    }
    void moveTo(int addX, int addY) {
        x += addX;  //操作自体移動
        y += addY ;
        setTextFieldPosition();
        if (plate != null) {
            plate.moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    String getScript() {
        if(isBoolean){
            return txf1.getText();
        }else{
            return txf1.getText() + " " + comboBox.getItem() + " " + txf2.getText();
        }
    }
    public boolean getCondition(){
        execute();
        return state == Enum.TRUE ? true : false;
    }
}
class IfCondPlate extends WallPlate {
    private ConditionPlate cond;
    private final int MARGIN = 5;
    IfCondPlate(int x, int y) {
        this.x = x;
        this.y = y;
        cond = new ConditionPlate(x+ 35, y + 2);
        pWidth = originalLoopWidth;
        pHeight = 60 + 40;
        isWallPlate = true;
        fillColor = carrot;
    }
    void execute(){
        if(cond.getCondition() && !loopOpes.isEmpty()){
            if(hasExecuteEnd) return;
            Plate p = loopOpes.get(0);
            do{
                p.execute();
                p = p.nextPlate;
            }while(p != null);
        }
    }
    void draw() {
        noStroke();
        fill(fillColor);
        rect(x, y, pWidth, 30, 10);
        rect(x, y, 30, pHeight, 10);
        rect(x, y+pHeight-30, pWidth, 30, 10);
        stroke(0);
        fill(0);
        textSize(18);
        textAlign(LEFT,TOP);
        text("if", x+10, y+5);
        cond.draw();
        cond.state = -1;
        updateByCondPlateWidth();
        updateWidth();
    }
    private void setConditionPlate(ConditionPlate cp){
        cond = cp;
        cond.moveTo(x+ 35, y + 2);
    }
    private void updateByCondPlateWidth(){
        int tmpWidth = (cond.x + cond.pWidth + MARGIN) -(this.x);
        if(tmpWidth > originalLoopWidth) this.pWidth = tmpWidth;
    }
    void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, 30, 10);
        rect(x+8, y+8, 30, pHeight, 10);
        rect(x+8, y+8+pHeight-30, pWidth, 30, 10);
        draw();
        if (loopOpes.size() > 0) {
            loopOpes.get(0).drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent() {
        noStroke();
        fill(255, 130, 58, alpha);
        rect(x, y, pWidth, 30, 10);
        rect(x, y, 30, pHeight, 10);
        rect(x, y+pHeight-30, pWidth, 30, 10);
        stroke(0, alpha);
        fill(0, alpha);
        textSize(15);
        text("if", x+10, y+5);
        cond.drawTransparent();
    }
    void moveTo(int addX, int addY){
        x += addX;
        y += addY;
        cond.moveTo(addX, addY);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    String getScript() {
        StringBuilder result = new StringBuilder();
        result.append(getIndent() + "if" + " ( " + cond.getScript() + " ) {\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){   //何も命令がなければ空行を追加
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append(getIndent() + "}\n");
        return result.toString();
    }
    void setPlateInDebugmode(){
    }
}

class ReturnPlate extends Plate {
    MyTextField txf;
    private final int MARGIN = 10;
    private int CONST_RESULT_TEXT_WIDTH;
    ReturnPlate(int x, int y){
        this.x = x;
        this.y = y;
        textFont(font);
        CONST_RESULT_TEXT_WIDTH = int(textWidth("return"));
        this.pWidth += MARGIN + CONST_RESULT_TEXT_WIDTH + MARGIN;
        txf = new MyTextField(0, 0, txfWidth, txfHeight);
        txf.moveTo(x + pWidth, y + txfPosY);
        this.pWidth += txf.getWidth() + MARGIN;
        this.pHeight = 30;
        fillColor = peterRiver;
    }
    void draw(){
        noStroke();
        textFont(font);
        fill(fillColor);
        rect(x, y, pWidth, pHeight, 10);
        fill(0);
        textAlign(LEFT,CENTER);
        text("return", x + MARGIN, y+pHeight/2);
        txf.draw();
        checkGUIChange();
    }
    void drawShadow(){
    }
    void drawTransparent(){
    }
    void moveTo(int addX, int addY){
        x += addX;
        y += addY ; //命令文を移動
        txf.x += addX;
        txf.y += addY;
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    void execute(){
    }
    String getScript(){
        StringBuilder result = new StringBuilder();
        result.append(getIndent());
        result.append("return ");
        result.append(txf.getText());
        result.append(";\n");
		return result.toString();
    }
    private void checkGUIChange(){
        if(txf.checkChanged()){
            isChange = true;
            setTextFieldPosition();
        }
    }
    private void setTextFieldPosition(){
        this.pWidth = MARGIN + CONST_RESULT_TEXT_WIDTH + MARGIN;
        txf.moveTo(x + pWidth, y + txfPosY);
        this.pWidth += txf.getWidth() + MARGIN;
        this.pHeight = 30;
    }
}

final int MARGIN = 10;
public abstract class ArrayPlate extends Plate{
    protected MyComboBox typeBox;
    protected MyTextField nameTxf;
    protected ArrayList<ArrayAssignPlate> arrayAssignPlates = new ArrayList<ArrayAssignPlate>();

    public abstract void drawGUI();
    public abstract void setGUIPosition();
    public abstract void checkGUIChange();
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void draw(){
		if(executingPlate == this){
            setBorder();
        }  else {
            noStroke();
        }
		fill(fillColor);
		rect(x,y,pWidth,pHeight,10);
        checkGUIChange();
        drawGUI();
    }
    void moveTo(int addX, int addY){
        x += addX;
        y += addY ; //命令文を移動
        setGUIPosition();
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    public int getType(){
        String item = typeBox.getItem();
        if(item.equals("int")){
            return Enum.INT_ARRAY;
        }else if(item.equals("String")){
            return Enum.STRING_ARRAY;
        }else if(item.equals("boolean")){
            return Enum.BOOLEAN_ARRAY;
        }else{
            new Exception("Error occurs in getType(String item); : 未定義の型が指定されています");
            return Enum.OTHER;
        }
    }
    public String getArrayName(){
        return nameTxf.getText();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //ダブルクリックの判定を行う
            ArrayAssignPlate ap = new ArrayAssignPlate(this, x+50, y+50);
            plateList.add(ap);
            arrayAssignPlates.add(ap);
        }
    }
}
class ArrayPlate_Original extends  ArrayPlate {
    private MyTextField lengthTxf;
	ArrayPlate_Original(int x, int y){
		this.x    = x;
		this.y    = y;
		pHeight   = 30;
		fillColor = alizarin;
        String[] typeItems = {"int", "String", "boolean"};
        typeBox     = new MyComboBox(typeItems, x + MARGIN, y + 5, 70, 20);
        nameTxf     = new MyTextField(0, 0, txfWidth, txfHeight);
        lengthTxf   = new MyTextField(0, 0, txfWidth, txfHeight);
        setGUIPosition();
	}
    ArrayPlate_Original(int x, int y, String name, String length, int type){
        this(x,y);
        nameTxf.setText(name);
        lengthTxf.setText(length);
        if(type == Enum.INT_ARRAY){
            typeBox.setItem("int");
        }else if(type == Enum.STRING_ARRAY){
            typeBox.setItem("String");
        }else if(type == Enum.BOOLEAN_ARRAY){
            typeBox.setItem("boolean");
        }else{
            println("Error : 該当する型がありません -> " + type);
            new Exception();
        }
        setGUIPosition();
    }
    void drawTransparent(){
    }
    void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name     = nameTxf.getText();
            String content  = lengthTxf.getText();
            Variable v      = variableTable.searchName(name);
            int type        = getType();
            int length      = getValue(content, lengthTxf);
            if(v == null){
                if(type == Enum.INT_ARRAY){
                    variableTable.addVariable(new CompositeVariable(type, name, length, 0));
                }else if(type == Enum.STRING_ARRAY){
                    variableTable.addVariable(new CompositeVariable(type, name, length, ""));
                }else if(type == Enum.BOOLEAN_ARRAY){
                    variableTable.addVariable(new CompositeVariable(type, name, length, true));
                }else{
                    new Exception();
                }
            }else{
                if(v.kind != type) new Exception(); //すでに定義されているエラーを出さないといけない。修正しろよ。
                ((CompositeVariable)variableTable.searchName(name)).initArray(length);
            }
            step++;
        }

    }
    void drawGUI(){
        final int MARGIN_Y = pHeight/2;
        int tmp = MARGIN;
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        text("type:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("type:")) + MARGIN;
        tmp += typeBox.getWidth() + MARGIN;
        text("name:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("name:")) + MARGIN;
        tmp += nameTxf.getWidth() + MARGIN;
        text("length:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("length:")) + MARGIN;
        typeBox.draw();
        nameTxf.draw();
        lengthTxf.draw();
    }
    void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        tmp += int(textWidth("type:")) + MARGIN;
        typeBox.moveTo(x + tmp, y + 5);
        tmp += typeBox.getWidth() + MARGIN;
        tmp += int(textWidth("name:")) + MARGIN;
        nameTxf.moveTo(x + tmp, y + txfPosY);
        tmp += nameTxf.getWidth() + MARGIN;
        tmp += int(textWidth("length:")) + MARGIN;
        lengthTxf.moveTo(x + tmp, y + txfPosY);
        tmp += lengthTxf.getWidth() + MARGIN;
        pWidth = tmp;
    }
    void checkGUIChange(){
        boolean nameChange  = false;
        boolean typeChange  = false;
        boolean txfChange   = false;
        if(nameTxf.checkChanged()){
            txfChange   = true;
            nameChange  = true;
        }
        if(lengthTxf.checkChanged()) txfChange = true;
        if(typeBox.checkChanged()){
            int type = getType();
            String item = typeBox.getItem();
            fillColor   = getColorByToken(type);
            txfChange   = true;
            typeChange  = true;
        }
        if(txfChange){
            isChange = true;
            if(nameChange){
                for(ArrayAssignPlate ap : arrayAssignPlates){
                    ap.setGUIPosition();
                }
            }
            if(typeChange){
                for(ArrayAssignPlate ap : arrayAssignPlates){
                    ap.setColor(fillColor);
                }
            }
            setGUIPosition();
        }
    }
    String getScript(){
		StringBuilder result = new StringBuilder();
        String type = typeBox.getItem();
        String name = nameTxf.getText();
        String length = lengthTxf.getText();
        result.append(getIndent());
        result.append(type);
        result.append("[] ");
        result.append(name);
        result.append(" = new ");
        result.append(type + "[" + length+"];\n");
		return result.toString();
    }
}
class ArrayPlate_SyntaxSugar extends ArrayPlate {
    private ArrayList<MyTextField> elements;
    boolean hasExpanded = true;
	ArrayPlate_SyntaxSugar(int x, int y){
		this.x    = x;
		this.y    = y;
		pHeight   = 30;
		fillColor = alizarin;
        String[] typeItems = {"int", "String", "boolean"};
        typeBox     = new MyComboBox(typeItems, x + MARGIN, y + 5, 70, 20);
        nameTxf     = new MyTextField(0, 0, txfWidth, txfHeight);
        elements    = new ArrayList<MyTextField>();
        setGUIPosition();
	}
    ArrayPlate_SyntaxSugar(int x, int y, int type, String name, String[] contents){
        this(x,y);
        nameTxf.setText(name);
        if(type == Enum.INT_ARRAY_SYNTAX_SUGAR){
            typeBox.setItem("int");
        }else if(type == Enum.STRING_ARRAY_SYNTAX_SUGAR){
            typeBox.setItem("String");
        }else if(type == Enum.BOOLEAN_ARRAY_SYNTAX_SUGAR){
            typeBox.setItem("boolean");
        }else{
            new Exception("Error : 該当する型がありません -> " + type);
        }
        fillColor = getColorByToken(getType());

        for(String content : contents){
            elements.add(new MyTextField(content, 0,0,txfWidth,txfHeight));
        }
        setGUIPosition();
    }
    void drawTransparent(){
    }
    void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name     = nameTxf.getText();
            Variable v      = variableTable.searchName(name);
            int type        = getType();
            if(v == null){
                if(type == Enum.INT_ARRAY){
                    ArrayList<Integer> contents = new ArrayList<Integer>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getValue(elements.get(i).getText(), elements.get(i)));
                    }
                    variableTable.addVariable(new CompositeVariable(type, name, contents));
                }else if(type == Enum.STRING_ARRAY){
                    ArrayList<String> contents = new ArrayList<String>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getStringValue(elements.get(i).getText()));
                    }
                    variableTable.addVariable(new CompositeVariable(type, name, contents));
                }else if(type == Enum.BOOLEAN_ARRAY){
                    new Exception("未実装");
                    // variableTable.addVariable(new CompositeVariable(type, name, length, true));
                }else{
                    new Exception();
                }
            }else{
                if(v.kind != type) new Exception(); //すでに定義されているエラーを出さないといけない。修正しろよ。
                if(type == Enum.INT_ARRAY){
                    ArrayList<Integer> contents = new ArrayList<Integer>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getValue(elements.get(i).getText(), elements.get(i)));
                    }
                    ((CompositeVariable)v).setElements(contents);
                }else if(type == Enum.STRING_ARRAY){
                    ArrayList<String> contents = new ArrayList<String>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getStringValue(elements.get(i).getText()));
                    }
                    ((CompositeVariable)v).setElements(contents);
                }else if(type == Enum.BOOLEAN_ARRAY){
                    new Exception("未実装");
                    // variableTable.addVariable(new CompositeVariable(type, name, length, true));
                }else{
                    new Exception();
                }
            }
            step++;
        }

    }
    void drawGUI(){
        final int MARGIN_Y = pHeight/2;
        int tmp = MARGIN;
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        text("type:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("type:")) + MARGIN/2;
        tmp += typeBox.getWidth() + MARGIN/2;
        text("name:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("name:")) + MARGIN/2;
        tmp += nameTxf.getWidth() + MARGIN/2;
        typeBox.draw();
        nameTxf.draw();
        if(hasExpanded){
            for(int i = 0; i < elements.size(); i++){
                MyTextField element = elements.get(i);
                String text = "["+i+"]";
                text(text, x + tmp, y + MARGIN_Y);
                tmp += textWidth(text) + 3;
                element.draw();
                tmp += element.getWidth() + 3;
            }
        }else{
            String text = "...";
            text(text, x + tmp, y + MARGIN_Y);
        }
    }
    void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        tmp += int(textWidth("type:")) + MARGIN/2;
        typeBox.moveTo(x + tmp, y + 5);
        tmp += typeBox.getWidth() + MARGIN/2;
        tmp += int(textWidth("name:")) + MARGIN/2;
        nameTxf.moveTo(x + tmp, y + txfPosY);
        tmp += nameTxf.getWidth() + MARGIN/2;
        if(hasExpanded){
            for(int i = 0; i < elements.size(); i++){
                MyTextField element = elements.get(i);
                tmp += textWidth("["+i+"]") + 3;
                element.moveTo(x + tmp, y + txfPosY);
                tmp += element.getWidth() + 3;
            }
        }else{
            tmp += int(textWidth("...")) + MARGIN/2;
        }
        pWidth = tmp;
    }
    void checkGUIChange(){
        boolean txfChange   = false;
        boolean nameChange  = false;
        boolean typeChange  = false;
        if(nameTxf.checkChanged()){
            nameChange  = true;
            txfChange   = true;
        }
        for(MyTextField element : elements){
            if(element.checkChanged()) {
                txfChange = true;
                break;
            }
        }
        if(typeBox.checkChanged()){
            int type    = getType();
            fillColor   = getColorByToken(type);
            txfChange   = true;
            typeChange  = true;
        }
        if(txfChange){
            isChange = true;
            if(nameChange){
                for(ArrayAssignPlate ap : arrayAssignPlates){
                    ap.setGUIPosition();
                }
            }
            if(typeChange){
                for(ArrayAssignPlate ap : arrayAssignPlates){
                    ap.setColor(fillColor);
                }
            }
            setGUIPosition();
        }
    }
    String getScript(){
		StringBuilder result = new StringBuilder();
        String type = typeBox.getItem();
        String name = nameTxf.getText();
        result.append(getIndent() + type + "[] " + name + " = {");
        for(int i = 0; i < elements.size(); i++){
            result.append(elements.get(i).getText());
            if(i != elements.size()-1){
                result.append(", ");
            }
        }
        result.append("};\n");
		return result.toString();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //ダブルクリックの判定を行う
            hasExpanded = !hasExpanded;
            setGUIPosition();
        }
    }
}

class ArrayAssignPlate extends Plate {
    private ArrayPlate arrayPlate;
    private MyTextField indexTxf;
    private MyTextField contentTxf;
	ArrayAssignPlate(ArrayPlate ap, int x, int y){
        this.arrayPlate = ap;
		this.x      = x;
		this.y      = y;
		pHeight     = 30;
		fillColor   = alizarin;   //どうせあとで型の種類によって色は変更される
        indexTxf    = new MyTextField(0, 0, txfWidth, txfHeight);
        indexTxf.setFillColor(colorDict.get(Enum.INT));
        indexTxf.setKind(Enum.INT);
        contentTxf  = new MyTextField(0, 0, txfWidth, txfHeight);
        setGUIPosition();
	}
    void draw(){
		if(executingPlate == this){
            setBorder();
        }  else {
            noStroke();
        }
		fill(fillColor);
		rect(x,y,pWidth,pHeight,10);
        checkGUIChange();
        drawGUI();
    }
    void drawShadow(){
		noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
        drawGUI();
    }
    void drawTransparent(){
    }
    void moveTo(int addX, int addY){
		x += addX;
        y += addY ; //命令文を移動
        setGUIPosition();
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name    = arrayPlate.nameTxf.getText();
            int index      = getValue(indexTxf.getText(), indexTxf);
            int type       = arrayPlate.getType();
            String content = contentTxf.getText();
            Variable v     = (CompositeVariable)(variableTable.searchName(name));
            if(v.kind != type) new Exception(); //すでに定義されているエラーを出さないといけない。修正しろよ。
            CompositeVariable array = (CompositeVariable)variableTable.searchName(name);
            if(type == Enum.INT_ARRAY){
                int value = getValue(content, contentTxf);
                array.set(index, value);
            }else if(type == Enum.STRING_ARRAY){
                String value = getStringValue(content);
                array.set(index, value);
            }else{
                new Exception("erro occurs in AssingPlate execute():型情報がありません => " + type);
            }
            step++;
        }

    }
    void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        String name = arrayPlate.getArrayName();
        tmp += int(textWidth(name)) + MARGIN;
        tmp += int(textWidth("index:")) + MARGIN;
        indexTxf.moveTo(x + tmp, y + txfPosY);
        tmp += indexTxf.getWidth() + MARGIN;
        tmp += int(textWidth("=")) + MARGIN;
        contentTxf.moveTo(x + tmp, y + txfPosY);
        tmp += contentTxf.getWidth() + MARGIN;
        pWidth = tmp;
    }
    void drawGUI(){
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        int tmp = MARGIN;
        final int MARGIN_Y = pHeight/2;
        String name = arrayPlate.getArrayName();
        text(name, x + tmp, y + MARGIN_Y);
        tmp += int(textWidth(name)) + MARGIN;
        text("index:", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("index:")) + MARGIN;
        indexTxf.draw();
        tmp += indexTxf.getWidth() + MARGIN;
        text("=", x + tmp, y + MARGIN_Y);
        tmp += int(textWidth("=")) + MARGIN;
        contentTxf.draw();
    }
    void checkGUIChange(){
        boolean txfChange = false;
        if(indexTxf.checkChanged() || contentTxf.checkChanged()){
            txfChange = true;
        }
        if(txfChange){
            isChange = true;
            setGUIPosition();
        }
    }
    protected void setColor(color fc){
        this.fillColor = fc;
    }
    String getScript(){
		StringBuilder result = new StringBuilder();
        result.append(getIndent());
        result.append(arrayPlate.getArrayName() + "[" + indexTxf.getText() + "] = " + contentTxf.getText() + ";\n");
		return result.toString();
    }
}
class VariablePlate extends Plate {
    String word;
    int type;
	VariablePlate(int x, int y, String word, int type){
        this.x = x;
        this.y = y;
        this.word = word;
        this.type = type;
        this.fillColor = getColorByToken(type);
        textFont(font);
        this.pWidth = int(textWidth(word)) + MARGIN * 2;
        pHeight = textSize + MARGIN;
        isVariablePlate = true;
	}
    void draw(){
        noStroke();
        fill(fillColor);
        rect(x,y,pWidth,pHeight,10);
        textAlign(LEFT, TOP);
        textFont(font);
        fill(0);
        text(word,x+MARGIN,y+MARGIN/2);
    }
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent(){

    }
    void moveTo(int addX, int addY){
		x += addX;
        y += addY ; //命令文を移動
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    void execute(){

    }
    String getScript(){
		StringBuilder result = new StringBuilder(word + "\n");
		return result.toString();
    }
}
class DrawPlate extends WallPlate {
    DrawPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+40;
        isWallPlate = true;
        fillColor = alizarin;
    }
    void execute(){
        if(counter == -1) executingPlate = this;
        else{
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    if(hasExecuteEnd) return;
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
        }
    }
    void draw(){
        updateWidth();
        noStroke();
        if(executingPlate == this){
            setBorder();
        }
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        if(executingPlate == this){
            noStroke();
            rect(x+2, y+2, pWidth-4, wallPlateHeight-2, 10);
            rect(x+2, y+2, wallPlateWidth-2, pHeight-4, 10);
            rect(x+2, y+pHeight-wallPlateHeight+2, pWidth-4, wallPlateHeight-2, 10);
        }
        stroke(0);
        fill(0);
        textSize(18);
        textFont(font);
        textAlign(LEFT,TOP);
        text("setup", x+10, y+5);
    }
    void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateHeight, 10);
        rect(x+8, y+8, wallPlateWidth, pHeight, 10);
        rect(x+8, y+8+pHeight-wallPlateHeight, pWidth, wallPlateHeight, 10);
        draw();
        for (Plate plate : loopOpes) {
            plate.drawShadow();
        }
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    void drawTransparent(){
    }
    void moveTo(int addX, int addY) {
        x += addX;
        y += addY ; //命令文を移動
        if (loopOpes.size() > 0) {
            loopOpes.get(0).moveTo(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.moveTo(addX, addY);
        }
    }
    String getScript() {
        StringBuilder result = new StringBuilder("void setup() {\n");
        incrementIndent();
        if(loopOpes.size() > 0){
            Plate plate = this.loopOpes.get(0);
            while(plate != null){
                result.append(plate.getScript());
                plate = plate.nextPlate;
            }
        }else{
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append("}\n");
        return result.toString();
    }
}
