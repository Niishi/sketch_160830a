class LoadTextEditorThread extends Thread {
    MyTextEditor editor;
    LoadTextEditorThread(MyTextEditor editor){
        this.editor = editor;
    }
    void run(){
        new Lang(editor.getTokens()).run();
    }
}
