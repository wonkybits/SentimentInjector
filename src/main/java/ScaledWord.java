/**
 * Created by pstene on 4/3/16.
 */
public class ScaledWord {
    private String word;
    private double scale;

    public ScaledWord(String word, double scale) {
        this.word = word;
        this.scale = scale;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }
}
