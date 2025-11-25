package backend.title;

import java.util.Arrays;

public class TitleInfo {
    public final String entryName;
    public final String[] args;

    public TitleInfo(String line) {
        String[] frags = line.strip().split(", ");
        args = new String[frags.length - 1];
        entryName = frags[0];

        System.arraycopy(frags, 1, args, 0, args.length);
    }

    /**
     * Workaround for other titleinfo instances being created with the same data but
     * don't have the same reference.
     * 
     * @param other The title info to compare to
     * @return If they are equal.
     */
    public boolean equals(TitleInfo other) {
        return entryName.equals(other.entryName) && Arrays.equals(args, other.args);
    }
}