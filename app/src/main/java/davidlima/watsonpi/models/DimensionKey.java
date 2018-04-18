package davidlima.watsonpi.models;

public class DimensionKey {

    public static final String T_AGREEABLENESS = "big5_agreeableness";
    public static final String T_CONSCIENTIOUSNESS = "big5_conscientiousness";
    public static final String T_EXTRAVERSION = "big5_extraversion";
    public static final String T_EMOTIONAL_RANGE = "big5_neuroticism";
    public static final String T_OPENNESS = "big5_openness";
    public static final int L_LOW = 0;
    public static final int L_HIGH = 1;

    private String primaryTrait;
    private int primaryLevel;
    private String secondaryTrait;
    private int secondaryLevel;

    public DimensionKey(String primaryTrait, int primaryLevel, String secondaryTrait, int secondaryLevel) {
        this.primaryTrait = primaryTrait;
        this.primaryLevel = primaryLevel;
        this.secondaryTrait = secondaryTrait;
        this.secondaryLevel = secondaryLevel;
    }

    @Override
    public int hashCode() {
        return primaryTrait.hashCode() +
                primaryLevel +
                secondaryTrait.hashCode() +
                secondaryLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DimensionKey)) return false;
        DimensionKey p = (DimensionKey) obj;
        return p.primaryTrait.equals(primaryTrait) &&
                p.primaryLevel == primaryLevel &&
                p.secondaryTrait.equals(secondaryTrait) &&
                p.secondaryLevel == secondaryLevel;
    }
}
