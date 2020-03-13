package ch.reinhard;

public class Main {

    public static void main(String[] args) {
        String fotoVerzeichnis = "/Users/beatreinhard/Documents/Fotos/";

        if (args.length > 0) {
            fotoVerzeichnis =args[0];

            if (!fotoVerzeichnis.endsWith("/")) {
                fotoVerzeichnis = fotoVerzeichnis + "/";
            }
        }

        FotoSorter fotoSorter = new FotoSorter(fotoVerzeichnis);
        fotoSorter.sortiereFotos();

    }

}
