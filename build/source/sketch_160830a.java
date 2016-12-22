import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Stack; 
import processing.sound.*; 
import java.io.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class sketch_160830a extends PApplet {




/*
\u601d\u3044\u3064\u3044\u305f\u3053\u3068\u4e00\u89a7
\u30fb\u30b3\u30e1\u30f3\u30c8\u306e\u30bf\u30a4\u30eb\u5b9f\u88c5\u3092\u884c\u3046
\u30fb\u30bf\u30a4\u30eb\u306e\u4e00\u89a7\u8868\u3092\u4f5c\u6210\u3059\u308b
\u30fb\u5909\u6570\u4ee3\u5165\u3092\u8996\u899a\u7684\u306a\u5b9f\u884c\u6642\u8868\u73fe\u3067\u3042\u3089\u308f\u305b\u308b\u3088\u3046\u306b\u3059\u308b(\u7bb1\u3092\u4f5c\u308b\u30a4\u30e1\u30fc\u30b8)
\u30fb\u30c6\u30ad\u30b9\u30c8\u3068\u30d6\u30ed\u30c3\u30af\u306e\u95a2\u4fc2\u3065\u3051\u3092\u6301\u3063\u3066\u304a\u3044\u3066(Plate\u30af\u30e9\u30b9\u304c\u30c6\u30ad\u30b9\u30c8\u30a8\u30c7\u30a3\u30bf\u5185\u306e\u60c5\u5831\u3092\u6301\u3063\u3066\u304a\u308a\u3001\u304b\u3064\u30c6\u30ad\u30b9\u30c8\u306e\u65b9\u3082Plate\u306e\u60c5\u5831\u3092\u6301\u3063\u3066\u3044\u308b\u3088\u3046\u306a\u611f\u3058)\u76f8\u4e92\u9023\u643a\u3092\u56f3\u308a\u305f\u3044\u3002\u7de8\u96c6\u4e2d\u306b\u30b3\u30fc\u30c9\u306e\u63d0\u6848\u3092\u884c\u3048\u305f\u3089
\u30fb\u5236\u5fa1\u306e\u6d41\u308c\u304c\u300c\u7403\u300d\u306e\u52d5\u304d\u306b\u3088\u3063\u3066\u304a\u3063\u3066\u3044\u3051\u308b\u3068\u826f\u3044\u304b\u306a\u3041
\u30fb\u30df\u30cb\u30c1\u30e5\u30a2\u5316
\u30fb\u6301\u3063\u3066\u3044\u308b\u30d6\u30ed\u30c3\u30af\u3092\u30c6\u30ad\u30b9\u30c8\u306e\u65b9\u3067\u3082\u30cf\u30a4\u30e9\u30a4\u30c8
*/
/*
\u65e2\u77e5\u306e\u30d0\u30b0
\u30fbWallPlate\u5185\u3067StatementPlate\u4e00\u500b\u5206\u4ee5\u4e0a\u96e2\u3059\u3068\u30ea\u30f3\u30af\u304c\u30ad\u30e3\u30f3\u30bb\u30eb\u3055\u308c\u308b
*/

/*
\u4eca\u3059\u3050\u306b\u3067\u3082\u3067\u304d\u308b\u3053\u3068\u30ea\u30b9\u30c8
\u30fb\u30d6\u30ed\u30c3\u30af\u3092\u975e\u8868\u793a\u306b\u3057\u305f\u3068\u304d\u306f\u3001\u518d\u5b9f\u884c\u3057\u306a\u304f\u3066\u3082\u3044\u3044\u3088\u3046\u306b\u3059\u308b\u3002
\u30fb\u6298\u308a\u305f\u305f\u307f\u30d6\u30ed\u30c3\u30af\u306e\u751f\u6210
\u30fb\u69cb\u6587\u30a8\u30e9\u30fc\u304c\u3042\u308b\u5834\u5408\u306b\u30a8\u30e9\u30fc\u306e\u5834\u6240\u3092\u6307\u6458\u3059\u308b
*/

/*
\u9858\u671b\u30ea\u30b9\u30c8
\u30fb\u4f8b\u5916\u51e6\u7406\u3092\u5b9f\u88c5\u3057\u305f\u3044\u3088\u306d
\u30fb\u30de\u30eb\u30c1\u30b9\u30ec\u30c3\u30c9\u3068\u304b\u3082\u5b9f\u88c5\u3057\u305f\u3044\u3088\u306d
\u30fb\u30aa\u30d6\u30b8\u30a7\u30af\u30c8\u6307\u5411\u3001\u3084\u308a\u305f\u3044\u3088\u306d
*/
SoundFile errorSound, dumpSound, correctSound, putSound, nextStepSound, openWindowSound;

ArrayList<Plate> plateList          = new ArrayList<Plate>();
ArrayList<WallPlate> wallPlateList  = new ArrayList<WallPlate>();
ArrayList<Method> methodList        = new ArrayList<Method>();

ArrayList<Tile> allBlocks = new ArrayList<Tile>();    //\u30d6\u30ed\u30c3\u30af\u306e\u60c5\u5831\u3092\u4fdd\u6301\u3059\u308b\u5834\u6240

int RESULT_WINDOW_WIDTH;
int RESULT_WINDOW_HEIGHT;
final int MAX_STEP_COUNT = 10000;

PImage redFlagIcon, greenFlagIcon, trashBoxIcon, trashBoxOpenIcon;
MyTextEditor editor;
PFont font;
SetupPlate setupPlate;
DrawPlate drawPlate;

VariableTable variableTable;
Plate executingPlate;
WallPlate executingWallPlate;
int gradationR = 0;
int counter = -1;
int step = 0;
int debugIndex = 0;
boolean isChange = false;
ArrayList<Plate> allPlateForDebugmode = new ArrayList<Plate>();
ArrayList<MethodPlate> methodPlateList = new ArrayList<MethodPlate>();

HashMap<Integer,Integer> colorDict = new HashMap<Integer,Integer>();

boolean hasError            = false;
boolean isDebugMode         = false;
boolean hasExecuteEnd       = false;
boolean canSetupExecute     = true;     //setup\u30e1\u30bd\u30c3\u30c9\u3092\u5b9f\u884c\u3059\u308b\u304b\u3069\u3046\u304b
boolean isFillExisted       = false;    //fill\u30e1\u30bd\u30c3\u30c9\u304c\u5b58\u5728\u3059\u308b\u304b\u3069\u3046\u304b
boolean isTileConversion    = true;     //\u30bf\u30a4\u30eb\u3092\u8868\u793a\u3055\u305b\u308b\u304b\u3069\u3046\u304b
boolean canShowTile         = true;     //\u8996\u899a\u7684\u8868\u73fe\u3092\u51fa\u3059\u304b\u3069\u3046\u304b
boolean isSuperHackerMode   = false;

ArrayList<MyButton> buttonList = new ArrayList<MyButton>();

LogicalOpePlate logi;
MousePressedPlate mpp;
PApplet instance;
public void setup(){
    
    RESULT_WINDOW_WIDTH  = width / 2;
    RESULT_WINDOW_HEIGHT = height;
    editor = new MyTextEditor(RESULT_WINDOW_WIDTH, 0, RESULT_WINDOW_WIDTH, RESULT_WINDOW_HEIGHT, 20);
    variableTable = new VariableTable(RESULT_WINDOW_WIDTH - 300, 10);
    initImage();
    initSound();
    initButton();

    font = createFont("Ricty Diminished", 16);

    setupPlate = new SetupPlate(initialTileArrangement[0],initialTileArrangement[0]);
    drawPlate = new DrawPlate(initialTileArrangement[0], initialTileArrangement[1] + setupPlate.pWidth + MARGIN);
    plateList.add(setupPlate);
    wallPlateList.add(setupPlate);
    plateList.add(drawPlate);
    wallPlateList.add(drawPlate);

    changeTileToScript();

    initArgTypeList();
    initArgValueList();
    colorDict.put(Enum.INT, 0xffF3AFA9);  //#FADDDA
    colorDict.put(Enum.FLOAT, 0xffF3AF09);  //#FADDDA
    colorDict.put(Enum.STRING, 0xffB9EECF);   //#E6F9EE
    colorDict.put(Enum.BOOLEAN, 0xff69B0DD);   //#E6F1F9

    executingPlate = setupPlate;

    instance = this;

    // logi = new LogicalOpePlate(200,200);
    // plateList.add(logi);
    // mpp = new MousePressedPlate(300,200);
    // plateList.add(mpp);
    // wallPlateList.add(mpp);
    isChange =true;

}

int[] trashBoxPosition;

public void initImage(){
    redFlagIcon         = loadImage("flag.png");
    greenFlagIcon       = loadImage("green_flag.png");
    trashBoxIcon        = loadImage("trashbox.png");
    trashBoxOpenIcon    = loadImage("trashbox_open.png");
    trashBoxPosition    = new int[2];
    trashBoxPosition[0] = 30;
    trashBoxPosition[1] = height-70;
}
public void initSound(){
    dumpSound       = new SoundFile(this, "dumping.mp3");
    errorSound      = new SoundFile(this, "error_sound.mp3");
    correctSound    = new SoundFile(this, "correct_sound.mp3");
    putSound        = new SoundFile(this, "put_sound.mp3");
    nextStepSound   = new SoundFile(this, "next_step_sound.mp3");
    openWindowSound = new SoundFile(this, "open_window_sound.mp3");
}

MyButton statementButton, variableButton, ifButton, whileButton, forButton, methodButton, arrayButton;

public void initButton(){
    int x = 30;
    int y = 50;
    final int BUTTON_MARGIN = 70;

    statementButton = new MyButton("Statement", x,y);
    statementButton.setColor(peterRiver, color(0xff8AC3E9), color(0xff1A5F8E));
    buttonList.add(statementButton);
    y += BUTTON_MARGIN;

    variableButton = new MyButton("Variable", x, y);
    variableButton.setColor(alizarin, color(0xffF29E96), color(0xffA72114));
    buttonList.add(variableButton);
    y += BUTTON_MARGIN;

    ifButton = new MyButton("If", x, y);
    ifButton.setColor(carrot, color(0xffEFB17A), color(0xff8D4A10));
    buttonList.add(ifButton);
    y += BUTTON_MARGIN;

    whileButton = new MyButton("While", x, y);
    whileButton.setColor(schaussPink, color(0xffFFD1D8), color(0xffFF6B83));
    buttonList.add(whileButton);
    y += BUTTON_MARGIN;

    forButton = new MyButton("For", x, y);
    forButton.setColor(nephritis, color(0xff5CDA91), color(0xff13572F));
    buttonList.add(forButton);
    y += BUTTON_MARGIN;

    methodButton = new MyButton("Method", x, y);
    methodButton.setColor(color(78,205,196), color(0xff9CE2DC), color(0xff288A82));
    buttonList.add(methodButton);
    y += BUTTON_MARGIN;

    arrayButton = new MyButton("Array", x, y);
    arrayButton.setColor(amethyst, color(0xffC49FD4), color(0xff603474));
    buttonList.add(arrayButton);
    y += BUTTON_MARGIN;
}

int stmPos = 0;

public void draw( ) {
    if(isSuperHackerMode){
        shmDraw();
    }else{
    if(canShowTile) background(255);
    textSize(20);
    textAlign(LEFT,TOP);
    // text(Math.round(frameRate) + "fps",40,10);

    executePlate();

    drawEditor();
    if(canShowTile) drawPlate();
    drawUI();

    updateInitialTileArrangement();
    //\u91cd\u3055\u3092\u8003\u3048\u308b\u306a\u3089\u8981\u691c\u8a0e
    if(isTileConversion && isChange){
        changeTileToScript();
        if(editor.isLiveProgramming){
            isOK = false;
            selectedGUI = null;
            new Lang(editor.getTokens()).run();
            isOK = true;
        }
        allPlateForDebugmode = new ArrayList<Plate>();
        isChange = false;
    }

    }   //superhackermode
}
public void executePlate(){
    // if(!hasError){
        if(canSetupExecute){
            // background(255);
            fill(255);
            stroke(0);
            strokeWeight(2);
            variableTable.init();
            step = 0;
            isFillExisted = false;
            hasExecuteEnd = false;
            setupPlate.execute();
            if(drawPlate != null) canSetupExecute = false;
        }else{
            fill(255);
            stroke(0);

            step = 0;
            if(drawPlate != null)drawPlate.execute();
            else canSetupExecute = true;
        }
    // }
}
boolean isOK = true;
public void changeTileToScript(){
    String s = getAllScript();
    String[] ss = s.split("\n");
    editor.setTexts(ss);
}

public void drawEditor(){
    editor.display();
}

Plate hadPlate = null;  //\u76f4\u524d\u307e\u3067\u6301\u3063\u3066\u3044\u305f\u30d7\u30ec\u30fc\u30c8
public void drawPlate() {
    for(int i = 0; i < plateList.size(); i++){
        Plate plate = plateList.get(i);
        if(plate != null && plate != selectedPlate){
            plate.draw();
        }
    }
    if(executingPlate != null){
        executingPlate.draw();
    }
    if(hadPlate != null){
        hadPlate.draw();
    }
    //\u6301\u3063\u3066\u3044\u308b\u30d7\u30ec\u30fc\u30c8\u3060\u3051\u306f\u6700\u524d\u9762\u306b\u51fa\u3059\u305f\u3081\u306b\u6700\u5f8c\u306b\u63cf\u753b\u3059\u308b
    if(selectedPlate != null){
        selectingTime++;
        if(selectingTime > SELECTED_TIME){
            selectedPlate.drawShadow();
        }else{
            selectedPlate.draw();
        }
    }
}

public void drawUI(){
    for(MyButton button : buttonList){
        button.draw();
    }
    for(Balloon b : balloonList){
        b.draw();
    }
    if(editor.isLiveProgramming && isTileConversion){
        image(redFlagIcon, RESULT_WINDOW_WIDTH-80, 10,30,30);
        image(greenFlagIcon, RESULT_WINDOW_WIDTH-40, 10, 30, 30);
    }else if(isTileConversion){
        image(greenFlagIcon, RESULT_WINDOW_WIDTH-40, 10, 30, 30);
    }else if(editor.isLiveProgramming){
        image(redFlagIcon, RESULT_WINDOW_WIDTH-40, 10,30,30);
    }
    if(!isTrashBoxNear()){
        image(trashBoxIcon, trashBoxPosition[0], trashBoxPosition[1], 34, 40);
    }else{
        image(trashBoxOpenIcon, trashBoxPosition[0], trashBoxPosition[1]-20, 39,60);
    }
    variableTable.display();
    if(selectedGUI != null) selectedGUI.draw();
}
public void updateInitialTileArrangement(){
    if(plateList.size() > 0){
        Plate p = plateList.get(0);
        initialTileArrangement[0] = p.x;
        initialTileArrangement[1] = p.y;
    }
}
public void keyPressed(KeyEvent e){
    editor.keyPressed(e);
    if(!editor.getFocus()){
        if(key == 'a' && selectedPlate != null){
            println(selectedPlate.changePlatetoString());
        }else if(e.isControlDown() && keyCode == RIGHT){
            if(isDebugMode){
                counter++;
                nextStepSound.play();
            }
        }else if(e.isControlDown() && keyCode == LEFT){
            if(isDebugMode){
                if(counter > -1) counter--;
            }
        }
        // if(key == 'f'){
        //     plateList.add(new ConditionPlate(100,100));
        //     isChange = true;
        // }else if(key == 'd'){
        //     plateList.add(new LogicalOpePlate(100,100));
        //     isChange = true;
        // }
    }
    if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_P){     //\u30d7\u30ed\u30b0\u30e9\u30e0\u306e\u5b9f\u884c
        selectedGUI = null;
        balloonList = new ArrayList<Balloon>();
        drawPlate = null;
        new Lang(editor.getTokens()).run();
        canSetupExecute = true;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_O){     //\u30e9\u30a4\u30d6\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u30e2\u30fc\u30c9
        editor.isLiveProgramming = !editor.isLiveProgramming;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_I){     //\u30bf\u30a4\u30eb\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u30e2\u30fc\u30c9
        isTileConversion = !isTileConversion;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_U){     //statementList\u306e\u4e2d\u8eab\u3092\u3059\u3079\u3066\u8868\u793a
        for(int i = 0; i < statementList.size(); i++){
            println(statementList.get(i).kind);
        }
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_Y){     //debug\u30e2\u30fc\u30c9\u306e\u5207\u308a\u66ff\u3048
        isDebugMode = !isDebugMode;
    }
}

public String getAllScript(){
    String result = "";
    result += setupPlate.getScript();

    for(int i = 0; i < plateList.size(); i++){
        Plate plate = plateList.get(i);
        if(setupPlate != plate && !setupPlate.loopOpes.contains(plate) && isOut(plate)){
            result += plate.getScript();
        }
    }
    return result;
}
public boolean isOut(Plate p){
    for(WallPlate w : wallPlateList){
        if(w.loopOpes.contains(p)){
            return false;
        }
    }
    return true;
}

