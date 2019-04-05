import java.util.Iterator;
import java.util.List;

public class main {
    public static void main(String[] args) {
        String testSentence = "While I was at the grocery store, I met a woman.";
        SentimentInjector si = new SentimentInjector(testSentence);

        si.injectAdjective();
//        si.printSentences();

        // check polarity
        List<String> sentences = si.getMutatedSentences();
        Iterator<String> sentenceIterator = sentences.iterator();
        int sentiment = 0;
        String currentSentence = "";
        while(sentenceIterator.hasNext()) {
            currentSentence = sentenceIterator.next();
            sentiment = SentigemChecker.checkPolarity(currentSentence);
            switch(sentiment) {
                case 1:
                    System.out.println(currentSentence + " -> Positive");
                    break;
                case -1:
                    System.out.println(currentSentence + " -> Negative");
                    break;
                case 0:
                    System.out.println(currentSentence + " -> Neutral");
                    break;
                default:
                    System.out.println("Invalid result returned from Sentigem");
            }
        }
    }
}
