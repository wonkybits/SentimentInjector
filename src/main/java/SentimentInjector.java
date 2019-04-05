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
    private Properties props;
    private StanfordCoreNLP pipeline;
    private Annotation document;
    private String text;
    private List<CoreMap> sentences;
    private List<List<String>> tokenizedSentences;

    public SentimentInjector(String text) {
        // setup Stanford CoreNLP
        this.props = new Properties();
        this.props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        this.pipeline = new StanfordCoreNLP(props);
        this.text = text;
        this.document = new Annotation(this.text);
        this.pipeline.annotate(document);
        sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        tokenizedSentences = new ArrayList<List<String>>();

        for(CoreMap sentence : sentences) {
            List<String> tokens = new ArrayList<String>();
            for(CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                tokens.add(token.word());
            }
            tokenizedSentences.add(tokens);
        }
    }

    // handles one sentence at the moment
    private int getDOIndex() {
        int dobjDepIndex = -1;
        SemanticGraph dependencies = sentences.get(0).get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
        Collection typedDep = dependencies.typedDependencies();
        Iterator<TypedDependency> iterator = typedDep.iterator();

        while(iterator.hasNext()) {
            TypedDependency currentDep = iterator.next();
            if(currentDep.reln().getShortName().compareTo("dobj") == 0) dobjDepIndex = currentDep.dep().index();
        }

        return dobjDepIndex;
    }

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

    public void injectAdjective() {
        String adjective = "beautiful";

        for(List list : tokenizedSentences) {
            list.add(this.getDOIndex()-1, adjective);
        }
    }
}
