import java.util.Stack;
import processing.sound.*;

/*
思いついたこと一覧
・コメントのタイル実装を行う
・変数一覧を作成しそこから視覚的表現へ用いれるようにする
・変数タイルの作成(変数名は選択式に)
・関数を定義すると自動的に新たなタイルが作られるようにする
・タイルの一覧表を作成する
・変数代入を視覚的な実行時表現であらわせるようにする(箱を作るイメージ)
・例外処理を実装したいよね
・マルチスレッドとかも実装したいよね
・オブジェクト指向、やりたいよね
・テキストとブロックの関係づけを持っておいて(Plateクラスがテキストエディタ内の情報を持っており、かつテキストの方もPlateの情報を持っているような感じ)相互連携を図りたい。編集中にコードの提案を行えたら
・ゴミ箱の調整
・制御の流れが「球」の動きによっておっていけると良いかなぁ
・for文のブロックのデザインをどうしようか
*/
/*
既知のバグ
・WallPlate内でStatementPlate一個分以上離すとリンクがキャンセルされる
*/
SoundFile errorSound, dumpSound, correctSound, putSound, nextStepSound, openWindowSound;

ArrayList<Plate> plateList = new ArrayList<Plate>();
ArrayList<WallPlate> wallPlateList = new ArrayList<WallPlate>();
ArrayList<Method> methodList = new ArrayList<Method>();

ArrayList<Tile> allBlocks = new ArrayList<Tile>();    //ブロックの情報を保持する場所

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
boolean isDebugMode = true;
ArrayList<Plate> allPlateForDebugmode = new ArrayList<Plate>();
ArrayList<MethodPlate> methodPlateList = new ArrayList<MethodPlate>();
int debugIndex = 0;

HashMap<Integer,Integer> colorDict = new HashMap<Integer,Integer>();
boolean hasError = false;

boolean isSuperHackerMode = false;

int counter = -1;
int step = 0;
boolean hasExecuteEnd = false;
boolean canSetupExecute = true;
ArrayList<MyButton> buttonList = new ArrayList<MyButton>();

