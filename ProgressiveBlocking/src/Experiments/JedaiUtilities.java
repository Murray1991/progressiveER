package Experiments;


//import org.scify.jedai.datamodel.EntityProfile;
import DataStructures.EntityProfile;
import DataStructures.NewEntitySerializationReader;
import org.scify.jedai.datamodel.IdDuplicates;
import org.scify.jedai.datareader.AbstractReader;
import org.scify.jedai.datareader.entityreader.EntitySerializationReader;
import org.scify.jedai.datareader.groundtruthreader.GtSerializationReader;
import org.scify.jedai.utilities.datastructures.AbstractDuplicatePropagation;
import org.scify.jedai.utilities.datastructures.BilateralDuplicatePropagation;
import org.scify.jedai.utilities.datastructures.UnilateralDuplicatePropagation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JedaiUtilities {

    //private final static String mainDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    //private final static String mainDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";
    private final static String mainDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    private final static String mainDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";

    //    private final static String mainDirectory = "/media/gap2/Data/Data/profiles/";

    private final static String[] entitiesPathDER = {
            "censusProfiles",
            "restaurantProfiles",
            "coraProfiles",
            "cddbProfiles"
    };

    private final static String[] entitiesPathCER = {
            "imdbProfiles",    // 0
            "dbpediaProfiles", // 1
    };


    private final static String mainGTDirectoryDER = "/Users/leonardo/Desktop/data/dirtyErDatasets/";
    private final static String mainGTDirectoryCER = "/Users/leonardo/Desktop/data/cleanCleanErDatasets/";

    private final static String[] entitiesPathDER_GT = {
            "censusIdDuplicates",
            "restaurantIdDuplicates",
            "coraIdDuplicates",
            "cddbIdDuplicates"
    };

    private final static String[] entitiesPathCER_GT = {
            "moviesIdDuplicates",
            "restaurantIdDuplicates",
            "coraIdDuplicates",
            "cddbIdDuplicates"
    };

    public static List<DataStructures.EntityProfile> getEntities(int datasetId, boolean clean) {
        String eFile;
        if (clean)
            eFile = mainDirectoryCER + entitiesPathCER[datasetId];
        else
            eFile = mainDirectoryDER + entitiesPathDER[datasetId];

        NewEntitySerializationReader eReader = new NewEntitySerializationReader(eFile);
        System.out.println("Returning entities...");
        return eReader.getEntityProfiles();
    }

    public static List<DataStructures.EntityProfile> getEntities(String dataset, boolean clean) {
        String eFile;
        if (clean)
            eFile = mainDirectoryCER + dataset;
        else
            eFile = mainDirectoryDER + dataset;

        NewEntitySerializationReader eReader = new NewEntitySerializationReader(eFile);
        System.out.println("Returning entities...");
        return eReader.getEntityProfiles();
    }

    public static List<DataStructures.EntityProfile> getEntities(String BASE, String dataset, boolean clean) {
        String eFile;
        if (clean)
            eFile = BASE + dataset;
        else
            eFile = BASE + dataset;

        NewEntitySerializationReader eReader = new NewEntitySerializationReader(eFile);
        System.out.println("Returning entities...");
        return eReader.getEntityProfiles();
    }

    public static AbstractDuplicatePropagation getGroundTruth(int datasetId, boolean clean) {
        String gtFile = clean ? mainGTDirectoryCER + entitiesPathCER_GT[datasetId] :
                mainGTDirectoryDER + entitiesPathDER_GT[datasetId];

        GtSerializationReader gtReader = new GtSerializationReader(gtFile);
        //Set<IdDuplicates> duplicates = new HashSet<>();
        //duplicates.addAll( (List) gtReader.loadSerializedObject(gtFile) );

        //Set<IdDuplicates> duplicates = gtReader.getDuplicatePairs(null);
        if (clean)
            return new BilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
        else
            return new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
    }

    public static AbstractDuplicatePropagation getGroundTruth(String dataset, boolean clean) {
        String gtFile = clean ? mainGTDirectoryCER + dataset :
                mainGTDirectoryDER + dataset;

        GtSerializationReader gtReader = new GtSerializationReader(gtFile);
        //Set<IdDuplicates> duplicates = new HashSet<>();
        //duplicates.addAll( (List) gtReader.loadSerializedObject(gtFile) );

        //Set<IdDuplicates> duplicates = gtReader.getDuplicatePairs(null);
        if (clean)
            return new BilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
        else
            return new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
    }

    public static AbstractDuplicatePropagation getGroundTruth(String BASE, String dataset, boolean clean) {
        String gtFile = clean ? BASE + dataset :
                BASE + dataset;

        GtSerializationReader gtReader = new GtSerializationReader(gtFile);
        //Set<IdDuplicates> duplicates = new HashSet<>();
        //duplicates.addAll( (List) gtReader.loadSerializedObject(gtFile) );

        //Set<IdDuplicates> duplicates = gtReader.getDuplicatePairs(null);
        if (clean)
            return new BilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
        else
            return new UnilateralDuplicatePropagation(gtReader.getDuplicatePairs(null));
    }
}
