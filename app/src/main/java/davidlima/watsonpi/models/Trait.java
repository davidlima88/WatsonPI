package davidlima.watsonpi.models;

public class Trait {
    private String traitId;
    private String name;
    private String category;
    private Float percentile;

    public Trait(String traitId, String name, String category, Float percentile) {
        this.traitId = traitId;
        this.name = name;
        this.category = category;
        this.percentile = percentile;
    }

    public String getTraitId() {
        return traitId;
    }

    public void setTraitId(String traitId) {
        this.traitId = traitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Float getPercentile() {
        return percentile;
    }

    public void setPercentile(Float percentile) {
        this.percentile = percentile;
    }
}
