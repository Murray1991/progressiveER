/**
 * - NaiveProgressiveSn:                    naivePSN
 * - LocalWeightedProgressiveSn(ws=null):   naivePSN w/o duplicate comparisons within W
 * - LocalWeightedProgressiveSn(ws):        PSN local ordering w/o duplicate comparisons within W
 * - ProgressiveSnHeap:                     PSN hybrid ordering w/ duplicate comparison (they could be removed)
 * - GlobalNaiveProgressiveSnIterator:      naivePSN w/o duplicate comparisons (ALL duplicates comparisons are removed)
 * - GlobalWeightedProgressiveSn(ws=null):  For each entity retain a fixed num of comparisons
 * - GlobalWeightedProgressiveSn(ws):       For each entity retain a fixed num of comparisons + WEIGHTS
 */
package Experiments.complete_experiment;

import BlockProcessing.ComparisonRefinement.AbstractDuplicatePropagation;
import DataStructures.Comparison;
import DataStructures.EntityProfile;
import DataStructures.SchemaBasedProfiles.ProfileType;
import Experiments.Utilities;
import Experiments.Utility.Result;
import Experiments.complete_experiment.MethodsList.ExperimentsSN_list;
import gr.demokritos.iit.jinsect.algorithms.estimators.DistanceEstimator;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.Levenshtein;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author giovanni
 */
public class CepExperimentsSN {

    /*
    "censusProfiles",
    "restaurantProfiles",
    "coraProfiles",
    "cddbProfiles"
    */

    /**
     * For the baseline (psn):s
     * To do schema_aware modify getKey functino in ExperimentSN_list
     */

    private static boolean CLEAN = false;
    private static String BASEPATH = "/Users/gio/Desktop/umich/data/data_blockingFramework/";

    private static double MAX_PC = 1.0;
    private static int MAX_WINDOW = 500;
    private static boolean REMOVE_REPETED = false;

    private static boolean COMPUTE_DISTANCE = true;
    /*private static NormalizedLevenshtein distance = new NormalizedLevenshtein();*/
    private static Levenshtein distance = new Levenshtein();

    private static double RESOLUTION = 0.01;
    /*private static String FILE_OUT = "psn_dirty_completa_2017_03_09_local_heap_global.json";*/
    /*private static String FILE_OUT = "psn_dirty_completa_2017_03_09_local_heap_global.json";*/
    /*private static String FILE_OUT = "psn_clean_completa_2017_03_09_local_heap_global_.json";*/

    private static String PATH_OUT = "/Users/gio/Desktop/notebook progressive/data_json/paris/";
    /*private static String PATH_OUT = "";*/
    private static String FILE_OUT = PATH_OUT + "dirty_psn_distance_edit_myfunction.json";

    private static int[] DATASETS = new int[]{0, 1};
    /*private static int[] DATASETS = new int[]{2};*/
    /*private static int[] DATASETS = new int[]{0};*/

    private static ProfileType[] profileTypes = new ProfileType[]{
            ProfileType.CENSUS_PROFILE,
            ProfileType.RESTAURANT_PROFILE,
            ProfileType.CORA_PROFILE,
            ProfileType.CDDB_PROFILE,
    };

