import java.io.*;

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
            // 無限ループを防ぐためicMax回以上は命令を実行しないこととする。
            // 実用的にはwhile(true){とする。
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
                //要検討！！！！！！！！！！！！！！！！！！！！！！！！！！！！
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