Tile selectedBlock;
Plate selectedPlate;
int selectingTime = 0;
int mousePressedTime = 0;
final int SELECTED_TIME = 8;
final int GUI_SELECTED_MAX_TIME = 8;
public void mousePressed() {
    for(int i = 0; i < plateList.size(); i++){
        Plate plate = plateList.get(i);
        if (plate.isMouseOver()) {
            selectedPlate = plate;
            hadPlate = plate;
        }
    }
    for(Balloon b : balloonList){
        if(b.isMouseOver()) balloonList.remove(b);
        break;
    }
    for(MyGUI gui : guiList){
        if(gui.isMouseOver()) selectedGUI = gui;
        break;
    }
    /////////////////////////////////////////////
    for(int i = 0; i < blocks.size(); i++){
        Tile block = blocks.get(i);
        if(block.isOnMouse()){
            selectedBlock = block;
        }
    }
    editor.mousePressed();
    buttonAction();

    if(mouseButton == RIGHT){
        for(WallPlate wp : wallPlateList){
            if(wp.isMouseOver()){
                if(!wp.isMiniature){
                    wp.miniature();
                    selectedGUI = null;
                }else{
                    wp.unminiature();
                }
                break;
            }
        }
    }
}
public void mouseDragged(){
    editor.mouseDragged();
    if(selectedPlate != null){
        selectedPlate.shiftPosition(mouseX-pmouseX, mouseY-pmouseY);
    }
    ///////////////////////////////////////////
    if(selectedBlock != null){
        int addX = mouseX - pmouseX;
        int addY = mouseY - pmouseY;
        selectedBlock.move(addX, addY);
    }
    mousePressedTime++;
}
public void mouseReleased() {
    if(selectedPlate != null){
        if(selectedPlate.isVariablePlate){
            VariablePlate variablePlate = (VariablePlate)selectedPlate;
            for(MyGUI gui : guiList){
                if(!gui.isTextField) continue;
                MyTextField txf = (MyTextField)gui;
                if(txf.isMouseOver() &&  txf.kind == variablePlate.type){
                    txf.addText(variablePlate.word);
                    plateList.remove(variablePlate);
                    break;
                }
            }
            hadPlate = null;
            selectedPlate = null;
            return;
        }
        //\u30bf\u30a4\u30eb\u306e\u4e0a\u306b\u6301\u3063\u3066\u3044\u308b\u30bf\u30a4\u30eb\u304c\u4e57\u3063\u3066\u3044\u308b\u304b\u3069\u3046\u304b\u3092\u5224\u5b9a\u3059\u308b
        boolean isEnter = false;
        for(Plate plate : plateList){
            if(plate != selectedPlate && plate.isLogicalOpePlate && plate.isMouseOver() && selectedPlate.oyaPlate != plate) {
                ((LogicalOpePlate)plate).insertPlate(selectedPlate);
                isEnter = true;
            }
        }
        if(isEnter ) return;

        //\u30bf\u30a4\u30eb\u306e\u4e0b\u306b\u304f\u3063\u3064\u3051\u308b
        selectedPlate.checkPlateLink();
        for(int i = 0; i < plateList.size(); i++){
            Plate plate = plateList.get(i);
            if(plate != selectedPlate && plate.isPlateBelow(selectedPlate)){
                plate.combinePlate(selectedPlate);
                selectedPlate.goToUnderThePlate(plate);
            }
        }
        //\u30bf\u30a4\u30eb\u306e\u4e2d\u306b\u5165\u308c\u3053\u307e\u305b\u308b
        WallPlate nearestPlate = selectedPlate.getNearestWallPlate(wallPlateList);
        selectedPlate.checkWallPlateLink(nearestPlate);
        if(nearestPlate != null) {
            if(selectedPlate.upperPlate != nearestPlate){
                selectedPlate.combineWallPlate(nearestPlate);
                if(nearestPlate == setupPlate) canSetupExecute = true;
            }
            selectedPlate.goIntoWallPlate(nearestPlate);
        }
        //\u30b4\u30df\u7bb1\u884c\u304d
        if(isTrashBoxNear()){
            dumpSound.play();
            deletePlate(selectedPlate);
            selectedGUI = null;
            hadPlate = null;
        }else {
            if(selectingTime > SELECTED_TIME){
                putSound.play();
            }
        }
        isChange = true;
    }
    selectedPlate = null;
    selectedBlock = null;
    selectingTime = 0;
    mousePressedTime = 0;
}
public void mouseWheel(MouseEvent e){
    editor.mouseWheel(e);
}
public void mouseClicked(MouseEvent e){
    for(int i = 0; i < plateList.size(); i++){
        plateList.get(i).mouseClicked(e);
    }
}
public boolean isTrashBoxNear(){
    int r = 100;
    if(dist(mouseX, mouseY, trashBoxPosition[0], trashBoxPosition[1]) < r){
        return true;
    }else{
        return false;
    }
}
public void buttonAction(){
    if(statementButton.isOver) {
        String[] arg = {"300","100","300","200"};
        plateList.add(new StatementPlate("rect0", 200,100, arg));
        isChange = true;
    }else if(variableButton.isOver){
        plateList.add(new DeclPlate(Enum.INT,200,100,"x","0"));
        isChange = true;
    }else if(whileButton.isOver){
        WhilePlate wp = new WhilePlate(200, 100);
        plateList.add(wp);
        wallPlateList.add(wp);
        isChange = true;
    }else if(ifButton.isOver){
        IfCondPlate fp = new IfCondPlate(200,100);
        plateList.add(fp);
        wallPlateList.add(fp);
        isChange = true;
    }else if(forButton.isOver){
        ForPlate l = new ForPlate(200,100);
        wallPlateList.add(l);
        plateList.add(l);
        isChange = true;
    }else if(methodButton.isOver){
        MethodPlate mp = new MethodPlate(200,100);
        plateList.add(mp);
        wallPlateList.add(mp);
        isChange = true;
    }else if(arrayButton.isOver){
        ArrayPlate_Original ap = new ArrayPlate_Original(200, 100, "a", "10",Enum.INT_ARRAY);
        plateList.add(ap);
        isChange = true;
    }
}
//\u518d\u5e30\u3092\u7528\u3044\u3066p\u4ee5\u4e0b\u306ePlate\u3092\u3059\u3079\u3066\u524a\u9664\u3002\u30a2\u30cb\u30e1\u30fc\u30b7\u30e7\u30f3\u3092\u52a0\u3048\u305f\u3044
public void deletePlate(Plate p){
    if(p == null){
        return;
    }
    if(p.isWallPlate){
        WallPlate wp = (WallPlate)(p);
        if(!wp.loopOpes.isEmpty()){
            deletePlate(wp.loopOpes.get(0));
        }
        wallPlateList.remove(wp);
        methodPlateList.remove(wp);
        if(wp.isDrawPlate) drawPlate = null;
    }
    plateList.remove(p);
    declPlateList.remove(p);
    deletePlate(p.nextPlate);
}
class Box{
    private int x, y;
    private int w, h;
    private String name = "x";
    private String content = ""+1;
    private final float rate = 0.5f;
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
    public void display(){
        strokeWeight(2);
        stroke(0);
        fill(0xfff1c40f);
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
        fill(0xffbdc3c7);
        noStroke();
        float rateH = 0.6f;
        float rateBandH = 0.3f;
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
    private int fillColor;
    private int textColor;
    Balloon(String text, MyGUI gui){
        this.text = text;
        this.x = gui.x + MARGIN;
        this.y = gui.y + gui.h + MARGIN;
        this.targetX = gui.x + gui.w/2;
        this.targetY = gui.y + gui.h/2;
        fillColor = clouds;
        textColor = color(0xffff0000);
        textFont(font);
        this.w = PApplet.parseInt(textWidth(text)) + MARGIN * 2;
        this.h = text.split("\n").length * textSize + MARGIN * 2;
        balloonList.add(this);
        gui.balloon = this;
    }
    Balloon(String text, int x, int y, int targetX, int targetY){
        this.text = text;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        fillColor = clouds;
        textColor = color(0xffFF0000);
        textFont(font);
        this.w = PApplet.parseInt(textWidth(text)) + MARGIN * 2;
        this.h = text.split("\n").length * textSize + MARGIN * 2;
        balloonList.add(this);
    }
    public void draw(){
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
    public boolean isMouseOver(){
        return mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h;
    }
    public void setPosition(int x, int y, int targetX, int targetY){
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
    }
    public void shiftPos(int addX, int addY){
        this.x += addX;
        this.y += addY;
        this.targetX += addX;
        this.targetY += addY;
    }
    public void putForward(){
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
    public void display() {
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
            nameW = PApplet.parseInt(newW1);
        }
        if(newW2 > contentW){
            contentW = PApplet.parseInt(newW2);
        }
        if(newW3 > typeW){
            typeW = PApplet.parseInt(newW3);
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
        }
        else if(type == Enum.STRING_ARRAY){
            return "String[]";
        }
        else {
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
    public Variable searchName(String name)throws UndefinedVariableException{
        for(Variable v : vars){
            if(v.name.equals(name)){
                return v;
            }
        }
        throw new UndefinedVariableException(name);
    }
    public void updateVariable(String name, String content) throws UndefinedVariableException{
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
    public T getVarValue(){
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


public class HsmAssembler {
    BufferedReader br;
    public HsmAssembler(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    public HsmAssembler(String s) {
        this(new ByteArrayInputStream(s.getBytes()));
    }

    public int [] assemble() {
        int []code = new int[256*3];
        int pc = 0;
        String line;
        try {
            while ( (line = br.readLine ()) != null) {
                String[] inst = line.split(" ");
                if (inst.length != 3) {
                    println(inst[0] + ", "+inst.length);
                    throw new RuntimeException("illegal instruction: " + inst);
                }
                decodeAndLoad(inst, code, pc);
                pc += 3;
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException("IOError in assemble()");
        }
        return code;
    }

    private void decodeAndLoad(String[] inst, int[] code, int pc) {
        String op = inst[0];
        String operand1 = inst[1];
        String operand2 = inst[2];
        code[pc+1] = Integer.parseInt(operand1);
        code[pc+2] = Integer.parseInt(operand2);
        if("MOUSE_X".equals(op)){
            code[pc+2] = mouseX;
        }else if("MOUSE_Y".equals(op)){
            code[pc+2] = mouseY;
        }else if("HEIGHT".equals(op)){
            code[pc+2] = RESULT_WINDOW_HEIGHT;
        }else if("WIDTH".equals(op)){
            code[pc+2] = RESULT_WINDOW_WIDTH;
        }
        if ("LDC".equals(op)) {
            code[pc] = LDC;
        } else
            if ("WRN".equals(op)) {
            code[pc] = WNL;
        } else
            if ("WRI".equals(op)) {
            code[pc] = WRI;
        } else
            if ("HLT".equals(op)) {
            code[pc] = HLT;
        } else
            if ("AD".equals(op)) {
            code[pc] = AD;
        } else
            if ("ML".equals(op)) {
            code[pc] = ML;
        } else
            if ("SUB".equals(op)) {
            code[pc] = SUB;
        } else
            if ("DIV".equals(op)) {
            code[pc] = DIV;
        } else
            if ("PUSH".equals(op)) {
            code[pc] = PUSH;
        } else
            if ("POP".equals(op)) {
            code[pc] = POP;
        } else
            if ("LDV".equals(op)) {
            code[pc] = LDV;
        } else
            if ("MOUSE_X".equals(op)) {
            code[pc] = 0;
        } else
            if ("MOUSE_Y".equals(op)) {
            code[pc] = 0;
        } else
            if ("HEIGHT".equals(op)) {
            code[pc] = 0;
        } else
            if ("WIDTH".equals(op)) {
            code[pc] = 0;
        } else
            if ("STV".equals(op)) {
            code[pc] = STV;
        } else
            if("LDA".equals(op)){
            code[pc] = LDA;
        } else
            throw new RuntimeException("should not reach:" + op);
    }
}


final int LDC = 0;
final int STV = 1;
final int LDV = 2;
final int LDA = 3;
final int PUSH = 16;
final int POP = 17;

final int AD = 32;
final int ML = 34;
final int SUB = 36;
final int DIV = 38;

final int WNL = 64;
final int WRI = 65;

final int MOUSE_X = 70;
final int MOUSE_Y = 71;
final int HEIGHT = 72;
final int WIDTH = 73;

final int HLT = 255;

public class SimpleHSM {
    int ic =0; // instruction count
    final int icMax = 65535;

    public int execute(int [] code)throws ArrayIndexOutOfBoundsException {
        int pc = 0;
        int s[] = new int[256];
        int sp = -1;
        int result = 0;
loop:
        while (ic < icMax) {
            // \u7121\u9650\u30eb\u30fc\u30d7\u3092\u9632\u3050\u305f\u3081icMax\u56de\u4ee5\u4e0a\u306f\u547d\u4ee4\u3092\u5b9f\u884c\u3057\u306a\u3044\u3053\u3068\u3068\u3059\u308b\u3002
            // \u5b9f\u7528\u7684\u306b\u306fwhile(true){\u3068\u3059\u308b\u3002
            ic ++;
            switch(code[pc]) {
            case LDC:
                s[++sp] = code[pc + 2];
                break;
            case STV:
                s[code[pc + 2]] = s[sp];
                sp--;
                break;
            case LDV:
                //\u8981\u691c\u8a0e\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01\uff01
                s[++sp] = Integer.parseInt((variableTable.get(code[pc + 2]).content));
                break;
            case LDA:
                s[++sp] =(Integer)((CompositeVariable)(variableTable.get(code[pc + 2]))).get(code[pc + 1]);
                break;
            case PUSH:
                sp+= code[pc + 2];
                break;
            case POP:
                sp-= code[pc + 2];
                break;
            case AD:
                s[sp-1] = s[sp] + s[sp-1];
                sp--;
                break;
            case MOUSE_X:
                break;
            case MOUSE_Y:
                break;
            case WIDTH:
                break;
            case HEIGHT:
                break;
            case ML:
                s[sp-1] = s[sp] * s[sp-1];
                sp--;
                break;
            case SUB :
                s[sp-1] = s[sp-1] - s[sp];
                sp--;
                break;
            case DIV :
                s[sp-1] = s[sp-1] / s[sp];
                sp--;
                break;
            case WNL:
                System.out.println();
                break;
            case WRI:
                System.out.print(s[sp--]);
                break;

            case HLT:
                result = s[sp--];
                break loop;

            default:
                throw new RuntimeException("Illegal instruction:" + code[pc]);
            }
            pc += 3;
        }
        return result;
    }
}
class LoadTextEditorThread extends Thread {
    MyTextEditor editor;
    LoadTextEditorThread(MyTextEditor editor){
        this.editor = editor;
    }
    public void run(){
        new Lang(editor.getTokens()).run();
    }
}
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
//\u81ea\u4f5c\u306eGUI\u30dc\u30bf\u30f3
//\u3064\u304f\u308a\u304b\u3051\u3001\u3082\u3063\u3068\u3057\u3063\u304b\u308a\u3064\u304f\u308b

MyGUI selectedGUI;

int kindCursor;
boolean isRenew;
int txfWidth = 60;  //\u30c6\u30ad\u30b9\u30c8\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u5e45
int txfHeight = 20; //\u30c6\u30ad\u30b9\u30c8\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u9ad8\u3055

ArrayList<MyGUI> guiList = new ArrayList<MyGUI>();
public abstract class MyGUI {
    public abstract void draw();
    public abstract boolean isMouseOver();
    int textSize = 12;
    int x, y, w, h;
    boolean isTextField = false;

    Balloon balloon;    //\u975e\u5e38\u306b\u6c5a\u3044\u304b\u304d\u304b\u305f\uff01\uff01\uff01\uff01
}

class MyButton extends MyGUI {

    String label;
    PImage image;
    int bColor        = color(255);
    int bHighlight    = color(255);
    int bNotPushColor = color(120);
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
    public void draw () {
        noStroke();
        update();
        int c = bColor;
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
                text(label, x + w / 2, y + h / 2+h*(1.0f/15));
            }
            else if(isOver){
                stroke(0);
                strokeWeight(2);
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h / 2-h*(1.0f/6));
            }else{
                stroke(0);
                strokeWeight(2);
                fill(0);
                textAlign(CENTER, CENTER);
                text(label, x + w / 2, y + h / 2);
            }
        }
    }
    public void drawButton(int c){
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
    public void drawShadowSide(){
        for(int i = 1; i <= shadowX; i++){
            fill(color(120),100-10*i);
            rect(x-i,y-i,w+2*i,h+PApplet.parseInt(2*1.5f)*i);
        }
    }
    public void update() {
        if ( isMouseOver() ) {
            kindCursor = HAND;
            isOver = true;
        } else {
            isOver = false;
        }
    }
    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public boolean isMouseOver () {
        if (mouseX >= x && mouseX <= x+w &&
            mouseY >= y && mouseY <= y+h) {
            return true;
        } else {
            return false;
        }
    }
    public void setColor(int bColor, int highlight, int pushColor) {
        this.bColor = bColor;
        bHighlight = highlight;
        bNotPushColor = pushColor;
    }
    public int getWidth(){
        return w;
    }
}
//\u81ea\u4f5c\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9
class MyComboBox extends MyGUI {
    String[] items;  //\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u306b\u8868\u793a\u3059\u308b\u3082\u306e
    Boolean isSelected = false;  //\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u3092\u9078\u629e\u3057\u3066\u3044\u308b\u304b\u3069\u3046\u304b\uff08\u9078\u629e\u3055\u308c\u3066\u3044\u308b\u3068\u4e2d\u8eab\u5168\u4f53\u304c\u898b\u3048\u308b\uff09
    private boolean isChanged;
    private final int MARGIN = 10;
    //\u5e45\u3068\u9ad8\u3055\u3092\u8a2d\u5b9a\u3057\u306a\u3044\u3068\u52dd\u624b\u306b\u6c7a\u3081\u308b
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
    public void draw() {
        textFont(font);
        stroke(0);
        strokeWeight(2);
        textAlign(LEFT, TOP);
        drawContent();
        actionByMouseEvent();
    }
    private void drawContent(){
        int itemMaxWidth = getMaxWidth() + MARGIN;
        if (!isSelected) {  //\u9078\u629e\u3055\u308c\u3066\u3044\u306a\u3051\u308c\u3070
            fill(255);      //\u4e00\u884c\u5206\u3057\u304b\u8868\u793a\u3057\u306a\u3044
            rect(x, y, w, h);
            fill(0);
            text(items[0], x+ MARGIN/2, y);
        } else {                                    //\u9078\u629e\u3055\u308c\u3066\u3044\u308c\u3070
            for (int i = 0; i < items.length; i++) {  //\u4e2d\u8eab\u5168\u90e8\u8868\u793a\u3059\u308b
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
                String a = getSelectedItem();  //\u9078\u629e\u3055\u308c\u305f\u9805\u76ee\u3092\u53d6\u308a\u51fa\u3057\u3066\u304d\u3066
                int selectIndex = getSelectedIndex();  //\u305d\u306e\u30a4\u30f3\u30c7\u30c3\u30af\u30b9\u3082\u53d6\u308a\u51fa\u3059
                if(selectIndex > 0){
                    items[selectIndex] = items[0];  //\u73fe\u6642\u70b9\u3067\u5148\u982d\u306b\u3042\u308b\u9805\u76ee\u3068\u9078\u629e\u3057\u305f\u9805\u76ee\u3092\u5165\u308c\u66ff\u3048\u308b
                    items[0] = a;
                    this.w = PApplet.parseInt(textWidth(items[0]))+MARGIN;
                    isRenew =  true;
                    isChanged = true;
                }
            }
            isSelected = false;  //\u9078\u629e\u3092\u89e3\u9664
            mousePressed =false;
        }
        //\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u304c\u305d\u3082\u305d\u3082\u9078\u629e\u3055\u308c\u3066\u3044\u306a\u304b\u3063\u305f\u3089
        if (mousePressed  && mousePressedTime < GUI_SELECTED_MAX_TIME && !isSelected && isMouseOver()) {
            isSelected = true;  //\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u3092\u9078\u629e\u72b6\u614b\u306b\u3059\u308b
            mousePressed = false;
            selectedGUI = this;
        }
    }

    //\u30de\u30a6\u30b9\u304c\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u306e\u30a2\u30a4\u30c6\u30e0\u5185\u306b\u3042\u308b\u304b\u3069\u3046\u304b
    private boolean isMouseIn(int x, int y, int w) {
        if (mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y+h) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isMouseOver(){
        return mouseX >= x && mouseX < x + w && mouseY > y && mouseY <= y + h;
    }

    public boolean checkChanged(){
        if(isChanged){
            isChanged = false;
            return true;
        }else{
            return false;
        }
    }

    public int getWidth(){
        return w;
    }

    //\u8868\u793a\u3055\u308c\u3066\u3044\u308bitem\u3092\u8fd4\u3059
    public String getItem() {
        return items[0];
    }

    //\u9078\u629e\u3057\u305f\u30a2\u30a4\u30c6\u30e0\u3092\u8fd4\u3059
    public String getSelectedItem() {
        for (int i = 0; i < items.length; i++) {
            if (y + h * i < mouseY && mouseY <= y + h* (i+1)) {
                return items[i];
            }
        }
        return null;
    }
    public int getSelectedIndex() {
        for (int i = 0; i < items.length; i++) {
            if (y + h * i < mouseY && mouseY <= y + h* (i+1)) {
                return i;
            }
        }
        return -1;
    }

    public void addItem(String item) {
        String[] newItems = new String[items.length+1];
        for (int i = 0; i < items.length; i++) {
            newItems[i] = items[i];
        }
        newItems[items.length] = item;
        items = newItems;
    }
    public void setItem(String name) {
        int i = 0;
        for (String item : items) {
            if (item.equals(name)) {
                items[i] = items[0];
                items[0] = name;
                textFont(font);
                this.w = PApplet.parseInt(textWidth(items[0]))+MARGIN;
            }
            i++;
        }
    }
    private int getMaxWidth(){
        int max = 0;
        for(String item : items){
            int itemWidth = PApplet.parseInt(textWidth(item));
            if(itemWidth > max) max = itemWidth;
        }
        return max;
    }

    public void setItems(String[] newItems) {
        items = newItems;
    }

    public void moveTo(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void shiftPosition(int addX, int addY){
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
    private int index = 0;    //\u6587\u5b57\u5217\u306b\u633f\u5165\u3059\u308b\u5834\u6240
    private int myFrameCount = 0;
    private boolean isChanged;
    private int fillColor;
    private int kind;
    private boolean isNotError = true;
    MyTextField(int x, int y) {
        this.x = x;  this.y = y;
        this.fillColor = 0xffecf0f1;
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
        this.fillColor = 0xffecf0f1;
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
    public void draw() {

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
    public boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + w && y <= mouseY && mouseY <= y + h) {
            return true;
        }
        return false;
    }
    public int getWidth(){
        return w;
    }
    public boolean checkChanged(){
        if(isChanged){
            isChanged = false;
            return true;
        }else{
            return false;
        }
    }
    public String getText() {
        return text;
    }
    public int getKind(){
        return kind;
    }
    public void setText(String text) {
        this.text = text;
        int tLength = text.length();
        w = PApplet.parseInt(textWidth(text)) + offsetX * 2;
    }
    public void setKind(int kind){
        this.kind = kind;
    }
    public void setFillColor(int fc){
        this.fillColor = fc;
    }
    public void addText(String text) {
        this.text += text;
        w = PApplet.parseInt(textWidth(this.text)) + offsetX * 2;
        isChange = true;
    }
    public void moveTo(int x, int y) { //\u76ee\u7684\u5730\u3092\u5f15\u6570\u306b\u3082\u3064
        this.x = x;
        this.y = y;
        // if(balloon != null) balloon.setPosition(x + w + MARGIN, y + h + MARGIN, x + w/2, y + h/2);
    }
    public void shiftPos(int addX, int addY){ //\u73fe\u5728\u4f4d\u7f6e\u304b\u3089\u306e\u52d5\u304b\u3059\u8ddd\u96e2\u3092\u5f15\u6570\u306b\u3082\u3064
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
            //\u4f55\u3082\u3057\u306a\u3044
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
        this.posX =  PApplet.parseInt(this.index * charWidth) + offsetX;
    }
}
int alpha = 255;
int alizarin;
int pomegranate;
int pumpkin;
int sunFlower;
int carrot;
int orange;
int turquoise;
int emerald;
int greenSea;
int nephritis;
int peterRiver;
int belizeHole;
int amethyst;
int silver;
int concrete;
int clouds;
int wetAsphalt, schaussPink;
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
    private int cursorHeight    = 0;   //\u6587\u5b57\u30b5\u30a4\u30ba\u306b\u3088\u308b
    private int row             = 0;    //\u5b9f\u969b\u306e\u884c\u6570
    private int col             = 0;    //\u5b9f\u969b\u306e\u5217\u6570
    private int nowRow          = 0; //\u753b\u9762\u4e0a\u3067\u306e\u884c\u6570
    private int topRowNumber    = 0;   //\u753b\u9762\u4e0a\u3067\u306e\u4e00\u756a\u4e0a\u306e\u884c\u6570\u304c\u5168\u4f53\u306e\u884c\u6570\u306e\u4f55\u756a\u76ee\u304b
    private int selectStartRow  = -1;
    private int selectStartCol  = -1;
    private int selectEndRow    = -1;
    private int selectEndCol    = -1;
    private float charWidth     = 0;    //\u6587\u5b57\u30b5\u30a4\u30ba\u306b\u3088\u308b
    private boolean isLoad      = false;
    private int maxRowperPage;  //1\u753b\u9762\u306b\u8868\u793a\u3067\u304d\u308b\u6700\u5927\u306e\u884c\u6570
    private float charHeight;
    private int editorWidth;
    private int editorHeight;
    private int textSize;

    private int backgroundColorDark  = color(10);
    private int backgroundColorLight = color(245);

    int elapsedTimeFromKeyPressed = -1;
    final int DURATION = 100;

    boolean isLiveProgramming = false;
    MyTextEditor(int x, int y, int w, int h, int textSize){
        this.x = x;
        this.y = y;
        this.textSize = textSize;
        //\u30d5\u30a9\u30f3\u30c8\u8a2d\u5b9a\uff08\u7b49\u5e45\u30d5\u30a9\u30f3\u30c8\u306e\u307f)
        textSize(textSize);
        textAlign(LEFT, TOP);
        textFont(createFont("Ricty Diminished", textSize));
        texts.add(new StringBuilder(""));
        initColor();
        loadTokenByJSONFile();
        cursorHeight = textSize;
        charWidth = textWidth('a'); //\u4e00\u6587\u5b57\u5206\u306e\u5e45\u3092\u53d6\u5f97\u3001a\u306e\u90e8\u5206\u306f\u4e00\u6587\u5b57\u306a\u3089\u4f55\u3067\u3082\u3088\u3044
        editorWidth = w - merginLeft * 2;
        editorHeight = h - merginTop * 2;
        maxRowperPage = PApplet.parseInt(editorHeight / charHeight);
        charHeight = textSize;
        background(0);
    }
    public void display() {
        //\u30d5\u30a9\u30f3\u30c8\u8a2d\u5b9a
        textSize(textSize);
        textAlign(LEFT, TOP);
        textFont(createFont("Ricty Diminished", textSize));
        //\u80cc\u666f\u8272\u306e\u8a2d\u5b9a
        if(isColorLight){
            fill(backgroundColorLight);
        }else{
            fill(backgroundColorDark);
        }
        rect(x,y,editorWidth+merginLeft*2, editorHeight+merginTop*2);
        if(isFocus){
            fill(0xff3498db);
        }else{
            fill(100);
        }
        noStroke();
        rect(x, y, editorWidth + merginLeft * 2, merginTop);    //\u4e0a
        rect(x, y, merginLeft, editorHeight + merginTop * 2);  //\u5de6
        rect(x + editorWidth + merginLeft, y, merginLeft, editorHeight+merginTop*2);  //\u53f3
        rect(x, y + editorHeight + merginTop, editorWidth+merginLeft*2, merginTop);  //\u4e0b

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
        if(isLiveProgramming && elapsedTimeFromKeyPressed != -1 &&  millis() - elapsedTimeFromKeyPressed > DURATION){
            new LoadTextEditorThread(this).run();
            elapsedTimeFromKeyPressed = -1;
        }
    }
    public void drawBlocks(){
        for(int i = 0; i < blocks.size(); i++){
            Tile block = blocks.get(i);
            block.display();
        }
    }
    public void drawCursor(){
        noStroke();
        if(isColorLight){
            fill(0);
        }else{
            fill(255);
        }
        rect(x + merginLeft + charWidth * col, y + merginTop + charHeight * nowRow, cursorWidth, cursorHeight);
    }
    public void drawText() {
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
    public void analyze(String text, int rowNumber){
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
            int fillColor = getColorByToken(kind);
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
    public int getToken(String word){
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
        float result = PApplet.parseFloat(x);
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
    //\u3053\u3053\u306f\u9811\u5f35\u308c\u3070\u3082\u3063\u3068\u77ed\u304f\u304b\u3051\u308b
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
    public void keyPressed(KeyEvent e) {
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
        }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {    //ctrl+s \u30bb\u30fc\u30d6
            saveScript();
        }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {    //ctrl+l \u30ed\u30fc\u30c9
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
            }else if(26 < key && key < 127){  //alt\u30ad\u30fc\u3084shift\u30ad\u30fc\u3092\u62bc\u3057\u305f\u6642\u306b\u7a7a\u767d\u6587\u5b57\u304c\u633f\u5165\u3055\u308c\u308b\u306e\u3092\u9632\u3050\u305f\u3081
                text.insert(col,key);        //\u8a73\u3057\u304f\u306fASCII\u30b3\u30fc\u30c9\u898b\u3066\u306d
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
    public void complementChar(char c){
        text.insert(col,c);
        col++;
        text.insert(col,PApplet.parseChar(c+1));
    }
    public void determineSameChar(char c){
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
    public void initBlock() {
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
    public void initText(){
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
    // TODO: \u3082\u3063\u3068\u3044\u3044\u30bd\u30fc\u30c8\u3092\u4f7f\u3046\u3079\u304d
    public void sortByBlockY(Tile[] a){
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
    public void loadTokenByJSONFile() {
        JSONArray values = loadJSONArray("token.json");
        myTokens = new MyToken[values.size()];
        for(int i = 0; i < values.size(); i++){
            JSONObject value = values.getJSONObject(i);
            myTokens[i] = new MyToken(value.getString("word"), value.getInt("token"));
        }
    }
    public void sortByBlockX(Tile[] a){
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
    public String getRowTextByBlock(Tile[] bs){
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

    public void createBlock(String text, int rowNumber){
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

    public void createNewLine(){
        if(row >= texts.size()){
            text = new StringBuilder();
            texts.add(text);
        }else{
            text = new StringBuilder();
            texts.add(row,text);
        }
    }

    public void deleteLine(){
        if(row > 0){
            texts.remove(row);
            row--;
            text= texts.get(row);
            col = text.length();
        }
    }

    public void upLine (){
        row--;
        text = texts.get(row);
        if(col >= text.length()){
            col = text.length();
        }
    }
    public void upCursor(){
        if(nowRow > 0){
            nowRow--;
        }else{
            if(topRowNumber > 0) {
                topRowNumber--;
            }
        }
    }

    public void downLine(){
        row++;
        text = texts.get(row);
        if(col >= text.length()){
            col = text.length();
        }
    }
    public void downCursor(){
        if(nowRow < maxRowperPage-1){
            nowRow++;
        }else{
            topRowNumber++;
        }
    }

    public void moveCursorRight() {
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
    public void moveCursorLeft() {
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
    public void moveCursorDown() {
        col = 0;
        row++;
    }

    public void mousePressed(){
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
    public void mouseDragged(){
        setRowAndColByCursor();
        selectEndRow = row;
        selectEndCol = col;
    }
    public void mouseWheel(MouseEvent e ){
        addRow(PApplet.parseInt(e.getAmount()));
    }
    private boolean isCursorOnEditor(){
        if(x < mouseX && mouseX < x + editorWidth && y < mouseY && mouseY < y + editorHeight){
            return true;
        }else{
            return false;
        }
    }
    public void setRowAndColByCursor() {
        int newCol = round((mouseX - x - merginLeft)/charWidth);
        int newRow = PApplet.parseInt((mouseY - y - merginTop)/charHeight);  //\u753b\u9762\u4e0a\u3067\u306e\u898b\u304b\u3051\u4e0a\u306e\u884c\u6570
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
    public void addRow(int add){    //\u8ffd\u52a0\u5206
        if((add < 0 &&  0 < topRowNumber) || (add > 0 && topRowNumber + maxRowperPage < texts.size() - 1)){
            row += add;    //\u5b9f\u969b\u306e\u884c\u6570
            col = 0;    //\u5b9f\u969b\u306e\u5217\u6570
            // int nowRow = 0; //\u753b\u9762\u4e0a\u3067\u306e\u884c\u6570
            topRowNumber += add;   //\u753b\u9762\u4e0a\u3067\u306e\u4e00\u756a\u4e0a\u306e\u884c\u6570\u304c\u5168\u4f53\u306e\u884c\u6570\u306e\u4f55\u756a\u76ee\u304b
            text = texts.get(row);
        }
    }
    public void saveScript() {
        if(file == null){
            selectOutput("Choose WriteFile", "writeScript");
        }else{
            writeScript(file);
        }
    }
    public void loadScript() {
        isLoad = true;
        allclear();
        selectInput("Choose ReadFile", "readScript");
        isLoad = false;
    }
    public void allclear(){
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

    public void writeScript(File selection){
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
    public void readScript(File selection) {
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

    public ArrayList<Token> getTokens(){
        return getTokens(texts.size());
    }
    public ArrayList<Token> getTokens(String text){
        ArrayList<Token> result = new ArrayList<Token>();
        analyze(text, result);
        result.add(new Token("", Enum.EOF));
        return result;
    }
    public ArrayList<Token> getTokens(int index) {
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

    public void analyze(String text, ArrayList<Token> allToken){
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

    public boolean getFocus(){
        return isFocus;
    }
    public void setTexts(String[] textArray){
        texts = new ArrayList<StringBuilder>();
        for(int i = 0; i < textArray.length; i++){
            StringBuilder text = new StringBuilder(textArray[i]);
            texts.add(text);
        }
        text = texts.get(0);    //\u8981\u691c\u8a0e
    }
}
// \u30d5\u30a1\u30a4\u30eb\u306b\u30b9\u30af\u30ea\u30d7\u30c8\u3092\u66f8\u304d\u51fa\u3059
public void writeScript(File selection) {
    editor.writeScript(selection);
}

//\u30d5\u30a1\u30a4\u30eb\u304b\u3089\u30b9\u30af\u30ea\u30d7\u30c8\u3092\u8aad\u307f\u8fbc\u3093\u3067\u304f\u308b
public void readScript(File selection) {
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
    public void display() {
        int fillColor = getColorByToken(kind);
        fill(fillColor,alpha);
        rect(x,y,w,h);
        fill(50,alpha);
        text(word,x,y);
    }
    public boolean isOnMouse(){
        if(x < mouseX && mouseX < x + w && y < mouseY && mouseY < y + h){
            return true;
        }else{
            return false;
        }
    }
    public void move(int addX, int addY){
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

    //\u5b9f\u4f53\u306e\u306a\u3044\u3084\u3064\u3089
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

public int getColorByToken(int kind){
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
        return color(0xffA092E5);
    }else if(kind == Enum.TRUE){
        return color(0xff0000FF);
    }
    else if(kind == Enum.YUYA || kind == Enum.FALSE){
        return color(0xffff0000);
    }else{
        if(isColorLight){
            return color(0);
        }
        return color(255);
    }
}
public void initColor(){
    pomegranate   = color(0xffc0392b);
    alizarin      = color(0xffe74c3c);
    schaussPink   = color(0xffFFA3B2);
    pumpkin       = color(0xffd35400);
    carrot        = color(0xffe67e22);
    orange        = color(0xfff39c12);
    sunFlower     = color(0xfff1c40f);
    turquoise     = color(0xff1abc9c);
    emerald       = color(0xff2ecc71);
    greenSea      = color(0xff16a085);
    nephritis     = color(0xff27ae60);
    peterRiver    = color(0xff3498db);
    belizeHole    = color(0xff2980b9);
    wetAsphalt    = color(0xff34495e);
    amethyst      = color(0xff9b59b6);
    silver        = color(0xffbdc3c7);
    concrete      = color(0xff95a5a6);
    clouds        = color(0xffecf0f1);
}
Plate errPlate;
int marginX = 30;
int marginY = 20;
int indent = 0;
private int txfPosX = 10;  //\u64cd\u4f5c\u5185\u306e\u30c6\u30ad\u30b9\u30c8\u30d5\u30a3\u30fc\u30eb\u30c9\u306e\u4f4d\u7f6e
private int txfPosY = 5;
int loopTxfPosX = 50, loopTxfPosY = 5;
final int originalLoopHeight = 100;
final int originalLoopWidth = 180;
final int indentVolume = 4;

float RATE = 0.2f;
public abstract class Plate {

    int x, y;
    int z = 0;  //\u5965\u884c\u3001\u3069\u306e\u9762\u3092\u4e0a\u306b\u51fa\u3059\u304b\u6c7a\u3081\u308b\u3002\u6b63\u306e\u6570\u304c\u624b\u524d
    int pWidth, pHeight;
    int[] targetPos;
    int type = Enum.VOID;

    Plate oyaPlate;

    WallPlate upperPlate;  //\u6240\u5c5e\u3059\u308b\u30eb\u30fc\u30d7
    Plate prePlate;  //\u4e0a\u306b\u3042\u308b\u64cd\u4f5c
    Plate nextPlate;  //\u4e0b\u306b\u3042\u308b\u64cd\u4f5c
    FuncPlate belongPlate;
    protected int fillColor;
    boolean isWallPlate         = false;
    boolean isVariablePlate     = false;
    boolean isLogicalOpePlate   = false;
    boolean isDrawPlate         = false;
    boolean isMiniature         = false;

    Balloon balloon;

    public abstract void draw();
    public abstract void drawShadow();
    public abstract void drawTransparent();
    public abstract void shiftPosition(int addX, int addY);
    public abstract void execute();
    public abstract String getScript();

    public String getNoIndentScript(){
        return "\u672a\u5b9a\u7fa9:getNoIndentScript()";
    }
    public boolean isMouseOver(){
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public boolean isPlateBelow(Plate plate) {     //\u5f15\u6570\u306b\u6e21\u3057\u305fplate\u304c\u81ea\u5206\u306e\u4e0b\u306b\u3044\u308b\u304b\u3069\u3046\u304b
        if ((!plate.isMiniature && !this.isMiniature) && abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
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
    public void combinePlate(Plate plate){  //this\u306e\u4e0b\u306b\u5f15\u6570\u306eplate\u3092\u304f\u3063\u3064\u3051\u308b
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
        this.shiftPosition(differencePosX, differencePosY);
    }
    public void goIntoWallPlate(WallPlate wallPlate){
        int index = wallPlate.loopOpes.indexOf(this);
        int differencePosX = wallPlate.x + wallPlate.wallPlateWidth - this.x;
        int differencePosY = wallPlate.getPositionYinLoopOpes(index) - this.y;
        this.shiftPosition(differencePosX, differencePosY);
    }
    public void setBorder(){
        if(isDebugMode)
        {gradationR += 3;
        gradationR = gradationR % 350;
        strokeWeight(3);
        stroke(gradationR,0,0);}
    }
    private int getDistBetweenPlate(Plate plate){
        return PApplet.parseInt(dist(this.x, this.y, plate.x, plate.y));
    }

    //\u30b9\u30af\u30ea\u30d7\u30c8\u95a2\u4fc2\u306e\u30e1\u30bd\u30c3\u30c9\u7fa4
    public String getIndent(){
        String result = "";
        for(int i = 0; i < indent; i++){
            result += " ";
        }
        return result;
    }
    public void incrementIndent(){
        indent += indentVolume;
    }
    public void decrementIndent(){
        indent -= indentVolume;
    }

    public void mouseClicked(MouseEvent e) {
        //\u7279\u306b\u4f55\u3082\u3057\u306a\u3044\u3002\u5fc5\u8981\u306a\u3089\u30b5\u30d6\u30af\u30e9\u30b9\u3067\u5b9a\u7fa9\u3057\u3066\u306d
    }
    public int getFillColor(){
        return fillColor;
    }
    public void moveTo(int x, int y){
        this.x = x; this.y = y;
    }
    public void miniature(){
        isMiniature = true;
        pWidth      = PApplet.parseInt(pWidth * RATE);
        pHeight     = PApplet.parseInt(pHeight * RATE);
    }
    public void unminiature(){
        isMiniature = false;
        pWidth      = PApplet.parseInt(pWidth/RATE);
        pHeight     = PApplet.parseInt(pHeight/RATE);
    }
}
public int getValue(String text, MyTextField gui){
    try{
        return new Lang(editor.getTokens(text)).getValue();
    }catch(UndefinedVariableException e){
        balloonList.remove(gui.balloon);
        gui.balloon = new Balloon("UndefinedVariableException : \n variable name : " + e.varName, gui);
        hasError = true;
        return 0;
    }catch(IndexOutOfBoundsException e){
        balloonList.remove(gui.balloon);
        gui.balloon = new Balloon("IndexOutOfBoundsException : \n" + e.getMessage(), gui);
        println(e.getStackTrace());
        hasError = true;
        return 0;
    }catch(ArithmeticException e){
        balloonList.remove(gui.balloon);
        gui.balloon = new Balloon("ArithmeticException:\n" + e.getMessage(), gui);
        hasError = true;
        return 0;
    }catch(IncorrectSyntaxException e){
        balloonList.remove(gui.balloon);
        gui.balloon = new Balloon("IncorrectSyntaxException", gui);
        hasError = true;
        return 0;
    }catch(Exception e){
        balloonList.remove(gui.balloon);
        gui.balloon = new Balloon("", gui);
        hasError = true;
        return 0;
    }
}
public int getValue(MyTextField gui){
    return getValue(gui.getText(), gui);
}
public String getStringValue(MyTextField txf){
    try{
        return new Lang(editor.getTokens(txf.getText())).getStringValue();
    }catch(IndexOutOfBoundsException e){
        balloonList.remove(txf.balloon);
        txf.balloon = new Balloon("IndexOutOfBoundsException:\n"+e.getMessage(),txf);
        hasError = true;
        return "";
    }catch(Exception e){
        balloonList.remove(txf.balloon);
        txf.balloon = new Balloon("Error", txf);
        hasError = true;
        return "";
    }
}
public Variable fetchVariableByName(String name) throws UndefinedVariableException{
    return variableTable.searchName(name);
}
public Variable fetchVariableByName(MyTextField txf){
    try{
        return fetchVariableByName(txf.getText());
    }catch(UndefinedVariableException e){
        return null;
    }
}
public void updateVariableValue(String name, String content) throws UndefinedVariableException {
    variableTable.updateVariable(name, content);
}
public void updateVariableValue(MyTextField txf, String content){
    try{
        updateVariableValue(txf.getText(), content);
    }catch(UndefinedVariableException e){
        txf.balloon = new Balloon("UndefinedVariableException:\n" + e.varName, txf);
        hasError = true;
    }
}
public abstract class WallPlate extends Plate {
    ArrayList<Plate> loopOpes = new ArrayList<Plate>();
    int wallPlateWidth  = 30;   //\u56f2\u307f\u30bf\u30a4\u30eb\u306e\u4e0a\u306e\u90e8\u5206\u306e\u5e45
    int wallPlateHeight = 30;   //\u56f2\u307f\u30bf\u30a4\u30eb\u306e\u4e0a\u306e\u90e8\u5206\u306e\u9ad8\u3055
    int wallPlateHeightBottom = 30;
    Balloon balloon;
    public void removePlate(Plate plate) {
        loopOpes.remove(plate);
    }
    public void resize(int addX, int addY) {
        pWidth += addX;
        pHeight += addY;
        if (upperPlate != null) {
            addX = this.pWidth + wallPlateWidth - upperPlate.pWidth;
            if(addX < 0) addX = 0;
            upperPlate.resize(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(0, addY);
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
    public boolean isPlateIn(Plate stm) {
        if (x  <= stm.x && stm.x <= x+pWidth +wallPlateWidth && y  <= stm.y && stm.y <= y + pHeight - wallPlateHeightBottom) {
            return true;
        }
        return false;
    }
    public boolean isMouseOver() {
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
    public void cancelLink(Plate plate){
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
        if(plate.nextPlate != null) cancelLink(plate.nextPlate);
        if(this == setupPlate) canSetupExecute = true;
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
    public void miniature(){
        super.miniature();
        wallPlateWidth          = PApplet.parseInt(wallPlateWidth * RATE);
        wallPlateHeight         = PApplet.parseInt(wallPlateHeight * RATE);
        wallPlateHeightBottom   = PApplet.parseInt(wallPlateHeightBottom * RATE);
        int x = this.x + wallPlateWidth;
        int y = this.y + wallPlateHeight;
        for(Plate plate : loopOpes){
            plate.moveTo(x,y);
            plate.miniature();
            y += plate.pHeight;
        }
    }
    public void unminiature(){
        super.unminiature();
        wallPlateWidth = PApplet.parseInt(wallPlateWidth / RATE);
        wallPlateHeight         = PApplet.parseInt(wallPlateHeight / RATE);
        wallPlateHeightBottom   = PApplet.parseInt(wallPlateHeightBottom / RATE);
        int x = this.x + wallPlateWidth;
        int y = this.y + wallPlateHeight;
        for(Plate plate : loopOpes){
            plate.moveTo(x,y);
            plate.unminiature();
            y += plate.pHeight;
        }
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

HashMap<String, int[]> argTypeList;
StringList methodNameList = new StringList();
public void initArgTypeList(){
    argTypeList = new HashMap<String, int[]>();
    // Integer[] a = {Enum.INT, Enum.INT, Enum.INT, Enum.INT};
    // argTypeList.put("rect", a);
    // argTypeList.put("ellipse", a);
    // argTypeList.put("line", a);
    // Integer[] b = {Enum.INT, Enum.INT, Enum.INT};
    // argTypeList.put("fill", b);
    // argTypeList.put("background", b);
    // argTypeList.put("stroke", b);
    // Integer[] c = {Enum.STRING};
    // argTypeList.put("println", c);
    // Integer[] d = {Enum.INT};
    // argTypeList.put("textSize", d);
    // Integer[] e = {Enum.STRING, Enum.INT, Enum.INT};
    // argTypeList.put("text", e);

    BufferedReader reader = createReader("methodList.txt");
    String line;
    int id = 0;
    while(true){
        try{
            line = reader.readLine();
            if(line == null) break;
            String[] aaa = line.split(":");
            String name = aaa[0];
            if(aaa.length > 1){
                String[] bbb = aaa[1].split(",");
                IntList typeList = new IntList();
                boolean isOK = true;
                for(String typeWord : bbb){
                    if(typeWord.equals("java.lang.String")) typeWord = "String";
                    int type = editor.getToken(typeWord);
                    if(type == Enum.OTHER){
                        isOK = false;
                        break;
                    }
                    typeList.append(type);
                }
                if(isOK){
                    if(argTypeList.containsKey(name)){
                        id++;
                    }
                    else{
                        id = 0;
                    }
                    name = name + id;
                    argTypeList.put(name, typeList.array());
                    methodNameList.append(name);
                }
            }else{
                argTypeList.put(name, new int[0]);
                methodNameList.append(name);
            }
        }catch(IOException e){
            println("no");
        }
    }
    try{
        reader.close();
    }catch(IOException e){
        println(e.getMessage());
    }
}
HashMap<String, String[]> argValueList;
public void initArgValueList(){
    argValueList = new HashMap<String, String[]>();
    argValueList.put("rect", new String[]{"400","200","300","200"});
    argValueList.put("ellipse", new String[]{"400","200","150","150"});
    argValueList.put("line", new String[]{"300","200","500","400"});
    argValueList.put("fill", new String[]{"46", "204", "113"});
    argValueList.put("background", new String[]{"52", "152", "219"});
    argValueList.put("stroke", new String[]{"243", "156", "18"});
    argValueList.put("textSize", new String[]{"32"});
    argValueList.put("text", new String[]{"\"text\"", "300","300"});
}
class StatementPlate extends Plate {
    private MyComboBox comboBox;  //\u547d\u4ee4\u306e\u9078\u629e\u6b04
    ArrayList<MyTextField> textFields = new ArrayList<MyTextField>();
    ConditionPlate condPlate;
    private int txfInterval = 10;
    private int comboBoxWidth = 0;
    private int comboBoxX = 10;
    StatementPlate(String methodName, int x, int y, String[] textFieldContents) {//\u8ffd\u52a0\u3059\u308b\u3068\u304d\u306f\u7de8\u96c6\u3088\u308d\u3057\u304f
        this.x = x;
        this.y = y;
        pWidth = 20;
        pHeight = 30;
        fillColor = peterRiver;
        String[] stmItems = {
            "rect", "ellipse", "fill", "background", "noStroke", "stroke", "line",  "println", "text", "textSize"
        };
        comboBox = new MyComboBox(methodNameList.array(), x + comboBoxX, y + 5, 70, 20);
        comboBox.setItem(methodName);
        comboBoxWidth = comboBox.getWidth();
        pWidth += comboBoxWidth;
        changeArraySizeByComboBox();
        setTextFieldContents(textFieldContents);
        setTextFieldPosition();
    }
    StatementPlate(int x, int y) {//\u8ffd\u52a0\u3059\u308b\u3068\u304d\u306f\u3053\u3053\u3082\u7de8\u96c6
        this.x = x;
        this.y = y;
        pWidth = 150;
        pHeight = 30;
        fillColor = peterRiver;
        String[] stmItems = {
            "rect", "ellipse", "line", "background", "stroke", "noStroke", "fill",  "println", "text", "textSize"
        };
        comboBox = new MyComboBox(methodNameList.array(), x + 10, y +5, 70, 20);
        changeArraySizeByComboBox();
    }
    String val = "";
    public void draw() {
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
        if(!isMiniature){
            comboBox.draw();
            for(int i = 0; i < textFields.size(); i++){
                textFields.get(i).draw();
            }
        }
    }
    public void execute(){//\u5b9f\u884c\u90e8\u306f\u3053\u3053\u306b\u8ffd\u52a0\u3059\u308b
        String comboboxItem = comboBox.getItem();
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            try{
                int[] typeList = argTypeList.get(comboboxItem);
                Class[] a = new Class[typeList.length];
                for(int i = 0; i < typeList.length; i++){
                    int type = typeList[i];
                    if(type == Enum.INT) a[i] = int.class;
                    else if(type == Enum.FLOAT) a[i] = float.class;
                    else if(type == Enum.STRING) a[i] = String.class;
                    else if(type == Enum.BOOLEAN) a[i] = boolean.class;
                    else println("kaisyaku dekinai class error() in execute() of StatementPlate");
                }
                Object[] b = new Object[typeList.length];
                for(int i = 0; i < typeList.length; i++){
                    int type = typeList[i];
                    MyTextField txf = textFields.get(i);
                    if(type == Enum.INT) b[i] = new Integer(getValue(txf));
                    else if(type == Enum.FLOAT) b[i] = new Float(getValue(txf));
                    else if(type == Enum.STRING) b[i] = new String(getStringValue(txf));
                    else if(type == Enum.BOOLEAN) println("boolean ha miteigi in execute() in StatementPlate");
                    else println("kaisyaku dekinai class error() in execute() of StatementPlate");
                }
                String method = deleteLastChar(comboboxItem);
                try{
                    instance.getClass().getMethod(method, a).invoke(instance, b);
                }catch(ReflectiveOperationException e){
                    println(e.getMessage());
                }
                // if(comboboxItem.equals("rect")){
                //     if(!isFillExisted) fill(255);
                //     int[] arg = getArg(textFields.size());
                //     rect(arg[0],arg[1],arg[2],arg[3]);
                // }else if(comboboxItem.equals("ellipse")){
                //     if(!isFillExisted) fill(255);
                //     int[] arg = getArg(textFields.size());
                //     ellipse(arg[0], arg[1], arg[2], arg[3]);
                // }else if(comboboxItem.equals("fill")){
                //     isFillExisted = true;
                //     int[] arg = getArg(textFields.size());
                //     fill(arg[0], arg[1], arg[2]);
                // }else if(comboboxItem.equals("noStroke")){
                //     noStroke();
                // }else if(comboboxItem.equals("stroke")){
                //     int[] arg = getArg(textFields.size());
                //     stroke(arg[0], arg[1], arg[2]);
                // }else if(comboboxItem.equals("background")){
                //     int[] arg = getArg(textFields.size());
                //     background(arg[0], arg[1], arg[2]);
                // }else if(comboboxItem.equals("println")){
                //     int[] arg = getArg(textFields.size());
                //     println(arg[0]);
                // }else if(comboboxItem.equals("text")){
                //     if(!isFillExisted) fill(0);
                //     String text = getStringValue(textFields.get(0));
                //     int[] arg = getArg(1,2);
                //     text(text, arg[0], arg[1]);
                // }else if(comboboxItem.equals("textSize")){
                //     int[] arg = getArg(textFields.size());
                //     textSize(arg[0]);
                // }else if(comboboxItem.equals("line")){
                //     int[] arg = getArg(textFields.size());
                //     line(arg[0], arg[1], arg[2], arg[3]);
                // }
            }catch(ArrayIndexOutOfBoundsException ex){
                println(ex.toString());
            }
            step++;
        }
    }
    private void setTextField(int count, int[] typeKind) {
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
    //\u3053\u306e\u30bf\u30a4\u30eb\u5168\u4f53\u306e\u4e2d\u8eab\u3092\u8a2d\u5b9a\u3059\u308b\u3068\u304d\u306b\u7528\u3044\u308b
    private void setTextFieldContents(String[] textFieldContents){
        println(textFields.size());
        for(int i = 0; i < textFieldContents.length; i++){
            textFields.get(i).setText(textFieldContents[i]);
        }
    }
    private void setTextFieldPosition(){
        if(isMiniature) return;
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
    public void changeArraySizeByComboBox(){//\u8ffd\u52a0\u3059\u308b\u3068\u304d\u306f\u3053\u3053\u3092\u7de8\u96c6\u3057\u3066\u306d
        String name = comboBox.getItem();
        int[] typeList = argTypeList.get(name);
        setTextField(typeList.length, typeList);

        // if(item.equals("rect")){
        //     setTextField(4, argTypeList.get("rect"));
        //     setTextFieldContents(argValueList.get("rect"));
        // }else if(item.equals("ellipse")){
        //     setTextField(4,argTypeList.get("ellipse"));
        //     setTextFieldContents(argValueList.get("ellipse"));
        // }else if(item.equals("fill")){
        //     setTextField(3, argTypeList.get("fill"));
        //     setTextFieldContents(argValueList.get("fill"));
        // }else if(item.equals("background")){
        //     setTextField(3, argTypeList.get("background"));
        //     setTextFieldContents(argValueList.get("background"));
        // }else if(item.equals("println")){
        //     setTextField(1, argTypeList.get("println"));
        // }else if(item.equals("text")){
        //     setTextField(3, argTypeList.get("text"));
        //     setTextFieldContents(argValueList.get("text"));
        // }else if(item.equals("textSize")){
        //     setTextField(1, argTypeList.get("textSize"));
        //     setTextFieldContents(argValueList.get("textSize"));
        // }else if(item.equals("line")){
        //     setTextField(4, argTypeList.get("line"));
        //     setTextFieldContents(argValueList.get("line"));
        // }else if(item.equals("noStroke")){
        //     setTextField(0, new int[0]);    //new Integer[0]\u306f\u9069\u5f53\u306b\u57cb\u3081\u305f\u3060\u3051\u3002\u306a\u305c\u306a\u3089\u7b2c1\u5f15\u6570\u304c0\u3067\u5f15\u6570\u304c\u306a\u3044\u304b\u3089
        // }else if(item.equals("stroke")){
        //     setTextField(3, argTypeList.get("stroke"));
        //     setTextFieldContents(argValueList.get("stroke"));
        // }
        // else {
        //     new Exception();
        // }
        setTextFieldPosition();
    }
    public void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void drawTransparent() {
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
    public boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public void shiftPosition(int addX, int addY) {
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        comboBox.x += addX;  //\u64cd\u4f5c\u5185\u306e\u30b3\u30f3\u30dc\u30dc\u30c3\u30af\u30b9\u3092\u79fb\u52d5
        comboBox.y += addY;
        setTextFieldPosition();
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public String getComboBoxText() {
        return comboBox.getItem();
    }
    public String getScript() {
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

ArrayList<DeclPlate> declPlateList = new ArrayList<DeclPlate>();
public DeclPlate getDeclPlateByName(String name){
    for(DeclPlate mp : declPlateList){
        if(mp.getVarName().equals(name)) return mp;
    }
    return null;
}
class DeclPlate extends Plate {

    int lshType;
    MyComboBox typeBox;
    MyTextField variableNameTxf;
    MyTextField valueTxf;
    private final int TXF_INTERVAL = 10;
    private int equalWidth = 24;
    ArrayList<VariablePlate> variablePlates = new ArrayList<VariablePlate>();

    DeclPlate(int lshType, int x, int y){
        this.x = x;
        this.y = y;
        this.pWidth     = originalStatementWidth;
        this.pHeight    = 30;
        this.lshType    = lshType;
        textSize(24);
        equalWidth  = PApplet.parseInt(textWidth("="));
        fillColor   = clouds;
        String[] typeItems = {"int", "String", "boolean"};
        typeBox = new MyComboBox(typeItems, x + MARGIN, y + 5, 70, 20);
        if(lshType == Enum.INT) typeBox.setItem("int");
        else if(lshType == Enum.STRING) typeBox.setItem("String");
        else if(lshType == Enum.BOOLEAN) typeBox.setItem("boolean");
        else println("undefined type : " + lshType + " in DeclPlate class Constructor");

        declPlateList.add(this);
    }
    DeclPlate(int lshType, int x, int y, String varName, String value){
        this(lshType, x, y);
        int totalTxfWidth = TXF_INTERVAL;
        variableNameTxf = new MyTextField(x + totalTxfWidth, y + txfPosY, varName);
        totalTxfWidth += variableNameTxf.getWidth() + TXF_INTERVAL + equalWidth + TXF_INTERVAL;
        valueTxf = new MyTextField(x + totalTxfWidth, y + txfPosY, value);
        totalTxfWidth += valueTxf.getWidth();
        pWidth += totalTxfWidth;
        fillColor = getColorByToken(lshType);
        valueTxf.setFillColor(colorDict.get(lshType));
        valueTxf.setKind(lshType);
        setGUIPosition();
    }
    public void execute(){
        String name     = variableNameTxf.getText();
        String content  = valueTxf.getText();
        Variable v      = fetchVariableByName(variableNameTxf);
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            if(v == null){
                if(lshType == Enum.INT){
                    int value = getValue(content, valueTxf);
                    content = "" + value;
                    variableTable.addVariable(new Variable(lshType, name, value, content));
                }else if(lshType == Enum.STRING){
                    String value = getStringValue(valueTxf);
                    content = value;
                    variableTable.addVariable(new Variable(lshType, name, value, content));
                }else{
                    println("error occurs in AssingPlate execute():\u578b\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093 => " + lshType);
                    new Exception("error occurs in AssingPlate execute():\u578b\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093 => " + lshType);
                }
            }else{
                if(v.kind != lshType) new Exception(); //\u3059\u3067\u306b\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u308b\u30a8\u30e9\u30fc\u3092\u51fa\u3055\u306a\u3044\u3068\u3044\u3051\u306a\u3044\u3002\u4fee\u6b63\u3057\u308d\u3088\u3002
                if(lshType == Enum.INT){
                    int value = getValue(content, valueTxf);
                    content = "" + value;
                    updateVariableValue(variableNameTxf, content);
                }else if(lshType == Enum.STRING){
                    String value = getStringValue(valueTxf);
                    content = value;
                    updateVariableValue(variableNameTxf, content);
                }
            }
            step++;
        }
    }
    public void draw(){
        if(executingPlate == this){
            setBorder();
        }else{
            noStroke();
        }
        fill(fillColor);
        rect(x, y, pWidth, pHeight, 10);
        checkGUIChange();
        if(!isMiniature){
            textFont(font);
            drawContents();
        }
    }
    public void drawContents(){
        textAlign(LEFT,CENTER);
        textSize(24);
        fill(0);
        text("=", x + typeBox.getWidth() + variableNameTxf.getWidth() + TXF_INTERVAL * 3, y+pHeight/2);
        typeBox.draw();
        variableNameTxf.draw();
        valueTxf.draw();
    }
    public void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    private void checkGUIChange(){
        boolean txfChange = false;
        if(variableNameTxf.checkChanged() || valueTxf.checkChanged()) txfChange = true;
        if(typeBox.checkChanged()){
            lshType = getType();
            fillColor = getColorByToken(lshType);
            valueTxf.setFillColor(colorDict.get(lshType));
            valueTxf.setKind(lshType);
            txfChange = true;
        }
        if(txfChange){
            isChange = true;
            setGUIPosition();
        }
    }
    public void drawTransparent(){
    }
    private void setGUIPosition(){
        int tmpx = x + TXF_INTERVAL;
        typeBox.moveTo(tmpx, y + txfPosY);
        tmpx += typeBox.getWidth() + TXF_INTERVAL;
        variableNameTxf.moveTo(tmpx, y + txfPosY);
        tmpx += variableNameTxf.getWidth() + TXF_INTERVAL + equalWidth + TXF_INTERVAL;
        valueTxf.moveTo(tmpx, y + txfPosY);
        tmpx += valueTxf.getWidth() + TXF_INTERVAL;
        if(!isMiniature) pWidth = tmpx - x;
    }
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (nextPlate != null) nextPlate.shiftPosition(addX, addY);
        typeBox.shiftPosition(addX, addY);
        variableNameTxf.shiftPos(addX, addY);
        valueTxf.shiftPos(addX, addY);
    }
    public boolean isMouseOver(){
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public String getVarName(){
        return variableNameTxf.getText();
    }
    public int getType(){
        String item = typeBox.getItem();
        if(item.equals("int")){
            return Enum.INT;
        }else if(item.equals("String")){
            return Enum.STRING;
        }else if(item.equals("boolean")){
            return Enum.BOOLEAN;
        }else{
            println("undefined type. declPlate getType");
            return Enum.OTHER;
        }
    }
    public String getScript(){
        StringBuilder result = new StringBuilder(getIndent());
        if(lshType == Enum.INT) {
            result.append("int ");
        }else if(lshType == Enum.STRING){
            result.append("String ");
        }else if(lshType == Enum.BOOLEAN){
            result.append("boolean ");
        }
        result.append(variableNameTxf.getText() + " = " + valueTxf.getText() + ";\n");
        return result.toString();
    }
    public String getNoIndentScript(){
        StringBuilder result = new StringBuilder();
        if(lshType == Enum.INT) {
            result.append("int ");
        }else if(lshType == Enum.STRING){
            result.append("String ");
        }
        result.append(variableNameTxf.getText() + " = " + valueTxf.getText() + ";");
        return result.toString();
    }
    public void mouseClicked(MouseEvent e){
        if (isMouseOver() && e.getClickCount() >= 2) {  //\u30c0\u30d6\u30eb\u30af\u30ea\u30c3\u30af\u306e\u5224\u5b9a\u3092\u884c\u3046
            plateList.add(new AssignmentPlate(this, x + pWidth + MARGIN, y + pHeight + MARGIN));
            // plateList.add(new VariablePlate(x + pWidth +  MARGIN, y + pHeight + MARGIN, variableNameTxf.getText(), lshType));
        }
    }
}


class AssignmentPlate extends Plate {

    DeclPlate declPlate;
    String name;
    MyTextField valueTxf;
    private int TXF_INTERVAL = 10;
    private int equalWidth = 24;
    ArrayList<VariablePlate> variablePlates = new ArrayList<VariablePlate>();
    Balloon balloon;

    AssignmentPlate(int x, int y, String name, String value){
        this.x = x;
        this.y = y;
        this.pWidth     = originalStatementWidth;
        this.pHeight    = 30;
        this.name = name;
        valueTxf = new MyTextField(x , y + txfPosY, value);
        declPlate = getDeclPlateByName(name);
        setGUIPosition();
    }

    AssignmentPlate(DeclPlate declPlate, int x, int y){
        this.x = x;
        this.y = y;
        this.pWidth     = originalStatementWidth;
        this.pHeight    = 30;
        this.declPlate  = declPlate;
        textSize(24);
        name = declPlate.getVarName();
        fillColor = declPlate.fillColor;
        equalWidth  = PApplet.parseInt(textWidth("="));
        valueTxf = new MyTextField(x , y + txfPosY, "");
        setGUIPosition();
    }
    public void execute(){
        String content  = valueTxf.getText();
        Variable v = null;
        try{
            v = fetchVariableByName(name);
        }catch(UndefinedVariableException e){
            balloon = new Balloon("UndefinedVariableException:\n" + name, x + MARGIN, y + MARGIN, x + pWidth/2, y + pHeight/2);
            hasError = true;
            return;
        }
        int type = declPlate.getType();
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            if(v.kind != type) new Exception(); //\u3059\u3067\u306b\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u308b\u30a8\u30e9\u30fc\u3092\u51fa\u3055\u306a\u3044\u3068\u3044\u3051\u306a\u3044\u3002\u4fee\u6b63\u3057\u308d\u3088\u3002
            if(type == Enum.INT){
                int value = getValue(content, valueTxf);
                content = "" + value;
                try{
                    updateVariableValue(name, content);
                }catch(UndefinedVariableException e){
                    balloon = new Balloon("UndefinedVariableException:\n" + name, x + MARGIN, y + MARGIN, x + pWidth/2, y + pHeight/2);
                    hasError = true;
                    return;
                }
            }else if(type == Enum.STRING){
                String value = getStringValue(valueTxf);
                content = value;
                try{
                    updateVariableValue(name, content);
                }catch(UndefinedVariableException e){
                    balloon = new Balloon("UndefinedVariableException:\n" + name, x + MARGIN, y + MARGIN, x + pWidth/2, y + pHeight/2);
                    hasError = true;
                    return;
                }
            }

            step++;
        }
    }
    public void draw(){
        if(executingPlate == this){
            setBorder();
        }else{
            noStroke();
        }

        textFont(font);
        fill(fillColor);
        rect(x, y, pWidth, pHeight, 10);
        if(!isMiniature){
            checkGUIChange();
            drawContents();
        }
    }
    public void drawContents(){
        textAlign(LEFT,CENTER);
        textFont(font);
        fill(0);
        text(name, x + TXF_INTERVAL, y + pHeight/2);
        text("=", x + PApplet.parseInt(textWidth(name)) + TXF_INTERVAL * 2, y+pHeight/2);
        valueTxf.draw();
    }
    public void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void drawTransparent(){
    }
    private void checkGUIChange(){
        if(declPlate != null){
            name = declPlate.getVarName();
            fillColor = declPlate.fillColor;
        }else if(balloon == null){
            declPlate = getDeclPlateByName(name);
            balloon = new Balloon(name + " is undefined.", x + MARGIN, y + MARGIN, x + pWidth/2, y + pHeight/2);
        }
        if(valueTxf.checkChanged()){
            isChange = true;
        }
        setGUIPosition();
    }
    private void setGUIPosition(){
        int tmpx = x + TXF_INTERVAL + PApplet.parseInt(textWidth(name)) + TXF_INTERVAL + equalWidth + TXF_INTERVAL;
        valueTxf.moveTo(tmpx, y + txfPosY);
        tmpx += valueTxf.getWidth() + TXF_INTERVAL;
        if(!isMiniature) pWidth = tmpx - x;
    }
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
        valueTxf.shiftPos(addX, addY);
    }
    public boolean isMouseOver(){
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public String getScript(){
        StringBuilder result = new StringBuilder(getIndent());
        result.append(name + " = " + valueTxf.getText() + ";\n");
        return result.toString();
    }
    public String getNoIndentScript(){
        StringBuilder result = new StringBuilder();
        result.append(name + " = " + valueTxf.getText() + ";");
        return result.toString();
    }
    public void mouseClicked(MouseEvent e) {
        // if (isMouseOver() && e.getClickCount() >= 2) {  //\u30c0\u30d6\u30eb\u30af\u30ea\u30c3\u30af\u306e\u5224\u5b9a\u3092\u884c\u3046
        //     plateList.add(new VariablePlate(x + pWidth +  MARGIN, y + pHeight + MARGIN, variableNameTxf.getText(), lshType));
        // }
    }
}
class ForPlate extends WallPlate{
    private Plate firstPlate;
    private Plate lastPlate;
    private ConditionPlate cond;
    Balloon balloon;
    ForPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+wallPlateHeight+MARGIN + 40;
        isWallPlate = true;
        wallPlateHeight = 30 * 2 + MARGIN;
        wallPlateHeightBottom = 30;
        fillColor = color(179, 204, 87);
        //\u547d\u4ee4\u306e\u6307\u5b9a\u304c\u306a\u3044\u306e\u3067\u6587\u3092\u9069\u5f53\u306b\u633f\u5165\u3059\u308b
        firstPlate  = new DeclPlate(Enum.INT, x + wallPlateWidth + MARGIN, y + MARGIN/2, "i", "0");
        lastPlate   = new DeclPlate(Enum.INT, x + wallPlateWidth + MARGIN, y + pHeight - wallPlateHeightBottom, "i", "i+1");
        cond        = new ConditionPlate(x + wallPlateWidth + MARGIN, y + 30 + MARGIN, "<", "i", "10");
    }
    ForPlate(int x, int y, Plate firstPlate, Plate lastPlate, ConditionPlate cond){
        this(x, y);
        this.firstPlate = firstPlate;
        this.lastPlate  = lastPlate;
        this.cond       = cond;
        this.firstPlate.shiftPosition(wallPlateWidth + MARGIN, MARGIN/2);
        this.lastPlate.shiftPosition(wallPlateWidth + MARGIN, pHeight - wallPlateHeightBottom);
        this.cond.shiftPosition(x + wallPlateWidth + MARGIN, y + 30 + MARGIN);
    }
    public void draw(){
        updateWidth();
        noStroke();
        if(executingPlate == this) setBorder();
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight - wallPlateHeightBottom, pWidth, wallPlateHeightBottom, 10);
        if(!isMiniature){
            fill(0);
            textAlign(LEFT,TOP);
            textFont(font);
            text("for", x + MARGIN, y + MARGIN);
            firstPlate.draw();
            lastPlate.draw();
            cond.draw();
        }
    }
    public void drawShadow(){
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
    public void drawTransparent(){
    }
    public void resize(int addX, int addY){
        super.resize(addX, addY);
        lastPlate.shiftPosition(0, addY);
    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        firstPlate.shiftPosition(addX, addY);
        lastPlate.shiftPosition(addX, addY);
        cond.shiftPosition(addX, addY);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
        if(balloon != null) balloon.shiftPos(addX,addY);
    }
    public void execute(){
        firstPlate.execute();
        while(cond.getCondition() && checkStepCount(this)){
            if(hasExecuteEnd)   return;
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    if(hasExecuteEnd)   return;
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
            lastPlate.execute();
        }
    }
    public String getScript(){
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
public boolean checkStepCount(WallPlate p){
    if(step < MAX_STEP_COUNT){
        return true;
    }else{
        p.balloon = new Balloon("Error: step count is over " + MAX_STEP_COUNT +".", p.x + p.pWidth + MARGIN, p.y + p.wallPlateHeight+ MARGIN, p.x+ MARGIN, p.y + MARGIN);
        hasError = true;
        return false;
    }
}

public String deleteLastChar(String word){
    return word.substring(0,word.length()-1);
}

public MethodPlate getMethodPlateByName(String name){
    for(MethodPlate mp : methodPlateList){
        if(mp.getName().equals(name)) return mp;
    }
    return null;
}
//\u95a2\u6570\u547c\u3073\u51fa\u3057\u306e\u65b9
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
    public void execute(){
        methodPlate.execute();
    }
    public void draw() {
        if(methodPlate == null){
            methodPlate = getMethodPlateByName(name);
            balloonList.remove(balloon);
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
    public void drawShadow() {
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
    }
    public void drawTransparent() {
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
    public void checkVar() {
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
    public void addVar() {
        int argSize = args.size();
        args.add(new MyTextField(x+argSize*(argWidth+10)+135 + 10, y + 5));
        pWidth += argWidth +10;
    }
    public void removeVar() {
        int argSize = args.size();
        if (argSize > 0) {
            args.remove(argSize-1);
            pWidth -= argWidth +10;
        }
    }
    private void setTextFieldPosition(){
        int textwidth = PApplet.parseInt(textWidth(name));
        pWidth = MARGIN + textwidth + MARGIN;
        int tmpx = x + pWidth;
        for(int i = 0; i < args.size(); i++){
            MyTextField arg = args.get(i);
            arg.moveTo(tmpx , y + txfPosY);
            tmpx += arg.getWidth() + MARGIN;
            pWidth += arg.getWidth() + MARGIN;
        }
    }
    public String[] getArgNames() {
        String[] names = new String[args.size()];
        for (int i = 0; i < names.length; i++) {
            String txt = args.get(i).getText().trim();
            names[i] = txt;
        }
        return names;
    }
    public void shiftPosition(int addX, int addY) {
        x += addX;
        y += addY;
        setTextFieldPosition();
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public boolean isPlateBelow(Plate plate) {
        if (abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
            return true;
        }
        return false;
    }
    public String getScript() {
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
    public void linkPlate() {
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
    public void execute(){
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
    public void draw(){
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
        if(!isMiniature){
            stroke(0);
            fill(0);
            textSize(18);
            textFont(font);
            textAlign(LEFT,TOP);
            text("setup", x+10, y+5);
        }
    }
    public void drawShadow(){
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
    public void drawTransparent(){
    }
    public void shiftPosition(int addX, int addY) {
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public String getScript() {
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
//\u95a2\u6570\u5b9a\u7fa9\u306e\u65b9
class MethodPlate extends WallPlate {
    MyComboBox typeBox;
    MyTextField methodNameTxf;
    MyButton addVarButton;
    MyButton removeVarButton;
    ArrayList<Integer> types    = new ArrayList<Integer>();
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
            addVar();    //\u5f15\u6570\u8ffd\u52a0
            args.get(i).setText(argNames[i]);    //\u5f15\u6570\u306e\u4e2d\u8eab\u3092\u4ee3\u5165
            args.get(i).setFillColor(colorDict.get(argTypes[i]));   //\u5f15\u6570\u306b\u5fdc\u3058\u3066\u8272\u3092\u5909\u66f4
            args.get(i).setKind(argTypes[i]);
        }
        setTextFieldPosition();
        isWallPlate = true;
    }
    public void execute(ArrayList<MyTextField> argumentList){
        for(int i = 0; i < args.size(); i++){
            String name = args.get(i).getText();
            int type = types.get(i);
            MyTextField valueTxf = argumentList.get(i);
            String content = "";
            if(type == Enum.INT){
                int value = getValue(valueTxf.getText(), valueTxf);
                content = "" + value;
                variableTable.addVariable(new Variable(type, name, value, content));
            }else if(type == Enum.STRING){
                String value = getStringValue(valueTxf);
                content = value;
                variableTable.addVariable(new Variable(type, name, value, content));
            }else if(type == Enum.BOOLEAN){

            }else{
                println("Type is not defined. in MethodPlate execute()");
            }
        }
    }
    public void execute(){
        if(!loopOpes.isEmpty()){
            Plate p = loopOpes.get(0);
            do{
                if(hasExecuteEnd) return;
                p.execute();
                p = p.nextPlate;
            }while(p != null);
        }
    }
    public void setMethod(String name){
        for(Method method : methodList){
            if(method.name.equals(name)){
                method.methodPlate = this;
            }
        }
    }
    public void draw() {

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
        if(!isMiniature) pWidth = tmp;
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
    public int getAllWidth(){
        int result = MARGIN + typeBox.getWidth() + MARGIN + methodNameTxf.getWidth() + MARGIN;
        for(int i = 0; i < args.size(); i++){
            result += args.get(i).getWidth() + MARGIN;
        }
        return result;
    }
    public void addVar() {
        MyTextField arg = new MyTextField(x + this.getAllWidth(), y + 5);
        args.add(arg);
        removeVarButton.moveTo(x + this.getAllWidth(), y+5);
        addVarButton.moveTo(x + this.getAllWidth(), y+20);
        pWidth += arg.getWidth() + txfInterval;
    }
    public void removeVar() {
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
    public void drawShadow() {
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
    public void drawTransparent() {
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
    public void shiftPosition(int addX, int addY) {
        x += addX;
        y += addY;
        typeBox.moveTo(x + MARGIN, y + 5);
        setTextFieldPosition();
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public boolean isPlateBelow(Plate plate) {
        if (abs(y + pHeight - plate.y) <= marginY && abs(x - plate.x) <= marginX) {
            return true;
        }
        return false;
    }
    public String getScript() {
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
        if(loopOpes.size() > 0){    //\u547d\u4ee4\u304c\u3042\u308c\u3070\u305d\u306e\u547d\u4ee4\u3092\u8ffd\u52a0
            Plate plate = this.loopOpes.get(0);
            while(plate != null){
                result.append(plate.getScript());
                plate = plate.nextPlate;
            }
        } else {    //\u547d\u4ee4\u304c\u306a\u3051\u308c\u3070\u7a7a\u884c\u30921\u884c\u3060\u3051\u30bd\u30fc\u30b9\u30b3\u30fc\u30c9\u30d6\u30ed\u30c3\u30af\u5185\u306b\u633f\u5165\u3059\u308b
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append("}\n");
        return result.toString();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //\u30c0\u30d6\u30eb\u30af\u30ea\u30c3\u30af\u306e\u5224\u5b9a\u3092\u884c\u3046
            plateList.add(new Method(x+50, y+50, this));
        }
    }
    public String getName(){
        return methodNameTxf.getText();
    }
}
class ConditionPlate extends Plate {
    MyComboBox comboBox;  //\uff1c\u3001\uff1d\u3001\uff1e\u306e\u9078\u629e\u6b04
    MyTextField txf1;       //\u5024\u306e\u8a18\u5165\u6b04
    MyTextField txf2;
    IfCondPlate plate;
    int state = -1; //-1\u306e\u72b6\u6cc1\u306f\u6761\u4ef6\u5f0f\u306e\u5224\u5b9a\u524d
    boolean isBoolean       = false;
    final int START_MARGIN  = 5;
    final int MARGIN        = 10;
    final int COMBOBOX_Y    = 3;
    final int TXF_POS_Y     = 3;
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
        type = Enum.BOOLEAN;
    }
    ConditionPlate(int x, int y, String enzanshi, String lh, String rh){
        this(x,y);
        comboBox.setItem(enzanshi);
        txf1.setText(lh);
        txf2.setText(rh);
    }
    String leftVal;
    public void execute(){
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
                state = Enum.FALSE;    //TODO:\u3046\u3081\u308b
            }
            step++;
        }
    }
    public void draw() {
        noStroke();
        if(executingPlate == this) setBorder();
        if(state == -1) fill(0xffE0E4CC);
        else if(state == Enum.FALSE) fill(getColorByToken(Enum.FALSE));
        else if(state == Enum.TRUE) fill(getColorByToken(Enum.TRUE));

        rect(x, y, pWidth, pHeight, 10);
        comboBox.draw();
        txf1.draw();
        if (txf2 != null) {
            txf2.draw();
        }
        checkGUIChange();
        state = -1;
    }
    public void drawShadow() {
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
    }
    public void drawTransparent() {
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
                txf1.setFillColor(0xffecf0f1);
                isBoolean = false;
            }
        }
        if(txfChange){
            isChange = true;
            setTextFieldPosition();
        }
    }
    public void setBooleanTrue(){
        txf2 = null;
        txf1.setFillColor(colorDict.get(Enum.BOOLEAN));
        isBoolean = true;
    }
    public void shiftPosition(int addX, int addY) {
        x += addX;  //\u64cd\u4f5c\u81ea\u4f53\u79fb\u52d5
        y += addY ;
        setTextFieldPosition();
        if (plate != null) {
            plate.shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public boolean isMouseOver() {
        if (x <= mouseX && mouseX <= x + pWidth && y <= mouseY && mouseY <= y + pHeight) {
            return true;
        }
        return false;
    }
    public String getScript() {
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
class LogicalOpePlate extends ConditionPlate {
    Plate leftCond;
    Plate rightCond;
    MyComboBox operationBox;
    static final int SPACE_WIDTH = 30;
    static final int SPACE_HEIGHT = 20;
	LogicalOpePlate(int x, int y){
        super(x,y);
		this.x = x;
		this.y = y;
		pHeight = 30;
		fillColor = color(0xffE0E4CC);//\u597d\u304d\u306a\u8272\u3092\u57cb\u3081\u308b
        String[] opeItems = {
            "&&", "||", "!"
        };
        operationBox = new MyComboBox(opeItems, x + 10, y +5, 70, 20);
        operationBox.setItem(opeItems[0]);
        setGUIPosition();
        isLogicalOpePlate = true;
	}
    public void draw(){
		if(executingPlate == this){
            setBorder();
        }  else {
            noStroke();
        }
		fill(fillColor);
		rect(x,y,pWidth,pHeight,10);
        if(leftCond != null){
            leftCond.draw();
        }else{
            fill(255);
            noStroke();
            rect(leftHandX,y+5, SPACE_WIDTH, SPACE_HEIGHT,10);
        }
        if(rightCond != null){
            rightCond.draw();
        }else{
            fill(255);
            noStroke();
            rect(rightHandX,y+5, SPACE_WIDTH, SPACE_HEIGHT,10);
        }
        operationBox.draw();
    }
    public void drawShadow(){
		noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void drawTransparent(){
    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if(leftCond != null) leftCond.shiftPosition(addX,addY);
        else leftHandX += addX;
        if(rightCond != null) rightCond.shiftPosition(addX,addY);
        else rightHandX += addX;

        operationBox.shiftPosition(addX, addY);
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public void execute(){

    }
    int leftHandX = 0;
    int rightHandX = 0;
    public void setGUIPosition(){
        pWidth = START_MARGIN;
        int tmpx = x + pWidth;

        if(leftCond != null) tmpx += leftCond.pWidth + MARGIN;
        else {
            leftHandX = tmpx;
            tmpx += SPACE_WIDTH + MARGIN;
        }
        operationBox.moveTo(tmpx, y + TXF_POS_Y);
        tmpx += operationBox.getWidth() + MARGIN;

        if(rightCond != null){
            rightCond.moveTo(tmpx, y);
            tmpx += rightCond.pWidth + START_MARGIN;
        }else{
            rightHandX = tmpx;
            tmpx += SPACE_WIDTH + START_MARGIN;
        }
        pWidth = tmpx - x;
    }
    public void insertPlate(Plate p){
        if(p.type == Enum.BOOLEAN){
            if(mouseX < x + pWidth/2 && leftCond == null){
                leftCond = p;
                leftCond.moveTo(leftHandX, y);
            }
            if(mouseX > x + pWidth/2 && rightCond == null){
                rightCond = p;
                rightCond.moveTo(rightHandX, y);
            }
            setGUIPosition();
            p.z += this.z + 1;
            p.oyaPlate = this;
        }
    }
    public void moveTo(int x, int y){
        super.moveTo(x,y);
        setGUIPosition();
    }
    public String getScript(){
		StringBuilder result = new StringBuilder();
        if(leftCond != null)result.append(leftCond.getScript());
        result.append(" " + operationBox.getItem() + " ");
        if(rightCond != null)result.append(rightCond.getScript());
		return result.toString();
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
    public void execute(){
        if(cond.getCondition() && !loopOpes.isEmpty()){
            if(hasExecuteEnd) return;
            Plate p = loopOpes.get(0);
            do{
                p.execute();
                p = p.nextPlate;
            }while(p != null);
        }
    }
    public void draw() {
        noStroke();
        fill(fillColor);
        rect(x, y, pWidth, 30, 10);
        rect(x, y, 30, pHeight, 10);
        rect(x, y+pHeight-30, pWidth, 30, 10);
        if(!isMiniature){
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
    }
    private void setConditionPlate(ConditionPlate cp){
        cond = cp;
        cond.shiftPosition(x+ 35, y + 2);
    }
    private void updateByCondPlateWidth(){
        int tmpWidth = (cond.x + cond.pWidth + MARGIN) -(this.x);
        if(tmpWidth > originalLoopWidth) this.pWidth = tmpWidth;
    }
    public void drawShadow() {
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
    public void drawTransparent() {
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
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY;
        cond.shiftPosition(addX, addY);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public String getScript() {
        StringBuilder result = new StringBuilder();
        result.append(getIndent() + "if" + " ( " + cond.getScript() + " ) {\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){   //\u4f55\u3082\u547d\u4ee4\u304c\u306a\u3051\u308c\u3070\u7a7a\u884c\u3092\u8ffd\u52a0
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append(getIndent() + "}\n");
        return result.toString();
    }
    public void setPlateInDebugmode(){
    }
}

class WhilePlate extends WallPlate {
    private ConditionPlate cond;
    private final int MARGIN = 5;
    WhilePlate(int x, int y) {
        this.x = x;
        this.y = y;
        textFont(font);
        int textWidth = PApplet.parseInt(textWidth("while"));
        cond = new ConditionPlate(x + MARGIN + textWidth + MARGIN, y + 2);
        pWidth = originalLoopWidth;
        pHeight = 60 + 40;
        isWallPlate = true;
        fillColor = getColorByToken(Enum.WHILE);
    }
    public void execute(){
        while(cond.getCondition() && !loopOpes.isEmpty() && checkStepCount(this)){
            if(hasExecuteEnd) return;
            Plate p = loopOpes.get(0);
            do{
                p.execute();
                p = p.nextPlate;
            }while(p != null);
        }
    }
    public void draw() {

        noStroke();
        fill(fillColor);
        rect(x, y, pWidth, wallPlateHeight, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight - wallPlateHeightBottom, pWidth, wallPlateHeightBottom, 10);
        stroke(0);
        fill(0);
        textFont(font);
        textAlign(LEFT,TOP);
        text("while", x+MARGIN, y+5);
        cond.draw();
        cond.state = -1;
        updateByCondPlateWidth();
        updateWidth();
    }
    private void setConditionPlate(ConditionPlate cp){
        cond = cp;
        textFont(font);
        int textWidth = PApplet.parseInt(textWidth("while"));
        cond.shiftPosition(x+ MARGIN + textWidth + MARGIN, y + 2);
        println("pre  : " + wallPlateHeight);
        this.wallPlateHeight = cp.pHeight + 2 * 2;
        println("post : " + wallPlateHeight);
    }
    private void updateByCondPlateWidth(){
        int tmpWidth = (cond.x + cond.pWidth + MARGIN) -(this.x);
        if(tmpWidth > originalLoopWidth) this.pWidth = tmpWidth;
    }
    public void drawShadow() {
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
    public void drawTransparent() {
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
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY;
        cond.shiftPosition(addX, addY);
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public String getScript() {
        StringBuilder result = new StringBuilder();
        result.append(getIndent() + "while" + " (" + cond.getScript() + ") {\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){   //\u4f55\u3082\u547d\u4ee4\u304c\u306a\u3051\u308c\u3070\u7a7a\u884c\u3092\u8ffd\u52a0
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append(getIndent() + "}\n");
        return result.toString();
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
        CONST_RESULT_TEXT_WIDTH = PApplet.parseInt(textWidth("return"));
        this.pWidth += MARGIN + CONST_RESULT_TEXT_WIDTH + MARGIN;
        txf = new MyTextField(0, 0, txfWidth, txfHeight);
        txf.moveTo(x + pWidth, y + txfPosY);
        this.pWidth += txf.getWidth() + MARGIN;
        this.pHeight = 30;
        fillColor = peterRiver;
    }
    public void draw(){
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
    public void drawShadow(){
    }
    public void drawTransparent(){
    }
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        txf.x += addX;
        txf.y += addY;
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public void execute(){
    }
    public String getScript(){
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
    public void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void draw(){
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
    public void shiftPosition(int addX, int addY){
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        setGUIPosition();
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
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
            new Exception("Error occurs in getType(String item); : \u672a\u5b9a\u7fa9\u306e\u578b\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u3059");
            return Enum.OTHER;
        }
    }
    public String getArrayName(){
        return nameTxf.getText();
    }
    public void mouseClicked(MouseEvent e) {
        if (isMouseOver() && e.getClickCount() >= 2) {  //\u30c0\u30d6\u30eb\u30af\u30ea\u30c3\u30af\u306e\u5224\u5b9a\u3092\u884c\u3046
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
            println("Error : \u8a72\u5f53\u3059\u308b\u578b\u304c\u3042\u308a\u307e\u305b\u3093 -> " + type);
            new Exception();
        }
        setGUIPosition();
    }
    public void drawTransparent(){
    }
    public void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name     = nameTxf.getText();
            String content  = lengthTxf.getText();
            Variable v      = fetchVariableByName(nameTxf);
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
                if(v.kind != type) new Exception(); //\u3059\u3067\u306b\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u308b\u30a8\u30e9\u30fc\u3092\u51fa\u3055\u306a\u3044\u3068\u3044\u3051\u306a\u3044\u3002\u4fee\u6b63\u3057\u308d\u3088\u3002
                ((CompositeVariable)fetchVariableByName(nameTxf)).initArray(length);
            }
            step++;
        }

    }
    public void drawGUI(){
        final int MARGIN_Y = pHeight/2;
        int tmp = MARGIN;
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        text("type:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("type:")) + MARGIN;
        tmp += typeBox.getWidth() + MARGIN;
        text("name:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("name:")) + MARGIN;
        tmp += nameTxf.getWidth() + MARGIN;
        text("length:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("length:")) + MARGIN;
        typeBox.draw();
        nameTxf.draw();
        lengthTxf.draw();
    }
    public void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        tmp += PApplet.parseInt(textWidth("type:")) + MARGIN;
        typeBox.moveTo(x + tmp, y + 5);
        tmp += typeBox.getWidth() + MARGIN;
        tmp += PApplet.parseInt(textWidth("name:")) + MARGIN;
        nameTxf.moveTo(x + tmp, y + txfPosY);
        tmp += nameTxf.getWidth() + MARGIN;
        tmp += PApplet.parseInt(textWidth("length:")) + MARGIN;
        lengthTxf.moveTo(x + tmp, y + txfPosY);
        tmp += lengthTxf.getWidth() + MARGIN;
        pWidth = tmp;
    }
    public void checkGUIChange(){
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
    public String getScript(){
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
            new Exception("Error : \u8a72\u5f53\u3059\u308b\u578b\u304c\u3042\u308a\u307e\u305b\u3093 -> " + type);
        }
        fillColor = getColorByToken(getType());

        for(String content : contents){
            elements.add(new MyTextField(content, 0,0,txfWidth,txfHeight));
        }
        setGUIPosition();
    }
    public void drawTransparent(){
    }
    public void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name     = nameTxf.getText();
            Variable v      = fetchVariableByName(nameTxf);
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
                        contents.add(getStringValue(elements.get(i)));
                    }
                    variableTable.addVariable(new CompositeVariable(type, name, contents));
                }else if(type == Enum.BOOLEAN_ARRAY){
                    new Exception("\u672a\u5b9f\u88c5");
                    // variableTable.addVariable(new CompositeVariable(type, name, length, true));
                }else{
                    new Exception();
                }
            }else{
                if(v.kind != type) new Exception(); //\u3059\u3067\u306b\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u308b\u30a8\u30e9\u30fc\u3092\u51fa\u3055\u306a\u3044\u3068\u3044\u3051\u306a\u3044\u3002\u4fee\u6b63\u3057\u308d\u3088\u3002
                if(type == Enum.INT_ARRAY){
                    ArrayList<Integer> contents = new ArrayList<Integer>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getValue(elements.get(i).getText(), elements.get(i)));
                    }
                    ((CompositeVariable)v).setElements(contents);
                }else if(type == Enum.STRING_ARRAY){
                    ArrayList<String> contents = new ArrayList<String>();
                    for(int i = 0; i < elements.size(); i++){
                        contents.add(getStringValue(elements.get(i)));
                    }
                    ((CompositeVariable)v).setElements(contents);
                }else if(type == Enum.BOOLEAN_ARRAY){
                    new Exception("\u672a\u5b9f\u88c5");
                    // variableTable.addVariable(new CompositeVariable(type, name, length, true));
                }else{
                    new Exception();
                }
            }
            step++;
        }

    }
    public void drawGUI(){
        final int MARGIN_Y = pHeight/2;
        int tmp = MARGIN;
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        text("type:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("type:")) + MARGIN/2;
        tmp += typeBox.getWidth() + MARGIN/2;
        text("name:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("name:")) + MARGIN/2;
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
    public void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        tmp += PApplet.parseInt(textWidth("type:")) + MARGIN/2;
        typeBox.moveTo(x + tmp, y + 5);
        tmp += typeBox.getWidth() + MARGIN/2;
        tmp += PApplet.parseInt(textWidth("name:")) + MARGIN/2;
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
            tmp += PApplet.parseInt(textWidth("...")) + MARGIN/2;
        }
        pWidth = tmp;
    }
    public void checkGUIChange(){
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
    public String getScript(){
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
        if (isMouseOver() && e.getClickCount() >= 2) {  //\u30c0\u30d6\u30eb\u30af\u30ea\u30c3\u30af\u306e\u5224\u5b9a\u3092\u884c\u3046
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
		fillColor   = alizarin;   //\u3069\u3046\u305b\u3042\u3068\u3067\u578b\u306e\u7a2e\u985e\u306b\u3088\u3063\u3066\u8272\u306f\u5909\u66f4\u3055\u308c\u308b
        indexTxf    = new MyTextField(0, 0, txfWidth, txfHeight);
        indexTxf.setFillColor(colorDict.get(Enum.INT));
        indexTxf.setKind(Enum.INT);
        contentTxf  = new MyTextField(0, 0, txfWidth, txfHeight);
        setGUIPosition();
	}
    public void draw(){
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
    public void drawShadow(){
		noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
        drawGUI();
    }
    public void drawTransparent(){
    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        setGUIPosition();
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public void execute(){
        if(isDebugMode&& step == counter){
            hasExecuteEnd = true;
            executingPlate = this;
        }
        if(!isDebugMode ||(isDebugMode && step <= counter)){
            String name    = arrayPlate.nameTxf.getText();
            int index      = getValue(indexTxf.getText(), indexTxf);
            int type       = arrayPlate.getType();
            String content = contentTxf.getText();
            CompositeVariable array;
            try{
                array = (CompositeVariable)(fetchVariableByName(name));
            }catch(UndefinedVariableException e){
                balloon = new Balloon("UndefinedVariableException:\n" + name, x + MARGIN, y + MARGIN, x + pWidth/2, y + pHeight/2);
                hasError = true;
                return;
            }
            if(array.kind != type) new Exception(); //\u3059\u3067\u306b\u5b9a\u7fa9\u3055\u308c\u3066\u3044\u308b\u30a8\u30e9\u30fc\u3092\u51fa\u3055\u306a\u3044\u3068\u3044\u3051\u306a\u3044\u3002\u4fee\u6b63\u3057\u308d\u3088\u3002
            if(type == Enum.INT_ARRAY){
                int value = getValue(content, contentTxf);
                array.set(index, value);
            }else if(type == Enum.STRING_ARRAY){
                String value = getStringValue(contentTxf);
                array.set(index, value);
            }else{
                new Exception("erro occurs in AssingPlate execute():\u578b\u60c5\u5831\u304c\u3042\u308a\u307e\u305b\u3093 => " + type);
            }
            step++;
        }
    }
    public void setGUIPosition(){
        textFont(font);
        int tmp = MARGIN;
        String name = arrayPlate.getArrayName();
        tmp += PApplet.parseInt(textWidth(name)) + MARGIN;
        tmp += PApplet.parseInt(textWidth("index:")) + MARGIN;
        indexTxf.moveTo(x + tmp, y + txfPosY);
        tmp += indexTxf.getWidth() + MARGIN;
        tmp += PApplet.parseInt(textWidth("=")) + MARGIN;
        contentTxf.moveTo(x + tmp, y + txfPosY);
        tmp += contentTxf.getWidth() + MARGIN;
        pWidth = tmp;
    }
    public void drawGUI(){
        textFont(font);
        textAlign(LEFT,CENTER);
        fill(0);
        int tmp = MARGIN;
        final int MARGIN_Y = pHeight/2;
        String name = arrayPlate.getArrayName();
        text(name, x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth(name)) + MARGIN;
        text("index:", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("index:")) + MARGIN;
        indexTxf.draw();
        tmp += indexTxf.getWidth() + MARGIN;
        text("=", x + tmp, y + MARGIN_Y);
        tmp += PApplet.parseInt(textWidth("=")) + MARGIN;
        contentTxf.draw();
    }
    public void checkGUIChange(){
        boolean txfChange = false;
        if(indexTxf.checkChanged() || contentTxf.checkChanged()){
            txfChange = true;
        }
        if(txfChange){
            isChange = true;
            setGUIPosition();
        }
    }
    protected void setColor(int fc){
        this.fillColor = fc;
    }
    public String getScript(){
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
        this.pWidth = PApplet.parseInt(textWidth(word)) + MARGIN * 2;
        pHeight = textSize + MARGIN;
        isVariablePlate = true;
	}
    public void draw(){
        noStroke();
        fill(fillColor);
        rect(x,y,pWidth,pHeight,10);
        textAlign(LEFT, TOP);
        textFont(font);
        fill(0);
        text(word,x+MARGIN,y+MARGIN/2);
    }
    public void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void drawTransparent(){

    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public void execute(){

    }
    public String getScript(){
		StringBuilder result = new StringBuilder(word + "\n");
		return result.toString();
    }
}
class BooleanPlate extends ConditionPlate {
    MyComboBox comboBox;
	BooleanPlate(int x, int y){
        super(x,y);
		this.x = x;
		this.y = y;
		pHeight = 30;
		fillColor = getColorByToken(Enum.BOOLEAN);//\u597d\u304d\u306a\u8272\u3092\u57cb\u3081\u308b
        type = Enum.BOOLEAN;
        String[] stmItems = {
            "true", "false"
        };
        comboBox = new MyComboBox(stmItems, x + MARGIN, y +5, 70, 20);
        comboBox.setItem(stmItems[0]);
        setGUIPosition();
	}
    BooleanPlate(int x, int y, int trueOrFalse){
        this(x,y);
        if(trueOrFalse == Enum.TRUE) comboBox.setItem("true");
        else comboBox.setItem("false");
        setGUIPosition();
    }
    public void draw(){
		if(executingPlate == this){
            setBorder();
        }  else {
            noStroke();
        }
		fill(fillColor);
		rect(x,y,pWidth,pHeight,10);
        checkGUIChange();
        comboBox.draw();
    }
    public void drawShadow(){
		noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, pHeight, 10);
        draw();
        if (nextPlate != null) {
            nextPlate.drawShadow();
        }
    }
    public void drawTransparent(){

    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        comboBox.shiftPosition(addX,addY);
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    private void checkGUIChange(){
        if(comboBox.checkChanged()){
            isChange = true;
            setGUIPosition();
        }
    }
    private void setGUIPosition(){
        this.pWidth = comboBox.getWidth() + MARGIN * 2;
    }
    public void execute(){
        if(comboBox.getItem().equals("true")) state = Enum.TRUE;
        else state = Enum.FALSE;
    }

    public String getScript(){
        if(comboBox.getItem().equals("true")) return "true";
        else return "false";
	}
}
class DrawPlate extends WallPlate {
    DrawPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+40;
        fillColor = getColorByToken(Enum.DRAW);
        isWallPlate = true;
        isDrawPlate = true;
    }
    public void execute(){
        // if(counter == -1) executingPlate = this;
        // else{
            if(!loopOpes.isEmpty()){
                Plate p = loopOpes.get(0);
                do{
                    if(hasExecuteEnd) return;
                    p.execute();
                    p = p.nextPlate;
                }while(p != null);
            }
        // }
    }
    public void draw(){
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
        text("draw", x+10, y+5);
    }
    public void drawShadow(){
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
    public void drawTransparent(){
    }
    public void shiftPosition(int addX, int addY) {
        x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
        if (nextPlate != null) {
            nextPlate.shiftPosition(addX, addY);
        }
    }
    public String getScript() {
        StringBuilder result = new StringBuilder("void draw() {\n");
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

class MousePressedPlate extends WallPlate{
    MousePressedPlate(int x, int y){
        this.x = x;
        this.y = y;
        pWidth = 180;
        pHeight = 60+40;
        isWallPlate = true;
        fillColor = color(0xffA092E5);   //\u597d\u304d\u306a\u8272\u3092\u8a18\u5165
    }
    public void draw(){
        updateWidth();
        noStroke();
        if(executingPlate == this){
            setBorder();
        }
        fill(fillColor);
        rect(x, y, pWidth, wallPlateWidth, 10);
        rect(x, y, wallPlateWidth, pHeight, 10);
        rect(x, y+pHeight-wallPlateWidth, pWidth, wallPlateWidth, 10);
        fill(0);
        textFont(font);
        text("mousePressed", x + MARGIN, y + 5);
        if(executingPlate == this){
            noStroke();
            rect(x+2, y+2, pWidth-4, wallPlateWidth-2, 10);
            rect(x+2, y+2, wallPlateWidth-2, pHeight-4, 10);
            rect(x+2, y+pHeight-wallPlateWidth+2, pWidth-4, wallPlateWidth-2, 10);
        }
    }
    public void drawShadow(){
        noStroke();
        fill(0, 0, 0, 180);
        rect(x+8, y+8, pWidth, wallPlateWidth, 10);
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
    public void drawTransparent(){

    }
    public void shiftPosition(int addX, int addY){
		x += addX;
        y += addY ; //\u547d\u4ee4\u6587\u3092\u79fb\u52d5
        if (loopOpes.size() > 0) {
            loopOpes.get(0).shiftPosition(addX, addY);
        }
    }
    public void execute(){

    }
    public String getScript(){
        StringBuilder result = new StringBuilder();
        result.append("void mousePressed(){\n");
        incrementIndent();
        for (Plate plate : loopOpes) {
            result.append(plate.getScript());
        }
        if(loopOpes.size() == 0){   //\u4f55\u3082\u547d\u4ee4\u304c\u306a\u3051\u308c\u3070\u7a7a\u884c\u3092\u8ffd\u52a0
            result.append(getIndent() + "\n");
        }
        decrementIndent();
        result.append("}\n");
        return result.toString();
    }
}
ArrayList<Statement> statementList;
int stmCount = -1;
boolean isExecutable = false;

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
    public void run(){
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
    public void STMLIST(){
        try{
            while(index < tokenSize){
                STM();
                if(isSuperHackerMode){  //\u6c17\u306b\u3057\u306a\u3044\u3067
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
    public void STM()throws Exception{
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
        }else if(next.kind == Enum.WHILE){
            stmWhile();
        }else if(next.kind == Enum.VOID){
            next = getNextToken();
            if(next.kind == Enum.SETUP){
                declSetupMethod();
            }else if(next.kind == Enum.DRAW){
                declDrawMethod();
            }else if(next.kind == Enum.MOUSE_PRESSED_METHOD){
                declMousePressed();
            }
            else if(next.kind == Enum.OTHER){
                declMethod();
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
            //\u3053\u3053\u306f\u8aad\u307f\u98db\u3070\u3057\u3066\u306f\u3044\u3051\u306a\u3044
        }else if(next.kind == Enum.COMMENT){
            next = getNextToken();  //\u8aad\u307f\u98db\u3070\u3059
        }else if(next.kind == Enum.YUYA){
            isSuperHackerMode = true;
            next = getNextToken();
        }
        else {
            unexpectedTokenError(next);
        }
    }
    public Token getNextToken(){
        index++;
        try{
            return runTokens.get(index);
        }catch(IndexOutOfBoundsException e){
            return null;
        }
    }
    public void unexpectedTokenError(Token token) throws Exception{
        throw new IncorrectSyntaxException(token);
    }
    public void DECL() throws Exception { // DECL -> INT OTHER SEMI
        //\u3053\u306e\u6642\u70b9\u3067\u306f\u5909\u6570\u540d\u306e\u3068\u3053\u308d\u307e\u3067\u8aad\u307f\u8fbc\u307e\u308c\u3066\u3044\u308b
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
            String result = E();

            String[] argString = new String[2];
            argString[0] = name;
            argString[1] = result;
            statementList.add(new Statement(Enum.INT, argString));
        }
        if (next.kind != Enum.SEMI)
            unexpectedTokenError(next);
        next = getNextToken();
    }
    public void DECL_ARRAY()throws Exception{  //DECL_ARRAY -> INT LBRACKET RBRACKET OTHER ASSIGN
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
    public void declString() throws Exception{
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
    public void declStringArray() throws Exception{
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
    public void stmRect()throws Exception{
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
    public void stmTextSize()throws Exception{
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
    public void stmText()throws Exception{
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
    public void declSetupMethod()throws Exception{
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
    public void declDrawMethod() throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.DRAW_METHOD_START));
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        next = getNextToken();

        statementList.add(new Statement(Enum.DRAW));
    }
    public void stmEllipse()throws Exception{
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
    public void stmFill()throws Exception{
        int argSize = 3;    //\u3053\u3053\u306f1or3\u306a\u306e\u3067\u67d4\u8edf\u306b\u5bfe\u5fdc\u3067\u304d\u308b\u3088\u3046\u306b\u5909\u66f4\u3057\u306a\u3051\u308c\u3070\u306a\u3089\u306a\u3044
        String[] argString = new String[argSize];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        for(int i = 0; i < argSize; i++){
            String result = "";
            next = getNextToken();
            while(true){
                result = E();
                if(next.kind == Enum.COMMA || (i==argSize-1 && next.kind==Enum.RBRACE)) break;
            }
            argString[i] = result;
        }

        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();

        statementList.add(new Statement(Enum.FILL, argString));
    }
    public void stmStroke()throws Exception{
        int argSize = 3;    //\u3053\u3053\u306f1or3\u306a\u306e\u3067\u67d4\u8edf\u306b\u5bfe\u5fdc\u3067\u304d\u308b\u3088\u3046\u306b\u5909\u66f4\u3057\u306a\u3051\u308c\u3070\u306a\u3089\u306a\u3044
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
    public void stmNoStroke()throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.SEMI) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.NO_STROKE));
    }
    public void stmBackground()throws Exception{
        int argSize = 3;
        float[] arg = new float[argSize];

        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        for(int i = 0; i < argSize; i++){
            if(next.kind == Enum.NUM){
                arg[i] = PApplet.parseInt(next.word);
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
    public void stmLine()throws Exception{
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
    public void stmAccessArray(String varName) throws Exception{
        //\u3053\u306e\u6642\u70b9\u3067OTHER LBRACE\u307e\u3067\u306f\u8aad\u307f\u8fbc\u307e\u308c\u3066\u3044\u308b
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
    public int getValue() throws Exception{
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
    public void stmPrintln()throws Exception{
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
    public void stmFor() throws Exception{
        next = getNextToken();
        if (next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
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
    public void stmIf()throws Exception{
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
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        statementList.add(new Statement(Enum.IF));
        next = getNextToken();
    }
    public void stmWhile() throws Exception{
        next = getNextToken();
        if (next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.WHILE_START));
        booleanE();
        changeCodeToConditionPlate();
        if (next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if (next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        statementList.add(new Statement(Enum.WHILE));
        next = getNextToken();
    }
    public void stmAssign(String varName)throws Exception{
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
        statementList.add(new Statement(Enum.DECL, argString));
    }
    //\u95a2\u6570\u5b9a\u7fa9
    public void declMethod() throws Exception{
        String[] argString = new String[1];
        argString[0] = next.word;   //\u30e1\u30bd\u30c3\u30c9\u540d\u306b\u306a\u308b
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();

        //\u4eee\u5f15\u6570\u306e\u8a2d\u5b9a
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
        //\u95a2\u6570\u5185\u306b\u3042\u308b\u547d\u4ee4\u5217\u3092\u53d6\u5f97
        next = getNextToken();
        if(next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        next = getNextToken();

        statementList.add(new Statement(Enum.METHOD));
    }
    public void declMousePressed() throws Exception{
        next = getNextToken();
        if(next.kind != Enum.LBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.RBRACE) unexpectedTokenError(next);
        next = getNextToken();
        if(next.kind != Enum.LCBRACE) unexpectedTokenError(next);
        next = getNextToken();
        statementList.add(new Statement(Enum.MOUSE_PRESSED_METHOD_START));
        do{
            STM();
        }while(next.kind != Enum.RCBRACE);
        next = getNextToken();

        statementList.add(new Statement(Enum.MOUSE_PRESSED_METHOD));
    }
    //\u95a2\u6570\u547c\u3073\u51fa\u3057
    public void stmCallMethod(String methodName) throws Exception{
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
    public String stringE() throws Exception{
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
            if(token.kind == Enum.MOJIRETSU){
                String s = deleteLastChar(token.word);
                s = s.substring(1,s.length());
                sb.append(s);
            }else if(token.kind == Enum.NUM){
                sb.append(token.word);
            }else if(token.kind == Enum.OTHER){
                try{
                    Variable v = fetchVariableByName(token.word);
                    sb.append(v.getVarValue());
                }catch(UndefinedVariableException e){
                    println("undefinedVariable : " + token.word);
                }
            }else if(token.kind == Enum.STRING_ARRAY){
                String[] words = token.word.split(",");
                try{
                    CompositeVariable cv = (CompositeVariable)variableTable.searchName(words[0]);
                    sb.append(cv.get(Integer.parseInt(words[1])));
                }catch(UndefinedVariableException e){
                    println("undefinedVariable : " + words[0]);
                }
            }
        }
        return sb.toString();
    }
    ArrayList codeList;
    public void booleanE() throws Exception{
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
    public void booleanT() throws Exception{
        booleanF();
        if(next.kind == Enum.LESS || next.kind == Enum.LESS_THAN || next.kind == Enum.GRATER || next.kind == Enum.GRATER_THAN || next.kind == Enum.EQUAL || next.kind == Enum.NOT_EQUAL){
            int tmpKind = next.kind;
            next = getNextToken();
            booleanF();
            codeList.add(tmpKind);
        }
    }
    public void booleanF() throws Exception {
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
        } else if(next.kind == Enum.WIDTH || next.kind == Enum.HEIGHT){
            codeList.add(next.kind);
            codeList.add(next.word);
            next = getNextToken();
        }else if(next.kind == Enum.TRUE || next.kind == Enum.FALSE){
            codeList.add(next.kind);
            codeList.add(next.word);
            next = getNextToken();
        }
        else
            unexpectedTokenError(next);
    }
    public String E() throws Exception{
        StringBuilder sb = new StringBuilder();
        sb.append(T());
        while (true) {
            if (next.kind == Enum.PLUS) {
                sb.append(next.word);
                next = getNextToken();
                sb.append(T());
                addCode("AD 0 0");
            } else if (next.kind == Enum.MINUS) {
                sb.append(next.word);
                next = getNextToken();
                sb.append(T());
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
                sb.append(F());
                addCode("ML 0 0");
            }else if (next.kind == Enum.DIV) {
                sb.append(next.word);
                next = getNextToken();
                sb.append(F());
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
            sb.append(E());
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
                try{
                    addCode("LDV 0 " + getAdress(varName));
                }catch(UndefinedVariableException e){
                    addCode("LDV 0 " + 0);
                }
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
        } else if(next.kind == Enum.HEIGHT){
            sb.append(next.word);
            addCode("HEIGHT 0 0");
            next = getNextToken();
        } else if(next.kind == Enum.WIDTH){
            sb.append(next.word);
            addCode("WIDTH 0 0");
            next = getNextToken();
        }else if (next.kind == Enum.MINUS) {
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
    public int getAdress(String name) throws UndefinedVariableException{
        int index = variableTable.indexOf(name);
        if(index == -1) throw new UndefinedVariableException(name);
        return index;
    }
    public void addCode(String st) {
        result.append(st).append("\n");
    }
    WallPlate wallPlate = null;
    WallPlate preWallPlate = null;
    Plate prePlate = null;
    //\u5927\u4e8b:\u30c6\u30ad\u30b9\u30c8\u304b\u3089\u30bf\u30a4\u30eb\u8868\u73fe\u306b\u76f4\u3059
    public void statmentToPlate(){
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
                Plate plate = new DeclPlate(Enum.INT, currentTileArrangement[0], currentTileArrangement[1],stm.argString[0], stm.argString[1]);
                plateList.add(plate);
                currentTileArrangement[1] += plate.pHeight;
                if(prePlate != null){
                    prePlate.combinePlate(plate);
                }
                if(wallPlate != null){
                    plate.combineWallPlate(wallPlate);
                }
                prePlate = plate;
            }else if(stm.kind == Enum.DECL){
                Plate plate = new AssignmentPlate(currentTileArrangement[0], currentTileArrangement[1], stm.argString[0], stm.argString[1]);
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
                stm = statementList.get(++i);
                ((IfCondPlate)wallPlate).setConditionPlate(getConditionPlate(stm));
            }else if(stm.kind == Enum.IF){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 70;
                prePlate = wallPlate;
                wallPlate = wallPlate.upperPlate;
            }else if(stm.kind == Enum.WHILE_START){
                changeToWallPlate(stm);
                stm = statementList.get(++i);
                ((WhilePlate)wallPlate).setConditionPlate(getConditionPlate(stm));
            }else if(stm.kind == Enum.WHILE){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 70;
                prePlate = wallPlate;
                wallPlate = wallPlate.upperPlate;
            }else if(stm.kind == Enum.SETUP){
                setupPlate = (SetupPlate)changeToWallPlate(stm);
            }else if(stm.kind == Enum.SETUP_END){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 100;
                wallPlate = null;
                prePlate = null;
            }else if(stm.kind == Enum.DRAW_METHOD_START){
                drawPlate = (DrawPlate)changeToWallPlate(stm);
            }else if(stm.kind == Enum.DRAW){
                currentTileArrangement[0] -= wallPlate.wallPlateWidth;
                currentTileArrangement[1] += 100;
                wallPlate = null;
                prePlate = null;
            }else if(stm.kind == Enum.MOUSE_PRESSED_METHOD_START){
                changeToWallPlate(stm);
            }else if(stm.kind == Enum.MOUSE_PRESSED_METHOD){
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
    public Plate getPlateByStatement(Statement stm){
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
            return new DeclPlate(Enum.INT, currentTileArrangement[0], currentTileArrangement[1],stm.argString[0], stm.argString[1]);
        }else if(stm.kind == Enum.DECL){
            return new AssignmentPlate(currentTileArrangement[0], currentTileArrangement[1], stm.argString[0], stm.argString[1]);
        }
        else if(stm.kind == Enum.RECT){
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
        }
        else{
            println("Error!!!!!!!!!!!!!!!getPlateByStatement() method");
            return null;
        }
    }
    public Plate getAssignmentPlate(int type, String leftHand, String rightHand){
        return new DeclPlate(type, currentTileArrangement[0], currentTileArrangement[1],leftHand, rightHand);
    }
    public Plate getStatementPlate(String statementName, Statement stm){
        return new StatementPlate(statementName, currentTileArrangement[0], currentTileArrangement[1], stm.argString);
    }
    public void changeToAssignmentPlate(int type, String leftHand, String rightHand){
        Plate plate = new DeclPlate(type, currentTileArrangement[0], currentTileArrangement[1],leftHand, rightHand);
        updatePlateEnv(plate);
    }
    public void changeToStatementPlate(String statementName, Statement stm){
        Plate plate = new StatementPlate(statementName, currentTileArrangement[0], currentTileArrangement[1], stm.getAllArgAsString());
        updatePlateEnv(plate);
    }
    public void changeToStatementPlate2(String statementName, Statement stm){
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
    public ConditionPlate getConditionPlate(Statement stm){
        String a = "";
        if(stm.kind == Enum.LESS) a = "<";
        else if(stm.kind == Enum.LESS_THAN) a = "<=";
        else if(stm.kind == Enum.GRATER) a = ">";
        else if(stm.kind == Enum.GRATER_THAN) a = ">=";
        else if(stm.kind == Enum.EQUAL) a = "==";
        else if(stm.kind == Enum.NOT_EQUAL) a = "!=";
        if(!a.equals("")){
            return new ConditionPlate(0,0,a,stm.argString[0],stm.argString[1]);
        }else{
            return new BooleanPlate(0,0,stm.kind);
        }
    }
    //\u8981\u4fee\u6b63\uff1a\u8907\u6570\u306eboolean\u306b\u5bfe\u5fdc\u3067\u304d\u308b\u3088\u3046\u306b\u3059\u308b
    public void changeCodeToConditionPlate() throws Exception{
        Stack<String> stack = new Stack<String>();
        for(int i = 0; i < codeList.size(); i++){
            int kind = (Integer)codeList.get(i);
            if(kind == Enum.NUM || kind == Enum.OTHER || kind == Enum.MOUSE_X || kind == Enum.WIDTH || kind == Enum.HEIGHT){
                i++;
                stack.push((String)codeList.get(i));
            }else if(kind == Enum.LESS || kind == Enum.LESS_THAN || kind == Enum.GRATER || kind == Enum.GRATER_THAN || kind == Enum.EQUAL || kind == Enum.NOT_EQUAL){
                String[] argString = new String[2];
                argString[1] = stack.pop();
                argString[0] = stack.pop();
                statementList.add(new Statement(kind,argString));
            }else if(kind == Enum.TRUE || kind == Enum.FALSE){
                i++;
                statementList.add(new Statement(kind));
            }
        }
    }
    public WallPlate changeToWallPlate(Statement stm){
        //\u975e\u5e38\u306b\u66f8\u304d\u65b9\u304c\u6c5a\u3044\u3002\u3042\u3068\u3067\u306a\u304a\u3059\u3002
        WallPlate wplate = null;
        int kind = stm.kind;
        if(kind == Enum.SETUP){
            wplate = new SetupPlate(currentTileArrangement[0], currentTileArrangement[1]);
        }else if(kind == Enum.DRAW_METHOD_START){
            wplate = new DrawPlate(currentTileArrangement[0], currentTileArrangement[1]);
        }else if(kind == Enum.MOUSE_PRESSED_METHOD_START){
            wplate = new MousePressedPlate(currentTileArrangement[0], currentTileArrangement[1]);
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
        } else if(kind == Enum.IF_START) {
            wplate = new IfCondPlate(currentTileArrangement[0], currentTileArrangement[1]);
        } else if(kind == Enum.WHILE_START) {
            wplate = new WhilePlate(currentTileArrangement[0], currentTileArrangement[1]);
        }
        currentTileArrangement[0] += wplate.wallPlateWidth;
        currentTileArrangement[1] += wplate.wallPlateHeight;
        if (prePlate != null) {
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
    public WallPlate changeToForPlate(Plate firstPlate, ConditionPlate cond, Plate lastPlate){
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
    public int getArgCount(){
        return argInt.length + arg.length + argString.length;
    }

    public String[] getAllArgAsString(){
        String[] allArgStr = new String[argInt.length + arg.length + argString.length];
        int index = 0;
        for(int i = 0; i < argInt.length; i++){
            allArgStr[index] = ""+argInt[i];
            index++;
        }
        for(int i = 0; i < arg.length; i++){
            allArgStr[index] = ""+PApplet.parseInt(arg[i]);
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
        // \u30b3\u30f3\u30b9\u30c8\u30e9\u30af\u30bf
        ident = id;
        address = addr;
        level = lv;
    }
}
int textSize= 20;
public void shmSetup(){
    settings();
    frameRate(15);
    textSize(textSize);
}
public void shmDraw(){
    background(0);
    fill(255);
    for(int j = 0; j < height/textSize; j++){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 500; i++){
            if(random(100) >50){
                sb.append("1");
            }else{
                sb.append("0");
            }
        }
        text(sb.toString(), 0,10 + textSize*j);
    }
}
    public void settings() {  size(1800,900); }
    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "sketch_160830a" };
        if (passedArgs != null) {
          PApplet.main(concat(appletArgs, passedArgs));
        } else {
          PApplet.main(appletArgs);
        }
    }
}
