package Controlador;

import Vista.Principal;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Isaac
 */
public class Brain {

    public static String text;//Texto leido del archivo txt.
    public static String ASCIICode;//Codigo ASCII del texto leido.
    public static String HexCode;//Codigo Hex del taxto leido.
    public static String Bin;//Codigo Binario del Hex.
    public static ArrayList DataWords;//Datawords de 128bits maximo
    public static ArrayList lecture;//Datawords de 128bits maximo
    public static String nombreDeSalida;//Nombre del txt de salida.
    //public static String DIV;//Polinomio necesario para codificar. Es un Binario
    public static ArrayList CodeWords;//Lista donde se almacenaran los codewords
    public static String Generador;
    public static String NombreS;

    public static void main(String[] args) {
        Principal ventana = new Principal();
        ventana.setVisible(true);
    }

    public static void CreateTxt(String generador, ArrayList CodeWord, String nombre) { // Le mando el generador, el CodeWord y el nombre del archivo que el usuario escogió
        File Archivo;
        FileWriter w;
        BufferedWriter bw;
        PrintWriter wr;
        Archivo = new File("C:\\ArchivosTxt\\" + nombre + ".crc"); // Crea el archivo en la direccion dada con el nombre escogido
        if (Archivo.exists()) {
            Archivo.delete();
            Archivo = new File("C:\\ArchivosTxt\\" + nombre + ".crc");
        }
        try {
            if (Archivo.createNewFile()) { // Verifica que el archivo se haya creado exitosamente
                w = new FileWriter(Archivo); // Se prepara para escribir en el archivo
                bw = new BufferedWriter(w);
                wr = new PrintWriter(bw);
                wr.write(generador);
                wr.write("\r\n");
                String dato;
                for (int i = 0; i < CodeWord.size(); i++) {
                    dato = CodeWord.get(i).toString();
                    wr.write(dato);
                    wr.write("\r\n");
                }
                wr.close();
                bw.close();
                System.out.println("Se ha creado exitosamente el archivo salida");
            }
        } catch (IOException e) {
            System.out.println("Ha habido un error creando el Archivo");
        }
    }

