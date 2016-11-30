class LoadTextEditorThread extends Thread {
    MyTextEditor editor;
    LoadTextEditorThread(MyTextEditor editor){
        this.editor = editor;
    }
    void run(){
        new Lang(editor.getTokens()).run();
    }
}

boolean isRunning = false;
class RunningStateThread extends Thread{
    int x;
    int y;
    RunningStateThread(int x, int y){
        this.x = x;
        this.y = y;
    }
    void run(){

    }
}
