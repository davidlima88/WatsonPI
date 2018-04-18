package davidlima.watsonpi.models;

public class Facet {
    private String term;
    private String description;

    public Facet(String term, String description) {
        this.term = term;
        this.description = description;
    }

    public String getTerm() {
        return term;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return term + ": " + description;
    }
}
