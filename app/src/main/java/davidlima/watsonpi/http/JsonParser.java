package davidlima.watsonpi.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import davidlima.watsonpi.models.DimensionKey;
import davidlima.watsonpi.models.Facet;
import davidlima.watsonpi.models.FacetKey;
import davidlima.watsonpi.models.Trait;

public class JsonParser {

    private static JsonParser jsonParser;

    public final String wordCount;
    public final TreeSet<Trait> needsTS;
    public final TreeSet<Trait> valuesTS;
    public final TreeSet<Trait> big5TS;
    public final TreeSet<Trait> facetsTS;
    public final TreeMap<Float, String> characteristicsTS;

    private JsonParser(String json) {
        wordCount = getWordCount(json);
        needsTS = getNeedsTreeSet(json);
        valuesTS = getValuesTreeSet(json);
        big5TS = getBig5TreeSet(json);
        facetsTS = getFacetsTreeSet(json);
        characteristicsTS = getCharacteristicsTreeSet();
    }

    public static JsonParser getInstance(String json) {
        if (jsonParser == null) {
            jsonParser = new JsonParser(json);
        }
        return jsonParser;
    }

    private String getWordCount(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            return obj.getString("word_count");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TreeSet<Trait> getNeedsTreeSet(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray myArray = obj.getJSONArray("needs");
            TreeSet<Trait> myTreeSet = new TreeSet<>(new Comparator<Trait>() {
                @Override
                public int compare(Trait o1, Trait o2) {
                    return o1.getPercentile().compareTo(o2.getPercentile());
                }
            });

            for (int i = 0; i < myArray.length(); i++) {
                JSONObject myJsonObject = myArray.getJSONObject(i);
                myTreeSet.add(new Trait(
                        myJsonObject.getString("trait_id"),
                        myJsonObject.getString("name"),
                        myJsonObject.getString("category"),
                        (float) myJsonObject.getDouble("percentile")));
            }
            return myTreeSet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TreeSet<Trait> getValuesTreeSet(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray myArray = obj.getJSONArray("values");
            TreeSet<Trait> myTreeSet = new TreeSet<>(new Comparator<Trait>() {
                @Override
                public int compare(Trait o1, Trait o2) {
                    return o1.getPercentile().compareTo(o2.getPercentile());
                }
            });

            for (int i = 0; i < myArray.length(); i++) {
                JSONObject myJsonObject = myArray.getJSONObject(i);
                myTreeSet.add(new Trait(
                        myJsonObject.getString("trait_id"),
                        myJsonObject.getString("name"),
                        myJsonObject.getString("category"),
                        (float) myJsonObject.getDouble("percentile")));
            }
            return myTreeSet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TreeSet<Trait> getBig5TreeSet(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray myArray = obj.getJSONArray("personality");
            TreeSet<Trait> myTreeSet = new TreeSet<>(new Comparator<Trait>() {
                @Override
                public int compare(Trait o1, Trait o2) {
                    return o1.getPercentile().compareTo(o2.getPercentile());
                }
            });

            for (int i = 0; i < myArray.length(); i++) {
                JSONObject myJsonObject = myArray.getJSONObject(i);
                myTreeSet.add(new Trait(
                        myJsonObject.getString("trait_id"),
                        myJsonObject.getString("name"),
                        myJsonObject.getString("category"),
                        (float) myJsonObject.getDouble("percentile")));
            }
            return myTreeSet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TreeSet<Trait> getFacetsTreeSet(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray myArray = obj.getJSONArray("personality");
            TreeSet<Trait> myTreeSet = new TreeSet<>(new Comparator<Trait>() {
                @Override
                public int compare(Trait o1, Trait o2) {
                    return o1.getPercentile().compareTo(o2.getPercentile());
                }
            });

            for (int i = 0; i < myArray.length(); i++) {
                JSONObject myJsonObject = myArray.getJSONObject(i);
                JSONArray children = myJsonObject.getJSONArray("children");
                for (int j = 0; j < children.length(); j++) {
                    JSONObject child = children.getJSONObject(j);
                    myTreeSet.add(new Trait(
                            child.getString("trait_id"),
                            child.getString("name"),
                            child.getString("category"),
                            (float) child.getDouble("percentile")));
                }
            }
            return myTreeSet;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TreeMap<Float, String> getCharacteristicsTreeSet() {
        TreeMap<Float, String> characteristicsTS = new TreeMap<>();
        for (Trait primaryTrait : big5TS) {
            for (Trait secondaryTrait : big5TS) {
                if (!primaryTrait.equals(secondaryTrait)) {
                    String primaryTreatId = primaryTrait.getTraitId();
                    int primaryTreatLevel = primaryTrait.getPercentile() < 0.5 ? 0 : 1;
                    String secondaryTreatId = secondaryTrait.getTraitId();
                    int secondaryTreatLevel = secondaryTrait.getPercentile() < 0.5 ? 0 : 1;
                    float relevance = primaryTrait.getPercentile() < 0.5 ?
                            secondaryTrait.getPercentile() < 0.5 ?
                                    (1f - primaryTrait.getPercentile()) * (1f - secondaryTrait.getPercentile()) :
                                    (1f - primaryTrait.getPercentile()) * secondaryTrait.getPercentile()
                            :
                            secondaryTrait.getPercentile() < 0.5 ?
                                    primaryTrait.getPercentile() * (1f - secondaryTrait.getPercentile()) :
                                    primaryTrait.getPercentile() * secondaryTrait.getPercentile();
                    characteristicsTS.put(relevance,
                            characteristics.get(new DimensionKey(primaryTreatId, primaryTreatLevel, secondaryTreatId, secondaryTreatLevel))
                    );
                }
            }
        }
        return characteristicsTS;
    }

    public static String getTraitDescription(Trait trait) {
        return definitions.get(trait.getTraitId());
    }

    public static Facet getFacetDescription(Trait trait) {
        return facets.get(new FacetKey(trait.getTraitId(), trait.getPercentile() < 0.5 ? 0 : 1));
    }

    private static Map<String, String> definitions;

    static {
        Map<String, String> aMap = new HashMap<>();
        aMap.put("big5_agreeableness", "is a person's tendency to be compassionate and cooperative toward others.");
        aMap.put("facet_altruism", "Find that helping others is genuinely rewarding, that doing things for others is a form of self-fulfillment rather than self-sacrifice.");
        aMap.put("facet_cooperation", "Dislike confrontation. They are perfectly willing to compromise or to deny their own needs to get along with others.");
        aMap.put("facet_modesty", "Are unassuming, rather self-effacing, and humble. However, they do not necessarily lack self-confidence or self-esteem.");
        aMap.put("facet_morality", "See no need for pretense or manipulation when dealing with others and are therefore candid, frank, and genuine.");
        aMap.put("facet_sympathy", "Are tender-hearted and compassionate.");
        aMap.put("facet_trust", "Assume that most people are fundamentally fair, honest, and have good intentions. They take people at face value and are willing to forgive and forget.");
        aMap.put("big5_conscientiousness", "is a person's tendency to act in an organized or thoughtful way.");
        aMap.put("facet_achievement_striving", "Try hard to achieve excellence. Their drive to be recognized as successful keeps them on track as they work hard to accomplish their goals.");
        aMap.put("facet_cautiousness", "Are disposed to think through possibilities carefully before acting.");
        aMap.put("facet_dutifulness", "Have a strong sense of duty and obligation.");
        aMap.put("facet_orderliness", "Are well-organized, tidy, and neat.");
        aMap.put("facet_self_discipline", "Have the self-discipline, or 'will-power', to persist at difficult or unpleasant tasks until they are completed.");
        aMap.put("facet_self_efficacy", "Are confident in their ability to accomplish things.");
        aMap.put("big5_extraversion", "is a person's tendency to seek stimulation in the company of others.");
        aMap.put("facet_activity_level", "Lead fast-paced and busy lives. They do things and move about quickly, energetically, and vigorously, and they are involved in many activities.");
        aMap.put("facet_assertiveness", "Like to take charge and direct the activities of others. They tend to be leaders in groups.");
        aMap.put("facet_cheerfulness", "Experience a range of positive feelings, including happiness, enthusiasm, optimism, and joy.");
        aMap.put("facet_excitement_seeking", "Are easily bored without high levels of stimulation.");
        aMap.put("facet_friendliness", "Genuinely like other people and openly demonstrate positive feelings toward others.");
        aMap.put("facet_gregariousness", "Find the company of others pleasantly stimulating and rewarding. They enjoy the excitement of crowds.");
        aMap.put("big5_neuroticism", "also referred to as Neuroticism or Natural reactions, is the extent to which a person's emotions are sensitive to the person's environment.");
        aMap.put("facet_anger", "Have a tendency to feel angry.");
        aMap.put("facet_anxiety", "Often feel like something unpleasant, threatening, or dangerous is about to happen. The \"fight-or-flight\" system of their brains is too easily and too often engaged.");
        aMap.put("facet_depression", "Tend to react more readily to life's ups and downs.");
        aMap.put("facet_immoderation", "Feel strong cravings and urges that they have difficulty resisting, even though they know that they are likely to regret them later. They tend to be oriented toward short-term pleasures and rewards rather than long-term consequences.");
        aMap.put("facet_self_consciousness", "Are sensitive about what others think of them. Their concerns about rejection and ridicule cause them to feel shy and uncomfortable around others; they are easily embarrassed.");
        aMap.put("facet_vulnerability", "Have difficulty coping with stress. They experience panic, confusion, and helplessness when under pressure or when facing emergency situations.");
        aMap.put("big5_openness", "is the extent to which a person is open to experiencing a variety of activities.");
        aMap.put("facet_adventurousness", "Are eager to try new activities and experience different things. They find familiarity and routine boring.");
        aMap.put("facet_artistic_interests", "Love beauty, both in art and in nature. They become easily involved and absorbed in artistic and natural events. With intellect, this facet is one of the two most important, central aspects of this characteristic.");
        aMap.put("facet_emotionality", "Have good access to and awareness of their own feelings.");
        aMap.put("facet_imagination", "View the real world as often too plain and ordinary. They use fantasy not as an escape but as a way of creating for themselves a richer and more interesting inner-world.");
        aMap.put("facet_intellect", "Are intellectually curious and tend to think in symbols and abstractions. With artistic interests, this facet is one of the two most important, central aspects of this characteristic.");
        aMap.put("facet_liberalism", "Have a readiness to challenge authority, convention, and traditional values.");
        aMap.put("needs", "describe which aspects of a product will resonate with a person. The model includes twelve characteristic needs.");
        aMap.put("need_excitement", "dWant to get out there and live life, have upbeat emotions, and want to have fun.");
        aMap.put("need_harmony", "Appreciate other people, their viewpoints, and their feelings.");
        aMap.put("need_curiosity", "Have a desire to discover, find out, and grow.");
        aMap.put("need_ideal", "Desire perfection and a sense of community.");
        aMap.put("need_closeness", "Relish being connected to family and setting up a home.");
        aMap.put("need_liberty", "Have a desire for fashion and new things, as well as the need for escape.");
        aMap.put("need_love", "Enjoy social contact, whether one-to-one or one-to-many. Any brand that is involved in bringing people together taps this need.");
        aMap.put("need_practicality", "Have a desire to get the job done, a desire for skill and efficiency, which can include physical expression and experience.");
        aMap.put("need_stability", "Seek equivalence in the physical world. They favor the sensible, the tried and tested.");
        aMap.put("need_challenge", "Have an urge to achieve, to succeed, and to take on challenges.");
        aMap.put("need_structure", "Exhibit groundedness and a desire to hold things together. They need things to be well organized and under control.");
        aMap.put("values", "describe motivating factors that influence a person's decision making. The model includes five values.");
        aMap.put("value_self_transcendence", "Show concern for the welfare and interests of others.");
        aMap.put("value_conservation", "Emphasize self-restriction, order, and resistance to change.");
        aMap.put("value_hedonism", "Seek pleasure and sensuous gratification for themselves.");
        aMap.put("value_self_enhancement", "Seek personal success for themselves.");
        aMap.put("value_openness_to_change", "Emphasize independent action, thought, and feeling, as well as a readiness for new experiences.");
        definitions = Collections.unmodifiableMap(aMap);
    }

    private static Map<FacetKey, Facet> facets;

    static {
        Map<FacetKey, Facet> aMap = new HashMap<>();
        aMap.put(new FacetKey("facet_altruism", 0), new Facet("Self-focused", "You are more concerned with taking care of yourself than taking time for others."));
        aMap.put(new FacetKey("facet_altruism", 1), new Facet("Altruistic", "You feel fulfilled when helping others and will go out of your way to do so."));
        aMap.put(new FacetKey("facet_cooperation", 0), new Facet("Contrary", "You do not shy away from contradicting others."));
        aMap.put(new FacetKey("facet_cooperation", 1), new Facet("Accommodating", "You are easy to please and try to avoid confrontation."));
        aMap.put(new FacetKey("facet_modesty", 0), new Facet("Proud", "You hold yourself in high regard and are satisfied with who you are."));
        aMap.put(new FacetKey("facet_modesty", 1), new Facet("Modest", "You are uncomfortable being the center of attention."));
        aMap.put(new FacetKey("facet_morality", 0), new Facet("Compromising", "You are comfortable using every trick in the book to get what you want."));
        aMap.put(new FacetKey("facet_morality", 1), new Facet("Uncompromising", "You think it is wrong to take advantage of others to get ahead."));
        aMap.put(new FacetKey("facet_sympathy", 0), new Facet("Hard-hearted", "You think people should generally rely more on themselves than on others."));
        aMap.put(new FacetKey("facet_sympathy", 1), new Facet("Empathetic", "You feel what others feel and are compassionate toward them."));
        aMap.put(new FacetKey("facet_trust", 0), new Facet("Cautious of others", "You are wary of other people's intentions and do not trust easily."));
        aMap.put(new FacetKey("facet_trust", 1), new Facet("Trusting of others", "You believe the best in others and trust people easily."));
        aMap.put(new FacetKey("facet_achievement_striving", 0), new Facet("Content", "You are content with your level of accomplishment and do not feel the need to set ambitious goals."));
        aMap.put(new FacetKey("facet_achievement_striving", 1), new Facet("Driven", "You set high goals for yourself and work hard to achieve them."));
        aMap.put(new FacetKey("facet_cautiousness", 0), new Facet("Bold", "You would rather take action immediately than spend time deliberating making a decision."));
        aMap.put(new FacetKey("facet_cautiousness", 1), new Facet("Deliberate", "You carefully think through decisions before making them."));
        aMap.put(new FacetKey("facet_dutifulness", 0), new Facet("Carefree", "You do what you want, disregarding rules and obligations."));
        aMap.put(new FacetKey("facet_dutifulness", 1), new Facet("Dutiful", "You take rules and obligations seriously, even when they are inconvenient."));
        aMap.put(new FacetKey("facet_orderliness", 0), new Facet("Unstructured", "You do not make a lot of time for organization in your daily life."));
        aMap.put(new FacetKey("facet_orderliness", 1), new Facet("Organized", "You feel a strong need for structure in your life."));
        aMap.put(new FacetKey("facet_self_discipline", 0), new Facet("Intermittent", "You have a hard time sticking with difficult tasks for a long period of time."));
        aMap.put(new FacetKey("facet_self_discipline", 1), new Facet("Persistent", "You can tackle and stick with tough tasks."));
        aMap.put(new FacetKey("facet_self_efficacy", 0), new Facet("Self-doubting", "You frequently doubt your ability to achieve your goals."));
        aMap.put(new FacetKey("facet_self_efficacy", 1), new Facet("Self-assured", "You feel you have the ability to succeed in the tasks you set out to do."));
        aMap.put(new FacetKey("facet_activity_level", 0), new Facet("Laid-back", "You appreciate a relaxed pace in life."));
        aMap.put(new FacetKey("facet_activity_level", 1), new Facet("Energetic", "You enjoy a fast-paced, busy schedule with many activities."));
        aMap.put(new FacetKey("facet_assertiveness", 0), new Facet("Demure", "You prefer to listen than to talk, especially in group situations."));
        aMap.put(new FacetKey("facet_assertiveness", 1), new Facet("Assertive", "You tend to speak up and take charge of situations, and you are comfortable leading groups."));
        aMap.put(new FacetKey("facet_cheerfulness", 0), new Facet("Solemn", "You are generally serious and do not joke much."));
        aMap.put(new FacetKey("facet_cheerfulness", 1), new Facet("Cheerful", "You are a joyful person and share that joy with the world."));
        aMap.put(new FacetKey("facet_excitement_seeking", 0), new Facet("Calm-seeking", "You prefer activities that are quiet, calm, and safe."));
        aMap.put(new FacetKey("facet_excitement_seeking", 1), new Facet("Excitement-seeking", "You are excited by taking risks and feel bored without lots of action going on."));
        aMap.put(new FacetKey("facet_friendliness", 0), new Facet("Reserved", "You are a private person and do not let many people in."));
        aMap.put(new FacetKey("facet_friendliness", 1), new Facet("Outgoing", "You make friends easily and feel comfortable around other people."));
        aMap.put(new FacetKey("facet_gregariousness", 0), new Facet("Independent", "You have a strong desire to have time to yourself."));
        aMap.put(new FacetKey("facet_gregariousness", 1), new Facet("Socialble", "You enjoy being in the company of others."));
        aMap.put(new FacetKey("facet_anger", 0), new Facet("Mild-tempered", "It takes a lot to get you angry."));
        aMap.put(new FacetKey("facet_anger", 1), new Facet("Fiery", "You have a fiery temper, especially when things do not go your way."));
        aMap.put(new FacetKey("facet_anxiety", 0), new Facet("Self-assured", "You tend to feel calm and self-assured."));
        aMap.put(new FacetKey("facet_anxiety", 1), new Facet("Prone to worry", "You tend to worry about things that might happen."));
        aMap.put(new FacetKey("facet_depression", 0), new Facet("Content", "You are generally comfortable with yourself as you are."));
        aMap.put(new FacetKey("facet_depression", 1), new Facet("Melancholy", "You think quite often about the things you are unhappy about."));
        aMap.put(new FacetKey("facet_immoderation", 0), new Facet("Self-controlled", "You have control over your desires, which are not particularly intense."));
        aMap.put(new FacetKey("facet_immoderation", 1), new Facet("Hedonistic", "You feel your desires strongly and are easily tempted by them."));
        aMap.put(new FacetKey("facet_self_consciousness", 0), new Facet("Confident", "You are hard to embarrass and are self-confident most of the time."));
        aMap.put(new FacetKey("facet_self_consciousness", 1), new Facet("Self-conscious", "You are sensitive about what others might be thinking of you."));
        aMap.put(new FacetKey("facet_vulnerability", 0), new Facet("Calm under pressure", "You handle unexpected events calmly and effectively."));
        aMap.put(new FacetKey("facet_vulnerability", 1), new Facet("Susceptible to stress", "You are easily overwhelmed in stressful situations."));
        aMap.put(new FacetKey("facet_adventurousness", 0), new Facet("Consistent", "You enjoy familiar routines and prefer not to deviate from them."));
        aMap.put(new FacetKey("facet_adventurousness", 1), new Facet("Adventurous", "You are eager to experience new things."));
        aMap.put(new FacetKey("facet_artistic_interests", 0), new Facet("Unconcerned with art", "You are less concerned with artistic or creative activities than most people."));
        aMap.put(new FacetKey("facet_artistic_interests", 1), new Facet("Appreciative of art", "You enjoy beauty and seek out creative experiences."));
        aMap.put(new FacetKey("facet_emotionality", 0), new Facet("Dispassionate", "You do not frequently think about or openly express your emotions."));
        aMap.put(new FacetKey("facet_emotionality", 1), new Facet("Emotionally aware", "You are aware of your feelings and how to express them."));
        aMap.put(new FacetKey("facet_imagination", 0), new Facet("Drown-to-earth", "You prefer facts over fantasy."));
        aMap.put(new FacetKey("facet_imagination", 1), new Facet("Imaginative", "You have a wild imagination."));
        aMap.put(new FacetKey("facet_intellect", 0), new Facet("Concrete", "You prefer dealing with the world as it is, rarely considering abstract ideas."));
        aMap.put(new FacetKey("facet_intellect", 1), new Facet("Philosophical", "You are open to and intrigued by new ideas and love to explore them."));
        aMap.put(new FacetKey("facet_liberalism", 0), new Facet("Respectful of authority", "You prefer following with tradition to maintain a sense of stability."));
        aMap.put(new FacetKey("facet_liberalism", 1), new Facet("Authority-challenging", "You prefer to challenge authority and traditional values to effect change."));
        facets = Collections.unmodifiableMap(aMap);
    }

    private static Map<DimensionKey, String> characteristics;

    static {
        Map<DimensionKey, String> aMap = new HashMap<>();
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_conscientiousness", 1), "Helpful, cooperative, considerate, respectful, polite");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_conscientiousness", 0), "Unpretentious, self-effacing");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_conscientiousness", 1), "Strict, rigid, stern");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_conscientiousness", 0), "Inconsiderate, impolite, distrustful, uncooperative, thoughtless");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_extraversion", 1), "Effervescent, happy, friendly, merry, jovial");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_extraversion", 0), "Soft-hearted, agreeable, obliging, humble, lenient");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_extraversion", 1), "Bullheaded, abrupt, crude, combative, rough");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_extraversion", 0), "Cynical, wary of others, reclusive, detached, impersonal");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_neuroticism", 1), "Sentimental, affectionate, sensitive, soft, passionate");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_neuroticism", 0), "Generous, pleasant, tolerant, peaceful, flexible");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_neuroticism", 1), "Critical, selfish, ill-tempered, antagonistic, grumpy");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_neuroticism", 0), "Insensitive, unaffectionate, passionless, unemotional");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_openness", 1), "Genial, tactful, diplomatic, deep, idealistic");
        aMap.put(new DimensionKey("big5_agreeableness", 1, "big5_openness", 0), "Dependent, simple");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_openness", 1), "Shrewd, eccentric, individualistic");
        aMap.put(new DimensionKey("big5_agreeableness", 0, "big5_openness", 0), "Coarse, tactless, curt, narrow-minded, callous");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_agreeableness", 1), "Dependable, responsible, reliable, mannerly, considerate");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_agreeableness", 0), "Stern, strict, rigid");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_agreeableness", 1), "Unpretentious, self-effacing");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_agreeableness", 0), "Rash, uncooperative, unreliable, distrustful, thoughtless");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_extraversion", 1), "Ambitious, alert, firm, purposeful, competitive");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_extraversion", 0), "Cautious, confident, punctual, formal, thrifty");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_extraversion", 1), "Unruly, boisterous, reckless, devil-may-care, demonstrative");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_extraversion", 0), "Indecisive, aimless, wishy-washy, noncommittal, un-ambitious");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_neuroticism", 1), "Particular, high-strung");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_neuroticism", 0), "Thorough, steady, consistent, self-disciplined, logical");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_neuroticism", 1), "Scatterbrained, inconsistent, erratic, forgetful, impulsive");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_neuroticism", 0), "Informal, low-key");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_openness", 1), "Sophisticated, perfectionistic, industrious, dignified, refined");
        aMap.put(new DimensionKey("big5_conscientiousness", 1, "big5_openness", 0), "Traditional, conventional");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_openness", 1), "Unconventional, quirky");
        aMap.put(new DimensionKey("big5_conscientiousness", 0, "big5_openness", 0), "Foolhardy, illogical, immature, haphazard, lax");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_agreeableness", 1), "Social, energetic, enthusiastic, communicative, vibrant");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_agreeableness", 0), "Opinionated, forceful, domineering, boastful, bossy");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_agreeableness", 1), "Unaggressive, humble, submissive, timid, compliant");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_agreeableness", 0), "Skeptical, wary of others, seclusive, uncommunicative, unsociable");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_conscientiousness", 1), "Active, competitive, persistent, ambitious, purposeful");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_conscientiousness", 0), "Boisterous, mischievous, exhibitionistic, gregarious, demonstrative");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_conscientiousness", 1), "Restrained, serious, discreet, cautious, principled");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_conscientiousness", 0), "Indirect, unenergetic, sluggish, non-persistent, vague");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_neuroticism", 1), "Explosive, wordy, extravagant, volatile, flirtatious");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_neuroticism", 0), "Confident, bold, assured, uninhibited, courageous");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_neuroticism", 1), "Guarded, pessimistic, secretive, cowardly");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_neuroticism", 0), "Tranquil, sedate, placid, impartial, unassuming");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_openness", 1), "Expressive, candid, dramatic, spontaneous, witty");
        aMap.put(new DimensionKey("big5_extraversion", 1, "big5_openness", 0), "Verbose, unscrupulous, pompous");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_openness", 1), "Inner-directed, introspective, meditative, contemplating, self-examining");
        aMap.put(new DimensionKey("big5_extraversion", 0, "big5_openness", 0), "Somber, meek, unadventurous, passive, apathetic");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_agreeableness", 1), "Emotional, gullible, affectionate, sensitive, soft");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_agreeableness", 0), "Temperamental, irritable, quarrelsome, impatient, grumpy");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_agreeableness", 1), "Patient, relaxed, undemanding, down-to-earth");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_agreeableness", 0), "Unemotional, insensitive, unaffectionate, passionless");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_conscientiousness", 1), "Particular, high-strung");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_conscientiousness", 0), "Compulsive, nosy, self-indulgent, forgetful, impulsive");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_conscientiousness", 1), "Rational, objective, steady, logical, decisive");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_conscientiousness", 0), "Informal, low-key");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_extraversion", 1), "Excitable, wordy, flirtatious, explosive, extravagant");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_extraversion", 0), "Guarded, fretful, insecure, pessimistic, secretive");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_extraversion", 1), "Unselfconscious, weariless, indefatigable");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_extraversion", 0), "Unassuming, unexcitable, placid, tranquil");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_openness", 1), "Excitable, passionate, sensual");
        aMap.put(new DimensionKey("big5_neuroticism", 1, "big5_openness", 0), "Easily rattled, easily irked, apprehensive");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_openness", 1), "Heartfelt, versatile, creative, intellectual, insightful");
        aMap.put(new DimensionKey("big5_neuroticism", 0, "big5_openness", 0), "Imperturbable, insensitive");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_agreeableness", 1), "Idealistic, diplomatic, deep, tactful, genial");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_agreeableness", 0), "Shrewd, eccentric, individualistic");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_agreeableness", 1), "Simple, dependent");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_agreeableness", 0), "Coarse, tactless, curt, narrow-minded, callous");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_conscientiousness", 1), "Analytical, perceptive, informative, articulate, dignified");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_conscientiousness", 0), "Unconventional, quirky");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_conscientiousness", 1), "Conventional, traditional");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_conscientiousness", 0), "Shortsighted, foolhardy, illogical, immature, haphazard");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_extraversion", 1), "Worldly, theatrical, eloquent, inquisitive, intense");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_extraversion", 0), "Introspective, meditative, contemplating, self-examining, inner-directed");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_extraversion", 1), "Verbose, unscrupulous, pompous");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_extraversion", 0), "Predictable, unimaginative, somber, apathetic, unadventurous");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_neuroticism", 1), "Passionate, excitable, sensual");
        aMap.put(new DimensionKey("big5_openness", 1, "big5_neuroticism", 0), "Creative, intellectual, insightful, versatile, inventive");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_neuroticism", 1), "Easily rattled, easily irked, apprehensive");
        aMap.put(new DimensionKey("big5_openness", 0, "big5_neuroticism", 0), "Imperturbable, insensitive");
        characteristics = Collections.unmodifiableMap(aMap);
    }
}
