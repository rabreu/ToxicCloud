package br.toxiccloud;

import java.util.Arrays;
import java.util.logging.Logger;

public class Main {

    static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String args[]) {
        long inicio = System.currentTimeMillis();
        new ToxicCloud().start(Arrays.stream(args).findFirst().orElse(null));
        long fim = System.currentTimeMillis();
        LOGGER.info("Tempo de execução: " + ((fim - inicio) / 1000.0) + " segundos.");
    }
}
