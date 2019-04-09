import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class SentimentInjector {
    // Standford CoreNLP variables
    private Properties props;
    private StanfordCoreNLP pipeline;
    private Annotation document;
    private String text;
    private List<CoreMap> sentences;
    private List<List<String>> tokenizedSentences;

    // Adjective arrays and lists
    private ArrayList<ScaledWord> anger_adj_arr;
    private ArrayList<ScaledWord> fear_adj_arr;
    private ArrayList<ScaledWord> joy_adj_arr;
    private ArrayList<ScaledWord> sadness_adj_arr;
    private ArrayList<ScaledWord> disgust_adj_arr;

    private final String ANGER_ADJS = "anger_adj.xml";
    private final String FEAR_ADJS = "fear_adj.xml";
    private final String SADNESS_ADJS = "sadness_adj.xml";
    private final String DISGUST_ADJS = "disgust_adj.xml";
    private final String JOY_ADJS = "joy_adj.xml";

    // sentiment variables
    private double scale;
    private char sentiment;

    // thesholds
    private final double ADJECTIVE_INJECTION_THRESHOLD = 1.0;

    // XML Parser
    XMLToSWArray xmlParser;

    public SentimentInjector(String text, double scale, char sentiment) {
        // set scale and sentiment
        this.scale = scale;
        this.sentiment = sentiment;

        // populate adjective arrays
        xmlParser = new XMLToSWArray();
        anger_adj_arr = xmlParser.parseXMLToSWaArray(ANGER_ADJS);
        fear_adj_arr = xmlParser.parseXMLToSWaArray(FEAR_ADJS);
        sadness_adj_arr = xmlParser.parseXMLToSWaArray(SADNESS_ADJS);
        disgust_adj_arr = xmlParser.parseXMLToSWaArray(DISGUST_ADJS);
        joy_adj_arr = xmlParser.parseXMLToSWaArray(JOY_ADJS);

        // setup Stanford CoreNLP
        this.props = new Properties();
        this.props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        this.pipeline = new StanfordCoreNLP(props);
        this.text = text;
        this.document = new Annotation(this.text);
        this.pipeline.annotate(document);
        sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        // tokenize sentences
        tokenizedSentences = new ArrayList<List<String>>();
        for(CoreMap sentence : sentences) {
            List<String> tokens = new ArrayList<String>();
            for(CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                tokens.add(token.word());
            }
            tokenizedSentences.add(tokens);
        }
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public char getSentiment() {
        return sentiment;
    }

    public void setSentiment(char sentiment) {
        this.sentiment = sentiment;
    }

    // determine the index of the dependent element in the Direct Object relation
    // -1 on error
    private int getDOIndex(int index) {
        int dobjDepIndex = -1;
        Collection typedDep = this.getTypedDependencies(index);
        Iterator<TypedDependency> iterator = typedDep.iterator();

        while(iterator.hasNext()) {
            TypedDependency currentDep = iterator.next();
            if(currentDep.reln().getShortName().compareTo("dobj") == 0) dobjDepIndex = currentDep.dep().index();
        }

        return dobjDepIndex;
    }

    // get typed dependencies
    private Collection getTypedDependencies(int index) {
        return sentences.get(index).get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).typedDependencies();
    }

    // assembles sentences from tokens
    public List<String> getMutatedSentences() {
        List<String> mutatedSentences = new ArrayList<String>();
        Iterator<List<String>> sentenceIterator = tokenizedSentences.iterator();
        while(sentenceIterator.hasNext()) {
            Iterator<String> tokenIterator = sentenceIterator.next().iterator();
            String output = tokenIterator.next();
            String token = "";
            while(tokenIterator.hasNext()) {
                token = tokenIterator.next();
                if(token.compareTo(",") != 0 && token.compareTo(".") != 0) {
                    output += " " + token;
                } else {
                    output += token;
                }
            }
            mutatedSentences.add(output);
        }

        return mutatedSentences;
    }

    // preform adjective injection
    public void injectAdjective() {
        String adjective = this.findBest();
        int index = 0;

        for(List list : tokenizedSentences) {
            try {
                list.add(this.getDOIndex(index)-1, adjective);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("The sentence:");
                System.out.println(list.toString());
                System.out.println("caused an error");
                System.out.println(this.getTypedDependencies(index).toString() + "\n");
            }
            index++;
        }
    }

    private ArrayList<ScaledWord> getList() {
        switch(this.sentiment) {
            case 'a':
                return this.anger_adj_arr;
            case 'd':
                return this.disgust_adj_arr;
            case 'f':
                return this.fear_adj_arr;
            case 'j':
                return this.joy_adj_arr;
            case 's':
                return this.sadness_adj_arr;
            default:
                return null;
        }
    }

    private String findBest() {
        String returnVal = null;
        double min = 10;
        double delta;

        for(ScaledWord sw : this.getList()) {
            delta = Math.abs(sw.getScale() - this.getScale());
            if (sw.getScale() == this.getScale()) {
                returnVal = sw.getWord();
            } else if (delta < min) {
                min = delta;
                returnVal = sw.getWord();
            }
        }

        return returnVal;
    }
}
