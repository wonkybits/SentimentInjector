import org.apache.log4j.BasicConfigurator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class main {
    public static void main(String[] args) {
        // logger config
        BasicConfigurator.configure();


        String testSentence = "While I was at the grocery store, I met a woman.";
        List<String> seeds = new ArrayList<String>();
        String document = "";
        seeds.add("While I was at the grocery store, I met a woman.");
        seeds.add("I took the freeway to work today.");
        seeds.add("I had an orange along with my sandwich.");
        seeds.add("Why does Joe make so much money?");
        seeds.add("I remember in college, at the food court, when I had a hamburger.");

        Iterator<String> seedsIterator = seeds.iterator();
        while(seedsIterator.hasNext()) {
            document += seedsIterator.next();
            if(seedsIterator.hasNext()) document += " ";
        }

        SentimentInjector si = new SentimentInjector(document, 7.0, 'j');

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
