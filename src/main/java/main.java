public class main {
    public static void main(String[] args) {
        String testSentence = "While I was at the grocery store, I met a woman.";
        SentimentInjector si = new SentimentInjector(testSentence);

        si.injectAdjective();
        si.printSentences();
    }
}
