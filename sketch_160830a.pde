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

PImage redFlagIcon, greenFlagIcon, trashBoxIcon, trashBoxOpenIcon;
MyTextEditor editor;
PFont font;
SetupPlate setupPlate;

VariableTable variableTable;
Plate executingPlate;
WallPlate executingWallPlate;
int gradationR = 0;
boolean isDebugMode = true;
ArrayList<Plate> allPlateForDebugmode = new ArrayList<Plate>();
int debugIndex = 0;

HashMap<Integer,Integer> colorDict = new HashMap<Integer,Integer>();
boolean hasError = false;

boolean isSuperHackerMode = false;


void setup(){
    size(1800,900);
    RESULT_WINDOW_WIDTH  = width / 2;
    RESULT_WINDOW_HEIGHT = height;
    editor = new MyTextEditor(RESULT_WINDOW_WIDTH, 0, RESULT_WINDOW_WIDTH, RESULT_WINDOW_HEIGHT, 20);
    variableTable = new VariableTable(RESULT_WINDOW_WIDTH - 300, 10);
    initImage();
    initSound();

    font = createFont("Ricty Diminished", 16);

    setupPlate = new SetupPlate(initialTileArrangement[0],initialTileArrangement[0]);
    plateList.add(setupPlate);
    wallPlateList.add(setupPlate);
    setAllExecutePlate(setupPlate);

    changeTileToScript();

    initArgTypeList();
    colorDict.put(Enum.INT, #F3AFA9);  //#FADDDA
    colorDict.put(Enum.STRING, #B9EECF);   //#E6F9EE
    colorDict.put(Enum.BOOLEAN, #69B0DD);   //#E6F1F9

    executingPlate = setupPlate;
    ForPlate testFP = new ForPlate(100,100);
    plateList.add(testFP);
    wallPlateList.add(testFP);
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

    fill(255);
    stroke(0);
    strokeWeight(2);
    variableTable.init();
    if(!hasError){
        if(isDebugMode){
            for(int i = 0; i <= debugIndex; i++){
                Plate p = allPlateForDebugmode.get(i);
                if(!p.isWallPlate){
                    p.execute();
                }
            }
        }else{
            setupPlate.execute();
        }
    }

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
        setAllExecutePlate(setupPlate);
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
    // if(selectedGUI != null) selectedGUI.draw();
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
        if(key == 'z'){
            String[] arg = {"100","100","300","200"};
            plateList.add(new StatementPlate("rect", 100,100, arg));
            isChange = true;
        }else if(key == 'x'){
            ForPlate l = new ForPlate(100,100);
            wallPlateList.add(l);
            plateList.add(l);
            isChange = true;
        }else if(key == 'y'){
            MethodPlate mp = new MethodPlate(100,100);
            plateList.add(mp);
            wallPlateList.add(mp);
            isChange = true;
        }else if(key == 'w'){
            plateList.add(new AssignmentPlate(Enum.INT,100,100,"x","0"));
            isChange = true;
        }else if(key == 'f'){
            IfCondPlate fp = new IfCondPlate(100,100);
            plateList.add(fp);
            wallPlateList.add(fp);
            isChange = true;
        }else if(key == 'r'){
            ReturnPlate rp = new ReturnPlate(100,100);
            plateList.add(rp);
            isChange = true;
        } else if(key == 't'){
            ArrayPlate_Original ap = new ArrayPlate_Original(100,100,"a","10",Enum.INT_ARRAY);
            plateList.add(ap);
            isChange = true;
        }
        else if(key == 'a' && selectedPlate != null){
            println(selectedPlate.changePlatetoString());
        }else if(e.isControlDown() && keyCode == RIGHT){
            if(isDebugMode){
                debugIndex++;
                if(debugIndex == allPlateForDebugmode.size()){
                    debugIndex = 0;
                }
                executingPlate = allPlateForDebugmode.get(debugIndex);
                nextStepSound.play();
            }
        }else if(e.isControlDown() && keyCode == LEFT){
            if(isDebugMode){
                debugIndex--;
                if(debugIndex < 0){
                    debugIndex = 0;
                }
                executingPlate = allPlateForDebugmode.get(debugIndex);
            }
        }
    }
    if (e.isControlDown() && e.getKeyCode() == java.awt.event.KeyEvent.VK_P){     //プログラムの実行
        new Lang(editor.getTokens()).run();
        allPlateForDebugmode = new ArrayList<Plate>();
        setAllExecutePlate(setupPlate);
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
        setAllExecutePlate(setupPlate);
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
    editor.mousePressed();
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
}
void mouseDragged(){
    editor.mouseDragged();
    if(selectedPlate != null){
        selectedPlate.moveTo(mouseX-pmouseX, mouseY-pmouseY);
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
void mouseWheel(MouseEvent e ){
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
    }
    plateList.remove(p);
    deletePlate(p.nextPlate);
}

void setAllExecutePlate(WallPlate wp){
    allPlateForDebugmode.add(wp);
    for(int i = 0; i < wp.loopOpes.size(); i++){
        Plate p = wp.loopOpes.get(i);
        if(p.isWallPlate){
            ((WallPlate)(p)).setPlateInDebugmode();
        }else{
            allPlateForDebugmode.add(p);
        }
    }
}