package davidlima.watsonpi.models;

public class FacetKey {

    private String facet;
    private int level;

    public FacetKey(String facet, int level) {
        this.facet = facet;
        this.level = level;
    }

    @Override
    public int hashCode() {
        return level + facet.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FacetKey)) return false;
        FacetKey p = (FacetKey) obj;
        return p.facet.equals(facet) && p.level == level;
    }
}
