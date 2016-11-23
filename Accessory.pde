class Box{
    private int x, y;
    private int w, h;
    private String name = "x";
    private String content = ""+1;
    private final float rate = 0.5;
    Box(int x, int y){
        this.x = x;
        this.y = y;
        this.w = 50;
        this.h = 50;
    }
    Box(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }
    void display(){
        strokeWeight(2);
        stroke(0);
        fill(#f1c40f);
        float x1 = x;
        float y1 = y;
        float x2 = x + w;
        float y2 = y;
        float x3 = x;
        float y3 = y + h;
        float x4 = x + w;
        float y4 = y + h;
        float x5 = x + w * rate;
        float y5 = y - h * rate;
        float x6 = x + w * rate + w;
        float y6 = y - h * rate;
        float x7 = x + w * rate;
        float y7 = y - h * rate + h;
        float x8 = x + w * rate + w;
        float y8 = y - h * rate + h;
        rect(x5, y5, w, h);
        quad(x1,y1,x3,y3,x7,y7,x5,y5);
        quad(x2,y2,x4,y4,x8,y8,x6,y6);
        rect(x1, y1, w, h);
        drawBand();
    }
    private void drawBand(){
        fill(#bdc3c7);
        noStroke();
        float rateH = 0.6;
        float rateBandH = 0.3;
        float bandX = x;
        float bandY = y + h * rateH;
        textAlign(CENTER,CENTER);
        rect(bandX, bandY, w, h * rateBandH);
        fill(0);
        text(name, bandX + w/2, bandY + h * rateBandH/2);
    }
}

ArrayList<Balloon> balloonList = new ArrayList<Balloon>();
int maxBalloonWidth = 200;
int maxBalloonHeight = 170;
class Balloon {
    private int x, y, targetX, targetY, w, h;
    private String text;
    private color fillColor;
    private color textColor;
    Balloon(String text, int x, int y, int targetX, int targetY){
        this.text = text;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        fillColor = clouds;
        textColor = color(#FF0000);
        textFont(font);
        this.w = int(textWidth(text)) + MARGIN * 2;
        this.h = text.split("\n").length * textSize + MARGIN * 2;
        balloonList.add(this);
    }
    void draw(){
        strokeWeight(1);
        stroke(wetAsphalt);
        fill(fillColor);
        rect(x,y,w,h,10);
        triangle(targetX, targetY, x+w/2-30, y, x+w/2+30, y);
        textFont(font);
        textLeading(textSize);
        textAlign(LEFT,TOP);
        fill(textColor);
        text(text, x + MARGIN, y + MARGIN);
    }
    boolean isMouseOver(){
        return mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h;
    }
    void setPosition(int x, int y, int targetX, int targetY){
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
    }
    void shiftPos(int addX, int addY){
        this.x += addX;
        this.y += addY;
        this.targetX += addX;
        this.targetY += addY;
    }
    void putForward(){
        int thisIndex = balloonList.indexOf(this);
        balloonList.set(thisIndex, balloonList.get(balloonList.size()-1));
        balloonList.set(balloonList.size()-1, this);
    }
}

class VariableTable {
    int x, y;
    private ArrayList<Variable> vars;
    private PFont headerFont;
    private PFont contentFont;
    int h                   = 20;
    private int typeW       = 50;
    private int nameW       = 80;
    private int contentW    = 80;
    final int maxW          = 300;
    VariableTable(int x, int y) {
        this.x = x;
        this.y = y;
        vars = new ArrayList<Variable>();
        headerFont  = createFont("Ricty Diminished Bold",14);
        contentFont = createFont("Ricty Diminished",14);
    }
    void display() {
        stroke(0);
        strokeWeight(1);
        textAlign(CENTER, CENTER);
        drawHeader();
        drawAllVariable();
    }
    public void init(){
        vars = new ArrayList<Variable>();
        h = 20;
        nameW = 80;
        contentW = 80;
    }
    private void drawHeader() {
        fill(155, 153, 211);
        rect(x, y, nameW + typeW + contentW, h, 5);
        fill(0);
        textFont(headerFont);
        text("name",  x + nameW/2, y+h/2);
        text("type",  x + nameW + typeW/2, y + h/2);
        text("value", x + nameW + typeW + contentW/2, y+h/2);
    }
    private void drawAllVariable() {
        textFont(contentFont);
        textAlign(CENTER, CENTER);
        for (int i = 0; i < vars.size(); i++) {
            Variable var = vars.get(i);
            String name     = var.name;
            String content  = var.content;
            String type     = typeToString(var.kind);
            fill(155, 153, 211);
            rect(x, y+h*(i+1), nameW + typeW + contentW, h, 5);
            fill(0);
            text(name, x+nameW/2, y+h*(i+1)+h/2);
            text(type, x + nameW + typeW/2, y+h*(i+1)+h/2);
            text(content, x + nameW + typeW + contentW/2, y+h*(i+1)+h/2);
        }
        line(x + nameW, y, x + nameW, y + h * (vars.size() + 1));
        line(x + nameW + typeW, y, x + nameW + typeW, y + h * (vars.size() + 1));
    }
    private void updateWidth(String name, String type, String content) {
        textFont(contentFont);
        float newW1 = textWidth(name);
        float newW2 = textWidth(content);
        float newW3 = textWidth(type);
        if(newW1 > nameW) {
            nameW = int(newW1);
        }
        if(newW2 > contentW){
            contentW = int(newW2);
        }
        if(newW3 > typeW){
            typeW = int(newW3);
        }
        if(x + nameW + contentW + typeW > width){
            x -= x + nameW + contentW + typeW - width + 20;
        }
    }
    private String typeToString(int type){
        if(type == Enum.INT){
            return "int";
        }else if(type == Enum.STRING){
            return "String";
        }else if(type == Enum.INT_ARRAY){
            return "int[]";
        }else {
            return "undefined";
        }
    }
    public void addVariable(Variable var) {
        vars.add(var);
        updateWidth(var.name,typeToString(var.kind), var.content);
    }
    public Variable get(int i){
        return vars.get(i);
    }
    public Variable searchName(String name){
        for(Variable v : vars){
            if(v.name.equals(name)){
                return v;
            }
        }
        return null;
    }
    public void updateVariable(String name, String content){
        searchName(name).content = content;
    }

    public int indexOf(String name){
        for(int i = 0; i < vars.size(); i++){
            Variable v = vars.get(i);
            if(name.equals(v.name)){
                return i;
            }
        }
        return -1;
    }
}

class Variable <T> {
    int kind;
    String name;
    private T value;
    String content;
    int level;
    Variable(int kind, String name, T value, String content){
        this.kind    = kind;
        this.name    = name;
        this.value   = value;
        this.content = content;
    }
    Variable(int kind, String name, T value, String content, int level){
        this(kind, name, value, content);
        this.level   = level;
    }
    T getVarValue(){
        return value;
    }
}
class CompositeVariable <T> extends Variable {
    ArrayList<T> elements;
    CompositeVariable(int kind, String name, int length, T initValue){
        super(kind,name,null,"length:" + length);
        elements = new ArrayList<T>(length);
        for(int i = 0; i < length; i++){
            elements.add(initValue);
        }
    }
    CompositeVariable(int kind, String name, ArrayList<T> elements){
        super(kind, name, null, "length:" + elements.size());
        this.elements = elements;
    }
    public void initArray(int length){
        this.elements = new ArrayList<T>(length);
    }
    public void updateArray(ArrayList<T> es){
        elements = es;
    }
    public void set(int index, T element){
        elements.set(index, element);
    }
    public void setElements(ArrayList<T> elements){
        this.elements = elements;
    }
    public void add(T e){
        elements.add(e);
    }
    public T get(int index){
        return elements.get(index);
    }
}
