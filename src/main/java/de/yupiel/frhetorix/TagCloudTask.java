package de.yupiel.frhetorix;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class TagCloudTask implements Supplier<String[]> {
    private String[] words;

    public TagCloudTask(String[] words) {
        this.words = words;
    }

    @Override
    public String[] get() {
        try {
            TimeUnit.MINUTES.sleep(1);
            return this.words;
        } catch (InterruptedException e) {
            System.out.println("Tag cloud generation interrupted");
            System.out.println(Arrays.toString(e.getStackTrace()));

            return null;
        }
    }
}

