package Experiments.complete_experiment;

import BlockBuilding.MemoryBased.TokenBlocking;
import BlockBuilding.Progressive.ProgressiveMetaBlocking.CepCnp;
import BlockBuilding.Progressive.SortedEntities.CepCnpEntities;
import BlockProcessing.BlockRefinement.BlockFiltering;
import BlockProcessing.BlockRefinement.ComparisonsBasedBlockPurging;

import DataStructures.AbstractBlock;
import DataStructures.Comparison;
import DataStructures.EntityProfile;
import Experiments.JedaiUtilities;
import Experiments.Utilities;
import MetaBlocking.WeightingScheme;
import Utilities.BlockStatistics;
import org.scify.jedai.datamodel.IdDuplicates;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author giovanni
 */
public class JedaiCepExperiments {

    public static void main(String[] args) {

        // ARGUMENTS: D1, D2, GT, FRACTION

        String BASE = args[0];
        String D1 = args[1];
        String D2 = args[2];
        String GT = args[3];
        Integer FRACTION = Integer.parseInt(args[4]);

        boolean CLEAN = true;
        double FILTER_RATIO = 0.8d;
        boolean RESERVE_BLOCK_FILTERING = false;

        PrintWriter writer = null;

        //System.out.println("\nDataset: " + Utilities.getName(dataset_num, CLEAN) + "\n");

        List<EntityProfile> profiles1;
        List<EntityProfile> profiles2;
        AbstractDuplicatePropagation adp;
        AbstractDuplicatePropagation adp_tmp;

        Instant start = Instant.now();

        System.out.println("D1: " + BASE+D1);
        System.out.println("D2: " + BASE+D2);

        profiles1 = JedaiUtilities.getEntities(BASE, D1, true);
        profiles2 = JedaiUtilities.getEntities(BASE, D2, true);

        System.out.println("D1 size : " + profiles1.size());
        System.out.println("D2 size : " + profiles2.size());


        String name = "";
        System.out.println("\n\nCurrent weighting scheme\t:\t");

        //WeightingScheme[] ws = WeightingScheme.values();
        WeightingScheme[] ws = new WeightingScheme[1];
        ws[0] = WeightingScheme.ARCS;
        //ws[1] = WeightingScheme.EJS;
        //ws[2] = WeightingScheme.ARCS;


        Instant end = Instant.now();
        Instant newStart = Instant.now();

        Duration timeInstant;

        for (WeightingScheme wScheme : ws) {
            CepCnp[] methods = new CepCnp[1];
            //methods[0] = new ProgressiveCardinalityEdgePruning(wScheme, 421075225, true);
            //methods[0] = new ProgressiveCardinalityEdgePruning(wScheme, 1000, true);
            //methods[0] = new CepCnp(wScheme);
            methods[0] = new CepCnpEntities(wScheme, profiles1.size()+profiles2.size());
            //methods[0] = new CepCnp(wScheme, 10);

            for (CepCnp progressiveBlocking : methods) {

                try {
                    writer = new PrintWriter("res_out_" + GT +"_" + FRACTION + "_" + progressiveBlocking.getName() + "_" + wScheme + "_f_" + FILTER_RATIO + "_p_" + FRACTION + ".txt", "UTF-8");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                adp = JedaiUtilities.getGroundTruth(BASE, GT, true);
                //adp_tmp = JedaiUtilities.getGroundTruth(0, true);

//                    //for (AbstractProgressiveMetaBlocking progressiveBloccking :) {
//                    if (args.length > 0) {
//                        BASEPATH = args[0] + "/";
//                        adp = Utilities.getGroundTruth(BASEPATH, dataset_num, CLEAN);
//                        adp_tmp = Utilities.getGroundTruth(BASEPATH, dataset_num, CLEAN);
//                    } else {
//                        adp = Utilities.getGroundTruth(dataset_num, CLEAN);
//                        adp_tmp = Utilities.getGroundTruth(dataset_num, CLEAN);
//                    }

                name = wScheme + "";
                System.out.println("\n\nCurrent weighting scheme\t:\t" + wScheme);

                //start = Instant.now();

                TokenBlocking tb;
                tb = new TokenBlocking(new List[]{profiles1, profiles2});
                List<AbstractBlock> blocks = tb.buildBlocks();

                // TODO ?
                double SMOOTH_FACTOR = CLEAN ? 1.005 : 1.015;

                ComparisonsBasedBlockPurging cbbp = new ComparisonsBasedBlockPurging(SMOOTH_FACTOR);
                cbbp.applyProcessing(blocks);

                BlockFiltering bf = new BlockFiltering(FILTER_RATIO, RESERVE_BLOCK_FILTERING);
                bf.applyProcessing(blocks);
                System.out.println("bf: " + blocks.size());
                System.out.println("bf: " + bf.getReserveBlocks().size());

                System.out.println("bf pc");

                System.out.println("progressive start");

                progressiveBlocking.applyProcessing(blocks);
                //methods[0].applyProcessing(blocks);


                double comparisons = 0;
                double comparisons_old = 0;

                double pc = 0.0;
                double pc_old = 0.0;
                double pq = 0.0;
                double detectedDuplicates = 0;
                double totalComparison = CLEAN ? profiles1.size() * profiles2.size() : ((profiles1.size() * (profiles2.size() - 1)) / 2);

                ArrayList<Double> pcs = new ArrayList<>();
                ArrayList<Double> counts = new ArrayList<>();
                ArrayList<String> times = new ArrayList<>();

                int print = 0;

                pcs.add(0.0);
                counts.add(0.0);

                //val (a, b) = (duplicates.contains(new IdDuplicates(cmp.e1, cmp.e2)), duplicates.contains(new IdDuplicates(cmp.e2, cmp.e1)))
                double duplicatesSize = (double) adp.getExistingDuplicates();
                System.out.println("NUMBER OF DUPLICATES: " + duplicatesSize);

                Set<IdDuplicates> duplicates = adp.getDuplicates();

                //Duration newTimeInstant = Duration.between(newStart, Instant.now());
                timeInstant = Duration.between(newStart, Instant.now());

                System.out.println("Initialization time: " + timeInstant.toString());
                //System.out.println("Initialization time: " + newTimeInstant.toString());

                while (progressiveBlocking.hasNext()) {

                    comparisons++;

                    Comparison c = progressiveBlocking.next();
                    boolean b1 = duplicates.contains(new IdDuplicates(c.getEntityId1(), c.getEntityId2()));
                    boolean isSuperfluous = adp.isSuperfluous(c.getEntityId1(), c.getEntityId2());
                    if (!isSuperfluous && b1)
                        detectedDuplicates += 1;

                    if (c.getEntityId1() > profiles1.size() || c.getEntityId2() > profiles2.size()) {
                        System.out.println("ERRROR");
                        return ;
                    }

                    pc = ((double) detectedDuplicates) / adp.getExistingDuplicates();
                    if ((pc - pc_old) > .02) {
                        pc_old = pc;
                        print++;
                        if (print == 5) {
                            print = 0;

                            timeInstant = Duration.between(newStart, Instant.now());
                            
                            //System.out.println("\npc: " + Math.round(pc * 100) / 100.0 + " - " + (comparisons - comparisons_old));
                            //System.out.println("nc: " + comparisons);
                            //System.out.println("partial time: " + timeInstant.toString());
                            System.out.println(comparisons/duplicatesSize + "\t" + comparisons + "\t" + pc + "\t" + Math.round(pc * 100) / 100.0 + "\t" + timeInstant.toString() + "\t" + (comparisons - comparisons_old) );
                        }
                        //pq = detectedDuplicates / (double) comparisons;
                        //System.out.println("pc: " + Math.round(pc * 100) / 100.0 + " - " + (comparisons - comparisons_old));
                        pcs.add(Math.round(pc * 100) / 100.0);
                        counts.add(comparisons);
                        times.add(timeInstant.toString());
                        comparisons_old = comparisons;
                    }

                }

                System.out.println("DETECTED DUPLICATES: "+ detectedDuplicates);

                /*System.out.println("reserve: " + bf.getReserveBlocks().size());
                //CepCnp pb = new CepCnp(wScheme);
                CepBlockScheduling pb = new CepBlockScheduling(wScheme);
                //pb.setThreshold(100000);
                pb.applyProcessing(bf.getReserveBlocks());
                System.out.println("reserve: " + bf.getReserveBlocks().size());

                System.out.println("reserve comparisons");

                while (pb.hasNext()) {
                    comparisons++;
                    detectedDuplicates = adp.getNoOfDuplicates();
                    pc = ((double) detectedDuplicates) / adp.getExistingDuplicates();
                    if ((pc - pc_old) > .02) {
                        pc_old = pc;
                        print++;
                        if (print == 5) {
                            print = 0;
                            System.out.println("\npc: " + Math.round(pc * 100) / 100.0 + " - " + (comparisons - comparisons_old));
                            System.out.println("nc: " + comparisons);
                            System.out.println("partial time: " + Duration.between(start, Instant.now()).toString());
                        }
                        pq = detectedDuplicates / (double) comparisons;
                        System.out.println("pc: " + Math.round(pc * 100) / 100.0 + " - " + (comparisons - comparisons_old));
                        pcs.add(Math.round(pc * 100) / 100.0);
                        counts.add(comparisons);
                        comparisons_old = comparisons;
                    }
                    adp.isSuperfluous(pb.next());
                }*/

                pcs.add(Math.round(pc * 100) / 100.0);
                counts.add(comparisons);

                //totalComparison = (dataset_num == 5) ? 1 : totalComparison;
                final double finalTotalComparison = totalComparison;
                List ec = counts.stream().map(e -> (e / duplicatesSize)).collect(Collectors.toList());
                List nc = counts.stream().map(e -> (e / finalTotalComparison)).collect(Collectors.toList());

                //detectedDuplicates = adp.getNoOfDuplicates();
                pc = ((double) detectedDuplicates) / adp.getExistingDuplicates();
                pq = detectedDuplicates / (double) comparisons;

                System.out.println("\n\nr_comparisons = " + comparisons);
                System.out.println("t_comparisons = " + totalComparison + "\n\n");

                System.out.println("ec_" + name + " = " + ec.toString());
                System.out.println("nc_" + name + " = " + nc.toString());
                //System.out.println("res nc_" + name + " = " + counts.toString());
                System.out.println("pc_" + name + " = " + pcs.toString());
                System.out.println("times_" + name + " = " + times.toString());

                end = Instant.now();

                System.out.println("final pc: " + pc);

                writer.println(" ec_" + name + " = " + ec.toString());
                writer.println(" nc_" + name + " = " + nc.toString());
                writer.println(" pc_" + name + " = " + pcs.toString());
                writer.println("Total time: " + Duration.between(start, end).toString());
                writer.println(" ");

                System.out.println("Total time: " + Duration.between(start, end).toString());

                writer.close();
            }
        }
    }
}