    public static boolean VerificarArchivo(File f) {
        try {
            String line;
            FileReader fr = new FileReader(f.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            if(line == null){
                return false;
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
        return true;
    }

    public static void GetInfo(File f, int tipo) { //Se consigue los valores de numregistro, numcampos, ks y tipos
        String line;//Con la que se saca linea por linea  el texto del txt.
        text = "";//Se "limpia" la variable donde todo el texto se va a contener.
        lecture = new ArrayList<>();
        try {
            FileReader fr = new FileReader(f.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            while (line != null) {
                text = text + line;//Se agrega linea por linea del texto a la variable text. No se tiene ninguna separacion entre lineas.
                line = br.readLine();
            }
            System.out.println("La información extraida es: " + text);//Esto es para comprobar
            if (tipo == 1) {
                GetASCII(text);//En esta funcion se le asigna a la variable "ASCIICode" la traduccion de todo "text" a ASCCI
                //GetHexAndBin(text);
                GetHexBinDataWords(text);//En esta funcion se hayan los valores hexadecimales y binarios del texto y se guarda en una lista los datawords
            } else if (tipo == 2) {
                System.out.println("Su CodeWord es: " + text);//Esto es para comprobar
            } else if (tipo == 3) {
                String nombre = f.getName();
                String linea;
                FileReader rr = new FileReader(f.getAbsolutePath());
                BufferedReader hr = new BufferedReader(rr);
                linea = hr.readLine();
                while (linea != null) {
                    lecture.add(linea);
                    linea = hr.readLine();
                }
                rr.close();
                hr.close();
                VerifInfo(lecture, nombre);
            }
            br.close();
            fr.close();

            //Se debe leer DIV antes!
        } catch (IOException e) {
            System.out.println("Error en GetInfo");
        }
    }

    public static void VerifInfo(ArrayList codewords, String nombre) {
        String generador = codewords.get(0).toString();
        String codeword = "";
        for (int i = 1; i < codewords.size(); i++) {
            codeword = codewords.get(i).toString() + codeword;
        }
        String ceros = "";
        for (int i = 0; i < generador.length(); i++) {
            ceros = ceros + "0";
        }
        System.out.println("El generador leido es: " + generador);
        System.out.println("El codeword completo : " + codeword);
        System.out.println("Los ceros son: " + ceros);
        if (HayError(codeword, generador, ceros) == true) {
            JOptionPane.showMessageDialog(null, "Hay un error en el archivo .crc leido");
        } else {
            JOptionPane.showMessageDialog(null, "No hay ningun error en el archivo .crc leido");
            int n = generador.length() - 1;
            String texto = "";
            for (int i = 1; i < codewords.size(); i++) {
                texto = texto + GetText(codewords.get(i).toString(), n);
            }
            nombre = nombre.substring(0, nombre.length() - 4);
            CreateTxt(texto, nombre);
            JOptionPane.showMessageDialog(null, "Se ha creado exitosamente el archivo con la decodificacion");
        }
    }

    public static void GetASCII(String characters) {//Nunca lo usamos... Pero bueno
        ASCIICode = "";
        for (int i = 0; i < characters.length(); i++) {
            ASCIICode = ASCIICode + Integer.toString((int) characters.charAt(i));
        }
        System.out.println("El codigo ASCII es: " + ASCIICode);
    }

    public static void GetHexBinDataWords(String characters) {//Se va conviertiendo el texto leido a hexadecimal y enseguida a binario, para ahorrar tiempo. Y enseguida se van separando las DataWords

        HexCode = "";//Se "limpia" la variable HexCode donde se va a guardar la traduccion de "text" a su codigo hexadecimal.
        Bin = "";//Se "limpia" la variable Bin donde se va a guardar la traduccion de los codigos Hexadecimales a Binario. Que es lo que se necesita enviar.
        String temp = "", as, bs;//Variables necesarias para convertir y agregar caracteres a los Strings.
        int a, b;
        DataWords = new ArrayList<>();//Inicializo donde voy a guardar los DataWords.
        String dataw = "";//Variable donde se guarden los binarios para formar los datawords.
        CodeWords = new ArrayList<>();//Inicializo la lista de codewords.
        for (int i = 0; i < characters.length(); i++) {
            temp = Integer.toHexString((int) characters.charAt(i));//Se convierte letra por letra a Hexadecimal, que siempre son dos letras. Ej: Letra:k -> Hex:4b
            HexCode = HexCode + temp;//Se le agrega al string que va a mantener todos los codigos hexadecimales en secuencia.
            a = Integer.parseInt(Character.toString(temp.charAt(0)), 16);//Se convierte el primer digito del numero hexadecimal a int para porder convertirlo a binario luego. Ej: String:4 -> Int:4
            b = Integer.parseInt(Character.toString(temp.charAt(1)), 16);//Se convierte el segundo digito a numero. Ej: String:B -> Int:11
            as = Integer.toBinaryString(a);//Convierte el int a binario. Ej: Int: 4 -> Bin: 100
            bs = Integer.toBinaryString(b);//Convierte el segundo int a binario. Ej: Int:11 -> Bin:1011
            while (as.length() < 4) {//Se le agregan los ceros necesario para completar los 4 digitos. Ej: Bin:100 -> String:0100
                as = "0" + as;
            }
            while (bs.length() < 4) {
                bs = "0" + bs;
            }
            Bin = Bin + as + bs;//Se guardan los codigos binarios. Cada caracter son 8 digitos binarios.

            dataw = dataw + as + bs;//Se le agrega el info al dataword que se esta formando.
            if (dataw.length() >= 128 || i == characters.length() - 1) {//Cuando el dataword llegue a los 128 digitos o cuando ya no se le vaya a agregar mas info, se agrega a la lista de DataWords.
                DataWords.add(dataw);
                CodeWords.add(GetCodeWord(dataw));//Se codifica enseguida.
                dataw = "";//Se reinicia la variable para grabar en ella los proximos 128 digitos.
            }
        }
        //System.out.println(HexCode);//Para comprobar
        //System.out.println(Bin);//Para comprobar
        System.out.println("Deberian haber " + (Bin.length() / 128 + 1) + " Datawords aproximadamente.");//Para comprobar
        //System.out.println("Lista con tamano: " + DataWords.size());//Para comprobar
        System.out.println("El ultimo data word es: " + (DataWords.get(DataWords.size() - 1)));//Para comprobar
        String codeword = "";
        for (int j = 0; j < CodeWords.size(); j++) {
            codeword = codeword + CodeWords.get(j).toString();
        }
        System.out.println("El CodeWord es: " + codeword);
        //JOptionPane.showMessageDialog(null, "El CodeWord generado es: " + codeword);
        String nombre = NombreS.substring(0, NombreS.length() - 4);
        CreateTxt(Generador, CodeWords, nombre); // Creo el archivo de salida
        JOptionPane.showMessageDialog(null, "Se ha generado exitosamente el archivo " + nombre + ".crc en la misma ruta de su archivo para codificar");
    }

    public static void AsignarPolinomioGenerador(String s) {
        Generador = s;
    }

    public static void AsignarNombreSalida(String s) {
        NombreS = s;
    }

    /*  
        A partir del dataword crea el codeword
        @param dataword
        @return codeword
     */
    public static String GetCodeWord(String dataword) {
        String dividendo = dataword;
        String ceros = "";
        for (int i = 0; i < Generador.length() - 1; i++) { //se generan la cantidad de 0's necesarios
            ceros = ceros + "0";
        }
        dividendo = dividendo + ceros; //se le adicionan los 0's al datawors
        //System.out.println("Dividendo=" + dividendo);
        ceros = ceros + "0";
        //System.out.println("Ceros=" + ceros);

        dividendo = dataword + Div(dividendo, Generador, ceros); //obtiene el residuo y se le adiciona al dataword para crear el codeword 
        //System.out.println("Dividendo: " + dividendo);
        return dividendo;
    }

    /**
     * Realiza la division binaria
     *
     * @param dividendo
     * @param divisor
     * @param ceros
     * @return residuo
     */
    public static String Div(String dividendo, String divisor, String ceros) {
        String residuo = dividendo.substring(0, divisor.length()); //se obtienen los primeros k digitos del divisor
        //System.out.println("Residuo 1: " + residuo);
        for (int i = 0; i <= dividendo.length() - divisor.length(); i++) { //se realiza la division

            if (residuo.startsWith("1")) { //si el residuo empieza por 1 el XOR se debe realizar con el divisor, de lo contrario con la cadena de 0's
                //System.out.println("Entro en 1");
                residuo = XOR(residuo.substring(1), divisor.substring(1));
                //System.out.println("Residuo XOR=" + residuo);
            } else {
                //System.out.println("Entro en 0");
                residuo = XOR(residuo.substring(1), ceros.substring(1));
                //System.out.println("Residuo XOR=" + residuo);
            }
            if (i != dividendo.length() - divisor.length()) {
                residuo = residuo + dividendo.substring(divisor.length() + i, divisor.length() + i + 1); //Baja el siguiente digito para que quede de la misma longitud que el divisor 
            }
            //System.out.println("Residuo " + i + "=" + residuo);
        }
        return residuo;
    }

    /**
     * Devuelve el XOR de las cadenas dadas
     *
     * @param a cadena binaria
     * @param b cadena binaria
     * @return la funcion XOR
     */
    public static String XOR(String a, String b) {
        String resultado = "";
        for (int i = 0; i < a.length(); i++) {
            if (a.substring(i, i + 1).equals(b.substring(i, i + 1))) {
                resultado = resultado + "0";
            } else {
                resultado = resultado + "1";
            }

        }
        return resultado;
    }

    public static boolean HayError(String dividendo, String divisor, String ceros) {//Comprueba Si una CodeWord tiene error
        String residuo = dividendo.substring(0, divisor.length());
        for (int i = 0; i <= dividendo.length() - divisor.length(); i++) { //se realiza la division

            if (residuo.startsWith("1")) { //si el residuo empieza por 1 el XOR se debe realizar con el divisor, de lo contrario con la cadena de 0's
                //System.out.println("Entro en 1");
                residuo = XOR(residuo.substring(1), divisor.substring(1));
                //System.out.println("Residuo XOR=" + residuo);
            } else {
                //System.out.println("Entro en 0");
                residuo = XOR(residuo.substring(1), ceros.substring(1));
                //System.out.println("Residuo XOR=" + residuo);
            }
            if (i != dividendo.length() - divisor.length()) {
                residuo = residuo + dividendo.substring(divisor.length() + i, divisor.length() + i + 1); //Baja el siguiente digito para que quede de la misma longitud que el divisor 
            }
        }
        System.out.println("Residuo: " + residuo);
        if (residuo.equals(ceros.substring(1))) {
            return false;
        } else {
            System.out.println("Si hubo error");
            return true;
        }
    }

    public static void GetInfo(File f) {//Comprueba que cada Codeword del .crc no tenga error
        String line;
        try {
            FileReader fr = new FileReader(f.getAbsolutePath());
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            AsignarPolinomioGenerador(line);
            String ceros = "";
            for (int i = 0; i < Generador.length(); i++) {
                ceros = ceros + "0";
            }
            line = br.readLine();
            String text = "";
            boolean NoHayError = true;
            while (line != null && NoHayError) {
                if (HayError(line, Generador, ceros)) {
                    NoHayError = false;
                    System.out.println("Hay un error en el mensaje.");
                } else {
                    text = text + GetText(line, Generador.length() - 1);
                }
                line = br.readLine();
            }
            br.close();
            fr.close();
            if (NoHayError) {
                System.out.println(text);
                String nombre = NombreS.substring(0, NombreS.length() - 4);
                CreateTxt(text, nombre);
            }
        } catch (IOException e) {
            System.out.println("Error en GetInfo");
        }
    }

    public static String GetText(String codewordCero, int n) {//n debe ser la longitud del generador MENOS 1
        String a, a1, c, c1;
        int b, b1;
        String text = "";
        String codeword = codewordCero.substring(0, codewordCero.length() - n);
        for (int i = 0; i < codeword.length() / 4; i = 2 + i) {
            a = codeword.substring(4 * i, 4 * (i + 1));
            a1 = codeword.substring(4 * (i + 1), 4 * (i + 2));
            b = Integer.parseInt(a, 2);
            b1 = Integer.parseInt(a1, 2);
            c = Integer.toHexString(b);
            c1 = Integer.toHexString(b1);
            text = text + hexToAscii(c + c1);
        }
        return text;
    }

    private static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static void CreateTxt(String text, String nombre) { // Le mando el generador, el CodeWord y el nombre del archivo que el usuario escogió
        File Archivo;
        FileWriter w;
        BufferedWriter bw;
        PrintWriter wr;
        Archivo = new File("C:\\ArchivosTxt\\" + nombre + ".txt"); // Crea el archivo en la direccion dada con el nombre escogido
        if (Archivo.exists()) {
            Archivo.delete();
            Archivo = new File("C:\\ArchivosTxt\\" + nombre + ".txt");
        }
        try {
            if (Archivo.createNewFile()) { // Verifica que el archivo se haya creado exitosamente
                w = new FileWriter(Archivo); // Se prepara para escribir en el archivo
                bw = new BufferedWriter(w);
                wr = new PrintWriter(bw);
                wr.write(text);
                wr.close();
                bw.close();
                System.out.println("Se ha creado exitosamente el archivo salida");
            }
        } catch (IOException e) {
            System.out.println("Ha habido un error creando el Archivo");
        }
    }

    public static char XORChar(char a, char b) {
        if (a == b) {
            return '0';
        } else {
            return '1';
        }
    }

    public static char XOR(String x) {//Recibe los bits de los cuales va a sacar el bit de paridad
        char t = x.charAt(0);
        for (int i = 1; i < x.length(); i++) {
            t = XORChar(t, x.charAt(i));
        }
        return t;
    }

    public static String CodificarHamming(String d) {
        char b1, b2, b3, b4;
        String temp = "" + d.charAt(7) + d.charAt(6) + d.charAt(4) + d.charAt(3) + d.charAt(1);
        b1 = XOR(temp);
        temp = "" + d.charAt(7) + d.charAt(5) + d.charAt(4) + d.charAt(2) + d.charAt(1);
        b2 = XOR(temp);
        temp = "" + d.charAt(6) + d.charAt(5) + d.charAt(4) + d.charAt(0);
        b3 = XOR(temp);
        temp = "" + d.charAt(3) + d.charAt(2) + d.charAt(1) + d.charAt(0);
        b4 = XOR(temp);
        System.out.println("" + b4 + b3 + b2 + b1);
        temp = "" + d.substring(0, 4) + b4 + d.substring(4, 7) + b3 + d.substring(7, 8) + b2 + b1;
        return temp;
    }

}