    public static void main(String[] args) {

        ExperimentsSN_list experiment;
        /*JaroWinkler distance = new JaroWinkler();*/

        int DATASET = 0;

        PrintWriter writer = null;

        Result res_final = new Result();

        //for (int dataset : new int[]{0, 1, 2, 3, 4, 5}) {
        //for (int dataset : new int[]{0, 1, 2, 3}) {
        //for (int dataset : new int[]{0,1}) {
        for (int dataset : DATASETS) {
            DATASET = dataset;

            System.out.println("\n\n\nDataset: " + Utilities.getName(dataset, CLEAN) + "\n");

            List<EntityProfile>[] profiles;
            AbstractDuplicatePropagation adp;

            Instant start = Instant.now();

            if (args.length > 0) {
                BASEPATH = args[0] + "/";
                profiles = Utilities.getEntities(BASEPATH, DATASET, CLEAN);
            } else {
                profiles = Utilities.getEntities(DATASET, CLEAN);
            }

            String name = "";
            System.out.println("\n\nCurrent weighting scheme\t:\t");

            //start = Instant.now();


            experiment = new ExperimentsSN_list(profiles, MAX_WINDOW);

            res_final.set_size(new Long[]{Long.valueOf(profiles[0].size()), CLEAN ? Long.valueOf(profiles[1].size()) : 0});

            System.out.println("build psn entity list");
            System.out.println("finish build psn entity list");

            /*try {
                writer = new PrintWriter("psn_res_out_" + Utilities.getName(dataset, CLEAN) + "_" + "psn_new" + "_classic_rep_" + ".txt", "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/

            for (int i = 0; i < experiment.descriptions.length; i++) {

                if (args.length > 0) {
                    BASEPATH = args[0] + "/";
                    adp = Utilities.getGroundTruth(BASEPATH, DATASET, CLEAN);
                } else {
                    adp = Utilities.getGroundTruth(DATASET, CLEAN);
                }

                Method pns_method = null;
                try {
                    pns_method = experiment.getClass().getMethod(experiment.descriptions[i], Integer.TYPE, ProfileType.class);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }

                res_final.set_dupl_e((long) adp.getExistingDuplicates());

                /*res_final.add_res_set();*/

                res_final.start();

                /*start = Instant.now();*/

                /*res_final.set_t_start(start.toEpochMilli());*/

                Iterator psn = null;
                try {
                    /*psn = (Iterator) pns_method.invoke(experiment, 1, profileTypes[dataset]);*/
                    psn = (Iterator) pns_method.invoke(experiment, -1, profileTypes[dataset]);
                    /*psn = (Iterator) pns_method.invoke(experiment, -1, null);*/
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                res_final.init();
                /*res_final.add_res(0, 0.0);*/

                String description = Utilities.getName(dataset, CLEAN) + "_";

                description += experiment.descriptions[i];

                res_final.set_desription(description);

                double comparisons = 0;
                double comparisons_old = 0;

                double pc = 0.0;
                double pc_old = 0.0;
                double pq = 0.0;
                double detectedDuplicates = 0;
                long totalComparison = CLEAN ? profiles[0].size() * profiles[1].size() : ((profiles[0].size() * (profiles[0].size() - 1)) / 2);

                ArrayList<Double> pcs = new ArrayList<>();
                ArrayList<Double> counts = new ArrayList<>();

                int print = 0;

                pcs.add(0.0);
                counts.add(0.0);

            /*HashSet<Comparison> comp = new HashSet<>();*/

                while (psn.hasNext()) {
                    comparisons++;
                    detectedDuplicates = adp.getNoOfDuplicates();
                    pc = ((double) detectedDuplicates) / adp.getExistingDuplicates();
                    if ((pc - pc_old) > RESOLUTION) {
                        res_final.add_res(detectedDuplicates, comparisons);
                        pc_old = pc;
                        print++;
                        if (print == 50) {
                            print = 0;
                            System.out.println("\npc: " + Math.round(pc * 100) / 100.0 + " - " + (comparisons - comparisons_old));
                            System.out.println("nc: " + comparisons);
                            System.out.println("partial time: " + Duration.between(start, Instant.now()).toString());
                        }
                        pcs.add(Math.round(pc * 100) / 100.0);
                        counts.add(comparisons);
                        comparisons_old = comparisons;
                    }
                    Comparison next = (Comparison) psn.next();
                    if (next != null) {
                    /*if (comp.contains(next)) {
                        System.out.println("not removing redundant comparisons");
                    }
                    comp.add(next);*/
                        adp.isSuperfluous(next);

                        if (COMPUTE_DISTANCE) {
                            EntityProfile e1 = profiles[0].get(next.getEntityId1());
                            EntityProfile e2 = profiles[0].get(next.getEntityId2());
                            /*distance.distance(e1.getString(), e2.getString());*/
                            minDistance(e1.getString(), e2.getString());
                            /*getEditDistance(e1.getString(), e2.getString());*/
                        /*System.out.println(e1.getString());
                        System.out.println(e2.getString());*/
                        }
                    }
                }
                res_final.add_res(detectedDuplicates, comparisons);

                /*res_final.set_comp_b((long) detectedDuplicates);*/

                //System.out.println("\n\nredundant comparison identifyied: " + ((NaiveProgressiveSn) psn).countRedundant + "\n");

                pcs.add(Math.round(pc * 100) / 100.0);
                counts.add(comparisons);

                List nc = new ArrayList<>();
                totalComparison = (dataset == 5) ? 1 : totalComparison;
                final long finalTotalComparison = totalComparison;
                /*nc = counts.stream().map(e -> (e / finalTotalComparison)).collect(Collectors.toList());*/
                int ff = adp.getExistingDuplicates();
                nc = counts.stream().map(e -> (e / ff)).collect(Collectors.toList());


                detectedDuplicates = adp.getNoOfDuplicates();
                pc = ((double) detectedDuplicates) / adp.getExistingDuplicates();
                pq = detectedDuplicates / (double) comparisons;

                System.out.println("\n\nr_comparisons = " + comparisons);
                System.out.println("t_comparisons = " + totalComparison + "\n\n");

                //System.out.println("nc_" + name + " = " + nc.toString());
                System.out.println("x_30_psn_t" + name + " = " + nc.toString());
                //System.out.println("res nc_" + name + " = " + counts.toString());
                //System.out.println("pc_" + name + " = " + pcs.toString());
                System.out.println("y_30_psn_t" + name + " = " + pcs.toString());

                Instant end = Instant.now();

            /*writer.println(" nc_" + name + " = " + nc.toString());
            writer.println(" pc_" + name + " = " + pcs.toString());
            writer.println(" ");*/

                /*res_final.set_t_end(end.toEpochMilli() - start.toEpochMilli());*/
                res_final.end();

                System.out.println("Total time: " + Duration.between(start, end).toString());

            /*writer.close();*/
            }
        }
        try {
            writer = new PrintWriter(FILE_OUT, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.print(res_final.toJson());
        writer.close();
    }

    public static int minDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // len1+1, len2+1, because finally return dp[len1][len2]
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update dp value for +1 length
                    dp[i + 1][j + 1] = dp[i][j];
                } else {
                    int replace = dp[i][j] + 1;
                    int insert = dp[i][j + 1] + 1;
                    int delete = dp[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    dp[i + 1][j + 1] = min;
                }
            }
        }
        return dp[len1][len2];
    }

    public static int getEditDistance(String sourceString, String destinationString) {
        if (sourceString == null || destinationString == null) {
            throw new IllegalArgumentException("String cannot be null");
        }

        int sourceLength = sourceString.length();
        int destLength = destinationString.length();
        int len = Math.min(sourceLength, destLength);

        int distance = Math.abs(sourceLength - destLength);
        for (int i = 0; i < len; ++i) {
            if (sourceString.charAt(i) != destinationString.charAt(i)) {
                ++distance;
            }
        }

        return distance;
    }
}