LogicalOpePlate logi;
MousePressedPlate mpp;
void setup(){
    size(1800,900);
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
    colorDict.put(Enum.INT, #F3AFA9);  //#FADDDA
    colorDict.put(Enum.STRING, #B9EECF);   //#E6F9EE
    colorDict.put(Enum.BOOLEAN, #69B0DD);   //#E6F1F9

    executingPlate = setupPlate;

    logi = new LogicalOpePlate(200,200);
    plateList.add(logi);
    mpp = new MousePressedPlate(300,200);
    plateList.add(mpp);
    wallPlateList.add(mpp);
    isChange =true;

}

int[] trashBoxPosition;

void initImage(){
    redFlagIcon         = loadImage("flag.png");
    greenFlagIcon       = loadImage("green_flag.png");
    trashBoxIcon        = loadImage("trashbox.png");
    trashBoxOpenIcon    = loadImage("trashbox_open.png");
    trashBoxPosition    = new int[2];
    trashBoxPosition[0] = 30;
    trashBoxPosition[1] = height-70;
}
void initSound(){
    dumpSound       = new SoundFile(this, "dumping.mp3");
    errorSound      = new SoundFile(this, "error_sound.mp3");
    correctSound    = new SoundFile(this, "correct_sound.mp3");
    putSound        = new SoundFile(this, "put_sound.mp3");
    nextStepSound   = new SoundFile(this, "next_step_sound.mp3");
    openWindowSound = new SoundFile(this, "open_window_sound.mp3");
}

MyButton statementButton, variableButton, ifButton, whileButton, forButton, methodButton, arrayButton;

void initButton(){
    int x = 30;
    int y = 50;
    final int BUTTON_MARGIN = 70;

    statementButton = new MyButton("Statement", x,y);
    statementButton.setColor(peterRiver, color(#8AC3E9), color(#1A5F8E));
    buttonList.add(statementButton);
    y += BUTTON_MARGIN;

    variableButton = new MyButton("Variable", x, y);
    variableButton.setColor(alizarin, color(#F29E96), color(#A72114));
    buttonList.add(variableButton);
    y += BUTTON_MARGIN;

    ifButton = new MyButton("If", x, y);
    ifButton.setColor(carrot, color(#EFB17A), color(#8D4A10));
    buttonList.add(ifButton);
    y += BUTTON_MARGIN;

    whileButton = new MyButton("While", x, y);
    whileButton.setColor(schaussPink, color(#FFD1D8), color(#FF6B83));
    buttonList.add(whileButton);
    y += BUTTON_MARGIN;

    forButton = new MyButton("For", x, y);
    forButton.setColor(nephritis, color(#5CDA91), color(#13572F));
    buttonList.add(forButton);
    y += BUTTON_MARGIN;

    methodButton = new MyButton("Method", x, y);
    methodButton.setColor(color(78,205,196), color(#9CE2DC), color(#288A82));
    buttonList.add(methodButton);
    y += BUTTON_MARGIN;

    arrayButton = new MyButton("Array", x, y);
    arrayButton.setColor(amethyst, color(#C49FD4), color(#603474));
    buttonList.add(arrayButton);
    y += BUTTON_MARGIN;
}

int stmPos = 0;
boolean isChange = false;
void draw( ) {
    if(isSuperHackerMode){
        shmDraw();
    }else{
    background(255);
    textSize(20);
    textAlign(LEFT,TOP);
    text(Math.round(frameRate) + "fps",40,10);

    // if(!hasError){
        if(canSetupExecute){
            fill(255);
            stroke(0);
            strokeWeight(1);
            variableTable.init();
            step = 0;
            hasExecuteEnd = false;
            setupPlate.execute();
            if(drawPlate != null){
                canSetupExecute = false;
            }
        }else{
            fill(255);
            stroke(0);

            step = 0;
            if(drawPlate != null)drawPlate.execute();
            else canSetupExecute = true;
        }
    // }

    drawEditor();
    drawPlate();
    drawUI();

    updateInitialTileArrangement();
    //重さを考えるなら要検討
    if(isTileConversion && isChange){
        changeTileToScript();
        if(editor.isLiveProgramming){
            isOK = false;
            new Lang(editor.getTokens()).run();
            isOK = true;
        }
        allPlateForDebugmode = new ArrayList<Plate>();
        isChange = false;
    }
    }   //superhackermode
}
boolean isOK = true;
void changeTileToScript(){
    String s = getAllScript();
    String[] ss = s.split("\n");
    editor.setTexts(ss);
}

void drawEditor(){
    editor.display();
}

Plate hadPlate = null;  //直前まで持っていたプレート
void drawPlate() {
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

    //持っているプレートだけは最前面に出すために最後に描画する
    if(selectedPlate != null){
        selectingTime++;
        if(selectingTime > SELECTED_TIME){
            selectedPlate.drawShadow();
        }else{
            selectedPlate.draw();
        }
    }
}

void drawUI(){
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
void updateInitialTileArrangement(){
    if(plateList.size() > 0){
        Plate p = plateList.get(0);
        initialTileArrangement[0] = p.x;
        initialTileArrangement[1] = p.y;
    }
}
void keyPressed(KeyEvent e){
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
        if(key == 'f'){
            plateList.add(new ConditionPlate(100,100));
            isChange = true;
        }else if(key == 'd'){
            plateList.add(new LogicalOpePlate(100,100));
            isChange = true;
        }

    }
    if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_P){     //プログラムの実行
        balloonList = new ArrayList<Balloon>();
        drawPlate = null;
        new Lang(editor.getTokens()).run();
        canSetupExecute = true;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_O){     //ライブプログラミングモード
        editor.isLiveProgramming = !editor.isLiveProgramming;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_I){     //タイルプログラミングモード
        isTileConversion = !isTileConversion;
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_U){     //statementListの中身をすべて表示
        for(int i = 0; i < statementList.size(); i++){
            println(statementList.get(i).kind);
        }
    }else if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_Y){     //debugモードの切り替え
        isDebugMode = !isDebugMode;
    }
}

String getAllScript(){
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
boolean isOut(Plate p){
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
final int SELECTED_TIME = 8;
void mousePressed() {
    for(int i = 0; i < plateList.size(); i++){
        Plate plate = plateList.get(i);
        if (plate.isMouseOver()) {
            selectedPlate = plate;
            hadPlate = plate;
        }
    }
    for(Balloon b : balloonList){
        if(b.isMouseOver()) b.putForward();
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
}
void mouseDragged(){
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
}
void mouseReleased() {
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
        //タイルの上に持っているタイルが乗っているかどうかを判定する
        boolean isEnter = false;
        for(Plate plate : plateList){
            if(plate != selectedPlate && plate.isLogicalOpePlate && plate.isMouseOver() && selectedPlate.oyaPlate != plate) {
                ((LogicalOpePlate)plate).insertPlate(selectedPlate);
                isEnter = true;
            }
        }
        if(isEnter ) return;

        //タイルの下にくっつける
        selectedPlate.checkPlateLink();
        for(int i = 0; i < plateList.size(); i++){
            Plate plate = plateList.get(i);
            if(plate != selectedPlate && plate.isPlateBelow(selectedPlate)){
                plate.combinePlate(selectedPlate);
                selectedPlate.goToUnderThePlate(plate);
            }
        }
        //タイルの中に入れこませる
        WallPlate nearestPlate = selectedPlate.getNearestWallPlate(wallPlateList);
        selectedPlate.checkWallPlateLink(nearestPlate);
        if(nearestPlate != null) {
            if(selectedPlate.upperPlate != nearestPlate){
                selectedPlate.combineWallPlate(nearestPlate);
            }
            selectedPlate.goIntoWallPlate(nearestPlate);
        }
        //ゴミ箱行き
        if(isTrashBoxNear()){
            dumpSound.play();
            deletePlate(selectedPlate);
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
}
void mouseWheel(MouseEvent e){
    editor.mouseWheel(e);
}
void mouseClicked(MouseEvent e){
    for(int i = 0; i < plateList.size(); i++){
        plateList.get(i).mouseClicked(e);
    }
}
boolean isTrashBoxNear(){
    int r = 100;
    if(dist(mouseX, mouseY, trashBoxPosition[0], trashBoxPosition[1]) < r){
        return true;
    }else{
        return false;
    }
}
void buttonAction(){
    if(statementButton.isOver) {
        String[] arg = {"100","100","300","200"};
        plateList.add(new StatementPlate("rect", 100,100, arg));
        isChange = true;
    }else if(variableButton.isOver){
        plateList.add(new DeclPlate(Enum.INT,100,100,"x","0"));
        isChange = true;
    }else if(whileButton.isOver){
        WhilePlate wp = new WhilePlate(100, 100);
        plateList.add(wp);
        wallPlateList.add(wp);
        isChange = true;
    }else if(ifButton.isOver){
        IfCondPlate fp = new IfCondPlate(100,100);
        plateList.add(fp);
        wallPlateList.add(fp);
        isChange = true;
    }else if(forButton.isOver){
        ForPlate l = new ForPlate(100,100);
        wallPlateList.add(l);
        plateList.add(l);
        isChange = true;
    }else if(methodButton.isOver){
        MethodPlate mp = new MethodPlate(100,100);
        plateList.add(mp);
        wallPlateList.add(mp);
        isChange = true;
    }else if(arrayButton.isOver){
        ArrayPlate_Original ap = new ArrayPlate_Original(100,100,"a","10",Enum.INT_ARRAY);
        plateList.add(ap);
        isChange = true;
    }
}
//再帰を用いてp以下のPlateをすべて削除。アニメーションを加えたい
void deletePlate(Plate p){
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
