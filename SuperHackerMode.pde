int textSize= 20;
void shmSetup(){
    settings();
    frameRate(15);
    textSize(textSize);
}
void shmDraw(){
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
